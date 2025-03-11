import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormsModule, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Income } from '../../entity/income';
import { IncomeService } from './income.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: 'income.component.html',
  styleUrls: ['income.component.css']
})
export class IncomeComponent implements OnInit {
  // Propriedades gerais
  title = 'income';
  incomes: Income[] = [];
  filteredIncomes: Income[] = [];
  filteredBarData: Income[] = [];
  filteredList: Income[] = []; // Lista para filtro de valor e data

  // Propriedades para datas e gráficos
  startDate: string = '';
  endDate: string = '';
  startMonth: string = '';
  endMonth: string = '';
  pieChartData: { categorias: { [key: string]: number } } | null = null;
  barChartData: { dadosMensais: { [key: string]: number } } | null = null;

  // Propriedades para modos e modais
  isRemoving = false;
  isEditing = false;
  editingIncomeId: string | null = null;
  modalType: 'create' | 'edit' | null = null;

  // Filtros de valor e data
  minValue: number | null = null;
  maxValue: number | null = null;
  filterStartDate: string = '';
  filterEndDate: string = '';

  // Injeção de dependências
  private incomeService = inject(IncomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  // Formulários reativos
  createIncomeForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  editIncomeForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required]
  });

  // ---------------------- Ciclo de Vida ----------------------
  async ngOnInit() {
    await this.loadIncomes();
    this.initializeDates();
    await this.loadPieChartData();
    await this.loadBarChartData();
  }

  private initializeDates() {
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    this.startDate = firstDayOfMonth.toISOString().split('T')[0];
    this.endDate = today.toISOString().split('T')[0];
    this.startMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
    this.endMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
  }

  // ---------------------- Métodos de Carregamento ----------------------
  async loadIncomes() {
    const response = await this.incomeService.getIncomes();
    if (response) {
      this.incomes = response;
      this.filteredIncomes = [...this.incomes];
      this.filteredBarData = [...this.incomes];
    }
  }

  async loadPieChartData() {
    if (this.startDate && this.endDate) {
      this.pieChartData = await this.incomeService.getIncomePizzaChart(this.startDate, this.endDate);
      this.drawPieChart(this.getPieChartSlicesFromBackend());
      this.updatePieChartLegend();
    }
  }

  async loadBarChartData() {
    if (this.startMonth && this.endMonth) {
      this.barChartData = await this.incomeService.getIncomeBarChart(this.startMonth, this.endMonth);
      this.drawBarChartFromBackend();
    }
  }

  // ---------------------- Métodos de Modo (Edit/Remove) e Navegação ----------------------
  toggleRemoveMode() {
    this.isRemoving = !this.isRemoving;
    if (this.isEditing) {
      this.isRemoving = false;
    }
  }

  toggleEditMode() {
    this.isEditing = !this.isEditing;
    if (this.isRemoving) {
      this.isEditing = false;
    }
  }

  home() {
    this.router.navigate(['/home']);
  }

  refreshPage() {
    window.location.reload();
  }

  // ---------------------- Métodos para Gráficos ----------------------
  // Métodos utilitários para manipulação do SVG
  private getSvgElement(id: string): SVGSVGElement | null {
    const element = document.getElementById(id);
    return element instanceof SVGSVGElement ? element : null;
  }

  private drawPieSlice(svg: SVGSVGElement, centerX: number, centerY: number, radius: number, startAngle: number, sliceAngle: number, color: string) {
    const endAngle = startAngle + sliceAngle;
    const x1 = centerX + radius * Math.cos(startAngle);
    const y1 = centerY + radius * Math.sin(startAngle);
    const x2 = centerX + radius * Math.cos(endAngle);
    const y2 = centerY + radius * Math.sin(endAngle);
    const largeArcFlag = sliceAngle > Math.PI ? 1 : 0;
    const pathData = `M ${centerX} ${centerY} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2} Z`;

    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('d', pathData);
    path.setAttribute('fill', color);
    svg.appendChild(path);
  }

  // Geração de slices para o gráfico de pizza vindo do backend
  private getPieChartSlicesFromBackend(): { label: string, value: number }[] {
    if (!this.pieChartData) return [];
    return Object.entries(this.pieChartData.categorias).map(([categoria, valor]) => ({ label: categoria, value: valor }));
  }

  // Geração de slices para o gráfico de pizza a partir das receitas filtradas
  private getPieChartSlicesFromIncomes(): { label: string, value: number }[] {
    const total = this.filteredIncomes.reduce((sum, income) => sum + income.valor, 0);
    return this.filteredIncomes.map(income => ({
      label: income.categoria,
      value: income.valor
    }));
  }

  // Desenha o gráfico de pizza usando os slices informados
  private drawPieChart(slices: { label: string, value: number }[]) {
    const svg = this.getSvgElement('incomeChart');
    if (!svg || slices.length === 0) return;
    svg.innerHTML = '';

    const total = slices.reduce((sum, slice) => sum + slice.value, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    slices.forEach((slice, index) => {
      const sliceAngle = (slice.value / total) * 2 * Math.PI;
      this.drawPieSlice(svg, centerX, centerY, radius, startAngle, sliceAngle, this.getColor(index));
      startAngle += sliceAngle;
    });
  }

  // Atualiza a legenda do gráfico de pizza (exemplo de uso: lista as categorias e valores)
  private updatePieChartLegend() {
    if (!this.pieChartData) return;
    // Aqui, a legenda é atualizada a partir dos dados do backend
    this.filteredIncomes = Object.entries(this.pieChartData.categorias).map(([categoria, valor]) => ({
      categoria,
      valor,
      data: new Date(),
      origemDoPagamento: '',
      observacoes: ''
    } as Income));
  }

  // Geração e desenho do gráfico de barras com dados do backend
  private drawBarChartFromBackend() {
    const svg = this.getSvgElement('barChart');
    if (!svg || !this.barChartData) return;
    svg.innerHTML = '';

    const months = Object.keys(this.barChartData.dadosMensais);
    if (months.length === 0) return;

    const values = months.map(m => this.barChartData!.dadosMensais[m]);
    const maxValor = Math.max(...values);
    const barWidth = 50;
    const barSpacing = 100;
    const startX = 50;
    const startY = 350;
    const chartHeight = 200;

    months.forEach((month, index) => {
      const barHeight = (this.barChartData!.dadosMensais[month] / maxValor) * chartHeight;
      const x = startX + index * barSpacing;
      const y = startY - barHeight;

      // Cria a barra
      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);

      // Rótulo do mês no eixo X (exibe as 3 primeiras letras)
      const displayMonth = month.split(' ')[0].substring(0, 3);
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 30).toString());
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "20px");
      text.textContent = displayMonth;
      svg.appendChild(text);

      // Valor acima da barra
      const valueText = document.createElementNS("http://www.w3.org/2000/svg", "text");
      valueText.setAttribute("x", (x + barWidth / 2).toString());
      valueText.setAttribute("y", (y - 10).toString());
      valueText.setAttribute("text-anchor", "middle");
      valueText.setAttribute("font-size", "18");
      valueText.textContent = `R$${this.barChartData!.dadosMensais[month].toFixed(0)}`;
      svg.appendChild(valueText);
    });

    // Linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // Geração do gráfico de barras a partir das receitas filtradas (fallback)
  generateBarChart() {
    const svg = this.getSvgElement('barChart');
    if (!svg) return;
    svg.innerHTML = '';

    // Agrega receitas por mês
    const totals: { [key: string]: number } = {};
    this.filteredBarData.forEach(income => {
      const date = new Date(income.data);
      const monthYear = `${date.toLocaleString('default', { month: 'short' })} ${date.getFullYear()}`;
      totals[monthYear] = (totals[monthYear] || 0) + income.valor;
    });

    const months = Object.keys(totals).sort((a, b) => {
      const [monthA, yearA] = a.split(' ');
      const [monthB, yearB] = b.split(' ');
      const dateA = new Date(`${yearA}-${this.getMonthNumber(monthA)}-01`);
      const dateB = new Date(`${yearB}-${this.getMonthNumber(monthB)}-01`);
      return dateA.getTime() - dateB.getTime();
    });

    const values = months.map(m => totals[m]);
    const maxValor = Math.max(...values);
    const barWidth = 25;
    const barSpacing = 50;
    const startX = 50;
    const startY = 350;
    const chartHeight = 200;

    months.forEach((month, index) => {
      const barHeight = (totals[month] / maxValor) * chartHeight;
      const x = startX + index * barSpacing;
      const y = startY - barHeight;

      // Cria a barra
      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);

      // Rótulo do mês
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 15).toString());
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "12px");
      text.textContent = month;
      svg.appendChild(text);
    });

    // Linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // ---------------------- Helpers para Gráficos ----------------------
  // Retorna cor com base no índice (ciclo entre cores predefinidas)
  getColor(index: number): string {
    const colors = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF",
      "#FF9F40", "#D4AF37", "#8A2BE2", "#20B2AA", "#DC143C",
      "#FFD700", "#4682B4", "#32CD32", "#FF4500", "#6A5ACD",
      "#008080", "#8B0000", "#556B2F", "#D2691E", "#1E90FF"
    ];
    return colors[index % colors.length];
  }

  // Converte abreviação do mês para número (auxiliar para ordenação)
  private getMonthNumber(month: string): number {
    const monthsMap: { [key: string]: number } = {
      'jan.': 1, 'fev.': 2, 'mar.': 3, 'abr.': 4, 'mai.': 5, 'jun.': 6,
      'jul.': 7, 'ago.': 8, 'set.': 9, 'out.': 10, 'nov.': 11, 'dez.': 12
    };
    return monthsMap[month.toLowerCase()] || 0;
  }

  // ---------------------- Métodos de Filtro ----------------------
  async applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredIncomes = [...this.incomes];
      this.drawPieChart(this.getPieChartSlicesFromIncomes());
      this.generateBarChart();
    } else {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);
      this.filteredIncomes = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        return incomeDate >= start && incomeDate <= end;
      });
      await this.loadPieChartData();
      await this.loadBarChartData();
    }
  }

  async clearFilter() {
    this.startDate = '';
    this.endDate = '';
    this.filteredIncomes = [...this.incomes];
    this.pieChartData = null;
    this.barChartData = null;
    this.drawPieChart(this.getPieChartSlicesFromIncomes());
    this.generateBarChart();
  }

  async applyMonthFilter() {
    if (!this.startMonth || !this.endMonth) return;
    await this.loadBarChartData();
    this.filteredBarData = this.incomes.filter(income => {
      const incomeDate = new Date(income.data);
      const incomeYearMonth = `${incomeDate.getFullYear()}-${String(incomeDate.getMonth() + 1).padStart(2, '0')}`;
      return incomeYearMonth >= this.startMonth && incomeYearMonth <= this.endMonth;
    });
  }

  clearMonthFilter() {
    this.startMonth = '';
    this.endMonth = '';
    this.barChartData = null;
    this.filteredBarData = [...this.incomes];
    this.generateBarChart();
  }

  applyValueDateFilter() {
    if (this.minValue || this.maxValue || this.filterStartDate || this.filterEndDate) {
      this.filteredList = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        return this.isDateInFilterRange(incomeDate) && this.isValueInFilterRange(income.valor);
      });
    } else {
      this.filteredList = [];
    }
  }

  private isValueInFilterRange(value: number): boolean {
    if (this.minValue !== null && value < this.minValue) return false;
    if (this.maxValue !== null && value > this.maxValue) return false;
    return true;
  }

  private isDateInFilterRange(date: Date): boolean {
    if (!this.filterStartDate && !this.filterEndDate) return true;
    const start = this.filterStartDate ? new Date(this.filterStartDate) : null;
    const end = this.filterEndDate ? new Date(this.filterEndDate) : null;
    if (start && date < start) return false;
    if (end && date > end) return false;
    return true;
  }

  clearValueDateFilter() {
    this.minValue = null;
    this.maxValue = null;
    this.filterStartDate = '';
    this.filterEndDate = '';
    this.filteredList = [];
  }

  // ---------------------- Métodos de Manipulação de Receitas ----------------------
  openModal(type: 'create' | 'edit') {
    this.modalType = type;
  }

  closeModal() {
    this.modalType = null;
  }

  onSubmitCreate() {
    if (this.createIncomeForm.valid) {
      const { data, categoria, valor, origemDoPagamento, observacoes } = this.createIncomeForm.value;
      const newIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };

      this.incomeService.createIncome(newIncome)
        .then(() => {
          alert("Receita criada com sucesso!");
          this.refreshPage();
        })
        .catch(err => alert('Erro ao criar receita: ' + err));
    }
  }

  async onSubmitRemove(id: string) {
    try {
      await this.incomeService.removeIncome(id);
      alert('Receita removida com sucesso!');
      await this.loadIncomes();
      await this.loadPieChartData();
      await this.loadBarChartData();
    } catch (err) {
      alert('Erro ao remover receita: ' + err);
    }
  }

  async onSubmitEdit(id: string) {
    if (this.editIncomeForm.valid) {
      try {
        const { data, categoria, valor, origemDoPagamento, observacoes } = this.editIncomeForm.value;
        const updatedIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };
        await this.incomeService.editIncome(id, updatedIncome);
        alert('Receita atualizada com sucesso!');
        this.refreshPage();
      } catch (err) {
        alert('Erro ao atualizar receita: ' + err);
      }
    }
  }

  openEditModal(income: Income) {
    this.modalType = 'edit';
    this.editingIncomeId = income.uuid!;
    this.editIncomeForm.setValue({
      data: income.data,
      categoria: income.categoria,
      valor: income.valor,
      origemDoPagamento: income.origemDoPagamento,
      observacoes: income.observacoes
    });
  }
}

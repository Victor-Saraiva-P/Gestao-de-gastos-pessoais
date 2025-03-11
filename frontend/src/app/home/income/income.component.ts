import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormsModule, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Income } from '../../entity/income';
import { OnInit } from '@angular/core';
import { IncomeService } from './income.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: 'income.component.html',
  styleUrls: ['income.component.css']
})
export class IncomeComponent implements OnInit{
  title = 'income';
  startDate: string = '';  // Data inicial
  endDate: string = '';    // Data final
  startMonth: string = '';
  endMonth: string = '';
  filteredIncomes: Income[] = [];  // Lista de receitas filtradas
  filteredBarData: Income[] = [];

  // Backend chart data properties
  pieChartData: { categorias: { [key: string]: number } } | null = null;
  barChartData: { dadosMensais: { [key: string]: number } } | null = null;

  incomes: Income[] = [];
  isRemoving = false;
  isEditing = false;
  editingIncomeId: string | null = null;
  modalType: 'create' | 'edit' | null = null;

  filteredList: Income[] = []; // Nova lista filtrada
  minValue: number | null = null;
  maxValue: number | null = null;
  filterStartDate: string = '';
  filterEndDate: string = '';

  private incomeService = inject(IncomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  createIncomeForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', [Validators.required]],
  });

  editIncomeForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required]
  });

  async ngOnInit() {
    await this.loadIncomes();  // Carrega as receitas do backend
    // Inicializa datas para os gráficos
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    this.startDate = firstDayOfMonth.toISOString().split('T')[0];
    this.endDate = today.toISOString().split('T')[0];

    this.startMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
    this.endMonth = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;

    // Carrega dados iniciais para ambos os gráficos
    await this.loadPieChartData();
    await this.loadBarChartData();
  }

  // Métodos para carregar dados dos gráficos do backend
  async loadPieChartData() {
    if (this.startDate && this.endDate) {
      this.pieChartData = await this.incomeService.getIncomePizzaChart(this.startDate, this.endDate);
      this.generatePieChartFromBackend();
    }
  }

  async loadBarChartData() {
    if (this.startMonth && this.endMonth) {
        this.barChartData = await this.incomeService.getIncomeBarChart(this.startMonth, this.endMonth);
        this.generateBarChartFromBackend();
    }
  }

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

  getColor(index: number): string {
    const colors = ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF",
    "#FF9F40", "#D4AF37", "#8A2BE2", "#20B2AA", "#DC143C",
    "#FFD700", "#4682B4", "#32CD32", "#FF4500", "#6A5ACD",
    "#008080", "#8B0000", "#556B2F", "#D2691E", "#1E90FF"];
    return colors[index % colors.length];
  }

  async loadIncomes() {
    const response = await this.incomeService.getIncomes();
    if (response) {
      this.incomes = response;
      this.filteredIncomes = [...this.incomes];
      this.filteredBarData = [...this.incomes];
    }
  }

  async applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredIncomes = [...this.incomes];
      this.generatePieChartFromIncomes(); // Fallback para o método anterior
      this.generateBarChart(); // Fallback para o método anterior
    } else {
      // Atualiza a lista filtrada para exibição na tabela
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);

      this.filteredIncomes = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        return incomeDate >= start && incomeDate <= end;
      });

      // Busca dados dos gráficos do backend
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
    this.generatePieChartFromIncomes();
    this.generateBarChart();
  }

  // Métodos para renderizar o gráfico de pizza
  generatePieChartFromBackend() {
    const svg = document.getElementById('incomeChart') as unknown as SVGSVGElement;
    if (!svg || !this.pieChartData) return;

    svg.innerHTML = '';

    const categories = Object.keys(this.pieChartData.categorias);
    if (categories.length === 0) return;

    const total = Object.values(this.pieChartData.categorias).reduce((sum, value) => sum + value, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    categories.forEach((category, index) => {
      const value = this.pieChartData!.categorias[category];
      const sliceAngle = (value / total) * 2 * Math.PI;
      const endAngle = startAngle + sliceAngle;

      const x1 = centerX + radius * Math.cos(startAngle);
      const y1 = centerY + radius * Math.sin(startAngle);
      const x2 = centerX + radius * Math.cos(endAngle);
      const y2 = centerY + radius * Math.sin(endAngle);

      const largeArcFlag = sliceAngle > Math.PI ? 1 : 0;
      const pathData = `M ${centerX} ${centerY} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2} Z`;

      const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
      path.setAttribute('d', pathData);
      path.setAttribute('fill', this.getColor(index));
      svg.appendChild(path);

      startAngle = endAngle;
    });

    this.updatePieChartLegend();
  }

  updatePieChartLegend() {
    if (!this.pieChartData) return;

    this.filteredIncomes = Object.entries(this.pieChartData.categorias).map(([categoria, valor]) => {
      return {
        categoria,
        valor,
        data: new Date(),
        origemDoPagamento: '',
        observacoes: ''
      } as Income;
    });
  }

  generatePieChartFromIncomes() {
    const svg = document.getElementById('incomeChart') as unknown as SVGSVGElement;
    if (!svg) return;

    svg.innerHTML = '';

    if (this.filteredIncomes.length === 0) return;

    const total = this.filteredIncomes.reduce((sum, income) => sum + income.valor, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    this.filteredIncomes.forEach((income, index) => {
      const sliceAngle = (income.valor / total) * 2 * Math.PI;
      const endAngle = startAngle + sliceAngle;

      const x1 = centerX + radius * Math.cos(startAngle);
      const y1 = centerY + radius * Math.sin(startAngle);
      const x2 = centerX + radius * Math.cos(endAngle);
      const y2 = centerY + radius * Math.sin(endAngle);

      const largeArcFlag = sliceAngle > Math.PI ? 1 : 0;
      const pathData = `M ${centerX} ${centerY} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2} Z`;

      const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
      path.setAttribute('d', pathData);
      path.setAttribute('fill', this.getColor(index));
      svg.appendChild(path);

      startAngle = endAngle;
    });
  }

  // Métodos para o gráfico de barras
  async applyMonthFilter() {
    if (!this.startMonth || !this.endMonth) return;

    // Busca dados do backend usando apenas ano-mês
    await this.loadBarChartData();

    // Atualiza a tabela filtrada comparando apenas ano e mês
    this.filteredBarData = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        const incomeYearMonth = `${incomeDate.getFullYear()}-${String(incomeDate.getMonth() + 1).padStart(2, '0')}`;
        return incomeYearMonth >= this.startMonth && incomeYearMonth <= this.endMonth;
    });
  }

  async clearMonthFilter() {
    this.startMonth = '';
    this.endMonth = '';
    this.barChartData = null;
    this.filteredBarData = [...this.incomes];
    this.generateBarChart();
  }

  // Nova função que gera gráfico de barras a partir dos dados do backend
  generateBarChartFromBackend() {
    const svg = document.getElementById('barChart') as unknown as SVGSVGElement;
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

        // Criando a barra
        const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        rect.setAttribute("x", x.toString());
        rect.setAttribute("y", y.toString());
        rect.setAttribute("width", barWidth.toString());
        rect.setAttribute("height", barHeight.toString());
        rect.setAttribute("fill", this.getColor(index));
        svg.appendChild(rect);

        // Processa o nome do mês para exibir apenas as 3 primeiras letras (ex: "março 2025" -> "mar 2025")
        const parts = month.split(' ');
        const displayMonth = parts.length >= 2
            ? `${parts[0].substring(0,3)} ${parts[1]}`
            : month.substring(0,3);

        // Adicionando o rótulo do mês no eixo X
        const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttribute("x", (x + barWidth / 2).toString());
        text.setAttribute("y", (startY + 30).toString()); // Aumenta a coordenada Y para afastar o rótulo da barra
        text.setAttribute("text-anchor", "middle");
        text.setAttribute("font-size", "20px");
        text.textContent = displayMonth;
        svg.appendChild(text);

        // Adicionando valor acima da barra
        const valueText = document.createElementNS("http://www.w3.org/2000/svg", "text");
        valueText.setAttribute("x", (x + barWidth / 2).toString());
        valueText.setAttribute("y", (y - 10).toString()); // Ajusta a coordenada Y para afastar o valor da barra
        valueText.setAttribute("text-anchor", "middle");
        valueText.setAttribute("font-size", "18");
        valueText.textContent = `R$${this.barChartData!.dadosMensais[month].toFixed(0)}`;
        svg.appendChild(valueText);
    });

    // Criando linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // Método de fallback para o gráfico de barras (mantido para compatibilidade)
  getMonthNumber(month: string): number {
    const monthsMap: { [key: string]: number } = {
      'jan.': 1, 'fev.': 2, 'mar.': 3, 'abr.': 4, 'mai.': 5, 'jun.': 6,
      'jul.': 7, 'ago.': 8, 'set.': 9, 'out.': 10, 'nov.': 11, 'dez.': 12
    };
    return monthsMap[month] || 0;
  }

  generateBarChart() {
    const svg = document.getElementById('barChart') as unknown as SVGSVGElement;
    if (!svg) return;
    svg.innerHTML = '';

    const totals: { [key: string]: number } = {};
    this.filteredBarData.forEach(income => {
      const date = new Date(income.data);
      const monthYear = `${date.toLocaleString('default', { month: 'short' })} ${date.getFullYear()}`;
      if (!totals[monthYear]) totals[monthYear] = 0;
      totals[monthYear] += income.valor;
    });

    const months = Object.keys(totals)
      .sort((a, b) => {
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

      // Criando a barra
      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);

      // Adicionando o rótulo do mês no eixo X
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 15).toString());
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "12px");
      text.textContent = month;
      svg.appendChild(text);
    });

    // Criando linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // Métodos relacionados à manipulação de incomes (create, edit, remove)
  refreshPage() {
    window.location.reload();
  }

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

      this.incomeService.createIncome(newIncome).then(() => {
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
        const {data, categoria, valor, origemDoPagamento, observacoes } = this.editIncomeForm.value;
        const updatedIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };

        await this.incomeService.editIncome(id, updatedIncome);
        alert('Receita atualizada com sucesso!');
        this.refreshPage();
      } catch (err) {
        alert('Error updating income: ' + err);
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

  // Métodos de filtro de valores
  applyValueDateFilter() {
    if (this.minValue || this.maxValue || this.filterStartDate || this.filterEndDate) {
      this.filteredList = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        return this.isDateInFilterRange(incomeDate) &&
              this.isValueInFilterRange(income.valor);
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

  home() {
    this.router.navigate(['/home']);
  }
}

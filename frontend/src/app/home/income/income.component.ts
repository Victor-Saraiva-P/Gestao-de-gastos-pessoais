import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormBuilder,
  FormsModule,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Income } from '../../entity/income';
import { IncomeService } from './income.service';
import { ChartUtils } from '../../utils/income-chart-utils';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: 'income.component.html',
  styleUrls: ['income.component.css'],
})
export class IncomeComponent implements OnInit, OnDestroy {
  public chartUtils = ChartUtils;
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

  // Propriedades para filtros avançados
  filterType: 'value' | 'date' | null = null;

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
    observacoes: ['', Validators.required],
  });

  // ---------------------- Ciclo de Vida ----------------------
  async ngOnInit() {
    await this.loadIncomes();
    this.initializeDates();
    await this.loadPieChartData();
    await this.loadBarChartData();

    // Limpar gráficos ao sair da página
    window.addEventListener('beforeunload', () => {
      ChartUtils.destroyCharts();
    });
  }

  ngOnDestroy() {
    ChartUtils.destroyCharts();
  }

  private initializeDates() {
    const today = new Date();
    // Para o gráfico de pizza, continua do primeiro dia do mês até hoje
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    this.startDate = firstDayOfMonth.toISOString().split('T')[0];
    this.endDate = today.toISOString().split('T')[0];

    // Para o gráfico de barras, define o filtro para o ano atual inteiro
    const currentYear = today.getFullYear();
    this.startMonth = `${currentYear}-01`;
    this.endMonth = `${currentYear}-12`;
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
      this.pieChartData = await this.incomeService.getIncomePizzaChart(
        this.startDate,
        this.endDate
      );
      ChartUtils.drawPieChart(
        ChartUtils.getPieChartSlicesFromBackend(this.pieChartData),
        'incomeChart'
      );
      this.filteredIncomes = ChartUtils.getPieChartLegend(this.pieChartData);
    }
  }

  async loadBarChartData() {
    if (this.startMonth && this.endMonth) {
      this.barChartData = await this.incomeService.getIncomeBarChart(
        this.startMonth,
        this.endMonth
      );
      ChartUtils.drawBarChartFromBackend(this.barChartData, 'barChart');
    }
  }

  // ---------------------- Métodos de Modo e Navegação ----------------------
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

  // ---------------------- Métodos de Filtro ----------------------
  async applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredIncomes = [...this.incomes];
      ChartUtils.drawPieChart(
        ChartUtils.getPieChartSlicesFromIncomes(this.filteredIncomes),
        'incomeChart'
      );
      ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
    } else {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);
      this.filteredIncomes = this.incomes.filter((income) => {
        const incomeDate = new Date(income.data);
        return incomeDate >= start && incomeDate <= end;
      });
      await this.loadPieChartData();
      await this.loadBarChartData();
    }
  }

  async clearFilter() {
    // 1. Zera as datas
    this.startDate = '';
    this.endDate = '';

    // 2. Restaura as receitas filtradas para TODAS as receitas
    this.filteredIncomes = [...this.incomes];

    // 3. Limpa os dados dos gráficos para "forçar" o redesenho
    this.pieChartData = null;
    this.barChartData = null;

    // 4. Desenha novamente o gráfico de pizza com todas as receitas
    ChartUtils.drawPieChart(
      ChartUtils.getPieChartSlicesFromIncomes(this.filteredIncomes),
      'incomeChart'
    );

    // 5. Desenha novamente o gráfico de barras com o array `filteredBarData`
    ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
  }


  async applyMonthFilter() {
    if (!this.startMonth || !this.endMonth) return;
    await this.loadBarChartData();
    this.filteredBarData = this.incomes.filter((income) => {
      const incomeDate = new Date(income.data);
      const incomeYearMonth = `${incomeDate.getFullYear()}-${String(
        incomeDate.getMonth() + 1
      ).padStart(2, '0')}`;
      return (
        incomeYearMonth >= this.startMonth && incomeYearMonth <= this.endMonth
      );
    });
  }

  clearMonthFilter() {
    this.startMonth = '';
    this.endMonth = '';
    this.barChartData = null;
    this.filteredBarData = [...this.incomes];
    ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
  }

  applyValueDateFilter() {
    if (
      this.minValue ||
      this.maxValue ||
      this.filterStartDate ||
      this.filterEndDate
    ) {
      this.filteredList = this.incomes.filter((income) => {
        const incomeDate = new Date(income.data);
        return (
          this.isDateInFilterRange(incomeDate) &&
          this.isValueInFilterRange(income.valor)
        );
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

  // Método para aplicar filtro de valores
  async applyValueFilter() {
    if (this.minValue !== null && this.maxValue !== null) {
      this.filterType = 'value';
      this.filterStartDate = '';
      this.filterEndDate = '';

      try {
        const response = await this.incomeService.getIncomesByValueInterval(this.minValue, this.maxValue);
        this.filteredList = response || [];
      } catch (error) {
        console.error("Erro ao filtrar por valores:", error);
        this.filteredList = [];
      }
    } else {
      alert('Por favor, informe os valores mínimo e máximo para filtrar.');
    }
  }

  // Método para aplicar filtro de datas
  async applyDateRangeFilter() {
    if (this.filterStartDate && this.filterEndDate) {
      this.filterType = 'date';
      this.minValue = null;
      this.maxValue = null;

      try {
        const response = await this.incomeService.getIncomesByDateInterval(
          this.filterStartDate,
          this.filterEndDate
        );
        this.filteredList = response || [];
      } catch (error) {
        console.error("Erro ao filtrar por datas:", error);
        this.filteredList = [];
      }
    } else {
      alert('Por favor, informe as datas inicial e final para filtrar.');
    }
  }

  // Limpar todos os filtros avançados
  clearAdvancedFilters() {
    this.minValue = null;
    this.maxValue = null;
    this.filterStartDate = '';
    this.filterEndDate = '';
    this.filterType = null;
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
      const { data, categoria, valor, origemDoPagamento, observacoes } =
        this.createIncomeForm.value;
      const newIncome: Income = {
        data,
        categoria,
        valor,
        origemDoPagamento,
        observacoes,
      };

      this.incomeService
        .createIncome(newIncome)
        .then(() => {
          alert('Receita criada com sucesso!');
          this.refreshPage();
        })
        .catch((err) => alert('Erro ao criar receita: ' + err));
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
        const { data, categoria, valor, origemDoPagamento, observacoes } =
          this.editIncomeForm.value;
        const updatedIncome: Income = {
          data,
          categoria,
          valor,
          origemDoPagamento,
          observacoes,
        };
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
      observacoes: income.observacoes,
    });
  }
}

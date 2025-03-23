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
import { Expense } from '../../entity/expense';
import { ExpenseService } from './expense.service';
import { ChartUtils } from '../../utils/expense-chart-utils';

@Component({
  selector: 'app-expense',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: 'expense.component.html',
  styleUrls: ['expense.component.css'],
})
export class ExpenseComponent implements OnInit, OnDestroy {
  public chartUtils = ChartUtils;
  // Propriedades gerais
  title = 'expense';
  expenses: Expense[] = [];
  filteredExpenses: Expense[] = [];
  filteredBarData: Expense[] = [];
  filteredList: Expense[] = []; // Lista para filtro de valor e data

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
  editingExpenseId: string | null = null;
  modalType: 'create' | 'edit' | null = null;

  // Filtros de valor e data
  minValue: number | null = null;
  maxValue: number | null = null;
  filterStartDate: string = '';
  filterEndDate: string = '';

  // Propriedades para filtros avançados
  filterType: 'value' | 'date' | null = null;

  // Injeção de dependências
  private expenseService = inject(ExpenseService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  // Formulários reativos
  createExpenseForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    destinoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  editExpenseForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    destinoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  // ---------------------- Ciclo de Vida ----------------------
  async ngOnInit() {
    await this.loadExpenses();
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
  async loadExpenses() {
    const response = await this.expenseService.getExpenses();
    if (response) {
      this.expenses = response;
      this.filteredExpenses = [...this.expenses];
      this.filteredBarData = [...this.expenses];
    }
  }

  async loadPieChartData() {
    if (this.startDate && this.endDate) {
      this.pieChartData = await this.expenseService.getExpensePizzaChart(
        this.startDate,
        this.endDate
      );
      ChartUtils.drawPieChart(
        ChartUtils.getPieChartSlicesFromBackend(this.pieChartData),
        'expenseChart'
      );
      this.filteredExpenses = ChartUtils.getPieChartLegend(this.pieChartData);
    }
  }

  async loadBarChartData() {
    if (this.startMonth && this.endMonth) {
      this.barChartData = await this.expenseService.getExpenseBarChart(
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

  costTarget() {
    this.router.navigate(['/home/expense/cost-targets']);
  }


  refreshPage() {
    window.location.reload();
  }

  // ---------------------- Métodos de Filtro ----------------------
  async applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredExpenses = [...this.expenses];
      ChartUtils.drawPieChart(
        ChartUtils.getPieChartSlicesFromExpenses(this.filteredExpenses),
        'expenseChart'
      );
      ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
    } else {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);
      this.filteredExpenses = this.expenses.filter((expense) => {
        const expenseDate = new Date(expense.data);
        return expenseDate >= start && expenseDate <= end;
      });
      await this.loadPieChartData();
      await this.loadBarChartData();
    }
  }

  async clearFilter() {
    // 1. Zera as datas
    this.startDate = '';
    this.endDate = '';

    // 2. Restaura as despesas filtradas para TODAS as despesas
    this.filteredExpenses = [...this.expenses];

    // 3. Limpa os dados dos gráficos para "forçar" o redesenho
    this.pieChartData = null;
    this.barChartData = null;

    // 4. Desenha novamente o gráfico de pizza com todas as despesas
    ChartUtils.drawPieChart(
      ChartUtils.getPieChartSlicesFromExpenses(this.filteredExpenses),
      'expenseChart'
    );

    // 5. Desenha novamente o gráfico de barras com o array `filteredBarData`
    ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
  }

  async applyMonthFilter() {
    if (!this.startMonth || !this.endMonth) return;
    await this.loadBarChartData();
    this.filteredBarData = this.expenses.filter((expense) => {
      const expenseDate = new Date(expense.data);
      const expenseYearMonth = `${expenseDate.getFullYear()}-${String(
        expenseDate.getMonth() + 1
      ).padStart(2, '0')}`;
      return (
        expenseYearMonth >= this.startMonth && expenseYearMonth <= this.endMonth
      );
    });
  }

  clearMonthFilter() {
    this.startMonth = '';
    this.endMonth = '';
    this.barChartData = null;
    this.filteredBarData = [...this.expenses];
    ChartUtils.generateBarChart(this.filteredBarData, 'barChart');
  }

  applyValueDateFilter() {
    if (
      this.minValue ||
      this.maxValue ||
      this.filterStartDate ||
      this.filterEndDate
    ) {
      this.filteredList = this.expenses.filter((expense) => {
        const expenseDate = new Date(expense.data);
        return (
          this.isDateInFilterRange(expenseDate) &&
          this.isValueInFilterRange(expense.valor)
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
        const response = await this.expenseService.getExpensesByValueInterval(this.minValue, this.maxValue);
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
        const response = await this.expenseService.getExpensesByDateInterval(
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

  // ---------------------- Métodos de Manipulação de Despesas ----------------------
  openModal(type: 'create' | 'edit') {
    this.modalType = type;
  }

  closeModal() {
    this.modalType = null;
  }

  onSubmitCreate() {
    if (this.createExpenseForm.valid) {
      const { data, categoria, valor, destinoPagamento, observacoes } = this.createExpenseForm.value;
      const newExpense: Expense = {
        data,
        categoria,
        valor,
        destinoPagamento,
        observacoes,
      };

      this.expenseService
        .createExpense(newExpense)
        .then(() => {
          alert('Despesa criada com sucesso!');
          this.refreshPage();
        })
        .catch((err) => alert('Erro ao criar despesa: ' + err));
    }
  }

  async onSubmitRemove(id: string) {
    try {
      await this.expenseService.removeExpense(id);
      alert('Despesa removida com sucesso!');
      await this.loadExpenses();
      await this.loadPieChartData();
      await this.loadBarChartData();
    } catch (err) {
      alert('Erro ao remover despesa: ' + err);
    }
  }

  async onSubmitEdit(id: string) {
    if (this.editExpenseForm.valid) {
      try {
        const { data, categoria, valor, destinoPagamento, observacoes } = this.editExpenseForm.value;
        const updatedExpense: Expense = {
          data,
          categoria,
          valor,
          destinoPagamento,
          observacoes,
        };
        await this.expenseService.updateExpense(id, updatedExpense);
        alert('Despesa atualizada com sucesso!');
        this.refreshPage();
      } catch (err) {
        alert('Erro ao atualizar despesa: ' + err);
      }
    }
  }

  openEditModal(expense: Expense) {
    this.modalType = 'edit';
    this.editingExpenseId = expense.uuid!;
    this.editExpenseForm.setValue({
      data: expense.data,
      categoria: expense.categoria,
      valor: expense.valor,
      destinoPagamento: expense.destinoPagamento,
      observacoes: expense.observacoes,
    });
  }
}

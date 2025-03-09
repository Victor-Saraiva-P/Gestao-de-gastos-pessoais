import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormsModule, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HomeService } from '../home.service';
import { Router } from '@angular/router';
import { Expense } from '../../entity/expense';

@Component({
  selector: 'app-expense',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
  <section class="expense-container">
    <button class ="home-button"(click)="home()">home</button>
    <div class="left-section">
      <!-- Botões para abrir os modais -->
      <button (click)="openModal('create')">Criar Despesa</button>
      <button (click)="toggleEditMode()"> {{ isEditing ? 'Cancelar Edição' : 'Editar Despesa' }} </button>
      <button (click)="toggleRemoveMode()"> {{ isRemoving ? 'Cancelar Remoção' : 'Remover Despesa' }} </button>
    </div>

    <div class="main-content">

    <div class="chart-container">
        <h2>Gráfico de Despesas</h2>
        <div class="date-filter">
          <label>Data Inicial:</label>
          <input type="date" [(ngModel)]="startDate" />

          <label>Data Final:</label>
          <input type="date" [(ngModel)]="endDate" />

          <button (click)="applyDateFilter()">Aplicar Filtro</button>
          <button (click)="clearFilter()">Limpar Filtro</button>
        </div>

        <svg id="expenseChart" width="300" height="300"></svg>

        <!-- Legenda Sincronizada -->
        <ul class="legend">
          <li *ngFor="let expense of filteredExpenses; let i = index">
            <span class="legend-color" [style.background]="getColor(i)"></span>
            {{ expense.categoria }} - R$ {{ expense.valor | number:'1.2-2' }}
          </li>
        </ul>
      </div>


    <!-- Lista de Despesas -->
        <div class="expense-list">
          <h2>Lista de Despesas</h2>
          <ul>
            <li *ngFor="let expense of expenses">
              <div>
                <strong>Data:</strong> {{ expense.data | date:'dd/MM/yyyy' }} <br>
                <strong>Categoria:</strong> {{ expense.categoria }} <br>
                <strong>Valor:</strong> R$ {{ expense.valor | number:'1.2-2' }} <br>
                <strong>Destino do pagamento:</strong> {{ expense.destinoPagamento }} <br>
                <strong>Observações:</strong> {{ expense.observacoes || 'Nenhuma' }}
              </div>

              <!-- Mostrar botão de remoção apenas se o modo de remoção ou edição estiver ativo -->
              <button *ngIf="isEditing" (click)="openEditModal(expense)">✏️</button>
              <button *ngIf="isRemoving" (click)="onSubmitRemove(expense.uuid!)">❌</button>
            </li>
          </ul>
        </div>

      <!-- Modal Criar Despesa -->
      <div [ngClass]="{'modal': true, 'show-modal': modalType === 'create'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Criar Despesa</h2>
          <form [formGroup]="createExpenseForm" (ngSubmit)="onSubmitCreate()">
            <label for="data">Data</label>
            <input type="date" formControlName="data" placeholder="Digite a data"/>

            <label for="categoria">Categoria</label>
            <div>
              <select id="categoria" formControlName="categoria" required>
                <option value="" disabled selected>Selecione uma categoria</option>
                <option value="ALIMENTACAO">Alimentação</option>
                <option value="MORADIA">Moradia</option>
                <option value="TRANSPORTE">Transporte</option>
                <option value="LAZER">Lazer</option>
              </select>
            </div>

            <label for="valor">Valor</label>
            <input type="text" formControlName="valor"/>

            <label for="destinoPagamento">Destino</label>
            <input type="text" formControlName="destinoPagamento"/>

            <label>Observação</label>
            <input type="text" formControlName="observacoes"/>

            <button type="submit" [disabled]="createExpenseForm.invalid">Criar Despesa</button>
          </form>
        </div>
      </div>

      <!-- Modal Editar Despesa -->
      <div [ngClass]="{'modal': true, 'show-modal': modalType === 'edit'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Editar Despesa</h2>
         <form [formGroup]="editExpenseForm" (ngSubmit)="onSubmitEdit(editingExpenseId!)">
            <label>Data</label>
            <input type="date" formControlName="data"/>

            <label>Categoria</label>
            <div>
              <select id="categoria" formControlName="categoria" required>
                <option value="" disabled selected>Selecione uma categoria</option>
                <option value="ALIMENTACAO">Alimentação</option>
                <option value="MORADIA">Moradia</option>
                <option value="TRANSPORTE">Transporte</option>
                <option value="LAZER">Lazer</option>
              </select>
            </div>

            <label>Valor</label>
            <input type="number" formControlName="valor"/>

            <label>Destino</label>
            <input type="text" formControlName="destinoPagamento"/>

            <label>Observação</label>
            <input type="text" formControlName="observacoes"/>

            <button type="submit" [disabled]="editExpenseForm.invalid">Salvar Alterações</button>
          </form>
        </div>
      </div>
    </div>
  </section>
  `,
  styleUrls: ['expense.component.css']
})
export class ExpenseComponent {
  title = 'expense'

  isRemoving = false;
  isEditing = false;
  editingExpenseId: string | null = null;
  modalType: 'create' | 'edit' | null = null;

  expenses: Expense[] = []; // Lista de despesas
  filteredExpenses: Expense[] = []; // Despesas filtradas
  pieChartStyles: any[] = []; // Lista de estilos CSS para o gráfico

  startDate: string = '';  // Data inicial
  endDate: string = '';    // Data final

  private homeService = inject(HomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

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

  async ngOnInit() {
    await this.carregarDespesas();
  }

  refreshPage() {
    window.location.reload();
  }

  toggleRemoveMode() {
    this.isRemoving = !this.isRemoving;
    if (this.isEditing) this.isRemoving = false;
  }


  toggleEditMode() {
    this.isEditing = !this.isEditing;
    if (this.isRemoving) {
      this.isEditing = false; 
    }
  }

  async carregarDespesas() {
    const response = await this.homeService.getExpenses();
    if (response) {
      this.expenses = response;
      this.filteredExpenses = [...this.expenses]; // Inicialmente, exibe todas as despesas
      this.gerarGraficoPizza();
    }
  }

  applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredExpenses = [...this.expenses]; // Exibe todas as despesas se não houver filtro
    } else {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);

      this.filteredExpenses = this.expenses.filter(expense => {
        const expenseDate = new Date(expense.data);
        return expenseDate >= start && expenseDate <= end;
      });
    }
    this.gerarGraficoPizza();
  }
  
  clearFilter() {
    this.startDate = '';
    this.endDate = '';
    this.filteredExpenses = [...this.expenses]; // Restaurar todas as despesas
    this.gerarGraficoPizza();
  }
  
  

  gerarGraficoPizza() {
    const svg = document.getElementById('expenseChart') as unknown as SVGSVGElement;
    if (!svg) return;

    svg.innerHTML = ''; // Limpa o gráfico antes de redesenhá-lo

    if (this.filteredExpenses.length === 0) return;

    const total = this.filteredExpenses.reduce((sum, expense) => sum + expense.valor, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    this.filteredExpenses.forEach((expense, index) => {
      const sliceAngle = (expense.valor / total) * 2 * Math.PI;
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

  getColor(index: number): string {
    const colors = ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"];
    return colors[index % colors.length];
  }

  // 🔹 Agrupar despesas por categoria
  agruparDespesasPorCategoria(): { [key: string]: number } {
    const totais: { [key: string]: number } = {};

    this.expenses.forEach(expense => {
      const categoria = expense.categoria;
      const valor = Number(expense.valor);

      if (totais[categoria]) {
        totais[categoria] += valor;
      } else {
        totais[categoria] = valor;
      }
    });

    return totais;
  }

  

  // Função para abrir o modal com base no tipo
  openModal(type: 'create' | 'edit') {
    this.modalType = type;
  }

  // Fechar o modal
  closeModal() {
    this.modalType = null;
  }

  // Envio para criar a despesa
  onSubmitCreate() {
    if (this.createExpenseForm.valid) {
      const { data, categoria, valor, destinoPagamento, observacoes } = this.createExpenseForm.value;
      const newExpense: Expense = { data, categoria, valor, destinoPagamento, observacoes };
      this.homeService.createExpense(newExpense)
        .then(() => {
          alert('Despesa criada com sucesso!');
        })
        .catch(err => alert('Erro ao criar despesa: ' + err));
        this.refreshPage();
    }
  }

  // Envio para editar a despesa
  onSubmitEdit(id: string) {
    if (this.editExpenseForm.valid) {
      const {data, categoria, valor, destinoPagamento, observacoes } = this.editExpenseForm.value;
      const editExpense: Expense = { data, categoria, valor, destinoPagamento, observacoes };

      this.homeService.updateExpense(id, editExpense)
      .then(() => {
        alert('Despesa atualizada com sucesso!');
      })
      .catch(err => alert('Erro ao atualizar despesa: ' + err));
    }
    this.refreshPage();
  }
  
  openEditModal(expense: Expense) {
      this.modalType = 'edit';
      this.editingExpenseId = expense.uuid!;

      this.editExpenseForm.setValue({
        data: expense.data,
        categoria: expense.categoria,
        valor: expense.valor,
        destinoPagamento: expense.destinoPagamento,
        observacoes: expense.observacoes
      });
    }
  

  // Envio para remover a despesa
  onSubmitRemove(id: string) {
    this.homeService.removeExpense(id).then(() => {
      alert('Despesa removida com sucesso!')
    })
    .catch(err => alert('Error removing expense: ' + err));
    this.refreshPage(); 
}

  home() {
    this.router.navigate(['/home']);
  }
}


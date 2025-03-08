import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HomeService } from '../home.service';
import { Router } from '@angular/router';
import { Expense } from '../../entity/expense';

@Component({
  selector: 'app-expense',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
  <section class="expense-container">
    <button class ="home-button"(click)="home()">home</button>
    <div class="left-section">
      <!-- Botões para abrir os modais -->
      <button (click)="openModal('create')">Criar Despesa</button>
      <button (click)="openModal('edit')">Editar Despesa</button> 
      <button (click)="openModal('remove')">Remover Despesa</button>
    </div>

    <div class="main-content">
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
          <form [formGroup]="editExpenseForm" (ngSubmit)="onSubmitEdit()">
            <label>ID</label>
            <input type="text" formControlName="id"/>

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

      <!-- Modal Remover Despesa -->
      <div [ngClass]="{'modal': true, 'show-modal': modalType === 'remove'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Remover Despesa</h2>
          <form [formGroup]="removeExpenseForm" (ngSubmit)="onSubmitRemove()">
            <label>Id da Despesa</label>
            <input type="text" formControlName="id"/>

            <button type="submit" [disabled]="removeExpenseForm.invalid">Remover</button>
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

  modalType: 'create' | 'edit' | 'remove' | null = null;

  private homeService = inject(HomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  createExpenseForm = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    destinoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  editExpenseForm = this.fb.group({
    id: ['', Validators.required],
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    destinoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  removeExpenseForm = this.fb.group({
      id: ['', Validators.required],
  });
  

  // Função para abrir o modal com base no tipo
  openModal(type: 'create' | 'edit' | 'remove') {
    this.modalType = type;
  }

  // Fechar o modal
  closeModal() {
    this.modalType = null;
  }

  // Envio para criar a despesa
  onSubmitCreate() {
    if (this.createExpenseForm.valid) {
      const { data, categoria, valor, destinoPagamento, observacoes }: any = this.createExpenseForm.value;
      const newExpense: Expense = { data, categoria, valor, destinoPagamento, observacoes };
      this.homeService.createExpense(newExpense)
        .then(() => {
          alert('Despesa criada com sucesso!');
          this.router.navigate(['/home/expenses']);
        })
        .catch(err => alert('Erro ao criar despesa: ' + err));
      this.closeModal();
    }
  }

  // Envio para editar a despesa
  onSubmitEdit() {
    if (this.editExpenseForm.valid) {
      const {data, categoria, valor, destinoPagamento, observacoes }: any = this.editExpenseForm.value;
      const {id}: any = this.editExpenseForm.value;
      const editExpense: Expense = { data, categoria, valor, destinoPagamento, observacoes };

      this.homeService.updateExpense(id, editExpense)
      .then(() => {
        alert('Despesa atualizada com sucesso!');
        this.router.navigate(['/home/expenses']);
      })
      .catch(err => alert('Erro ao atualizar despesa: ' + err));
    }
      this.closeModal();
  }
  

  // Envio para remover a despesa
  onSubmitRemove() {
    if (this.removeExpenseForm.valid) {
      const {id}: any = this.removeExpenseForm.value;
      this.homeService.removeExpense(id)
        .then(() => {
          alert('Despesa removida com sucesso!')
          this.router.navigate(['/home/expenses']);
        })
        .catch(err => alert('Erro ao remover despesa: ' + err));
      this.closeModal();
    }
  }

  home() {
    this.router.navigate(['/home']);
  }
}


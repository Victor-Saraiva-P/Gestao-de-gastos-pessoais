import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HomeService } from '../home.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Expense } from '../../entity/expense';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div>
        <h2>{{ isEditing ? 'Editar Despesa' : 'Criar Despesa' }}</h2>

        <form [formGroup]="creatExpenseForm" (ngSubmit)="onSubmit()">
          <!-- Data -->
          <label for="data">Data</label>
          <input type="date" formControlName="data" placeholder="Digite a data"/>
        
          <!-- Categoria -->
          <label for="categoria">Categoria</label>
          <input type="text" formControlName="categoria" placeholder="Digite a categoria"/>

          <!-- Valor -->
          <label for="valor">Valor</label>
          <input type="text" formControlName="valor" placeholder="Digite o valor"/>

          <!-- Origem -->
          <label for="destinoPagamento">Origem</label>
          <input type="text" formControlName="destinoPagamento" placeholder="Digite a origem"/>

          <!-- Observação --> 
          <label for="observacoes">Observação</label>
          <input type="text" formControlName="observacoes" placeholder="Digite a observação"/>

          <!-- Botão de Submit -->
          <button type="submit" [disabled]="creatExpenseForm.invalid">
            {{ isEditing ? 'Salvar Alterações' : 'Criar despesa' }}
          </button>
        </form>
      </div>

      <div>
        <h2>Remove Expense</h2>
        <form [formGroup]="removeExpenseForm" (ngSubmit)="onSubmitRemove()">
          <!-- Id -->
          <label for="id">Id</label>
          <input type="text" formControlName="id" placeholder="Digite o id"/>

          <!-- Botão de Submit -->
          <button type="submit" [disabled]="removeExpenseForm.invalid">Remover despesa</button>
        </form>
      </div>

      <div class="right-section">
          <button class="logout" (click)="home()">Home</button>
      </div>
    </section>
  `,
  styleUrls: ['expense.component.css']
})
export class ExpenseComponent implements OnInit {
  title = 'expense';
  isEditing = false;
  expenseId: string | null = null;

  private homeService = inject(HomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);

  creatExpenseForm: FormGroup = this.fb.group({
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    destinoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required],
  });

  removeExpenseForm: FormGroup = this.fb.group({
    id: ['', Validators.required],
  });

  ngOnInit(): void {
    // Verifica se há um ID na URL para detectar se estamos editando
    this.expenseId = this.route.snapshot.paramMap.get('id');

    if (this.expenseId) {
      this.isEditing = true;
      this.homeService.getExpenseById(this.expenseId)
        .then((expense: Expense | null) => {
          if (expense) {
            this.creatExpenseForm.patchValue(expense);
          } else {
            alert('Despesa não encontrada.');
            this.router.navigate(['/home']); // Redireciona para home se não encontrar a despesa
          }
        })
        .catch(err => {
          alert('Erro ao carregar a despesa: ' + err);
          this.router.navigate(['/home']); // Redireciona caso ocorra erro na requisição
        });
    }
  }

  onSubmit() {
    if (this.creatExpenseForm.valid) {
      const expenseData = this.creatExpenseForm.value;

      if (this.isEditing && this.expenseId) {
        // Atualizar despesa existente
        this.homeService.updateExpense(this.expenseId, expenseData)
          .then(() => {
            alert('Despesa atualizada com sucesso!');
            this.router.navigate(['/home']);
          })
          .catch(err => alert('Erro ao atualizar despesa: ' + err));
      } else {
        // Criar nova despesa
        this.homeService.createExpense(expenseData)
          .then(() => {
            alert('Despesa criada com sucesso!');
            this.router.navigate(['/home']);
          })
          .catch(err => alert('Erro ao criar despesa: ' + err));
      }
    }
  }

  onSubmitRemove() {
    if (this.removeExpenseForm.valid) {
      const {id} = this.removeExpenseForm.value;

      this.homeService.removeExpense(id).catch(err => alert('Error removing income: ' + err));
      this.router.navigate(['/home']);
    }
  }
  
    home(){
      this.router.navigate(['/home']);
    }
}

import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HomeService } from '../home.service';
import { Router } from '@angular/router';
import { Expense } from '../../entity/expense';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div>
        <h2>Create Expenses</h2>
        <form [formGroup]="creatExpenseForm" (ngSubmit)="onSubmit()">
          <!-- Data -->
          <label for="data">Data</label>
          <input type="data" formControlName="data" placeholder="Digite a data"/>
        
          <!-- Categoria -->
          <label for="despesaCategoria">Categoria</label>
          <input type="despesaCategoria" formControlName="despesaCategoria" placeholder="Digite a categoria"/>

          <!-- Valor -->
          <label for="valor">Valor</label>
          <input type="valor" formControlName="valor" placeholder="Digite o valor"/>

          <!-- Origem -->
          <label for="destinoPagamento">Origem</label>
          <input type="destinoPagamento" formControlName="destinoPagamento" placeholder="Digite a origem"/>

          <!-- Observação --> 
          <label for="observacoes">Observação</label>
          <input type="observacoes" formControlName="observacoes" placeholder="Digite a observação"/>

          <!-- Botão de Submit -->
          <button type="submit" [disabled]="creatExpenseForm.invalid">Criar receita</button>
        </form>
      </div>

      <div class="right-section">
          <button class="logout" (click)="home()">Home</button>
      </div>
</section>
  `,
  styleUrls: ['expense.component.css']
  
})
export class ExpenseComponent {
  title = 'expense';
    
    private HomeService = inject(HomeService);
    private router = inject(Router);
    private fb = inject(FormBuilder);
  
    creatExpenseForm: FormGroup = this.fb.group({
        data: ['', Validators.required],
        despesaCategoria: ['', Validators.required],
        valor: ['', Validators.required],
        destinoPagamento: ['', Validators.required],
        observacoes: ['', [Validators.required]],
    });
  
    onSubmit() {
        if (this.creatExpenseForm.valid) {
          const { data, despesaCategoria, valor, destinoPagamento, observacoes } = this.creatExpenseForm.value;
          const newExpense: Expense= { data, despesaCategoria, valor, destinoPagamento, observacoes };
    
          this.HomeService.createExpense(newExpense).catch(err => alert('Error registering expense: ' + err));
        }
    }
  
    home(){
      this.router.navigate(['/home']);
    }
}

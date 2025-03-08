import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Income } from '../../entity/income';
import { HomeService } from '../home.service';
import { OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div>
        <h2>Create Income</h2>
        <form [formGroup]="creatIncomeForm" (ngSubmit)="onSubmit()">
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
          <label for="origemDoPagamento">Origem</label>
          <input type="text" formControlName="origemDoPagamento" placeholder="Digite a origem"/>

          <!-- Observação --> 
          <label for="">Observação</label>
          <input type="text" formControlName="observacoes" placeholder="Digite a observação"/>

          <!-- Botão de Submit -->
          <button type="submit" [disabled]="creatIncomeForm.invalid">Criar receita</button>
        </form>
      </div>

      <div>
        <h2>Remove Income</h2>
        <form [formGroup]="removeIncomeForm" (ngSubmit)="onSubmitRemove()">
          <!-- Id -->
          <label for="id">Id</label>
          <input type="text" formControlName="id" placeholder="Digite o id"/>

          <!-- Botão de Submit -->
          <button type="submit" [disabled]="removeIncomeForm.invalid">Remover receita</button>
        </form>
      </div>
    
<div>
  <h2>Edit Income</h2>
  <form [formGroup]="editIncomeForm" (ngSubmit)="onSubmitEdit()">
    <!-- ID -->
    <label for="id">Id</label>
    <input type="text" formControlName="id" placeholder="Digite o id" required/>

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
    <label for="origemDoPagamento">Origem</label>
    <input type="text" formControlName="origemDoPagamento" placeholder="Digite a origem"/>

    <!-- Observação -->
    <label for="observacoes">Observação</label>
    <input type="text" formControlName="observacoes" placeholder="Digite a observação"/>

    <button type="submit" [disabled]="editIncomeForm.invalid">Editar receita</button>
  </form>
</div>

<div class="income-list">
  <h2>Receitas Cadastradas</h2>
  
  <table *ngIf="incomes.length > 0">
    <thead>
      <tr>
        <th>Data</th>
        <th>Categoria</th>
        <th>Valor</th>
        <th>Origem</th>
        <th>Observações</th>
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let income of incomes">
        <td>{{ income.data | date:'dd/MM/yyyy' }}</td>
        <td>{{ income.categoria }}</td>
        <td>{{ income.valor | currency:'BRL' }}</td>
        <td>{{ income.origemDoPagamento }}</td>
        <td>{{ income.observacoes }}</td>
      </tr>
    </tbody>
  </table>

  <p *ngIf="incomes.length === 0">Nenhuma receita cadastrada.</p>
</div>
<div class="right-section">
          <button class="logout" (click)="home()">Home</button>
      </div>
</section>
  `,
  styleUrls: ['income.component.css']
  
})
export class IncomeComponent implements OnInit {
  title = 'income';
  
  private homeService = inject(HomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  creatIncomeForm: FormGroup = this.fb.group({
      data: ['', Validators.required],
      categoria: ['', Validators.required],
      valor: ['', Validators.required],
      origemDoPagamento: ['', Validators.required],
      observacoes: ['', [Validators.required]],
  });

  removeIncomeForm: FormGroup = this.fb.group({
    id: ['', Validators.required],
  });

  editIncomeForm: FormGroup = this.fb.group({
    id: ['', Validators.required],
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required]
  });

    async onSubmit() {
      if (this.creatIncomeForm.valid) {
        try {
          const { data, categoria, valor, origemDoPagamento, observacoes } = this.creatIncomeForm.value;
          const newIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };
  
          await this.homeService.createIncome(newIncome);
          await this.ngOnInit(); 
          this.creatIncomeForm.reset(); 
          this.router.navigate(['/home']);
        } catch (err) {
          alert('Error registering income: ' + err);
        }
      }
    }
    
    async onSubmitRemove() {
      if (this.removeIncomeForm.valid) {
        try {
          const { id } = this.removeIncomeForm.value;
          await this.homeService.removeIncome(id);
          await this.ngOnInit(); 
          this.removeIncomeForm.reset(); 
          this.router.navigate(['/home']);
        } catch (err) {
          alert('Error removing income: ' + err);
        }
      }
    }
    
    async onSubmitEdit() {
      if (this.editIncomeForm.valid) {
        try {
          const { id, data, categoria, valor, origemDoPagamento, observacoes } = this.editIncomeForm.value;
          const updatedIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };
          
          await this.homeService.editIncome(id, updatedIncome);
          await this.ngOnInit();
          this.editIncomeForm.reset(); 
          this.router.navigate(['/home']);
        } catch (err) {
          alert('Error updating income: ' + err);
        }
      }
    }

  incomes: Income[] = [];
  async ngOnInit() {
    try {
      const incomes = await this.homeService.getIncomes();
      if (incomes) {
        this.incomes = incomes;
      }
    } catch (error) {
      console.error('Erro ao carregar receitas:', error);
    }
  }

  home(){
    this.router.navigate(['/home']);
  }
}
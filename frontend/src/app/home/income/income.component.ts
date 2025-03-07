import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Income } from '../../entity/income';
import { HomeService } from '../home.service';

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

      <div class="right-section">
          <button class="logout" (click)="home()">Home</button>
      </div>
</section>
  `,
  styleUrls: ['income.component.css']
  
})
export class IncomeComponent {
  title = 'income';
  
  private HomeService = inject(HomeService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  creatIncomeForm: FormGroup = this.fb.group({
      data: ['', Validators.required],
      categoria: ['', Validators.required],
      valor: ['', Validators.required],
      origemDoPagamento: ['', Validators.required],
      observacoes: ['', [Validators.required]],
  });

  onSubmit() {
      if (this.creatIncomeForm.valid) {
        const { data, categoria, valor, origemDoPagamento, observacoes } = this.creatIncomeForm.value;
        const newIncome: Income= { data, categoria, valor, origemDoPagamento, observacoes };
  
        this.HomeService.createIncome(newIncome).catch(err => alert('Error registering income: ' + err));
        this.router.navigate(['/home']);
      }
  }

  home(){
    this.router.navigate(['/home']);
  }
}
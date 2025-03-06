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
          <input type="data" formControlName="data" placeholder="Digite a data"/>
        
          <!-- Categoria -->
          <label for="categoria">Categoria</label>
          <input type="categoria" formControlName="categoria" placeholder="Digite a categoria"/>

          <!-- Valor -->
          <label for="valor">Valor</label>
          <input type="valor" formControlName="valor" placeholder="Digite o valor"/>

          <!-- Origem -->
          <label for="origem_do_pagamento">Origem</label>
          <input type="origem_do_pagamento" formControlName="origem_do_pagamento" placeholder="Digite a origem"/>

          <!-- Observação --> 
          <label for="">Observação</label>
          <input type="observacoes" formControlName="observacoes" placeholder="Digite a observação"/>

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
      origem_do_pagamento: ['', Validators.required],
      observacoes: ['', [Validators.required]],
  });

  onSubmit() {
      if (this.creatIncomeForm.valid) {
        const { data, categoria, valor, origem_do_pagamento, observacoes } = this.creatIncomeForm.value;
        const newIncome: Income= { data, categoria, valor, origem_do_pagamento, observacoes };
  
        this.HomeService.createIncome(newIncome).catch(err => alert('Error registering income: ' + err));
      }
  }

  home(){
    this.router.navigate(['/home']);
  }
}
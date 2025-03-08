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
    <section class="expense-container">
      <button class ="home-button"(click)="home()">home</button>
      <div class="left-section">
        <!-- Botões para abrir os modais -->
        <button (click)="openModal('create')">Criar Receita</button>
        <button (click)="openModal('edit')">Editar Receita</button> 
        <button (click)="openModal('remove')">Remover Despesa</button>
      </div>

      <div class="main-content">
      <!-- Lista de Receitas -->
        <div class="income-list">
          <h2>Lista de Receitas</h2>
          <ul>
            <li *ngFor="let income of incomes">
              <div>
                <strong>Data:</strong> {{ income.data | date:'dd/MM/yyyy' }} <br>
                <strong>Categoria:</strong> {{ income.categoria }} <br>
                <strong>Valor:</strong> R$ {{ income.valor | number:'1.2-2' }} <br>
                <strong>Origem do Pagamento:</strong> {{ income.origemDoPagamento }} <br>
                <strong>Observações:</strong> {{ income.observacoes || 'Nenhuma' }}
              </div>
            </li>
          </ul>
        </div>

      <!-- Modal Criar Receita -->
        <div [ngClass]="{'modal': true, 'show-modal': modalType === 'create'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Criar Receitas</h2>
          <form [formGroup]="createIncomeForm" (ngSubmit)="onSubmitCreate()">
            <label for="data">Data</label>
            <input type="date" formControlName="data" placeholder="Digite a data"/>

            <label for="categoria">Categoria</label>
            <div>
              <select id="categoria" formControlName="categoria" required>
                <option value="" disabled selected>Selecione uma categoria</option>
                <option value="SALARIO">Salario</option>
                <option value="RENDIMENTO_DE_INVESTIMENTO">Rendimento de investimentos</option>
                <option value="COMISSOES">Comissões</option>
                <option value="BONUS">Bonus</option>
                <option value="BOLSA_DE_ESTUDOS">Bolsa de estudos</option>
              </select>
            </div>

            <label for="valor">Valor</label>
            <input type="text" formControlName="valor"/>

            <label for="origemDoPagamento">Origem</label>
            <input type="text" formControlName="origemDoPagamento"/>

            <label>Observação</label>
            <input type="text" formControlName="observacoes"/>

            <button type="submit" [disabled]="createIncomeForm.invalid">Criar Receita</button>
          </form>
        </div>
        </div>

        <!-- Modal Editar Despesa -->
      <div [ngClass]="{'modal': true, 'show-modal': modalType === 'edit'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Editar Despesa</h2>
          <form [formGroup]="editIncomeForm" (ngSubmit)="onSubmitEdit()">
            <label>ID</label>
            <input type="text" formControlName="id"/>

            <label>Data</label>
            <input type="date" formControlName="data"/>

            <label>Categoria</label>
            <div>
              <select id="categoria" formControlName="categoria" required>
                <option value="" disabled selected>Selecione uma categoria</option>
                <option value="SALARIO">Salario</option>
                <option value="RENDIMENTO_DE_INVESTIMENTO">Rendimento de investimentos</option>
                <option value="COMISSOES">Comissões</option>
                <option value="BONUS">Bonus</option>
                <option value="BOLSA_DE_ESTUDOS">Bolsa de estudos</option>
              </select>
            </div>

            <label>Valor</label>
            <input type="number" formControlName="valor"/>

            <label>Origem</label>
            <input type="text" formControlName="origemDoPagamento"/>

            <label>Observação</label>
            <input type="text" formControlName="observacoes"/>

            <button type="submit" [disabled]="editIncomeForm.invalid">Salvar Alterações</button>
          </form>
        </div>
      </div>

      <!-- Modal Remover Receita -->
       <div [ngClass]="{'modal': true, 'show-modal': modalType === 'remove'}">
        <div class="modal-content">
          <button class="close" (click)="closeModal()">&times;</button>
          <h2>Remover Receitas</h2>
          <form [formGroup]="removeIncomeForm" (ngSubmit)="onSubmitRemove()">
              <label>Id da Receita</label>
              <input type="text" formControlName="id"/>

            <button type="submit" [disabled]="removeIncomeForm.invalid">Remover</button>
          </form>
        </div>
       </div>
    </div>
  </section>
  `,
  styleUrls: ['income.component.css']
  
})
export class IncomeComponent implements OnInit{
  title = 'income';

  incomes: Income[] = [];
  modalType: 'create' | 'edit' | 'remove' | null = null;

  private homeService = inject(HomeService);
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
    id: ['', Validators.required],
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required]
  });
  
  removeIncomeForm: FormGroup = this.fb.group({
    id: ['', Validators.required],
  });

  ngOnInit() {
    this.loadIncomes(); // Chama ao iniciar
  }

  async loadIncomes() {
    const response = await this.homeService.getIncomes();
    if (response) {
      this.incomes = response; // Atualiza a lista
    }
  }

  refreshPage() {
    window.location.reload();
  }


  openModal(type: 'create' | 'edit' | 'remove') {
    this.modalType = type;
  }

  // Fechar o modal
  closeModal() {
    this.modalType = null;
  }


  onSubmitCreate() {
    if (this.createIncomeForm.valid) {
      const { data, categoria, valor, origemDoPagamento, observacoes } = this.createIncomeForm.value;
      const newIncome: Income= { data, categoria, valor, origemDoPagamento, observacoes };

      this.homeService.createIncome(newIncome).catch(err => alert('Error registering income: ' + err));
      this.router.navigate(['/home']).then(() => {
        alert("Receita criada com sucesso!")
        this.router.navigate(['/home/income']);
      })
      .catch(err => alert('Erro ao criar receita: ' + err));
      this.refreshPage();
    }
  }
    
  onSubmitRemove() {
    if (this.removeIncomeForm.valid) {
      const {id} = this.removeIncomeForm.value;
      this.homeService.removeIncome(id).then(() => {
        alert('Receita removida com sucesso!')
        this.router.navigate(['/home/income']);
      })
      .catch(err => alert('Error removing income: ' + err));
      this.router.navigate(['/home/income']);
      this.refreshPage();
    }
  } 
    
  async onSubmitEdit() {
    if (this.editIncomeForm.valid) {
      try {
        const { id, data, categoria, valor, origemDoPagamento, observacoes } = this.editIncomeForm.value;
        const updatedIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };
        
        await this.homeService.editIncome(id, updatedIncome);
        this.router.navigate(['/home/income']);
        this.refreshPage();

      } catch (err) {
        alert('Error updating income: ' + err);
      }
    }
  }

  home(){
    this.router.navigate(['/home']);
  }
}
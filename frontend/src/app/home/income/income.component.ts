import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormsModule, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Income } from '../../entity/income';
import { HomeService } from '../home.service';
import { OnInit } from '@angular/core';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  template: `
    <section class="expense-container">
      <button class ="home-button"(click)="home()">home</button>
      <div class="left-section">
        <!-- Botões para abrir os modais -->
        <button (click)="openModal('create')">Criar Receita</button>
        <button (click)="toggleEditMode()"> {{ isEditing ? 'Cancelar Edição' : 'Editar Receita' }} </button>
        <button (click)="toggleRemoveMode()"> {{ isRemoving ? 'Cancelar Remoção' : 'Remover Receita' }} </button>
      </div>
       
      <div class="main-content">
        <div class="chart-container">
          <h2>Gráfico de Receitas</h2>
          <div class="date-filter">
        <label>Data Inicial:</label>
        <input type="date" [(ngModel)]="startDate" />

        <label>Data Final:</label>
        <input type="date" [(ngModel)]="endDate" />

        <!-- 🔹 BOTÃO PARA APLICAR O FILTRO -->
        <button (click)="applyDateFilter()">Aplicar Filtro</button>
        <button (click)="clearFilter()">Limpar Filtro</button>
      </div>

          <svg id="incomeChart" width="300" height="300"></svg>
           <!-- 🔹 Adicionando a legenda -->
            <ul class="legend">
              <li *ngFor="let income of filteredIncomes; let i = index">
                <span class="legend-color" [style.background]="getColor(i)"></span>
                {{ income.categoria }} - R$ {{ income.valor | number:'1.2-2' }}
              </li>
            </ul>
        </div>


      <!-- Grafico de Barras --->
      <div class = "chart-container">
       <h2>Receitas por Mês</h2>
          <div class = "data-filter">
            <label>Mês Inicial:</label>
            <input type="month" [(ngModel)]="startMonth" />
            <label>Mês Inicial:</label>
            <input type="month" [(ngModel)]="endMonth" />

            <button (click)="applyMonthFilter()">Aplicar Filtro</button>
            <button (click)="clearMonthFilter()">Aplicar Filtro</button>
          </div>
          <svg id="barChart" width="650" height="400" viewBox="0 0 650 400"></svg>
      </div>
        

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

              <!-- Mostrar botão de remoção apenas se o modo de remoção ou edição estiver ativo -->
              <button *ngIf="isEditing" (click)="openEditModal(income)">✏️</button>
              <button *ngIf="isRemoving" (click)="onSubmitRemove(income.uuid!)">❌</button>
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
          <form [formGroup]="editIncomeForm" (ngSubmit)="onSubmitEdit(editingIncomeId!)">

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
    </div>
  </section>
  `,
  styleUrls: ['income.component.css']
  
})
export class IncomeComponent implements OnInit{
  title = 'income';
  startDate: string = '';  // Data inicial
  endDate: string = '';    // Data final
  startMonth: string = ''; 
  endMonth: string = '';
  filteredIncomes: Income[] = [];  // Lista de receitas filtradas
  filteredBarData: Income[] = [];

  incomes: Income[] = [];
  isRemoving = false;
  isEditing = false;
  editingIncomeId: string | null = null; 
  modalType: 'create' | 'edit' | null = null;

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
    data: ['', Validators.required],
    categoria: ['', Validators.required],
    valor: ['', Validators.required],
    origemDoPagamento: ['', Validators.required],
    observacoes: ['', Validators.required]
  });

  async ngOnInit() {
    await this.loadIncomes();  // Carrega as receitas do backend
  }
  
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
  getColor(index: number): string {
    const colors = ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", 
    "#FF9F40", "#D4AF37", "#8A2BE2", "#20B2AA", "#DC143C",
    "#FFD700", "#4682B4", "#32CD32", "#FF4500", "#6A5ACD",
    "#008080", "#8B0000", "#556B2F", "#D2691E", "#1E90FF"];
    return colors[index % colors.length]; // Retorna uma cor com base no índice
  }
  

  async loadIncomes() {
    const response = await this.homeService.getIncomes();
    if (response) {
      this.incomes = response;
      this.filteredIncomes = [...this.incomes]; // Exibir todas as receitas no início
      this.filteredBarData = [...this.incomes];
      this.generateChart();
      this.generateBarChart();
    }
  }

  applyMonthFilter() {
    if (!this.startMonth || !this.endMonth) return;
    const startDate = new Date(`${this.startMonth}-01`);
    const endDate = new Date(`${this.endMonth}-31`);

    this.filteredBarData = this.incomes.filter(income => {
      const incomeDate = new Date(income.data);
      return incomeDate >= startDate && incomeDate <= endDate;
    });

    this.generateBarChart();
  }

  clearMonthFilter() {
    this.startMonth = '';
    this.endMonth = '';
    this.filteredBarData = [...this.incomes];
    this.generateBarChart();
  }

  getMonthNumber(month: string): number {
    const monthsMap: { [key: string]: number } = {
      'jan.': 1, 'fev.': 2, 'mar.': 3, 'abr.': 4, 'mai.': 5, 'jun.': 6,
      'jul.': 7, 'ago.': 8, 'set.': 9, 'out.': 10, 'nov.': 11, 'dez.': 12
    };
    return monthsMap[month] || 0;
  }
  

  generateBarChart() {
    const svg = document.getElementById('barChart') as unknown as SVGSVGElement;
    if (!svg) return;
    svg.innerHTML = '';
  
    const totals: { [key: string]: number } = {};
    this.filteredBarData.forEach(income => {
      const date = new Date(income.data);
      const monthYear = `${date.toLocaleString('default', { month: 'short' })} ${date.getFullYear()}`;
      if (!totals[monthYear]) totals[monthYear] = 0;
      totals[monthYear] += income.valor;
    });
  
    const months = Object.keys(totals)
    .sort((a, b) => {
      const [monthA, yearA] = a.split(' ');
      const [monthB, yearB] = b.split(' ');

      const dateA = new Date(`${yearA}-${this.getMonthNumber(monthA)}-01`);
      const dateB = new Date(`${yearB}-${this.getMonthNumber(monthB)}-01`);

      return dateA.getTime() - dateB.getTime(); // Ordena corretamente os meses
    });

   
    const values = months.map(m => totals[m]);
  
    const maxValor = Math.max(...values);
    const barWidth = 25;
    const barSpacing = 50;
    const startX = 50;
    const startY = 350;
    const chartHeight = 200;
  
    months.forEach((month, index) => {
      const barHeight = (totals[month] / maxValor) * chartHeight;
      const x = startX + index * barSpacing;
      const y = startY - barHeight;
  
      // Criando a barra
      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);
  
      // Adicionando o rótulo do mês no eixo X
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 15).toString()); // Ajuste a posição vertical do texto
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "12px");
      text.textContent = month;
  
      svg.appendChild(text);
    });
  
    // Criando linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

 


  applyDateFilter() {
    if (!this.startDate || !this.endDate) {
      this.filteredIncomes = [...this.incomes]; // Usa todas as receitas se não houver filtro
    } else {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);
      
      this.filteredIncomes = this.incomes.filter(income => {
        const incomeDate = new Date(income.data);
        return incomeDate >= start && incomeDate <= end;
      });
    }
    this.generateChart();
  }
  
  clearFilter() {
    this.startDate = '';
    this.endDate = '';
    this.filteredIncomes = [...this.incomes]; // Restaurar todas as receitas
    this.generateChart(); // Atualizar o gráfico para exibir todos os dados
  }
  

  generateChart() {
    const svg = document.getElementById('incomeChart') as unknown as SVGSVGElement;
    if (!svg) return;

    svg.innerHTML = ''; // Limpa o gráfico antes de redesenhá-lo

    if (this.filteredIncomes.length === 0) return;

    const total = this.filteredIncomes.reduce((sum, income) => sum + income.valor, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    this.filteredIncomes.forEach((income, index) => {
      const sliceAngle = (income.valor / total) * 2 * Math.PI;
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

  

  

  refreshPage() {
    window.location.reload();
  }


  openModal(type: 'create' | 'edit' ) {
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
      })
      .catch(err => alert('Erro ao criar receita: ' + err));
      this.refreshPage();
    }
  }
    
  async onSubmitRemove(id: string) {
    try {
      await this.homeService.removeIncome(id);
      alert('Receita removida com sucesso!');
      
      //  Atualiza a lista de receitas e os gráficos
      await this.loadIncomes(); 
      
    } catch (err) {
      alert('Erro ao remover receita: ' + err);
    }
  }
    
  async onSubmitEdit(id: string) {
    if (this.editIncomeForm.valid) {
      try {
        const {data, categoria, valor, origemDoPagamento, observacoes } = this.editIncomeForm.value;
        const updatedIncome: Income = { data, categoria, valor, origemDoPagamento, observacoes };
        
        await this.homeService.editIncome(id, updatedIncome).then(() =>{
          alert('Receita atualizada com sucesso!')
        });
        this.refreshPage();

      } catch (err) {
        alert('Error updating income: ' + err);
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
      observacoes: income.observacoes
    });
  }

  home(){
    this.router.navigate(['/home']);
  }
}
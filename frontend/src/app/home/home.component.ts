import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { DashboardService } from './dashboard/dashboard.service';
import { categoryResponse } from '../entity/response/categoryResponse';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
  
})
export class HomeComponent implements OnInit {
  title = "home" 
  isUserMenuOpen: boolean = false;
  userName: string = ''; 
  userEmail: string = ''
  
  protected Object = Object;
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);
  private router = inject(Router);

  currentMonth: Date = new Date();
  totalIncome: number = 0;
  totalExpenses: number = 0;
  balance: number = 0;
  incomeTrend: number = 0;
  expenseTrend: number = 0;
  highestExpense: any = null;
  highestIncome: any = null;
  topExpenseCategory: categoryResponse | null = null;
  topIncomeCategory: categoryResponse | null = null;


  async ngOnInit() {
    this.updateUserInfo();
    await this.loadDashboardData();
  }

  updateUserInfo() {
    this.userName = this.getUserName();
    this.userEmail = this.getEmail();
  }

  private formatYearMonth(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${year}-${month}`;
  }

  async loadDashboardData() {
    const formattedDate = this.formatYearMonth(this.currentMonth);

    this.totalExpenses = await this.dashboardService.getTotalExpenseInMonthData(formattedDate);
    this.totalIncome = await this.dashboardService.getTotalIncomeInMonthData(formattedDate);
    this.balance = this.totalIncome - this.totalExpenses;
  
    this.highestIncome = await this.dashboardService.getBiggerIncomeData(formattedDate);
    this.highestExpense = await this.dashboardService.getBiggerExpenseData(formattedDate);

    this.topIncomeCategory = await this.dashboardService.getBiggerCategoryIncomeData(formattedDate);
    this.topExpenseCategory = await this.dashboardService.getBiggerCategoryExpenseData(formattedDate);
  }

  previousMonth() {
    this.currentMonth = new Date(this.currentMonth.setMonth(this.currentMonth.getMonth() - 1));
    this.loadDashboardData();
  }

  nextMonth() {
    this.currentMonth = new Date(this.currentMonth.setMonth(this.currentMonth.getMonth() + 1));
    this.loadDashboardData();
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  receitas(): void {
    this.router.navigate(['/home/income']);
  }

  despesas(): void {
    this.router.navigate(['/home/expense']);
  }

  goAdmin(): void {
    this.router.navigate(['/home/admin']);
  }

  toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
    this.updateUserInfo();
  }

  getUserName(): string {
    return this.authService.getUserName();
  }

  getEmail(): string {
    return this.authService.getUserEmail();
  }

async desativarConta(): Promise<void> {
  const confirma = window.confirm('Tem certeza que deseja desativar sua conta? Esta ação só pode ser desfeita por um administrador.');
  
  if (confirma) {
      try {
          const userId = this.authService.getUserId();
          
          if (!userId) {
              throw new Error('ID do usuário não encontrado');
          }

          const sucesso = await this.authService.disableAccount(userId);
          
          if (sucesso) {
              this.router.navigate(['/root']);
              //forçar o recarregamento da página
              window.location.reload();
              window.alert('Conta desativada com sucesso!');
          }
          
      } catch (error) {
          console.error('Falha na desativação:', error);
          const errorMessage = error instanceof Error ? error.message : 'Erro ao desativar conta. Tente novamente mais tarde.';
          window.alert(errorMessage);
      }
  }
}

}
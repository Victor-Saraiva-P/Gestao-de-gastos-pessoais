import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

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
  
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit() {
    this.updateUserInfo();
  }

  updateUserInfo() {
    this.userName = this.getUserName();
    this.userEmail = this.getEmail();
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
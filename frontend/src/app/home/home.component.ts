import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
<nav class="navbar">
      <div class="nav-container">
        <div class="left-section">
          <h1 class="logo"><img src="assets/logo-horizontal.png"></h1>
        </div>
        <div class="right-section" *ngIf="isAdmin()">
          <button class="button" (click)="goAdmin()">Painel Admin</button>
        </div>
        <div class="right-section">
          <button class="button" (click)="receitas()">Minhas receitas</button>
        </div>
        <div class="right-section">
       <!-- <button class="button" (click)="despesas()">Minhas despesas</button>-->
       <button class="button despesas-btn" (click)="despesas()">Minhas despesas</button>
        </div>
        <div class="right-section">
          <button class="button" (click)="logout()">Sair</button>
        </div>
      </div>
</nav>

  `,
  styleUrls: ['./home.component.css']
  
})
export class HomeComponent {
  title = "home" 
  
  private authService = inject(AuthService);
  private router = inject(Router);

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
}
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Component({selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `

<section class="root-app">
  <header class="header">
    <h1 class="logo"><img src="assets/logo2-branco.png"/></h1>
  </header>

  <div class="hero">
    <div class="texto-hero">
      <h2>Controle suas finanças de forma inteligente</h2>
      <h3>Transforme sua relação com o dinheiro através de uma gestão simplificada e poderosa </h3>
      <div class="grupo-bnt">
        <button class="btn-primary" (click)="register()">Começar Agora</button>
        <button class="btn-secondary" (click)="login()">Já tenho conta</button>
      </div>
    </div>

  <!-- Value Proposition -->
  <div class="funcoes">
    <div class="card">
      <h3>Por que escolher o ContaCerta? <img class="moeda" src="assets/coin-r.png"></h3>
      <div class="grid">
        <div class="feature">
          <i class="icon">📊</i>
          <h4>Controle Total</h4>
          <p>Registro manual de receitas e despesas com categorização inteligente</p>
        </div>
        
        <div class="feature">
          <i class="icon">🔔</i>
          <h4>Alertas Inteligentes</h4>
          <p>Notificações sobre os limites de gastos</p>
        </div>

        <div class="feature">
          <i class="icon">📈</i>
          <h4>Insights Poderosos</h4>
          <p>Gráficos e relatórios detalhados para análise financeira</p>
        </div>
      </div>
    </div>
  </div>

  <footer class="footer">
    <p>© 2025 ContaCerta - Sua jornada para a liberdade financeira</p>
  </footer>

  `,
  styleUrls: ['root.component.css']
})
export class RootComponent {
  title = 'root';
  
    private authService = inject(AuthService);
    private router = inject(Router);
  
    login(): void {
      this.router.navigate(['/login']);
    }
  
    register(): void {
      this.router.navigate(['/register']);
    }
}

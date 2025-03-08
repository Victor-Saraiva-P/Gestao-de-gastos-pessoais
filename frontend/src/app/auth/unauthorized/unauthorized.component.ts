import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="acesso-negado">
      <h1>Acesso Negado</h1>
      <p>Você não tem permissão para acessar esta página.</p>
      <button (click)="home()">Voltar para a página inicial</button>
    </div>

  `,
  styleUrls: ['./unauthorized.component.css']
})
export class UnauthorizedComponent {
  title = "unauthorized"

  private router = inject(Router);

  home() {
    this.router.navigate(['/home']);
  }
}
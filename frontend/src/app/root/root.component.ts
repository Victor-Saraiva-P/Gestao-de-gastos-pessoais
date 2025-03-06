import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Component({selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <div class="left-section">
        </div>
        <div class="right-section">
          <button (click)="login()">Login</button>
        </div>
        <div class="right-section">
          <button (click)="register()">Cadastro</button>
        </div>
      </div>
    </nav>
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

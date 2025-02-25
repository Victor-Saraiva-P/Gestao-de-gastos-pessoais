import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
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
        </div>
        <div class="right-section">
          <button class="logout" (click)="logout()">Sair</button>
        </div>
      </div>
    </nav>
  `,
  styleUrls: ['./home.component.css']
  
})
export class HomeComponent {
  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
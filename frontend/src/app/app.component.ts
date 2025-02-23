import { Component, inject} from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth/auth.service';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [RouterModule, CommonModule],
  template: `
    <main>
      <header class="brand-name">
        <img class="brand-logo" src="assets/logo.svg" 
          alt="logo" aria-hidden="true" />
          
      </header>
      <section class="content">
        <router-outlet></router-outlet>
      </section>
    </main>
  `,
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'home';

  private authService = inject(AuthService);
  private router = inject(Router);
}
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <section>
      <div class="login-container">
        <h2>Login</h2>
        <form [formGroup]="loginForm">
          <label>Email</label>
          <input type="email" formControlName="email" placeholder="Digite seu email" />

          <label>Password</label>
          <input type="password" formControlName="password" placeholder="Digite sua senha" required />

          <button type="submit" click="loginForm.invalid">Login</button>
        </form>
        <p>No account?  <a [routerLink]="['/register']">Register</a></p>
      </div>
    </section>
  `,
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

}
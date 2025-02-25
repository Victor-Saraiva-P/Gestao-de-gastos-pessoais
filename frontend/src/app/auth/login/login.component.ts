import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <section class="form-section">
  <div class="login-container">
    <div class="title-container">
      <div>
        <h2>     Entre</h2>
        <h3>     na sua conta</h3>
      </div>
      <div class="coins">
        <img src="assets/coin.svg" />
      </div>
  </div>

<form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
<div class="input-container">
  <label for="email">Email</label>
  <div class="input-box">
    <input 
      type="email" 
      id="email" 
      formControlName="email" 
      placeholder="Teste@gmail.com" required>
    <i class="input-icon">
      <img src="assets/mail-icon.svg" alt="ícone cadeado">
    </i>
  </div>
</div>

<div class="input-container">
  <label for="password">Senha</label>
  <div class="input-box">
    <input 
      [type]="passwordVisible ? 'text' : 'password'"  
      id="password" 
      formControlName="password" 
      placeholder="Digite sua senha" 
      required
    >
    <i class="input-icon" (click)="togglePasswordVisibility('password')" style="cursor: pointer;">
    <img [src]="passwordVisible ? 'assets/open-lock.svg' : 'assets/lock-icon.svg'" alt="ícone senha">
    </i>
  </div>
</div>
<button class="entrar-bnt" type="submit" click="loginForm.invalid">Entrar</button>
</form>
   <p>  Não tem uma conta? </p>
   <a [routerLink]="['/register']">
  <button class="cadastro-btn" type="button">Cadastre-se</button>
</a>

</div>

<div class="image-container">
  <!--<div><h2>FINANCY</h2></div>-->
  <img src="assets/pig-coinr.png"/>    
</div>
</section>

  `,
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  passwordVisible = false; 
  
  togglePasswordVisibility(field: 'password') {
    if (field === 'password') {
      this.passwordVisible = !this.passwordVisible; 
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.authService.login(email, password)
      .then(user => this.router.navigate(['/home']))
      .catch(err => alert('Login error: ' + err));
    }
  }

}
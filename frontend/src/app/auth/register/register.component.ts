import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { Router, RouterLink } from '@angular/router';
import { User } from '../../entity/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="form-container">
  <div class="register-container">
    <div class="title-container"> 
      <h2>Cadastre-se</h2>
      <div class="coins">
        <img src="assets/purse-r.png"/>
      </div>
    </div>

    <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
      <!-- Usuário -->
      <div class="input-container">
        <label for="usuario">Usuário</label>
        <div class="input-box">
          <input 
            type="text"
            id="usuario" 
            formControlName="username" 
            placeholder="Digite um usuário" 
            required>
          <i class="input-icon">
            <img src="assets/mail-icon.svg" alt="ícone usuário">
          </i>
        </div>
        <p class="error" *ngIf="registerForm.controls['username'].invalid && registerForm.controls['username'].touched">
          {{ registerForm.controls['username'].hasError('required') ? 'Campo obrigatório' : 'Mínimo de 3 caracteres' }}
        </p>
      </div>

      <!-- Email -->
      <div class="input-container">
        <label for="email">Email</label>
        <div class="input-box">
          <input 
            type="email"
            id="email" 
            formControlName="email" 
            placeholder="Teste@gmail.com" 
            required>
          <i class="input-icon">
            <img src="assets/mail-icon.svg" alt="ícone email">
          </i>
        </div>
        <p class="error" *ngIf="registerForm.controls['email'].invalid && registerForm.controls['email'].touched">
          {{ registerForm.controls['email'].hasError('required') ? 'Campo obrigatório' : 'E-mail inválido' }}
        </p>
      </div>

      <!-- Senha -->
      <div class="input-container">
        <label for="password">Senha</label>
        <div class="input-box">
          <input 
          [type]="passwordVisible ? 'text' : 'password'" 
            id="password" 
            formControlName="password" 
            placeholder="Digite uma senha" 
            required>
          <i class="input-icon" (click)="togglePasswordVisibility('password')" style="cursor: pointer;" >
          <img [src]="passwordVisible ? 'assets/open-lock.svg' : 'assets/lock-icon.svg'" alt="ícone senha">
          </i>
        </div>
        <p class="error" *ngIf="registerForm.controls['password'].invalid && registerForm.controls['password'].touched">
          {{ registerForm.controls['password'].hasError('required') ? 'Campo obrigatório' : 'Mínimo de 6 caracteres' }}
        </p>
      </div>

      <!-- Confirmar Senha -->
      <div class="input-container">
        <label for="confirm-password">Confirme a senha</label>
        <div class="input-box">
          <input 
            [type]="confirmPasswordVisible ? 'text' : 'password'"
            id="confirm-password" 
            formControlName="confirmPassword" 
            placeholder="Confirme a senha" 
            required>
          <i class="input-icon" (click)="togglePasswordVisibility('confirmPassword')" style="cursor: pointer;">
            <img [src]="confirmPasswordVisible ? 'assets/open-lock.svg' : 'assets/lock-icon.svg'" alt="ícone senha">
          </i>
        </div>
        <p class="error" *ngIf="registerForm.hasError('passwordMismatch') && registerForm.controls['confirmPassword'].touched">
          As senhas não coincidem!
        </p>
      </div>

      <!-- Função -->
<div class="input-container-sub">
  <label for="role">Função</label>
  <div class="input-box-sub">
    <select id="role" formControlName="role" required>
      <option value="" disabled selected>Selecione um papel</option>
      <option value="ADMIN">Admin</option>
      <option value="USER">User</option>
    </select>
  </div>
  <p class="error" *ngIf="registerForm.controls['role'].invalid && registerForm.controls['role'].touched">
    Campo obrigatório
  </p>
</div>


      <!-- Botão de Submit -->
      <button type="submit" [disabled]="registerForm.invalid">Criar conta</button>
    </form>

    <p>Já possui uma conta? <a [routerLink]="'/login'" class="login-link">Entre aqui</a></p>

  </div>
  <div class="image-container">
    <img src="assets/pig-coinr.png"/>    
  </div>
</section>
  `,
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  registerForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]],
    role: ['', Validators.required]
  }, { validators: this.passwordMatchValidator });

  passwordVisible = false; 
  confirmPasswordVisible = false;  

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  togglePasswordVisibility(field: 'password' | 'confirmPassword') {
    if (field === 'password') {
      this.passwordVisible = !this.passwordVisible;  
    } else {
      this.confirmPasswordVisible = !this.confirmPasswordVisible;  
    }
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { username, email, password, role } = this.registerForm.value;
      const newUser: User = { username, email, password, role: role.toUpperCase() };

      this.authService.register(newUser)
      .then(() => this.router.navigate(['/login']))
      .catch(err => alert('Error registering user: ' + err));
    }
  }

}
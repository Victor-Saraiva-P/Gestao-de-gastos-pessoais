import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { User } from '../user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div class="register-container">
        <h2>Cadastro</h2>
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <label>Username</label>
          <input type="username" formControlName="username" placeholder="Digite seu nome de usuario" required />
          <p class="error" *ngIf="registerForm.controls['username'].invalid && registerForm.controls['username'].touched">
        {{ registerForm.controls['username'].hasError('required') ? 'Campo obrigatório' : 'Mínimo de 3 caracteres' }}
          </p>
          

          <label>Email</label>
          <input type="email" formControlName="email" placeholder="Digite seu email" required />
           <p class="error" *ngIf="registerForm.controls['email'].invalid && registerForm.controls['email'].touched">
        {{ registerForm.controls['email'].hasError('required') ? 'Campo obrigatório' : 'E-mail inválido' }}
          </p>
          
          

          <label>Password</label>
          <input type="password" formControlName="password" placeholder="Digite sua senha" required />
          <p class="error" *ngIf="registerForm.controls['password'].invalid && registerForm.controls['password'].touched">
        {{ registerForm.controls['password'].hasError('required') ? 'Campo obrigatório' : 'Mínimo de 6 caracteres' }}
          </p>
          

          <label>Password confirmation</label>
          <input type="password" formControlName="confirmPassword" placeholder="Confirme sua senha" required />
          <p class="error" *ngIf="registerForm.hasError('passwordMismatch') && registerForm.controls['confirmPassword'].touched">
            As senhas não coincidem!
          </p>


          <label>Role</label>
          <select formControlName="role">
            <option value="" disabled selected>Selecione um papel</option>
            <option value="admin">Admin</option>
            <option value="user">User</option>
          </select>
            <p class="error" *ngIf="registerForm.controls['role'].invalid && registerForm.controls['role'].touched">
          Campo obrigatório
            </p>
          
          <button type="submit" [disabled]="registerForm.invalid">Submit</button>
        </form>
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

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { username, email, password, role } = this.registerForm.value;
      const newUser: User = { id: 0, username, email, password, role};

      this.authService.register(newUser)
      .then(() => this.router.navigate(['/login']))
      .catch(err => alert('Error registering user: ' + err));
    }
  }

}
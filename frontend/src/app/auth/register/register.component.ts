import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div class="register-container">
        <h2>Cadastro</h2>
        <form [formGroup]="registerForm">
          <label>Username</label>
          <input type="username" formControlName="username" placeholder="Digite seu nome de usuario" required />
          

          <label>Email</label>
          <input type="email" formControlName="email" placeholder="Digite seu email" required />
          

          <label>Password</label>
          <input type="password" formControlName="password" placeholder="Digite sua senha" required />
          

          <label>Password confirmation</label>
          <input type="password" formControlName="confirmPassword" placeholder="Confirme sua senha" required />


          <label>Role</label>
          <select formControlName="role">
            <option value="" disabled selected>Selecione um papel</option>
            <option value="admin">Admin</option>
            <option value="user">User</option>
          </select>
          
          <button type="submit" click="registerForm.invalid">submit</button>
        </form>
      </div>
    </section>
  `,
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
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

}
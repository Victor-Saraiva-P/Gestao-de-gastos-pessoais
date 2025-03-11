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
  templateUrl: './register.component.html',
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
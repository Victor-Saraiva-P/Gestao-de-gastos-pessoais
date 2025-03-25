import { Injectable } from '@angular/core';
import { User } from '../entity/user';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl + '/auth'; 
  

  async register(newUser: User): Promise<User | null> {
    try {
      const response = await fetch(`${this.apiUrl}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newUser)
      });

      if (!response.ok) {
        throw new Error('Falha no registro');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar usuário:', error);
      return null;
    }
  }

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login falhou');
      }
      const data = await response.json();

      //salva o token JWT recebido
      localStorage.setItem('token', data.token);

      return true;
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      return false;
    }
  }
  
  hasRole(requiredRole: string): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.role === requiredRole;
    } catch (error) {
      console.error('Token error:', error);
      return false;
    }
  }
  
  logout(): void {
    localStorage.removeItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

}
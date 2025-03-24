import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { User } from '../entity/user';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = environment.apiUrl + '/admin'; 
  private authService = inject(AuthService);
    

  async getUsers(): Promise<User[] | null> {
      try {
        const response = await fetch(`${this.apiUrl}/users`, {
          method: 'GET',
          headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.authService.getToken()}`
          },
        });
  
        if (!response.ok) {
          throw new Error('Falha na busca de usuários');
        }
  
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar usuários:', error);
        return null;
      }
    }
}

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

  async changeUserRole(id: string, role: string): Promise<void> {
    try {
      const body = JSON.stringify({  role });
      const response = await fetch(`${this.apiUrl}/users/${id}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: body
      });

      if (!response.ok) {
        throw new Error('Falha na busca de usuários');
      }

    } catch (error) {
      console.error('Erro ao buscar usuários:', error);
    }
  }

    async toggleUserStatus(id: string, data: { estaAtivo: boolean; role: string }): Promise<boolean> {
      try {
        const response = await fetch(`${this.apiUrl}/users/${id}`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.authService.getToken()}`
          },
          body: JSON.stringify(data)
        });
        
          return response.ok;
        } catch (error) {
          console.error('Erro ao alterar status:', error);
          return false;
        }
   }
}

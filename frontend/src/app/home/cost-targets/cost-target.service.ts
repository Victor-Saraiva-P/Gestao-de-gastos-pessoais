import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../auth/auth.service';
import { Target } from '../../entity/costTarget';

@Injectable({
  providedIn: 'root'
})
export class CostTargetService {

  private apiUrl = environment.apiUrl + '/orcamento-mensal';
  private authService = inject(AuthService);

  async getAllTargets(): Promise<Target[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar metas');
      }

      const data = await response.json();
      
      return data;
    } catch (error) {
      console.error('Erro ao buscar metas:', error);
      return null;
    }
  }


  async createTarget(newTarget: Target): Promise<string[] | null> {
    try {
      const body = JSON.stringify(newTarget);

      const response = await fetch(`${this.apiUrl}`, {
        method: 'POST',

        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: body
      });

      if (!response.ok) {
        throw new Error('Falha ao criar uma meta');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao criar uma meta:', error);
      return null;
    }
  }

  async getTargetById(id:string): Promise<string | null> {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar meta de despesas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar meta de despesas:', error);
      return null;
    }
  }

  async getByPeriod(period: string): Promise<string[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}/periodo/${period}`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar meta de despesas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar meta de despesas:', error);
      return null;
    }
  }

  async uptadeTarget(id: string, updateTarget: Target): Promise<string[] | null> {
    try {
      const body = JSON.stringify(updateTarget);
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: body
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar meta de receitas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar meta de receitas:', error);
      return null;
    }
  }

  async deleteTarget(id: string): Promise<string[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'DELETE',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar meta de receitas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar meta de receitas:', error);
      return null;
    }
  }
  
}

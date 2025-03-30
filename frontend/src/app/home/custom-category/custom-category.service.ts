import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CustomCategoryService {

  private apiUrl = environment.apiUrl + '/categorias';
  private authService = inject(AuthService);

  async getAllCategories(): Promise<string[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar categorias');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar categorias:', error);
      return null;
    }
  }

  async createCategories(type: string, name: string): Promise<string[] | null> {
    try {
      const body = JSON.stringify({ type, name });

      const response = await fetch(`${this.apiUrl}`, {
        method: 'CREATE',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: body
      });

      if (!response.ok) {
        throw new Error('Falha ao criar uma categoria');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao criar uma categoria:', error);
      return null;
    }
  }

  async getAllExpenseCategories(): Promise<string[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}/despesas`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar categorias de despesas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar categorias de despesas:', error);
      return null;
    }
  }

  async changeNameCategory(id: string, name: string): Promise<string[] | null> {
    try {
      const body = JSON.stringify({ name });
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: body
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar categorias de receitas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar categorias de receitas:', error);
      return null;
    }
  }

  async deleteCategory(id: string): Promise<string[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'DELETE',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar categorias de receitas');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar categorias de receitas:', error);
      return null;
    }
  }
}

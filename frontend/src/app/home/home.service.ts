import { inject, Injectable } from '@angular/core';
import { Income } from '../entity/income';
import { Expense } from '../entity/expense';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private apiUrl = 'http://localhost:8080';
  private authService = inject(AuthService);
  
  //------------------------ Receitas -----------------------------------------
  async createIncome(newIncome: Income): Promise<Income | null> {
    try {
      const response = await fetch(`${this.apiUrl}/receitas`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify(newIncome)
      });

      if (!response.ok) {
        throw new Error('Falha ao criar renda');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao criar renda:', error);
      return null;
    }
  }
  
  async removeIncome(id: string){
    try {
      const response = await fetch(`${this.apiUrl}/receitas/${id}`, {
        method: 'DELETE',
        headers: { 
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao remover renda');
      }

      return null;
    } catch (error) {
      console.error('Erro ao remover renda:', error);
      return null;
    }
  }

  async editIncome(id: string, incomeData: Income): Promise<Income | null> {
    try {
      const response = await fetch(`${this.apiUrl}/receitas/${id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify(incomeData)
      });
  
      if (!response.ok) {
        throw new Error('Falha ao atualizar renda');
      }
  
      return await response.json();
    } catch (error) {
      console.error('Erro ao atualizar renda:', error);
      return null;
    }
  }
  //------------------------ Despesas -----------------------------------------

  async createExpense(newExpense: Expense): Promise<Expense | null> {
    try {
      const response = await fetch(`${this.apiUrl}/despesas`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify(newExpense)
      });

      if (!response.ok) {
        throw new Error('Falha ao criar despesa');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao criar despesa:', error);
      return null;
    }
  }
  
   // Buscar uma despesa pelo ID
   async getExpenseById(id: string): Promise<Expense | null> {
    try {
      const response = await fetch(`${this.apiUrl}/despesas/${id}`, {
        method: 'GET',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao buscar despesa');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar despesa:', error);
      return null;
    }
  }

  // Atualizar uma despesa existente
  async updateExpense(id: string, updatedExpense: Expense): Promise<Expense | null> {
    try {
      const response = await fetch(`${this.apiUrl}/expense/${id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify(updatedExpense)
      });

      if (!response.ok) {
        throw new Error('Falha ao atualizar despesa');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao atualizar despesa:', error);
      return null;
    }
  }

  async removeExpense(id: string){
    try {
      const response = await fetch(`${this.apiUrl}/despesas/${id}`, {
        method: 'DELETE',
        headers: { 
          'Authorization': `Bearer ${this.authService.getToken()}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao remover renda');
      }

      return null;
    } catch (error) {
      console.error('Erro ao remover renda:', error);
      return null;
    }
  }
}

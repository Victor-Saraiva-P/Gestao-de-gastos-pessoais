import { Injectable } from '@angular/core';
import { Income } from '../entity/income';
import { Expense } from '../entity/expense';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private apiUrl = 'http://localhost:8080/income';
  
  //------------------------ Receitas -----------------------------------------
  async createIncome(newIncome: Income): Promise<Income | null> {
    try {
      const response = await fetch(`${this.apiUrl}/createIncome`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
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


  //------------------------ Despesas -----------------------------------------

  async createExpense(newExpense: Expense): Promise<Expense | null> {
    try {
      const response = await fetch(`${this.apiUrl}/createExpense`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
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
      const response = await fetch(`${this.apiUrl}/expense/${id}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
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
        headers: { 'Content-Type': 'application/json' },
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
}

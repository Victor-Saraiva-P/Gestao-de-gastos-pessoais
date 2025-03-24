import { inject, Injectable } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Expense } from '../../entity/expense';
import { PizzaGraphic } from '../../entity/response/pizzaResponse';
import { BarsGraphic } from '../../entity/response/barsResponse';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  
  private apiUrl = environment.apiUrl + '/despesas'; 
  private authService = inject(AuthService);

   //------------------------ Despesas -----------------------------------------
  
    async createExpense(newExpense: Expense): Promise<Expense | null> {
      try {
        const response = await fetch(`${this.apiUrl}`, {
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
        const response = await fetch(`${this.apiUrl}/${id}`, {
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
        const response = await fetch(`${this.apiUrl}/${id}`, {
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
        const response = await fetch(`${this.apiUrl}/${id}`, {
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
  
    async getExpenses(): Promise<Expense[] | null> {
      try {
        const response = await fetch(`${this.apiUrl}`, {
          method: 'GET',
          headers: { 
            'Authorization': `Bearer ${this.authService.getToken()}`
          }
        });
    
        if (!response.ok) {
          throw new Error('Falha ao buscar despesas');
        }
        
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar despesas:', error);
        return null;
      }
    }

    async getExpensePizzaChart(inicio: string, fim: string): Promise<PizzaGraphic | null> {
      try {
        const response = await fetch(`${this.apiUrl}/grafico-pizza?inicio=${inicio}&fim=${fim}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${this.authService.getToken()}`,
          },
        });
        
        if (!response.ok) {
          throw new Error('Falha ao buscar gráfico de pizza');
        }
        
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar gráfico de pizza:', error);
        return null;
      }
    }
  
    async getExpenseBarChart(inicio: string, fim: string): Promise<BarsGraphic | null> {
      try {
        const response = await fetch(`${this.apiUrl}/grafico-barras?inicio=${inicio}&fim=${fim}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${this.authService.getToken()}`,
          },
        });
        
        if (!response.ok) {
          throw new Error('Falha ao buscar gráfico de barras');
        }
        
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar gráfico de barras:', error);
        return null;
      }
    }
  
    async getExpensesByDateInterval(inicio: string, fim: string): Promise<Expense[] | null> {
      try {
        const response = await fetch(
          `${this.apiUrl}/por-intervalo-de-datas?inicio=${inicio}&fim=${fim}`,
          {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${this.authService.getToken()}`,
            },
          }
        );
        
        if (!response.ok) {
          throw new Error('Falha ao buscar receitas por intervalo de datas');
        }
        
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar receitas por intervalo de datas:', error);
        return null;
      }
    }
  
    async getExpensesByValueInterval(min: number, max: number): Promise<Expense[] | null> {
      try {
        const response = await fetch(
          `${this.apiUrl}/por-intervalo-de-valores?min=${min}&max=${max}`,
          {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${this.authService.getToken()}`,
            },
          }
        );
        
        if (!response.ok) {
          throw new Error('Falha ao buscar receitas por intervalo de valores');
        }
        
        return await response.json();
      } catch (error) {
        console.error('Erro ao buscar receitas por intervalo de valores:', error);
        return null;
      }
    }
}

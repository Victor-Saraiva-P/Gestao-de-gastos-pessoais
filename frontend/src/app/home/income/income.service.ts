import { inject, Injectable } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Income } from '../../entity/income';
import { PizzaGraphic } from '../../entity/response/pizzaResponse';
import { BarsGraphic } from '../../entity/response/barsResponse';

@Injectable({
  providedIn: 'root',
})
export class IncomeService {
  private apiUrl = 'http://localhost:8080/receitas';
  private authService = inject(AuthService);

  //------------------------ Receitas -----------------------------------------
  async createIncome(newIncome: Income): Promise<Income | null> {
    try {
      const response = await fetch(`${this.apiUrl}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.authService.getToken()}`,
        },
        body: JSON.stringify(newIncome),
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

  async removeIncome(id: string) {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${this.authService.getToken()}`,
        },
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
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${this.authService.getToken()}`,
        },
        body: JSON.stringify(incomeData),
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

  async getIncomes(): Promise<Income[] | null> {
    try {
      const response = await fetch(`${this.apiUrl}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${this.authService.getToken()}`,
        },
      });

      if (!response.ok) throw new Error('Falha ao buscar receitas');
      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar receitas:', error);
      return null;
    }
  }

  async getIncomePizzaChart(
    inicio: string,
    fim: string
  ): Promise<PizzaGraphic | null> {
    try {
      const response = await fetch(
        `${this.apiUrl}/grafico-pizza?inicio=${inicio}&fim=${fim}`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${this.authService.getToken()}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error('Falha ao buscar gráfico de pizza');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar gráfico de pizza:', error);
      return null;
    }
  }

  async getIncomeBarChart(
    inicio: string,
    fim: string
  ): Promise<BarsGraphic | null> {
    try {
      const response = await fetch(
        `${this.apiUrl}/grafico-barras?inicio=${inicio}&fim=${fim}`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${this.authService.getToken()}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error('Falha ao buscar gráfico de barras');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao buscar gráfico de barras:', error);
      return null;
    }
  }

  async getIncomesByDateInterval(
    inicio: string,
    fim: string
  ): Promise<Income[] | null> {
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

  async getIncomesByValueInterval(
    min: number,
    max: number
  ): Promise<Income[] | null> {
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

import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Data } from '@angular/router';
import { totalResponse } from '../../entity/response/totalResponse';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = environment.apiUrl + '/dashboard';

  async getBalanceData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/saldo-total?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getBiggerExpenseData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/maior-despesa?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getBiggerIncomeData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/maior-receita?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getBiggerCategoryIncomeData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/categoria-maior-receita?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getBiggerCategoryExpenseData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/categoria-maior-despesa?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      return await response.json();
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getTotalIncomeInMonthData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/receita-total?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      const data: totalResponse = await response.json();

      return data.saldo;

    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }

  async getTotalExpenseInMonthData(mes: string): Promise<any> {
    try {
      const response = await fetch(`${this.apiUrl}/despesa-total?periodo=${mes}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('Falha ao obter dados do dashboard');
      }

      const data: totalResponse = await response.json();

      return data.saldo;
    } catch (error) {
      console.error('Erro ao obter dados do dashboard:', error);
      return null;
    }
  }
}

import { Injectable } from '@angular/core';
import { Income } from '../entity/income';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private apiUrl = 'http://localhost:8080/income';
  

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
}

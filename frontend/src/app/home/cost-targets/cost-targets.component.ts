import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cost-targets',
  imports: [CommonModule , FormsModule],
  templateUrl: './cost-targets.component.html',
  styleUrl: './cost-targets.component.css'
})
export class CostTargetsComponent {
  categorias = [
    { nome: 'Alimentação', limite: 0 },
    { nome: 'Moradia', limite: 0 },
    { nome: 'Transporte', limite: 0 },
    { nome: 'Lazer', limite: 0 }
  ];

  categoriaSelecionada: string | null = null; // Categoria que o usuário selecionou
  categoriaSelecionadaLimite: number | null = null; // Limite para a categoria selecionada

  // Função para capturar a categoria selecionada
  onCategoriaChange(event: any) {
    this.categoriaSelecionada = event.target.value; // Atualiza a categoria selecionada
    const categoria = this.categorias.find(cat => cat.nome === this.categoriaSelecionada);
    if (categoria) {
      this.categoriaSelecionadaLimite = categoria.limite; // Define o limite da categoria selecionada
    }
  }

  // Função para atualizar o limite da categoria selecionada
  atualizarLimite() {
    if (this.categoriaSelecionada) {
      const categoria = this.categorias.find(cat => cat.nome === this.categoriaSelecionada);
      if (categoria) {
        categoria.limite = this.categoriaSelecionadaLimite || 0; // Atualiza o limite da categoria
      }
    }
  }

  // Função para salvar os limites definidos
  salvarLimite() {
    console.log('Limites de despesas salvos:', this.categorias);
    alert('Limite da categoria ' + this.categoriaSelecionada + ' salvo com sucesso!');
  }
}

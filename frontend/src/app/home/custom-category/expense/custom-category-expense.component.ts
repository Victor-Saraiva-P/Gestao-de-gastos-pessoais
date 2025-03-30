import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomCategoryService } from '../custom-category.service';
import { CommonModule } from '@angular/common';
import { Categoria } from '../../../entity/categoria';

@Component({
  selector: 'app-custom-category-expense',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './custom-category-expense.component.html',
  styleUrl: './custom-category-expense.component.css'
})
export class CustomCategoryExpenseComponent implements OnInit{
  title = 'category-expense';

  expensesCatories: Categoria[] = [];
  isRemoving = false;
  isEditing = false;
  modalType: 'create' | 'edit' | null = null;
  editingCategoryId: string | null = null;

  private router = inject(Router);
  private customCategoryService = inject(CustomCategoryService);
  private fb = inject(FormBuilder);

  createCategoryExpenseForm: FormGroup = this.fb.group({
      name: ['', Validators.required],
  });

  editCategoryExpenseForm: FormGroup = this.fb.group({
    nome: ['', Validators.required],
});

  async ngOnInit() {
    await this.loadCategories();
  }

  async loadCategories() {
    const response = await this.customCategoryService.getAllExpenseCategories();
    if (response) {
      this.expensesCatories = response.filter(categoria => 
        categoria.nome !== 'Sem Categoria'
      ).sort((a, b) => a.nome.localeCompare(b.nome));
    }
  }


  openModal(type: 'create' | 'edit') {
    this.modalType = type;
  }

  closeModal() {
    this.modalType = null;
  }


  toggleRemoveMode() {
    this.isRemoving = !this.isRemoving;
    if (this.isEditing) {
      this.isRemoving = false;
    }
  }

  toggleEditMode() {
    this.isEditing = !this.isEditing;
    if (this.isRemoving) {
      this.isEditing = false;
    }
  }

  expense() {
    this.router.navigate(['/home/expense']);
  }

  refreshPage() {
    window.location.reload();
  }

  onSubmitCreate() {
      if (this.createCategoryExpenseForm.valid) {
        const nome: string = this.createCategoryExpenseForm.value.name;
    
        this.customCategoryService
          .createCategories('DESPESAS', this.correctCategory(nome))
          .then(() => {
            alert('Categoria criada com sucesso!');
            this.refreshPage();
          })
          .catch((err) => alert('Erro ao criar Categoria: ' + err));
      }
  }

  async onSubmitEdit(id: string) {
      if (this.editCategoryExpenseForm.valid) {
        try {
          const nome: string = this.editCategoryExpenseForm.value.nome;
          await this.customCategoryService.changeNameCategory(id, this.correctCategory(nome));
          alert('Categoria atualizada com sucesso!');
          this.refreshPage();
        } catch (err) {
          alert('Erro ao atualizar Categoria: ' + err);
        }
      }
    }

  async onSubmitRemove(id: string) {
    try {
      await this.customCategoryService.deleteCategory(id);
      alert('Categoria removida com sucesso!');
      await this.loadCategories();
    } catch (err) {
      alert('Erro ao remover Categoria: ' + err);
    }
  }

  openEditModal(categoria: Categoria) {
      this.modalType = 'edit';
      this.editingCategoryId = categoria.uuid!;
      this.editCategoryExpenseForm.setValue({
        nome: categoria.nome
      });
  }

  correctCategory(string: string): string {
    const newString = string.toLowerCase();
    return newString.charAt(0).toUpperCase() + newString.slice(1);
  }
}

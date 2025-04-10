import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CustomCategoryService } from '../custom-category.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Categoria } from '../../../entity/categoria';

@Component({
  selector: 'app-custom-category',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: 'custom-category-income.component.html',
  styleUrls: ['custom-category-income.component.css']
})
export class CustomCategoryIncomeComponent implements OnInit {
    title = 'category-income';

    incomesCatories: Categoria[] = [];
    isRemoving = false;
    isEditing = false;
    modalType: 'create' | 'edit' | null = null;
    editingCategoryId: string | null = null;
  
    private router = inject(Router);
    private customCategoryService = inject(CustomCategoryService);
    private fb = inject(FormBuilder);

    createCategoryincomeForm: FormGroup = this.fb.group({
        name: ['', Validators.required],
    });

    editCategoryIncomeForm: FormGroup = this.fb.group({
      nome: ['', Validators.required],
    });

    async ngOnInit() {
      await this.loadCategories();
    }
  
    async loadCategories() {
      const response = await this.customCategoryService.getAllIncomeCategories();
      if (response) {
        this.incomesCatories = response.filter(categoria => 
          categoria.nome !== 'Sem Categoria'
        ).sort((a, b) => a.nome.localeCompare(b.nome));;
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
  
    income() {
      this.router.navigate(['/home/income']);
    }

    refreshPage() {
      window.location.reload();
    }

    onSubmitCreate() {
        if (this.createCategoryincomeForm.valid) {
          const nome: string = this.createCategoryincomeForm.value.name;
      
          if (this.isDuplicateTarget(nome)) {
            alert('Já existe um nome para essa categoria.');
            return;
          }

          this.customCategoryService
            .createCategories('RECEITAS', this.correctCategory(nome))
            .then(() => {
              alert('Categoria criada com sucesso!');
              this.refreshPage();
            })
            .catch((err) => alert('Erro ao criar Categoria: ' + err));
        }
    }

    async onSubmitEdit(id: string) {
      if (this.editCategoryIncomeForm.valid) {
        try {
          const nome: string = this.editCategoryIncomeForm.value.nome;

          if (this.isDuplicateTarget(nome, id)) {
            alert('Já existe um nome para essa categoria.');
            return;
          }

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
        alert('Receita removida com sucesso!');
        await this.loadCategories();
      } catch (err) {
        alert('Erro ao remover receita: ' + err);
      }
    }
    
    openEditModal(categoria: Categoria) {
      this.modalType = 'edit';
      this.editingCategoryId = categoria.uuid!;
      this.editCategoryIncomeForm.setValue({
        nome: categoria.nome
      });
    }

    correctCategory(string: string): string {
      const newString = string.toLowerCase();
      return newString.charAt(0).toUpperCase() + newString.slice(1);
    }

    isDuplicateTarget(nome: string, excludeId: string | null = null): boolean {
      return this.incomesCatories.some(target => 
        target.nome.toLowerCase() === nome.toLowerCase() &&
        target.uuid !== excludeId 
      );
    }
}

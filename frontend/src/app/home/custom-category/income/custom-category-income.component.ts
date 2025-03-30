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
  
    private router = inject(Router);
    private customCategoryService = inject(CustomCategoryService);
    private fb = inject(FormBuilder);

    createCategoryincomeForm: FormGroup = this.fb.group({
        name: ['', Validators.required],
    });

    async ngOnInit() {
      await this.loadCategories();
    }
  
    async loadCategories() {
      const response = await this.customCategoryService.getAllIncomeCategories();
      if (response) {
        this.incomesCatories = response.filter(categoria => 
          categoria.nome !== 'Sem Categoria'
        );
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
      
    
          this.customCategoryService
            .createCategories('RECEITAS', nome.toUpperCase())
            .then(() => {
              alert('Categoria criada com sucesso!');
              this.refreshPage();
            })
            .catch((err) => alert('Erro ao criar Categoria: ' + err));
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
}

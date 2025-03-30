import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CustomCategoryService } from '../custom-category.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-custom-category',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: 'custom-category-income.component.html',
  styleUrls: ['custom-category-income.component.css']
})
export class CustomCategoryIncomeComponent {
  title = 'category-income';
  
    isRemoving = false;
    isEditing = false;
    modalType: 'create' | 'edit' | null = null;
  
    private router = inject(Router);
    private customCategoryService = inject(CustomCategoryService);
    private fb = inject(FormBuilder);

    createCategoryincomeForm: FormGroup = this.fb.group({
        name: ['', Validators.required],
    });

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
}

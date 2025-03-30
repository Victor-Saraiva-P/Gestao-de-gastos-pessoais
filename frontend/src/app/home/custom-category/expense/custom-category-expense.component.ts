import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomCategoryService } from '../custom-category.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-custom-category-expense',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './custom-category-expense.component.html',
  styleUrl: './custom-category-expense.component.css'
})
export class CustomCategoryExpenseComponent {
  title = 'category-expense';

  isRemoving = false;
  isEditing = false;
  modalType: 'create' | 'edit' | null = null;

  private router = inject(Router);
  private customCategoryService = inject(CustomCategoryService);
  private fb = inject(FormBuilder);

  createCategoryExpenseForm: FormGroup = this.fb.group({
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
          .createCategories('DESPESAS', nome.toUpperCase())
          .then(() => {
            alert('Categoria criada com sucesso!');
            this.refreshPage();
          })
          .catch((err) => alert('Erro ao criar Categoria: ' + err));
      }
  }
}

import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-custom-category-expense',
  imports: [],
  templateUrl: './custom-category-expense.component.html',
  styleUrl: './custom-category-expense.component.css'
})
export class CustomCategoryExpenseComponent {
  title = 'category-expense';

  isRemoving = false;
  isEditing = false;

  private router = inject(Router);


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
}

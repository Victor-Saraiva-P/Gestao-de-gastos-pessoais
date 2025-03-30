import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-custom-category',
  imports: [],
  templateUrl: 'custom-category-income.component.html',
  styleUrls: ['custom-category-income.component.css']
})
export class CustomCategoryIncomeComponent {
  title = 'category-income';
  
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
  
    income() {
      this.router.navigate(['/home/income']);
    }
}

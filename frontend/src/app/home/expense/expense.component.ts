import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section>
      <div>
        <h2>Expenses</h2>
      </div>
</section>
  `,
  styleUrls: ['income.component.css']
  
})
export class ExpenseComponent {

}

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCategoryExpenseComponent } from './custom-category-expense.component';

describe('CustomCategoryExpenseComponent', () => {
  let component: CustomCategoryExpenseComponent;
  let fixture: ComponentFixture<CustomCategoryExpenseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomCategoryExpenseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomCategoryExpenseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

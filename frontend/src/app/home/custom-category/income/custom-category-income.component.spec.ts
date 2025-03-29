import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCategoryIncomeComponent } from './custom-category-incomecomponent';

describe('CustomCategoryComponent', () => {
  let component: CustomCategoryIncomeComponent;
  let fixture: ComponentFixture<CustomCategoryIncomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomCategoryIncomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomCategoryIncomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

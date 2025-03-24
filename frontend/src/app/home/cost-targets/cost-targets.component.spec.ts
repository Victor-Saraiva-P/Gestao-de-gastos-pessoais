import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CostTargetsComponent } from './cost-targets.component';

describe('CostTargetsComponent', () => {
  let component: CostTargetsComponent;
  let fixture: ComponentFixture<CostTargetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CostTargetsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CostTargetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

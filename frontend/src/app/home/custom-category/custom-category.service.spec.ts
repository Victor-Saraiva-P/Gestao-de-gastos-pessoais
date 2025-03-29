import { TestBed } from '@angular/core/testing';

import { CustomCategoryService } from './custom-category.service';

describe('CustomCategoryService', () => {
  let service: CustomCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomCategoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

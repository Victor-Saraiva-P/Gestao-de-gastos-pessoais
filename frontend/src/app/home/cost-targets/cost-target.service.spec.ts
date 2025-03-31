import { TestBed } from '@angular/core/testing';

import { CostTargetService } from './cost-target.service';

describe('CostTargetService', () => {
  let service: CostTargetService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CostTargetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

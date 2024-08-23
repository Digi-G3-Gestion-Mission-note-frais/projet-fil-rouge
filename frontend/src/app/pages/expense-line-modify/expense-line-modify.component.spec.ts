import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseLineModifyComponent } from './expense-line-modify.component';

describe('ExpenseLineModifyComponent', () => {
  let component: ExpenseLineModifyComponent;
  let fixture: ComponentFixture<ExpenseLineModifyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseLineModifyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseLineModifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

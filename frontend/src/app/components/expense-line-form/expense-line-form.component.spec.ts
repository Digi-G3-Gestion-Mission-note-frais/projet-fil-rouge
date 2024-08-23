import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseLineFormComponent } from './expense-line-form.component';

describe('ExpenseLineFormComponent', () => {
  let component: ExpenseLineFormComponent;
  let fixture: ComponentFixture<ExpenseLineFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseLineFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseLineFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

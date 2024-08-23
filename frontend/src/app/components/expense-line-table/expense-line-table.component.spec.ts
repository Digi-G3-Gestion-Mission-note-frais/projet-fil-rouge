import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseLineTableComponent } from './expense-line-table.component';

describe('ExpenseLineTableComponent', () => {
  let component: ExpenseLineTableComponent;
  let fixture: ComponentFixture<ExpenseLineTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseLineTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseLineTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

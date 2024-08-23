import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseLineAddComponent } from './expense-line-add.component';

describe('ExpenseLineAddComponent', () => {
  let component: ExpenseLineAddComponent;
  let fixture: ComponentFixture<ExpenseLineAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseLineAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseLineAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

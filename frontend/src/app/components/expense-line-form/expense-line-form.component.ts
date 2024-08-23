import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ExpenseLine } from '../../expense.service';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-expense-line-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './expense-line-form.component.html',
  styleUrl: './expense-line-form.component.scss'
})
export class ExpenseLineFormComponent {
  @Input() expenseLine: ExpenseLine = {
    id: null,
    date: new Date(),
    tva: 0,
    amount: 0,
    expenseType: ''
  };

  @Output() formSubmit = new EventEmitter<ExpenseLine>();

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.formSubmit.emit(this.expenseLine);
    }
  }

}

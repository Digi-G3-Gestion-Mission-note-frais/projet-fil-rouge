import { Component } from '@angular/core';
import { ExpenseLine, ExpenseService } from '../../expense.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ExpenseLineFormComponent } from '../../components/expense-line-form/expense-line-form.component';

@Component({
  selector: 'app-expense-line-add',
  standalone: true,
  imports: [CommonModule, ExpenseLineFormComponent],
  templateUrl: './expense-line-add.component.html',
  styleUrl: './expense-line-add.component.scss'
})
export class ExpenseLineAddComponent {
  constructor(private expenseService: ExpenseService, private router: Router) { }

  onFormSubmit(expenseLine: ExpenseLine) {
    this.expenseService.addLine(expenseLine).subscribe({
      next: () => this.router.navigate(['/expense']), // redirection a gerer puiqu'il faut l'id de la mission
      error: (error) => console.error('Error adding expense line:', error)
    });
  }

}

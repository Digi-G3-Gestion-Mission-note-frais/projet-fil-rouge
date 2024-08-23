import { Component, OnInit } from '@angular/core';
import { ExpenseLine, ExpenseService } from '../../expense.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ExpenseLineFormComponent } from '../../components/expense-line-form/expense-line-form.component';

@Component({
  selector: 'app-expense-line-modify',
  standalone: true,
  imports: [CommonModule, ExpenseLineFormComponent],
  templateUrl: './expense-line-modify.component.html',
  styleUrl: './expense-line-modify.component.scss'
})
export class ExpenseLineModifyComponent implements OnInit {
  expenseLine: ExpenseLine = {
    id: null,
    date: new Date(),
    tva: 0,
    amount: 0,
    expenseType: ''
  };

  constructor(
    private expenseService: ExpenseService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.expenseService.getExpenseLineById(id, 'token').subscribe({
        next: (expenseLine) => this.expenseLine = expenseLine,
        error: (error) => console.error('Error fetching expense line:', error)
      });
    }
  }

  onFormSubmit(expenseLine: ExpenseLine) {
    if(expenseLine.id){
      this.expenseService.updateLine(expenseLine.id.toString(), expenseLine, "token").subscribe({
        next: () => this.router.navigate(['/expense-list']), // redirection après modification
        error: (error) => console.error('Error updating expense line:', error)
      });
    }
   
  }
}
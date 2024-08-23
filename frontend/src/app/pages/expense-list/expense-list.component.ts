import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Expense, ExpenseService } from '../../expense.service';
import { EMPTY, Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ExpenseLineTableComponent } from '../../components/expense-line-table/expense-line-table.component';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [CommonModule, ExpenseLineTableComponent],
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.scss'
})
export class ExpenseListComponent implements OnInit {
  expense$: Observable<Expense>= EMPTY;

  constructor(private expenseService: ExpenseService, private router: Router, private route: ActivatedRoute){
    
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('missionId');
    if (id === null) {
      console.error('Invalid ID: null');
      return;
    }
    this.expense$ = this.expenseService.getExpenseById(id, "token");
  }
  

  addExpenseLine(id: number) {
    this.expense$.subscribe({
      next: (expense) => {
    console.log(`Navigating to /expenseLine/add/${id}`);
    this.router.navigate([`/expenseLine/add/${id}`]);
  },
  error: (error) => {
    console.error('Error fetching expenseLine:', error);
  }
});  
  }

  modifyExpenseLine() {
    this.expense$.subscribe({
      next: (expense) => {
        console.log(`Navigating to /expenseLine/update/${expense.id}`);
        this.router.navigate([`/expenseLine/update/${expense.id}`]);
      },
      error: (error) => {
        console.error('Error fetching expenseLine:', error);
      }
    }); 
  }
  
  removeExpenseLine(id: number): void {
    this.expenseService.removeLine(id.toString()).subscribe(() => {
      this.expense$ = this.expenseService.getExpenseById(id.toString(), "token");
    })
  }

}

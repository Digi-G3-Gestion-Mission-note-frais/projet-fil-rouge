import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ExpenseLine } from '../../expense.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-expense-line-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './expense-line-table.component.html',
  styleUrl: './expense-line-table.component.scss'
})
export class ExpenseLineTableComponent {


  @Input() expenseLines: ExpenseLine[] = [];
  @Input() expenseId!: number;

  @Output() modifyLine = new EventEmitter<number>();
  @Output() removeLine = new EventEmitter<number>();
  @Output() addLine = new EventEmitter<number>();

  onModifyLine(id: number) {
    this.modifyLine.emit(id);
  }

  onRemoveLine(id: number) {
    this.removeLine.emit(id);
  }

  onAddLine() {
    this.addLine.emit(this.expenseId);
  }
}

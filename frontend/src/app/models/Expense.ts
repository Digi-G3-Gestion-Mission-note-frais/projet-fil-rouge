import { ExpenseLine } from "./ExpenseLine";

export interface Expense {
    /** The ID of the expense */
    id: number;
  
    /** The date of the expense */
    date: Date;
  
    /** The status of the expense */
    status: string;
  
    /** The expense lines associated with this expense */
    expenseLines: ExpenseLine[];
  }
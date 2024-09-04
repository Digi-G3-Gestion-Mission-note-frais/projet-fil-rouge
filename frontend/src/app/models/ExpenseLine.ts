export interface ExpenseLine {
    /** The ID of the expense */
    id: number | null;
  
    /** The date of the expense line */
    date: Date;
  
    /** The TVA of the expense line */
    tva: number;
  
    /** The amount of the expense line */
    amount: number;
  
    /** The type of this expense line */
    expenseType: string;
  }
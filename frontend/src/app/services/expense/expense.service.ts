import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Expense } from '../../models/Expense';
import { ExpenseLine } from '../../models/ExpenseLine';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private apiUrl: string = 'http://localhost:8080/api';

  private tokenTest: string = "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiTUFOQUdFUiIsInN1YiI6Im1pc3NseWx5ZHU3NUBob3RtYWlsLmZyIiwiaWF0IjoxNzI0NzU3MzY0LCJleHAiOjE3MjQ3NzUzNjR9.3QHMpQ_G_YtRN3JOlwJNlmokjOFWaceLL47RXiIyOpD_KCc1SAqUcGQf4vZ-kZZh";

  constructor(private http: HttpClient) { }

  getExpenses(token = this.tokenTest) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.tokenTest}`
    });
    return this.http.get<Expense>(`${this.apiUrl}/expenses/user`, { headers });
  }

  getExpenseById(id: string, token = this.tokenTest) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.tokenTest}`
    });
  
    return this.http.get<Expense>(`${this.apiUrl}/expenses/${id}`, { headers }).pipe(
      catchError(error => {
        console.error(`Expense with ID ${id} not found`, error);
        return throwError(() => new Error('Not Found'));
      })
    );
  }

  addLine(expenseLine: ExpenseLine):Observable<void>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.tokenTest}`
    });
    return this.http.post<void>(this.apiUrl+"/expenseLines", {expenseLine}, { headers })
  }

  removeLine(id: string): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.tokenTest}`
    });
    return this.http.delete<void>(`${this.apiUrl}/expenseLines/${id}`, { headers });
}

updateLine(id: string, updatedLine: ExpenseLine, token:string): Observable<ExpenseLine> {
  const headers = new HttpHeaders({ 'Content-Type': 'application/json', 'Authorization': `Bearer ${this.tokenTest}` }); // Définition des headers pour la requête
  return this.http.put<ExpenseLine>(`${this.apiUrl}/expenseLines/${id}`, updatedLine, { headers }) // Met à jour la ligne existante
    .pipe(
      catchError(error => {
        console.error(`Error updating expense line with ID ${id}: `, error); // Gestion des erreurs
        return throwError(() => new Error('Failed to update expense line')); // Retourne une erreur si la mise à jour échoue
      })
    );
}

getExpenseLineById(id: string, token: string = this.tokenTest) {
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${this.tokenTest}`
  });

  return this.http.get<ExpenseLine>(`${this.apiUrl}/expenseLines/${id}`, { headers }).pipe(
    catchError(error => {
      console.error(`ExpenseLine with ID ${id} not found`, error);
      return throwError(() => new Error('Not Found'));
    })
  );
}
}
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NatureMission } from '../../../models/NatureMission';

@Injectable({
  providedIn: 'root',
})
export class NatureMissionService {
  private apiUrl = 'http://localhost:8080/api/missions/natures';

  constructor(private http: HttpClient) {}
  token = "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiTUFOQUdFUiIsInN1YiI6Im1pc3NseWx5ZHU3NUBob3RtYWlsLmZyIiwiaWF0IjoxNzI0NzU3MzY0LCJleHAiOjE3MjQ3NzUzNjR9.3QHMpQ_G_YtRN3JOlwJNlmokjOFWaceLL47RXiIyOpD_KCc1SAqUcGQf4vZ-kZZh";

  // Méthode pour récupérer toutes les natures de mission
  getNatureMissions(): Observable<NatureMission[]> {
    const headers = new HttpHeaders({
        'Authorization': `Bearer ${this.token}`
    });
      
    return this.http.get<NatureMission[]>(this.apiUrl, {headers});
  }

  // Méthode pour récupérer une nature de mission par ID
  getNatureMission(id: number): Observable<NatureMission> {
    return this.http.get<NatureMission>(`${this.apiUrl}/${id}`, );
  }

  // Méthode pour créer une nouvelle nature de mission
  createNatureMission(natureMission: NatureMission): Observable<NatureMission> {
    return this.http.post<NatureMission>(this.apiUrl, natureMission);
  }

  // Méthode pour mettre à jour une nature de mission existante
  updateNatureMission(natureMission: NatureMission): Observable<NatureMission> {
    return this.http.put<NatureMission>(`${this.apiUrl}/${natureMission.id}`, natureMission);
  }

  // Méthode pour supprimer une nature de mission
  deleteNatureMission(id: number): Observable<void> {
    console.log(`Suppression de la mission avec l'ID: ${id}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
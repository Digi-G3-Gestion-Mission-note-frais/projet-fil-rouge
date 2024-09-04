import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NatureMission } from '../interfaces/nature-mission.interface';

@Injectable({
  providedIn: 'root',
})
export class NatureMissionService {
  private apiUrl = 'http://localhost:8080/api/mission/natures';

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer toutes les natures de mission
  getNatureMissions(): Observable<NatureMission[]> {
    return this.http.get<NatureMission[]>(`${this.apiUrl}/naturemissions`);
  }

  // Méthode pour récupérer une nature de mission par ID
  getNatureMission(id: number): Observable<NatureMission> {
    return this.http.get<NatureMission>(`${this.apiUrl}/${id}`);
  }

  // Méthode pour créer une nouvelle nature de mission
  createNatureMission(natureMission: NatureMission): Observable<NatureMission> {
    return this.http.post<NatureMission>(this.apiUrl, natureMission, {
      responseType: 'json',
    });
  }

  // Méthode pour mettre à jour une nature de mission existante
  updateNatureMission(id: number, natureMissionmission: NatureMission): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, natureMissionmission);
  }

  deleteNatureMission(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  getIdByLabel(label: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/id-by-label/${label}`);
  }
  
}

import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LayoutComponent } from '../../../layout/layout.component';
import { NatureMissionService } from '../../../services/nature-mission.service';
@Component({
  selector: 'app-delete-nature-mission',
  standalone: true,
  imports: [CommonModule, LayoutComponent],
  templateUrl: './delete-nature-mission.component.html',
  styleUrls: ['./delete-nature-mission.component.scss']
})
export class DeleteNatureMissionComponent implements OnInit {
  missionId: number | null = null;

  constructor(
    private natureMissionService: NatureMissionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Logique à définir selon la façon dont tu souhaites récupérer l'ID de la mission
  }

  deleteMission(): void {
    if (this.missionId !== null) {
      this.natureMissionService.deleteNatureMission(this.missionId).subscribe({
        next: () => {
          alert('Mission supprimée avec succès');
          this.router.navigate(['/naturemissions/delete']);
        },
        error: (error) => console.error('Erreur lors de la suppression de la mission', error),
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/naturemissions']);
  }
}

import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPen, faTrash, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { NatureMission } from '../../../interfaces/nature-mission.interface';
import { LayoutComponent } from '../../../layout/layout.component';
import { NatureMissionService } from '../../../services/nature-mission.service';

@Component({
  selector: 'app-admin-mission-list',
  standalone: true,
  imports: [CommonModule, LayoutComponent, FontAwesomeModule],
  providers: [Router],
  templateUrl: './admin-mission-list.component.html',
  styleUrls: ['./admin-mission-list.component.scss'],
})
export class AdminMissionListComponent implements OnInit {
  natureMissions: NatureMission[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalItems: number = 0;
  pages: number[] = [];
  faPenIcon: IconDefinition = faPen;
  faTrashIcon: IconDefinition = faTrash;
  missionToDelete: number | null = null; // ID de la mission à supprimer
  showModal: boolean = false; // Affichage du modal

  constructor(
    private natureMissionService: NatureMissionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMissions();
  }

  loadMissions(): void {
    this.natureMissionService.getNatureMissions().subscribe(data => {
      this.totalItems = data.length;
      this.pages = Array(Math.ceil(this.totalItems / this.itemsPerPage)).fill(0).map((_, i) => i + 1);
      this.natureMissions = data.slice((this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage);
    });
  }

  goToCreatePage(): void {
    this.router.navigate(['/naturemissions/create']);
  }

  onEdit(id: number): void {
    this.router.navigate([`naturemissions/edit/${id}`]);
  }
  

  onDelete(id: number): void {
    this.missionToDelete = id;
    this.showModal = true;
  }

  confirmDelete(): void {
    if (this.missionToDelete !== null) {
      this.natureMissionService.deleteNatureMission(this.missionToDelete).subscribe({
        next: () => {
          console.log('Mission supprimée avec succès');
          this.loadMissions(); // Recharger la liste après suppression
          this.showModal = false;
          this.missionToDelete = null;
        },
        error: (error) => console.error('Erreur lors de la suppression de la mission', error),
      });
    }
  }

  cancelDelete(): void {
    this.showModal = false;
    this.missionToDelete = null;
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadMissions();
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadMissions();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.pages.length) {
      this.currentPage++;
      this.loadMissions();
    }
  }

  searchMission(event: any): void {
    const query = event.target.value.toLowerCase();
    this.natureMissionService.getNatureMissions().subscribe(data => {
      const filteredMissions = data.filter(mission =>
        mission.label.toLowerCase().includes(query)
      );
      this.totalItems = filteredMissions.length;
      this.pages = Array(Math.ceil(this.totalItems / this.itemsPerPage)).fill(0).map((_, i) => i + 1);
      this.natureMissions = filteredMissions.slice((this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage);
    });
  }
}

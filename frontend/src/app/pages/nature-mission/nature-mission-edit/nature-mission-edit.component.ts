import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NatureMissionService } from '../../../services/nature-mission.service';
import { NatureMission } from '../../../interfaces/nature-mission.interface';
import { LayoutComponent } from '../../../layout/layout.component';

@Component({
  selector: 'app-nature-mission-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LayoutComponent],
  templateUrl: './nature-mission-edit.component.html',
  styleUrls: ['./nature-mission-edit.component.scss'],
})
export class NatureMissionEditComponent implements OnInit {
  editMissionForm: FormGroup;
  missionId: number;

  constructor(
    private fb: FormBuilder,
    private natureMissionService: NatureMissionService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.editMissionForm = this.fb.group({
      label: [''],
      prime: [''],
      startDate: [''],
      endDate: [''],
      rate: [''],
      eligibility: [''],
      billing: [''],
    });
    this.missionId = 0;
  }

  ngOnInit(): void {
    this.missionId = +this.route.snapshot.paramMap.get('id')!;
    this.natureMissionService.getNatureMission(this.missionId).subscribe({
      next: (mission: NatureMission) => {
        this.editMissionForm.patchValue(mission);
      },
      error: (error) => console.error('Erreur lors de la récupération de la mission', error),
    });
  }

  onSubmit(): void {
    if (this.editMissionForm.valid) {
      this.natureMissionService.updateNatureMission(this.missionId, this.editMissionForm.value).subscribe({
        next: () => {
          console.log('Mission mise à jour avec succès');
          this.router.navigate(['/naturemissions']);
        },
        error: (error) => console.error('Erreur lors de la mise à jour de la mission', error),
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/naturemissions']);
  }
}

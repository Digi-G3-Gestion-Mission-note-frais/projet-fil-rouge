import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LayoutComponent } from '../../../layout/layout.component';
import { NatureMissionService } from '../../../services/nature-mission.service';

@Component({
  selector: 'app-nature-mission-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, LayoutComponent],
  templateUrl: './nature-mission-create.component.html',
  styleUrls: ['./nature-mission-create.component.scss'],
})
export class NatureMissionCreateComponent {
  form: FormGroup = new FormGroup({
    label: new FormControl('', Validators.required),
    adr: new FormControl(0, Validators.required),
    startDate: new FormControl(new Date(), Validators.required),
    endDate: new FormControl(new Date(), Validators.required),
    bountyRate: new FormControl(20, Validators.required),
    isBilled: new FormControl(true, Validators.required),
    isEligibleToBounty: new FormControl(false, Validators.required),
  });

  constructor(
    private natureMissionService: NatureMissionService,
    private router: Router
  ) {}

  createNatureMission() {
    if (this.form.valid) {
      this.natureMissionService.createNatureMission(this.form.value).subscribe(() => {
        this.router.navigate(['/naturemissions']); // Redirige vers la liste des missions
      });
    }
  }

  cancel() {
    this.router.navigate(['/naturemission']); // Annule et retourne à la liste des missions
  }
}

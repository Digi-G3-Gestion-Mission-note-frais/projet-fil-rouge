import { Component, Input, ViewEncapsulation } from '@angular/core';
import { Mission } from '../../../models/Mission';
import { CommonModule } from '@angular/common';
import { StatusEnum } from '../../../enums/StatusEnum';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-missions-recap',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatMenuModule, MatIconModule, MatButtonModule],
  templateUrl: './missions-recap.component.html',
  styleUrl: './missions-recap.component.scss',
})
export class MissionsRecapComponent {
  @Input() missions?: Mission[];
  statusEnum = StatusEnum;
  constructor(private route: ActivatedRoute, private router: Router){}
  groupedMissions() {
    const groups = this.missions?.reduce((acc, mission) => {
      const group = acc.find(g => g.status === mission.status);
      if (group && group.missions.length <=2 ) {
        group.missions.push(mission);
      } else {
        acc.push({ status: mission.status, missions: [mission] });
      }
      return acc;
    }, [] as { status: string; missions: any[] }[]);

    if(groups){
      groups.length = 3;
    }
    return groups;
  }

  getStatusLabel(status: string): string {
    const upperCaseStatus = status.toUpperCase() as keyof typeof StatusEnum;
    return this.statusEnum[upperCaseStatus] || status;
  }

  navigateToMission(id: number) {
    this.router.navigate([`missions/${id}`],{relativeTo: this.route.parent});
  }

}

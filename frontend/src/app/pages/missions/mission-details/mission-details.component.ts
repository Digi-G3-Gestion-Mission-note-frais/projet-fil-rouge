import { Component } from '@angular/core';
import { Mission } from '../../../models/Mission';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { MissionService } from '../../../services/mission/mission.service';
import { CommonModule, CurrencyPipe, DatePipe, Location } from '@angular/common';
import { ExpenseService } from '../../../services/expense/expense.service';
import { StatusEnum } from '../../../enums/StatusEnum';
import { TransportEnum } from '../../../enums/TransportEnum';
import { MissionFormComponent } from '../mission-form/mission-form.component';
import { ConfirmDialogComponent } from '../../../components/confirm-dialog/confirm-dialog.component';
import { LayoutComponent } from '../../../layout/layout.component';

@Component({
  selector: 'app-mission-details',
  standalone: true,
  imports: [CommonModule,RouterLink, RouterOutlet,CurrencyPipe, DatePipe, MissionFormComponent, ConfirmDialogComponent, LayoutComponent],
  templateUrl: './mission-details.component.html',
  styleUrl: './mission-details.component.scss'
})
export class MissionDetailsComponent {

  mission?: Mission;
  // updatedMission?: Mission;
  expense: any = {};
  idMission? : string;
  statusEnum = StatusEnum;
  transportEnum = TransportEnum;
  editionMode = false;
  showConfirmDialog = false;
  dialogData = {
    title: 'Supprimer la mission',
    message: 'Êtes-vous sûr de vouloir supprimer la mission ? Cette action est irréversible.',
    confirmButtonText: 'Confirmer',
    cancelButtonText: 'Annuler'
  };

  constructor(private route: ActivatedRoute, public router: Router, private missionService : MissionService,private expenseService: ExpenseService, private _location: Location){}

  ngOnInit() : void{
    this.route.paramMap.subscribe(params => {
      const id: string | null = params.get('id');
      if(id){
        this.idMission = id;
        this.fetchData();

      }

    })
  }

  fetchData(){
    if(this.idMission){
      this.missionService.getMissionById(this.idMission).subscribe({
        next: (mission) => {
          this.mission = mission;
  
          if(mission.expense){
            this.expense = mission.expense; 
          }
        },
        error: (error) => {
          console.error(error);
          this.router.navigate(["/404"]);
        }
      })
    }
  }

  deleteMission(id : number){
    this.missionService.deleteMission(id.toString()).subscribe({
      next: () => {
        this. goBack();

      },
      error: (error) => {
        console.error(error);
        this.router.navigate(["/404"]);
      }
    })
  }

  handleMissionUpdate(updatedMission: Mission){
    this.fetchData();
    this.editionMode = false;
  }

  getStatusLabel(status: string): string {
    const upperCaseStatus = status.toUpperCase() as keyof typeof StatusEnum;
    return this.statusEnum[upperCaseStatus] || status;
  }
  getTransportLabel(transport: string): string {
    const upperCaseStatus = transport.toUpperCase() as keyof typeof TransportEnum;
    return this.transportEnum[upperCaseStatus] || transport;
  }

  calculateTotalHT(): number {
    return this.expense.expenseLines.reduce((total: number, line: any) => total + line.amount, 0);
  }
  
  calculateTotalTTC(): number {
    return this.expense.expenseLines.reduce((total: number, line: any) => {
      const tvaAmount = line.amount * (line.tva / 100);
      return total + line.amount + tvaAmount;
    }, 0);
  }
  displayConfirmDialog(){
    this.dialogData.title = this.mission ? `Supprimer la mission #${this.mission.id}` : this.dialogData.title;
    this.showConfirmDialog = true;
  }

  /**
   * Method to navigate back to the previous location.
   */
   goBack() : void{
    this._location.back();
  }
  
}

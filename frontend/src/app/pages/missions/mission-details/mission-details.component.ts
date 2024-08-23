import { Component } from '@angular/core';
import { Mission } from '../../../models/Mission';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { MissionService } from '../../../services/mission/mission.service';
import { CommonModule, CurrencyPipe, DatePipe, Location } from '@angular/common';
import { ExpenseService } from '../../../services/expense/expense.service';
import { StatusEnum } from '../../../enums/StatusEnum';
import { TransportEnum } from '../../../enums/TransportEnum';
import { MissionFormComponent } from '../mission-form/mission-form.component';

@Component({
  selector: 'app-mission-details',
  standalone: true,
  imports: [CommonModule,RouterLink, RouterOutlet,CurrencyPipe, DatePipe, MissionFormComponent],
  templateUrl: './mission-details.component.html',
  styleUrl: './mission-details.component.scss'
})
export class MissionDetailsComponent {

  mission?: Mission;
  updatedMission?: Mission;
  expense: any = {};
  statusEnum = StatusEnum;
  transportEnum = TransportEnum;
  editionMode = true;

  constructor(private route: ActivatedRoute, public router: Router, private missionService : MissionService,private expenseService: ExpenseService, private _location: Location){}

  ngOnInit() : void{
    this.route.paramMap.subscribe(params => {
      const id: string | null = params.get('id');
      if(id){
        this.missionService.getMissionById(id).subscribe({
          next: (mission) => {
            this.mission = mission;
            this.updatedMission = {...mission};

            if(mission.expenseId){
              this.expenseService.getExpenseById(mission.expenseId.toString(), undefined).subscribe({
                next: (exp) => {
                  this.expense = exp;
                  console.log("exp", exp);
                }
              }) 
            }
          },
          error: (error) => {
            console.error(error);
            this.router.navigate(["/404"]);
          }
        })

      }

    })
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
   /**
   * Method to navigate back to the previous location.
   */
   goBack() : void{
    this._location.back();
  }
  
//TODO:
//Create component for expense
}

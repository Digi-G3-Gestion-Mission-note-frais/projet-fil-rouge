import { Component } from '@angular/core';
import { RouterLink, RouterOutlet, RouterModule, Router, ActivatedRoute } from '@angular/router';
import { StatCardComponent } from '../../components/stat-card/stat-card.component';
import { CommonModule, CurrencyPipe, Location } from '@angular/common';
import { TableComponent } from '../../components/table/table.component';
import { Mission } from '../../models/Mission';
import { MissionService } from '../../services/mission/mission.service';
import { StatusEnum } from '../../enums/StatusEnum';
import { PageEvent } from '@angular/material/paginator';
import { ExpenseService } from '../../services/expense/expense.service';
import { Expense } from '../../models/Expense';
import { ConfirmDialogComponent } from '../../components/confirm-dialog/confirm-dialog.component';
import { LayoutComponent } from '../../layout/layout.component';

type HeaderConfigType = {
  label: string;
  value: keyof Mission;
  displayCurrency?: boolean;
  isChip?: boolean;
};

type Sort = {
  empty: boolean;
  unsorted: boolean;
  sorted: boolean;
}

type Pageable = {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  unpaged: boolean;
  paged: boolean;
}

type ExpensesData =  {
  totalPages: number;
  totalElements: number;
  size: number;
  content: Expense[];
  number: number;
  sort: Sort;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  pageable: Pageable;
  empty: boolean;
};

type responseData =  {
  totalPages: number;
  totalElements: number;
  size: number;
  content: Mission[];
  number: number;
  sort: Sort;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  pageable: Pageable;
  empty: boolean;
};

type BountiesReport = {
  totalNumberOfBounties: number,
  highestBountyAmount: number,
  totalAmountOfBounties: number,
  totalBountiesPerMonth: { string : number},
  missionsWithBounties: Mission[],  
}
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [StatCardComponent, CommonModule, TableComponent, RouterLink, RouterOutlet, ConfirmDialogComponent, LayoutComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  
  headers_missions_collaborators : HeaderConfigType[] = [
    { label: 'Dates ', value: 'startDate' },
    { label: 'Libelle', value: 'label' },
    { label: 'Nature', value: 'labelNatureMission' },
    { label: 'Villes', value: 'departureCity' },
    { label: 'Transport', value: 'transport' },
    { label: 'Statut', value: 'status', isChip: true },
    { label: 'Montant prime', value: 'bountyAmount', displayCurrency: true},
  ];

  headers_expenses : HeaderConfigType[] = [
    { label: 'Dates ', value: 'startDate' },
    { label: 'Libelle', value: 'label' },
    { label: 'Nature', value: 'labelNatureMission' },
    { label: 'Villes', value: 'departureCity' },
    { label: 'Transport', value: 'transport' },
    { label: 'Frais', value: 'expense' },
  ];

  headers_bounties: HeaderConfigType[] = [
    { label: 'Nature mission', value: 'labelNatureMission' },
    { label: 'Libelle mission', value: 'label' },
    { label: 'Montant prime', value: 'bountyAmount', displayCurrency: true },
    { label: 'Dates missions', value: 'startDate' },
  ];
 
  loading = true;
  responseData?: responseData;
  missions : Mission[] = [];
  missionsWithExpenses: Mission[] = [];
  expensesData? : ExpensesData;
  bountiesData? : BountiesReport;
  bounties : Mission[] = [];
  respMissionsWithExpenseData? : responseData;
  nbPendingMissions : number = 0;
  nbValidatedMissions : number = 0;
  nbMissionsInProgress : number = 0;
  totalBountiesAmountOfYear : number = 0;
  showConfirmDialog = false;
  selectedMission?: Mission;
  dialogData = {
    title: '', 
    message: 'Êtes-vous sûr de vouloir supprimer cette mission ? Cette action est irréversible.',
    confirmButtonText: 'Supprimer',
    cancelButtonText: 'Annuler'
  };
  

  constructor(private route: ActivatedRoute, private router: Router, private missionService : MissionService, private expenseService: ExpenseService, private _location: Location){}

  ngOnInit(): void {
    this.fetchData();
    this.fetchMissionsWithBounties();
    this.loadAllMissionsForStats();
    this.fetchMissionsWithExpenses();
  }

  handlePageEvent(event: PageEvent) {
    this.fetchData(event);
  }
  handleMissionsWithExpenses(event: PageEvent) {
    this.fetchMissionsWithExpenses(event);
  }
  
  fetchData(event : PageEvent | null = null){
    const queries = {
      page : event?.pageIndex || 0,
      size : event?.pageSize || 5,
      searchbar: undefined,
    };
    this.missionService.getMissions(queries).subscribe({
      next: (response : any) => {
        this.responseData = response;
        this.missions = response.content;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.router.navigate(['/404']);
        this.loading = false;
      }
    });
  }

  fetchMissionsWithExpenses(event : PageEvent | null = null){
    const queries = {
      page : event?.pageIndex || 0,
      size : event?.pageSize || 5,
      searchbar: undefined,
      withExpense: true,
    };
    this.missionService.getMissions(queries).subscribe({
      next: (response : any) => {
        // console.log("missions w/ exp: ", response);
        this.respMissionsWithExpenseData = response;
        this.missionsWithExpenses = response.content;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.router.navigate(['/404']);
        this.loading = false;
      }
    });
  }

  fetchMissionsWithBounties() {
    this.missionService.getBounties().subscribe({
      next: (bounties: any) => {
        this.bountiesData = bounties;
        this.bounties = bounties.missionsWithBounties;
      },
      error: (error) => {
        console.error(error);
        this.router.navigate(['/404']);
      }
    });
  }

  loadAllMissionsForStats() {
    const queries = {
      page : 0,
      size : undefined,
      searchbar: undefined,
    };
    this.missionService.getMissions(queries).subscribe({
      next: (response: any) => {
        // this.allMissions = response.content;
        this.calculateStats(response.content);
      },
      error: (error) => {
        console.error(error);
        this.router.navigate(['/404']);
      }
    });
  }

  calculateStats(missions : Mission[]) {
    this.nbPendingMissions = [...missions].filter(m => m.status.toUpperCase() === "WAITING").length;

    this.nbMissionsInProgress = [...missions].filter(m => m.status.toUpperCase() === "IN_PROGRESS").length;
    this.nbValidatedMissions = [...missions].filter(m => m.status.toUpperCase() === "VALIDATED").length;
    this.totalBountiesAmountOfYear = this.bountiesData?.totalAmountOfBounties || 0; 
  }

  deleteMission(){
    if (this.selectedMission) {
      this.missionService.deleteMission(this.selectedMission.id.toString()).subscribe({
        next: () => {
          this.fetchData();
          this.showConfirmDialog = false;

  
        },
        error: (error) => {
          console.error(error);
          this.router.navigate(["/404"]);
        }
      })    
    }
  }

  cancelDialog(){
    this.selectedMission = undefined;
    this.showConfirmDialog = false;
  }

  goBack() : void{
    this._location.back();
  }

  goToBountiesView() : void {
    console.log(this.route)
    this.router.navigate(['missions/bounties'],
      {relativeTo: this.route}
    );
  };

  showDialog(mission: Mission) {
    this.selectedMission = mission;
    this.dialogData = {...this.dialogData, title:`Supprimer la mission #${mission.id}`};
    this.showConfirmDialog = true;
  }  

}

import { CommonModule, Location } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MissionService } from '../../../services/mission/mission.service';
import { StatCardComponent } from '../../../components/stat-card/stat-card.component';
import { Mission } from '../../../models/Mission';
import { BaseChartDirective } from 'ng2-charts';

import {
  Chart,
  ChartOptions,
  BarController,
  ChartData,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  registerables,
} from 'chart.js';
import { TableComponent } from '../../../components/table/table.component';
import { LayoutComponent } from '../../../layout/layout.component';


Chart.register(...registerables);
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

type HeaderConfigType = {
  label: string;
  value: keyof Mission;
  displayCurrency?: boolean;
  isChip?: boolean;
};

type BountiesReport = {
  totalNumberOfBounties: number,
  highestBountyAmount: number,
  totalAmountOfBounties: number,
  totalBountiesPerMonth: { string : number},
  missionsWithBounties: Mission[],  
}

@Component({
  selector: 'app-bounties-view',
  standalone: true,
  imports: [CommonModule, StatCardComponent, BaseChartDirective, TableComponent, LayoutComponent],
  templateUrl: './bounties-view.component.html',
  styleUrl: './bounties-view.component.scss'
})
export class BountiesViewComponent {
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;
  bountiesData? : BountiesReport;
  bounties : Mission[] = [];
  currentYear = new Date().getFullYear();
  barChartOptions:  ChartOptions<'bar'>  = {
    scales: {
      y: {
        beginAtZero: true
      },
      
    },
  };
  barChartLabels: string[] = [];
  
  barChartData: ChartData<'bar'> = {
    labels: [], 
    datasets: [
      {
        data: [], 
        label: 'Total primes (en euros)',
        backgroundColor: '#4d8df0',
        borderColor: '#317df4',
        borderWidth: 1,
      },
    ],
  };

  headers_bounties: HeaderConfigType[] = [
    { label: 'Nature mission', value: 'labelNatureMission' },
    { label: 'Libelle mission', value: 'label' },
    { label: 'Montant prime', value: 'bountyAmount', displayCurrency: true },
    { label: 'Dates missions', value: 'startDate' },
  ];


  

  constructor(private route: ActivatedRoute, private router: Router, private missionService: MissionService, private _location: Location) {}

  ngOnInit() {
    this.fetchData();
  }

  fetchData() {
    this.missionService.getBounties().subscribe({
      next: (bountiesData: any) => {
        this.bountiesData = bountiesData;
        this.bounties = bountiesData.missionsWithBounties;
        this.updateChartData(bountiesData.totalBountiesPerMonth);
      },
      error: (error) => {
        console.error(error);
        this.router.navigate(['/404']);
      }
    });
  }

  updateChartData(totalBountiesPerMonth: { [key: string]: number }) {
    const sortedData = this.sortData(totalBountiesPerMonth);

    this.barChartLabels = Object.keys(sortedData);
    this.barChartData.labels = this.barChartLabels;
    this.barChartData.datasets[0].data = Object.values(sortedData);

    if (this.chart) {
      this.chart.update();
    }
    // console.log(this.barChartData.datasets[0].data);
  }

  sortData(totalBountiesPerMonth: { [key: string]: number }){
    const orderedMonths = [
      'Janvier',
      'Février',
      'Mars',
      'Avril',
      'Mai',
      'Juin',
      'Juillet',
      'Août',
      'Septembre',
      'Octobre',
      'Novembre',
      'Décembre'
    ];

    let orderedBounties : any = {};
    for (let i = 0; i < orderedMonths.length; i++) {
      const month = orderedMonths[i];
      orderedBounties[month.toUpperCase()] = totalBountiesPerMonth[month.toUpperCase()];
    }
    return orderedBounties;
  }

  exportBountiesToExcelFormat() {
    this.missionService.exportBounties().subscribe(response => {
      const blob = new Blob([response.body!], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'bounties.csv';
      a.click();
      window.URL.revokeObjectURL(url); 

      console.log("Export réussi !");
    }, error => {
      console.error('Erreur lors du téléchargement du CSV', error);
    });
  }

  goBack() : void{
    this._location.back();
  }
}

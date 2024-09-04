import { Component, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import {MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { StatusEnum } from '../../enums/StatusEnum';
import { Mission } from '../../models/Mission';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { TransportEnum } from '../../enums/TransportEnum';
import { ActivatedRoute, Router } from '@angular/router';
import { Expense } from '../../models/Expense';

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

type data =  {
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
@Component({
  selector: 'app-table',
  standalone: true,
  imports: [
    CommonModule, 
    MatTableModule,
    MatPaginatorModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss'
})
export class TableComponent {
  @Input() headers: HeaderConfigType[] = [];
  @Input() data: Mission[] = [];
  @Input() totalElements: number = 0; 
  @Input() showPagination: boolean = true; 
  @Input() pageIndex: number = 0; 
  @Input() pageSize: number = 5; 

  @Output() delete = new EventEmitter<Mission>();
  @Output() pageChange = new EventEmitter<PageEvent>(); 
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  dataSource = new MatTableDataSource<Mission>();

  statusEnum = StatusEnum;
  transportEnum = TransportEnum;
  hidePageSize = false;
  showPageSizeOptions = true;
  showFirstLastButtons = false;
  disabled = false;
  pageSizeOptions = [5, 10, 25];

  constructor(private route: ActivatedRoute, private router: Router){}
  
  ngAfterViewInit() {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
      this.paginator.page.subscribe((event: PageEvent) => {
        this.pageChange.emit(event);
      });
    }
  }

  
  handlePageEvent(event: PageEvent) {
    this.pageChange.emit(event);
  }
  
  navigateToMission(id: number) {
    this.router.navigate([`missions/${id}`],{relativeTo: this.route.parent});
  }

  getStatusLabel(status: string): string {
    const upperCaseStatus = status.toUpperCase() as keyof typeof StatusEnum;
    return this.statusEnum[upperCaseStatus] || status;
  }
  getTransportLabel(transport: string): string {
    const upperCaseStatus = transport.toUpperCase() as keyof typeof TransportEnum;
    return this.transportEnum[upperCaseStatus] || transport;
  }

  getTotalExpense(exp: Expense){
    return exp.expenseLines?.map(e => e.amount).reduce((a,b) => a + b, 0);
  }

  deleteMission(mission : Mission) {
    this.delete.emit(mission);
  }
  
}

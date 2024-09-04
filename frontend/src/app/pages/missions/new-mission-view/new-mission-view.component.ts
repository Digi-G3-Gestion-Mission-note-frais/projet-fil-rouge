import { Component } from '@angular/core';
import { MissionFormComponent } from '../mission-form/mission-form.component';
import { CommonModule, Location } from '@angular/common';
import { ConfirmDialogComponent } from '../../../components/confirm-dialog/confirm-dialog.component';
import { LayoutComponent } from '../../../layout/layout.component';
@Component({
  selector: 'app-new-mission-view',
  standalone: true,
  imports: [CommonModule, MissionFormComponent, ConfirmDialogComponent, LayoutComponent],
  templateUrl: './new-mission-view.component.html',
  styleUrl: './new-mission-view.component.scss'
})
export class NewMissionViewComponent {
  showConfirmDialog = false;
  dialogData = {
    title: '', 
    message: 'Êtes-vous sûr de vouloir annuler vos changements ? Cette action est irréversible.',
    confirmButtonText: 'Confirmer',
    cancelButtonText: 'Annuler'
  };
  constructor(private _location: Location){};


  showDialog() {
    this.dialogData = {...this.dialogData, title:`Annuler les changements`};
    this.showConfirmDialog = true;
  } 
  cancelDialog(){
    this.showConfirmDialog = false;
  }

  cancelChanges(){
    this.showConfirmDialog = false;
    this.goBack();
  }
  goBack() : void{
    this._location.back();
  }
}

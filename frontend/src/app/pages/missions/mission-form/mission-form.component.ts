import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { Mission } from '../../../models/Mission';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { TransportEnum } from '../../../enums/TransportEnum';
import { MissionService } from '../../../services/mission/mission.service';
import { NatureMission } from '../../../models/NatureMission';
import { NatureMissionService } from '../../../services/expense/nature-mission/nature-mission.service';
import { ConfirmDialogComponent } from '../../../components/confirm-dialog/confirm-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { MatNativeDateModule, MAT_DATE_FORMATS, DateAdapter, MAT_DATE_LOCALE, NativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { LayoutComponent } from '../../../layout/layout.component';


export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY',
  },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
@Component({
  selector: 'app-mission-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ConfirmDialogComponent,
    LayoutComponent
  ],
  providers: [
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE] },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS }
  ],
  templateUrl: './mission-form.component.html',
  styleUrls: ['./mission-form.component.scss']
})
export class MissionFormComponent {
  @Input() mission?: Mission;
  missionForm!: FormGroup;
  initialMissionData?: Mission;
  @Output() closeForm = new EventEmitter<void>();
  @Output() closeFormAndSave = new EventEmitter<Mission>();
  dialogData = {
    title: '', 
    message: 'Êtes-vous sûr de vouloir annuler vos changements ? Cette action est irréversible.',
    confirmButtonText: 'Confirmer',
    cancelButtonText: 'Annuler'
  };
  showConfirmDialog = false;

  cities = ['Paris', 'Lyon', 'Marseille', 'Nice', 'Toulouse']; //TODO: handle it dynamically ? Where ?
  transportModes = Object.keys(TransportEnum).map(key => ({
    key: key as keyof typeof TransportEnum,
    label: TransportEnum[key as keyof typeof TransportEnum]
  }));
  natureMissions : NatureMission[] = [];
  formSubmitted = false;

  constructor(private fb: FormBuilder, private missionService: MissionService, private natureMissionService: NatureMissionService, private _location: Location) {}

  ngOnInit(): void {
    this.initializeForm();
    this.getNatureMissions();

    this.missionForm.get('natureMissionId')?.valueChanges.subscribe(natureMissionId => {
      const selectedNature = this.natureMissions.find(n => n.id === natureMissionId);
      if(selectedNature){
        this.missionForm.patchValue({labelNatureMission: selectedNature.label});
      }
    });
  }
  get isStartDateValid(): boolean {
    const startDate = this.missionForm.get('startDate')?.value;
    const today = new Date();
    today.setHours(0, 0, 0, 0); 
    return startDate && new Date(startDate) > today;
  }

  get isTransportConditionValid(): boolean {
    const transport = this.missionForm.get('transport')?.value;
    const startDate = this.missionForm.get('startDate')?.value;
    const today = new Date();
    const requiredDate = new Date();
    requiredDate.setDate(today.getDate() + 7);

    if (transport === 'AIRPLANE') {
      return startDate && new Date(startDate) >= requiredDate;
    }
    return true; 
  }

  get isEndDateValid(): boolean {
    const startDate = this.missionForm.get('startDate')?.value;
    const endDate = this.missionForm.get('endDate')?.value;
    return endDate && startDate && new Date(endDate) >= new Date(startDate);
  }

  get areFormAndConditionsListValid() : boolean {
    const isFormValid = this.missionForm && this.missionForm.valid;
    const areConditionsListValid = [this.isStartDateValid, this.isEndDateValid, this.isTransportConditionValid].every(condition => condition);

    return isFormValid && areConditionsListValid;
  }

  formHasChanges(): boolean {
    return JSON.stringify(this.missionForm.value) !== JSON.stringify(this.initialMissionData);
  }

  getNatureMissions(){
    this.natureMissionService.getNatureMissions().subscribe({
      next : (nm: NatureMission[]) => {
        this.natureMissions = nm;
      },
      error: (error) => {
        console.error(error);
      }
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['mission'] && this.missionForm) {
      this.missionForm.patchValue(this.mission || {});
    }
  }

  private initializeForm(): void {
    this.missionForm = this.fb.group({
      ...(this.mission && {id: this.mission.id}),
      label: [this.mission?.label || '', [Validators.required, Validators.maxLength(150)]],
      totalPrice: [this.mission?.totalPrice || 10, Validators.required],
      status: [this.mission?.status || 'INITIAL', Validators.required],
      startDate: [this.mission?.startDate || undefined, Validators.required],
      endDate: [this.mission?.endDate || undefined, Validators.required],
      transport: [this.mission?.transport || '', Validators.required],
      departureCity: [this.mission?.departureCity || '', Validators.required],
      arrivalCity: [this.mission?.arrivalCity || '', Validators.required],
      labelNatureMission: [this.mission?.labelNatureMission || '', Validators.required],
      userId: [this.mission?.userId || 6, Validators.required], //TODO: CRITICAL : get userId from global store
      natureMissionId: [this.mission?.natureMissionId || undefined, Validators.required],
      expense: [this.mission?.expense || null],
      bountyAmount: this.mission?.bountyAmount || 0,
    });

    if (this.mission) {
      this.missionForm.patchValue(this.mission);
    } 
  }

  submit(): void {
    if (this.areFormAndConditionsListValid) {
      const missionData = this.missionForm.value;
      
      if(this.mission) {
        this.missionService.updateMission(missionData).subscribe(
          (m: Mission) => {
            console.log("mission saved", missionData);
            this.closeFormAndSave.emit(missionData);
          }
        ),
        (error: any) => {
          console.log("error on edition: ", this.missionForm.value);
          console.error(error);
        }
      } else {
        this.missionService.addMission(this.missionForm.value).subscribe(
          (m: Mission) => {
            this.goBack();
          }
        ),
        (error: any) => {
          console.log("error on crea: ", this.missionForm.value);
          
          console.error(error);
        }
      }
    } else {
      console.log("form not valid: ", this.missionForm);
    }

  }

  cancel(): void {
    if (this.mission && !this.formHasChanges()) {
      this.missionForm.patchValue(this.mission);
      this.closeForm.emit();
    } else if (this.mission && this.formHasChanges()) {
      this.showDialog();
    } else {
      this.initializeForm();
      this.closeForm.emit();
    }
  }

  showDialog() {
    this.dialogData = {...this.dialogData, title:`Annuler les changements`};
    this.showConfirmDialog = true;
  } 
  cancelDialog(){
    this.showConfirmDialog = false;
  }

  cancelChanges(){
    if (this.mission && !this.formHasChanges()) {
      this.missionForm.patchValue(this.mission);
      this.closeForm.emit();
    } else {
      this.initializeForm();
      this.closeForm.emit();
    }
  }

  goBack() : void{
    this._location.back();
  }
}

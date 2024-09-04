import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminMissionListComponent } from './admin-mission-list.component';

describe('AdminMissionListComponent', () => {
  let component: AdminMissionListComponent;
  let fixture: ComponentFixture<AdminMissionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminMissionListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminMissionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

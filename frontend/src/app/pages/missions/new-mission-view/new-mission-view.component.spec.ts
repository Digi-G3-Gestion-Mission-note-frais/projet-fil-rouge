import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewMissionViewComponent } from './new-mission-view.component';

describe('NewMissionViewComponent', () => {
  let component: NewMissionViewComponent;
  let fixture: ComponentFixture<NewMissionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewMissionViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewMissionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

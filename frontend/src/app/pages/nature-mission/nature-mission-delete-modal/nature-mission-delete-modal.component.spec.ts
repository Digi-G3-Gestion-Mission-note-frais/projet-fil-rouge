import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NatureMissionDeleteModalComponent } from './nature-mission-delete-modal.component';

describe('NatureMissionDeleteModalComponent', () => {
  let component: NatureMissionDeleteModalComponent;
  let fixture: ComponentFixture<NatureMissionDeleteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NatureMissionDeleteModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NatureMissionDeleteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

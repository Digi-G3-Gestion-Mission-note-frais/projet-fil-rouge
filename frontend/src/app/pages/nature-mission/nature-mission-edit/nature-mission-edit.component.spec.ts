import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NatureMissionEditComponent } from './nature-mission-edit.component';

describe('NatureMissionEditComponent', () => {
  let component: NatureMissionEditComponent;
  let fixture: ComponentFixture<NatureMissionEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NatureMissionEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NatureMissionEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

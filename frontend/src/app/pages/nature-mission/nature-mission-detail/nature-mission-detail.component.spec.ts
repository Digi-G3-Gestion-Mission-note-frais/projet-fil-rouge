import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NatureMissionDetailComponent } from './nature-mission-detail.component';

describe('NatureMissionDetailComponent', () => {
  let component: NatureMissionDetailComponent;
  let fixture: ComponentFixture<NatureMissionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NatureMissionDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NatureMissionDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

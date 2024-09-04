import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NatureMissionToastComponent } from './nature-mission-toast.component';

describe('NatureMissionToastComponent', () => {
  let component: NatureMissionToastComponent;
  let fixture: ComponentFixture<NatureMissionToastComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NatureMissionToastComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NatureMissionToastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

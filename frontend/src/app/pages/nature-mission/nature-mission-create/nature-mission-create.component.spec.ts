import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NatureMissionCreateComponent } from './nature-mission-create.component';

describe('NatureMissionCreateComponent', () => {
  let component: NatureMissionCreateComponent;
  let fixture: ComponentFixture<NatureMissionCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NatureMissionCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NatureMissionCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

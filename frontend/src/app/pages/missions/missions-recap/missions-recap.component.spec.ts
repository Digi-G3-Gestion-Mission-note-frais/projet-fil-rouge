import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MissionsRecapComponent } from './missions-recap.component';

describe('MissionsRecapComponent', () => {
  let component: MissionsRecapComponent;
  let fixture: ComponentFixture<MissionsRecapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionsRecapComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MissionsRecapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

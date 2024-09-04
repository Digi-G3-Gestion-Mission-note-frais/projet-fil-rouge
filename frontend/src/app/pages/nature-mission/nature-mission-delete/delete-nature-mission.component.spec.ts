import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteNatureMissionComponent } from './delete-nature-mission.component';

describe('DeleteNatureMissionComponent', () => {
  let component: DeleteNatureMissionComponent;
  let fixture: ComponentFixture<DeleteNatureMissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteNatureMissionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteNatureMissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

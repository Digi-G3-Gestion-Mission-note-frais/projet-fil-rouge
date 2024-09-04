import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BountiesViewComponent } from './bounties-view.component';

describe('BountiesViewComponent', () => {
  let component: BountiesViewComponent;
  let fixture: ComponentFixture<BountiesViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BountiesViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BountiesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

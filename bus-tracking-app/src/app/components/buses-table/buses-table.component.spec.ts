import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusesTableComponent } from './buses-table.component';

describe('BusesTableComponent', () => {
  let component: BusesTableComponent;
  let fixture: ComponentFixture<BusesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusesTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

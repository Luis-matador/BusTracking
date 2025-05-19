import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RutasTableComponent } from './rutas-table.component';

describe('RutasTableComponent', () => {
  let component: RutasTableComponent;
  let fixture: ComponentFixture<RutasTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RutasTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RutasTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

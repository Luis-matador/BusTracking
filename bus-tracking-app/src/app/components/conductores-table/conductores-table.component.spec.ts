import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConductoresTableComponent } from './conductores-table.component';

describe('ConductoresTableComponent', () => {
  let component: ConductoresTableComponent;
  let fixture: ComponentFixture<ConductoresTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConductoresTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConductoresTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

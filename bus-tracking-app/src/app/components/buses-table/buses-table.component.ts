import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Bus } from '../../interfaces/bus.interface';

@Component({
  selector: 'app-buses-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './buses-table.component.html',
  styleUrls: ['./buses-table.component.scss']
})
export class BusesTableComponent implements OnInit {
  buses: Bus[] = [];
  loading = true;
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.cargarBuses();
  }

  cargarBuses(): void {
    this.apiService.getBuses().subscribe({
      next: (data) => {
        this.buses = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los buses';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Conductor } from '../../interfaces/conductor.interface';

@Component({
  selector: 'app-conductores-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './conductores-table.component.html',
  styleUrls: ['./conductores-table.component.scss']
})
export class ConductoresTableComponent implements OnInit {
  conductores: Conductor[] = [];
  loading = true;
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.cargarConductores();
  }

  cargarConductores(): void {
    this.apiService.getConductores().subscribe({
      next: (data) => {
        this.conductores = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los conductores';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
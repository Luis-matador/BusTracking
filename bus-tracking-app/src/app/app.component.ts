import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from './map/map.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MapComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mapa-sevilla';
}
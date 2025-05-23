import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapComponent } from './map/map.component';
import { RutasTableComponent } from './components/rutas-table/rutas-table.component';
import { BusesTableComponent } from './components/buses-table/buses-table.component';
import { ConductoresTableComponent } from './components/conductores-table/conductores-table.component';
import { ListaUsuariosComponent } from './components/lista-usuarios/lista-usuarios.component';
import { WebsocketService } from './services/websocket.service';
import { Subscription } from 'rxjs';
import { Usuario } from './interfaces/usuario.interface';
import { ApiService } from './services/api.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MapComponent, 
    RutasTableComponent, BusesTableComponent, 
    ConductoresTableComponent, ListaUsuariosComponent, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mapa-sevilla';
  menuVisible = false;
  vistaActual: string | null = null;
  currentUser: Usuario | null = null;
  userMenuVisible = false;
  showLogin = false;
  showRegister = false;

  loginForm = {
    email: '',
    password: ''
  };

  registerForm = {
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    requestAdmin: false,
    adminPassword: ''
  };

  private wsSubscription: Subscription | null = null;

  constructor(private websocketService: WebsocketService, private apiService:ApiService) {}

  ngOnInit() {
    // Iniciar conexión WebSocket al cargar la aplicación
    this.websocketService.connect();
    
    // Supervisar el estado de la conexión
    this.wsSubscription = this.websocketService.isConnected().subscribe(
      connected => {
        if (!connected) {
          console.log('Reconectando WebSocket desde AppComponent...');
          this.websocketService.connect();
        }
      }
    );
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

  toggleMenu() {
    this.menuVisible = !this.menuVisible;
  }

  toggleUserMenu() {
    this.userMenuVisible = !this.userMenuVisible;
  }

  showLoginModal() {
    this.showLogin = true;
    this.userMenuVisible = false;
  }

  showRegisterModal() {
    this.showRegister = true;
    this.userMenuVisible = false;
  }

  closeModal() {
    this.showLogin = false;
    this.showRegister = false;
    this.resetForms();
  }

  resetForms() {
    this.loginForm = { email: '', password: '' };
    this.registerForm = {
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      requestAdmin: false,
      adminPassword: ''
    };
  }

  login() {
  if (!this.loginForm.email || !this.loginForm.password) {
    alert('Por favor complete todos los campos');
    return;
  }

  this.apiService.login(this.loginForm.email, this.loginForm.password).subscribe({
    next: (user) => {
      this.currentUser = user;
      this.closeModal();
      alert('¡Bienvenido ' + user.nombre + '!');
    },
    error: (error) => {
      console.error('Error de login:', error);
      alert(error.message);
    }
  });
}

register() {
  if (!this.registerForm.nombre || !this.registerForm.apellido || 
      !this.registerForm.email || !this.registerForm.password) {
    alert('Por favor complete todos los campos obligatorios');
    return;
  }

  if (this.registerForm.requestAdmin && this.registerForm.adminPassword !== '1234') {
    alert('Contraseña de administrador incorrecta');
    return;
  }

  const newUser: Usuario = {
    nombre: this.registerForm.nombre,
    apellido: this.registerForm.apellido,
    email: this.registerForm.email,
    password: this.registerForm.password,
    admin: this.registerForm.requestAdmin && this.registerForm.adminPassword === '1234',
    id: 0
  };

  this.apiService.register(newUser).subscribe({
    next: (user) => {
      this.currentUser = user;
      this.closeModal();
      alert('¡Registro exitoso! Bienvenido ' + user.nombre);
    },
    error: (error) => {
      console.error('Error de registro:', error);
      alert(error.message);
    }
  });
}
  logout() {
    this.currentUser = null;
    this.userMenuVisible = false;
  }
  volverInicio() {
    this.vistaActual = null;
  }

  mostrarRutas() {
    this.vistaActual = 'rutas';
    this.menuVisible = false;
  }

  mostrarBuses() {
  this.vistaActual = 'buses';
  this.menuVisible = false;
}

  mostrarConductores() {
    this.vistaActual = 'conductores';
    this.menuVisible = false;
  }

  mostrarUsuarios() {
    this.vistaActual = 'usuarios';
    this.menuVisible = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const menuContainer = document.querySelector('.menu-container');
    const menuButton = document.querySelector('.menu-button');
    const userMenu = document.querySelector('.user-menu');
    
    if (!menuContainer?.contains(event.target as Node) && 
        !menuButton?.contains(event.target as Node) &&
        !userMenu?.contains(event.target as Node)) {
      this.menuVisible = false;
      this.userMenuVisible = false;
    }
  }
}
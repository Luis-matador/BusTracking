import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Usuario } from '../../interfaces/usuario.interface';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-usuarios.component.html',
  styleUrl: './lista-usuarios.component.scss'
})
export class ListaUsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  loading = true;
  error = '';
  editingId: number | null = null;
  editingUser: Usuario | null = null;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.apiService.getUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  eliminarUsuario(id: number): void {
  const confirmar = window.confirm('¿Está seguro que desea eliminar este usuario?');
  
  if (confirmar) {
    this.apiService.borrarUsuario(id).subscribe({
      next: () => {
        this.apiService.getUsuarios().subscribe({
          next: (usuarios) => {
            this.usuarios = usuarios;
          },
          error: (error) => {
            this.error = error.message;
          }
        });
      },
    error: (error) => {
      this.error = error.message;
    }
  });
  }
}

startEditing(usuario: Usuario): void {
    this.editingId = usuario.id;
    this.editingUser = { ...usuario };
  }

  cancelEditing(): void {
    this.editingId = null;
    this.editingUser = null;
  }

  saveUser(): void {
    if (!this.editingUser) return;

    this.apiService.actualizarUsuario(this.editingUser.id, this.editingUser).subscribe({
      next: () => {
        this.editingId = null;
        this.editingUser = null;
        this.apiService.getUsuarios().subscribe({
          next: (usuarios) => {
            this.usuarios = usuarios;
          },
          error: (error) => {
            this.error = error.message;
          }
        });
      },
      error: (error) => {
        this.error = error.message;
      }
    });
  }

actualizarUsuario(usuario: Usuario): void {
  this.apiService.actualizarUsuario(usuario.id, usuario).subscribe({
    next: () => {
      this.apiService.getUsuarios().subscribe({
        next: (usuarios) => {
          this.usuarios = usuarios;
        },
        error: (error) => {
          this.error = error.message;
        }
      });
    },
    error: (error) => {
      this.error = error.message;
    }
  });
}
}

export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  admin: boolean;
}

export interface RegistroRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  codigoAdmin?: string;
}

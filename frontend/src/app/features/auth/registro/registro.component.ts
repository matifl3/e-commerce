import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  imports: [
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrl: './../login/auth.component.scss',
})
export class RegistroComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackbar = inject(MatSnackBar);

  protected nombre = '';
  protected apellido = '';
  protected email = '';
  protected password = '';
  protected telefono = '';
  protected direccion = '';
  protected enviando = false;

  async registrar() {
    if (!this.nombre || !this.apellido || !this.email || this.password.length < 6) {
      this.snackbar.open('Completá nombre, apellido, email y una contraseña de al menos 6 caracteres.', 'Cerrar', {
        duration: 5000,
      });
      return;
    }
    this.enviando = true;
    try {
      const respuesta = await firstValueFrom(
        this.auth.registrar({
          nombre: this.nombre,
          apellido: this.apellido,
          email: this.email,
          password: this.password,
          telefono: this.telefono || undefined,
          direccion: this.direccion || undefined,
        }),
      );
      this.auth.aplicarAutenticacion(respuesta);
      this.snackbar.open('Cuenta creada. ¡Bienvenido!', 'Cerrar', { duration: 3000 });
      await this.router.navigate(['/']);
    } catch {
      this.snackbar.open('No se pudo crear la cuenta. Verificá que el email no esté en uso.', 'Cerrar', {
        duration: 5000,
      });
    } finally {
      this.enviando = false;
    }
  }
}
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
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './auth.component.scss',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackbar = inject(MatSnackBar);

  protected email = '';
  protected password = '';
  protected enviando = false;

  async ingresar() {
    if (!this.email || !this.password) return;
    this.enviando = true;
    try {
      const respuesta = await firstValueFrom(this.auth.login({ email: this.email, password: this.password }));
      this.auth.aplicarAutenticacion(respuesta);
      const from = this.router.parseUrl(this.router.url).queryParamMap.get('from');
      await this.router.navigateByUrl(from ?? '/');
    } catch {
      this.snackbar.open('Email o contraseña incorrectos.', 'Cerrar', { duration: 4000 });
    } finally {
      this.enviando = false;
    }
  }
}
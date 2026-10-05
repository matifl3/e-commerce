import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './core/services/auth.service';

@Component({
  imports: [RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule, MatIconModule],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App implements OnInit {
  private readonly auth = inject(AuthService);
  protected readonly nombreUsuario = this.auth.usuario;

  ngOnInit() {
    void this.auth.cargarPerfil();
  }

  esAutenticado() {
    return this.auth.esAutenticado();
  }

  esAdmin() {
    return this.auth.esAdmin();
  }

  logout() {
    this.auth.logout();
  }
}
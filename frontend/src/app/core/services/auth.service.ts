import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuthResponseDto,
  LoginRequestDto,
  RegistroRequestDto,
  UsuarioResponseDto,
} from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'malaquita.jwt';
  private readonly SESION_KEY = 'malaquita.sesion';

  private readonly tokenSig = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));
  private readonly sesionSig = signal<string>(this.initSesion());
  private readonly usuarioSig = signal<UsuarioResponseDto | null>(null);

  readonly token = this.tokenSig.asReadonly();
  readonly sesionId = this.sesionSig.asReadonly();
  readonly usuario = this.usuarioSig.asReadonly();
  readonly esAutenticado = computed(() => this.tokenSig() != null);
  readonly esAdmin = computed(() => this.usuarioSig()?.rol === 'ADMIN');

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
  ) {}

  private initSesion(): string {
    let sesion = localStorage.getItem(this.SESION_KEY);
    if (!sesion) {
      sesion = crypto.randomUUID();
      localStorage.setItem(this.SESION_KEY, sesion);
    }
    return sesion;
  }

  login(credenciales: LoginRequestDto) {
    return this.http.post<AuthResponseDto>(`${environment.apiUrl}/auth/login`, credenciales);
  }

  registrar(datos: RegistroRequestDto) {
    return this.http.post<AuthResponseDto>(`${environment.apiUrl}/auth/registro`, datos);
  }

  me() {
    return this.http.get<UsuarioResponseDto>(`${environment.apiUrl}/usuarios/me`);
  }

  aplicarAutenticacion(respuesta: AuthResponseDto): void {
    localStorage.setItem(this.TOKEN_KEY, respuesta.token);
    this.tokenSig.set(respuesta.token);
    this.usuarioSig.set(respuesta.usuario);
  }

  async cargarPerfil(): Promise<void> {
    if (!this.esAutenticado()) return;
    try {
      this.usuarioSig.set(await firstValueFrom(this.me()));
    } catch {
      this.logout();
    }
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.tokenSig.set(null);
    this.usuarioSig.set(null);
    void this.router.navigate(['/login']);
  }
}
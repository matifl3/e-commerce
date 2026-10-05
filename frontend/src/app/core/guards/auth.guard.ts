import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = async () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.esAutenticado()) return router.createUrlTree(['/login']);
  if (auth.usuario() == null) {
    await auth.cargarPerfil();
  }
  return auth.esAutenticado() ? true : router.createUrlTree(['/login']);
};

export const adminGuard: CanActivateFn = async () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.esAutenticado()) return router.createUrlTree(['/login']);
  if (auth.usuario() == null) {
    await auth.cargarPerfil();
  }
  return auth.esAdmin() ? true : router.createUrlTree(['/']);
};
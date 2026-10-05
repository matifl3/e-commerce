import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token();
  const sesion = auth.sesionId();

  let request = req;
  if (token) {
    request = request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  if (sesion) {
    request = request.clone({ setHeaders: { 'X-Sesion-Id': sesion } });
  }

  return next(request).pipe(
    catchError((err) => {
      if (err.status === 401 && auth.esAutenticado()) {
        auth.logout();
        void router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
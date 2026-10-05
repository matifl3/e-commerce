import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CarritoItemRequestDto, CarritoResponseDto } from '../models';

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private readonly base = `${environment.apiUrl}/carritos`;

  constructor(private readonly http: HttpClient) {}

  activo() {
    return this.http.get<CarritoResponseDto>(`${this.base}/activo`);
  }

  agregarItem(carritoId: number, request: CarritoItemRequestDto) {
    return this.http.post<CarritoResponseDto>(`${this.base}/${carritoId}/items`, request);
  }

  actualizarCantidad(carritoId: number, itemId: number, cantidad: number) {
    return this.http.put<CarritoResponseDto>(
      `${this.base}/${carritoId}/items/${itemId}`,
      null,
      { params: { cantidad } },
    );
  }

  quitarItem(carritoId: number, itemId: number) {
    return this.http.delete<CarritoResponseDto>(`${this.base}/${carritoId}/items/${itemId}`);
  }
}
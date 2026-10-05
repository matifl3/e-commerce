import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  ColorDto,
  ProductoRequestDto,
  ProductoResponseDto,
  ProductoVariacionDto,
  TalleDto,
  VariacionRequestDto,
} from '../models';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly base = `${environment.apiUrl}/productos`;

  constructor(private readonly http: HttpClient) {}

  listar(activos = true) {
    return this.http.get<ProductoResponseDto[]>(this.base, { params: { activos } });
  }

  obtener(id: number) {
    return this.http.get<ProductoResponseDto>(`${this.base}/${id}`);
  }

  crear(request: ProductoRequestDto) {
    return this.http.post<ProductoResponseDto>(this.base, request);
  }

  actualizar(id: number, request: ProductoRequestDto) {
    return this.http.put<ProductoResponseDto>(`${this.base}/${id}`, request);
  }

  desactivar(id: number) {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  registrarVisita(id: number) {
    return this.http.post<void>(`${this.base}/${id}/visitas`, null);
  }

  listarColores() {
    return this.http.get<ColorDto[]>(`${this.base}/colores`);
  }

  crearColor(request: ColorDto) {
    return this.http.post<ColorDto>(`${this.base}/colores`, request);
  }

  listarTalles() {
    return this.http.get<TalleDto[]>(`${this.base}/talles`);
  }

  crearTalle(request: TalleDto) {
    return this.http.post<TalleDto>(`${this.base}/talles`, request);
  }

  crearVariacion(productoId: number, request: VariacionRequestDto) {
    return this.http.post<ProductoVariacionDto>(`${this.base}/${productoId}/variaciones`, request);
  }

  actualizarStock(variacionId: number, stock: number) {
    return this.http.put<ProductoVariacionDto>(`${this.base}/variaciones/${variacionId}/stock`, null, {
      params: { stock },
    });
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  ConversionDto,
  ProductoMasVistoDto,
  ProductoResponseDto,
  StockBajoDto,
  TopProductoDto,
  TotalVentasDto,
  UsuariosNuevosPorMesDto,
} from '../models';

@Injectable({ providedIn: 'root' })
export class EstadisticaService {
  private readonly base = `${environment.apiUrl}/estadisticas`;

  constructor(private readonly http: HttpClient) {}

  vendido() {
    return this.http.get<TotalVentasDto>(`${this.base}/ventas`);
  }

  topProductos() {
    return this.http.get<TopProductoDto[]>(`${this.base}/top-productos`);
  }

  masVistos() {
    return this.http.get<ProductoMasVistoDto[]>(`${this.base}/mas-vistos`);
  }

  stockBajo() {
    return this.http.get<StockBajoDto[]>(`${this.base}/stock-bajo`);
  }

  inventario() {
    return this.http.get<ProductoResponseDto[]>(`${this.base}/inventario`);
  }

  usuariosRegistrados() {
    return this.http.get<number>(`${this.base}/usuarios-registrados`);
  }

  nuevosPorMes() {
    return this.http.get<UsuariosNuevosPorMesDto[]>(`${this.base}/nuevos-por-mes`);
  }

  carritosAbandonados() {
    return this.http.get<number>(`${this.base}/carritos-abandonados`);
  }

  conversion() {
    return this.http.get<ConversionDto>(`${this.base}/conversion`);
  }
}
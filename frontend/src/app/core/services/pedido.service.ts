import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { EstadoPedido, PedidoRequestDto, PedidoResponseDto } from '../models';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly base = `${environment.apiUrl}/pedidos`;

  constructor(private readonly http: HttpClient) {}

  crear(request: PedidoRequestDto) {
    return this.http.post<PedidoResponseDto>(this.base, request);
  }

  mios() {
    return this.http.get<PedidoResponseDto[]>(`${this.base}/mios`);
  }

  listarTodos() {
    return this.http.get<PedidoResponseDto[]>(this.base);
  }

  obtener(id: number) {
    return this.http.get<PedidoResponseDto>(`${this.base}/${id}`);
  }

  actualizarEstado(id: number, estado: EstadoPedido) {
    return this.http.put<PedidoResponseDto>(`${this.base}/${id}/estado`, null, { params: { estado } });
  }
}
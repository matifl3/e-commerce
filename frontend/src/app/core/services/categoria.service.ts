import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CategoriaDto } from '../models';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly base = `${environment.apiUrl}/categorias`;

  constructor(private readonly http: HttpClient) {}

  listar() {
    return this.http.get<CategoriaDto[]>(this.base);
  }

  obtener(id: number) {
    return this.http.get<CategoriaDto>(`${this.base}/${id}`);
  }

  crear(request: CategoriaDto) {
    return this.http.post<CategoriaDto>(this.base, request);
  }

  actualizar(id: number, request: CategoriaDto) {
    return this.http.put<CategoriaDto>(`${this.base}/${id}`, request);
  }

  eliminar(id: number) {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
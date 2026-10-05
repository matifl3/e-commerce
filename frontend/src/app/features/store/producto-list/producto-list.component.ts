import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProductoService } from '../../../core/services/producto.service';
import { ProductoResponseDto } from '../../../core/models';
import { formatPeso } from '../../../core/util';

@Component({
  imports: [RouterLink, MatCardModule, MatButtonModule, MatProgressSpinnerModule],
  selector: 'app-producto-list',
  templateUrl: './producto-list.component.html',
  styleUrl: './producto-list.component.scss',
})
export class ProductoListComponent implements OnInit {
  private readonly productoService = inject(ProductoService);
  protected readonly productos = signal<ProductoResponseDto[]>([]);
  protected readonly cargando = signal(true);
  protected readonly formatPeso = formatPeso;

  async ngOnInit() {
    try {
      this.productos.set(await firstValueFrom(this.productoService.listar()));
    } finally {
      this.cargando.set(false);
    }
  }

  imagenPrincipal(p: ProductoResponseDto): string | null {
    const principal = p.imagenes.find((i) => i.esPrincipal) ?? p.imagenes[0];
    return principal?.urlImagen ?? null;
  }

  ocultarImagen(event: Event) {
    (event.target as HTMLElement).style.display = 'none';
  }
}
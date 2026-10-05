import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PedidoService } from '../../../core/services/pedido.service';
import { PedidoResponseDto } from '../../../core/models';
import { formatFecha, formatPeso } from '../../../core/util';

@Component({
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
  ],
  selector: 'app-mis-pedidos',
  templateUrl: './mis-pedidos.component.html',
  styleUrl: './mis-pedidos.component.scss',
})
export class MisPedidosComponent implements OnInit {
  private readonly pedidoService = inject(PedidoService);
  protected readonly pedidos = signal<PedidoResponseDto[]>([]);
  protected readonly cargando = signal(true);
  protected readonly columnas = ['id', 'fecha', 'total', 'estado', 'accion'];
  protected readonly formatPeso = formatPeso;
  protected readonly formatFecha = formatFecha;
  protected readonly mostrarChevron = computed(() => this.pedidos().length > 0);

  async ngOnInit() {
    try {
      this.pedidos.set(await firstValueFrom(this.pedidoService.mios()));
    } finally {
      this.cargando.set(false);
    }
  }
}
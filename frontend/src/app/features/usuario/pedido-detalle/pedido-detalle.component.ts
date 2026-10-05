import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
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
  selector: 'app-pedido-detalle',
  templateUrl: './pedido-detalle.component.html',
  styleUrl: './pedido-detalle.component.scss',
})
export class PedidoDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly pedidoService = inject(PedidoService);
  protected readonly pedido = signal<PedidoResponseDto | null>(null);
  protected readonly cargando = signal(true);
  protected readonly columnas = ['producto', 'variante', 'cantidad', 'precio', 'subtotal'];
  protected readonly formatPeso = formatPeso;
  protected readonly formatFecha = formatFecha;

  async ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    try {
      this.pedido.set(await firstValueFrom(this.pedidoService.obtener(Number(id))));
    } finally {
      this.cargando.set(false);
    }
  }
}
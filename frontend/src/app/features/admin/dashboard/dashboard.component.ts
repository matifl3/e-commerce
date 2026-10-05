import { Component, inject, OnInit, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { EstadisticaService } from '../../../core/services/estadistica.service';
import { PedidoService } from '../../../core/services/pedido.service';
import { ProductoService } from '../../../core/services/producto.service';
import {
  ConversionDto,
  EstadoPedido,
  PedidoResponseDto,
  ProductoMasVistoDto,
  StockBajoDto,
  TopProductoDto,
  TotalVentasDto,
  UsuariosNuevosPorMesDto,
} from '../../../core/models';
import { ESTADOS_PEDIDO, formatFecha, formatPeso } from '../../../core/util';
import { StockDialogComponent } from '../productos/stock-dialog.component';

@Component({
  imports: [
    MatTabsModule,
    MatCardModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly estadisticaService = inject(EstadisticaService);
  private readonly pedidoService = inject(PedidoService);
  private readonly productoService = inject(ProductoService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  protected readonly cargando = signal(true);
  protected readonly vendido = signal<TotalVentasDto>({ periodo: '', montoTotal: 0, cantidadPedidos: 0 });
  protected readonly top = signal<TopProductoDto[]>([]);
  protected readonly vistos = signal<ProductoMasVistoDto[]>([]);
  protected readonly stockBajo = signal<StockBajoDto[]>([]);
  protected readonly nuevos = signal<UsuariosNuevosPorMesDto[]>([]);
  protected readonly usuarios = signal(0);
  protected readonly carritosAbandonados = signal(0);
  protected readonly conversion = signal<ConversionDto>({
    carritosCompletados: 0,
    carritosAbandonados: 0,
    tasaPorcentaje: 0,
  });
  protected readonly pedidos = signal<PedidoResponseDto[]>([]);

  protected readonly formatPeso = formatPeso;
  protected readonly formatFecha = formatFecha;
  protected readonly estados = ESTADOS_PEDIDO;
  protected readonly colPedidos = ['id', 'usuario', 'fecha', 'total', 'estado'];
  protected readonly colTop = ['nombre', 'unidades', 'total'];
  protected readonly colVistos = ['nombre', 'visitas'];
  protected readonly colStock = ['producto', 'variante', 'stock', 'accion'];
  protected readonly colNuevos = ['mes', 'cantidad'];

  async ngOnInit() {
    try {
      const [vendido, top, vistos, stockBajo, nuevos, usuarios, carritos, conversion, pedidos] =
        await Promise.all([
          firstValueFrom(this.estadisticaService.vendido()),
          firstValueFrom(this.estadisticaService.topProductos()),
          firstValueFrom(this.estadisticaService.masVistos()),
          firstValueFrom(this.estadisticaService.stockBajo()),
          firstValueFrom(this.estadisticaService.nuevosPorMes()),
          firstValueFrom(this.estadisticaService.usuariosRegistrados()),
          firstValueFrom(this.estadisticaService.carritosAbandonados()),
          firstValueFrom(this.estadisticaService.conversion()),
          firstValueFrom(this.pedidoService.listarTodos()),
        ]);
      this.vendido.set(vendido);
      this.top.set(top);
      this.vistos.set(vistos);
      this.stockBajo.set(stockBajo);
      this.nuevos.set(nuevos);
      this.usuarios.set(usuarios);
      this.carritosAbandonados.set(carritos);
      this.conversion.set(conversion);
      this.pedidos.set(pedidos);
    } catch {
      this.snackbar.open('No se pudieron cargar las estadísticas.', 'Cerrar', { duration: 4000 });
    } finally {
      this.cargando.set(false);
    }
  }

  async cambiarEstado(p: PedidoResponseDto, estado: string) {
    try {
      const actualizado = await firstValueFrom(
        this.pedidoService.actualizarEstado(p.id, estado as EstadoPedido),
      );
      this.pedidos.update((lista) => lista.map((x) => (x.id === p.id ? actualizado : x)));
      this.snackbar.open(`Pedido #${p.id} actualizado a ${estado}.`, 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se pudo actualizar el estado.', 'Cerrar', { duration: 3000 });
    }
  }

  ajustarStock(item: StockBajoDto) {
    const ref = this.dialog.open(StockDialogComponent, {
      width: '380px',
      data: item,
    });
    ref.afterClosed().subscribe(async (nuevoStock) => {
      if (nuevoStock == null) return;
      try {
        await firstValueFrom(
          this.productoService.actualizarStock(item.variacionId, nuevoStock),
        );
        this.snackbar.open('Stock actualizado.', 'Cerrar', { duration: 3000 });
        this.stockBajo.set(await firstValueFrom(this.estadisticaService.stockBajo()));
      } catch {
        this.snackbar.open('No se pudo actualizar el stock.', 'Cerrar', { duration: 3000 });
      }
    });
  }
}
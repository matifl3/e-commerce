import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CarritoService } from '../../../core/services/carrito.service';
import { PedidoService } from '../../../core/services/pedido.service';
import { AuthService } from '../../../core/services/auth.service';
import { CarritoResponseDto, MetodoPago } from '../../../core/models';
import { formatPeso } from '../../../core/util';

@Component({
  imports: [
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
  ],
  selector: 'app-carrito',
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.scss',
})
export class CarritoComponent implements OnInit {
  private readonly carritoService = inject(CarritoService);
  private readonly pedidoService = inject(PedidoService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackbar = inject(MatSnackBar);

  protected readonly carrito = signal<CarritoResponseDto | null>(null);
  protected readonly cargando = signal(true);
  protected readonly confirmando = signal(false);
  protected readonly esAutenticado = this.auth.esAutenticado;
  protected readonly formatPeso = formatPeso;

  protected direccion = '';
  protected metodoPago: MetodoPago = 'EFECTIVO';
  protected readonly metodos: MetodoPago[] = ['EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'MERCADOPAGO'];

  async ngOnInit() {
    await this.refrescar();
  }

  async refrescar() {
    this.cargando.set(true);
    try {
      this.carrito.set(await firstValueFrom(this.carritoService.activo()));
    } catch {
      this.carrito.set(null);
    } finally {
      this.cargando.set(false);
    }
  }

  async cambiarCantidad(itemId: number, delta: number) {
    const c = this.carrito();
    if (!c) return;
    const item = c.items.find((i) => i.id === itemId);
    if (!item) return;
    const nueva = item.cantidad + delta;
    if (nueva < 1) return;
    try {
      this.carrito.set(await firstValueFrom(this.carritoService.actualizarCantidad(c.id, itemId, nueva)));
    } catch {
      this.snackbar.open('No se pudo actualizar la cantidad.', 'Cerrar', { duration: 3000 });
    }
  }

  async quitar(itemId: number) {
    const c = this.carrito();
    if (!c) return;
    try {
      this.carrito.set(await firstValueFrom(this.carritoService.quitarItem(c.id, itemId)));
      this.snackbar.open('Producto eliminado del carrito.', 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se pudo eliminar el producto.', 'Cerrar', { duration: 3000 });
    }
  }

  async confirmarPedido() {
    const c = this.carrito();
    if (!c || c.items.length === 0) return;
    this.confirmando.set(true);
    try {
      await firstValueFrom(
        this.pedidoService.crear({
          carritoId: c.id,
          direccionEnvio: this.direccion,
          metodoPago: this.metodoPago,
        }),
      );
      this.snackbar.open('Pedido confirmado.', 'Ir a mis pedidos', { duration: 4000 });
      await this.router.navigate(['/pedidos']);
    } catch {
      this.snackbar.open('No se pudo confirmar el pedido.', 'Cerrar', { duration: 4000 });
    } finally {
      this.confirmando.set(false);
    }
  }
}
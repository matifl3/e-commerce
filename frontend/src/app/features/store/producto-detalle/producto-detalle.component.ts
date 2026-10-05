import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductoService } from '../../../core/services/producto.service';
import { CarritoService } from '../../../core/services/carrito.service';
import { ProductoVariacionDto, ProductoResponseDto } from '../../../core/models';
import { formatPeso } from '../../../core/util';

@Component({
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  selector: 'app-producto-detalle',
  templateUrl: './producto-detalle.component.html',
  styleUrl: './producto-detalle.component.scss',
})
export class ProductoDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly productoService = inject(ProductoService);
  private readonly carritoService = inject(CarritoService);
  private readonly snackbar = inject(MatSnackBar);

  protected readonly producto = signal<ProductoResponseDto | null>(null);
  protected readonly cargando = signal(true);
  protected readonly agregando = signal(false);
  protected readonly colorId = signal<number | null>(null);
  protected readonly talleId = signal<number | null>(null);
  protected readonly cantidad = signal(1);
  protected readonly formatPeso = formatPeso;

  protected readonly colores = computed(() => {
    const p = this.producto();
    if (!p) return [] as { id: number; nombre: string }[];
    const map = new Map<number, { id: number; nombre: string }>();
    for (const v of p.variaciones) {
      map.set(v.colorId, { id: v.colorId, nombre: v.colorNombre });
    }
    return [...map.values()];
  });

  protected readonly talles = computed(() => {
    const p = this.producto();
    const color = this.colorId();
    if (!p || color == null) return [] as ProductoVariacionDto[];
    return p.variaciones.filter((v) => v.colorId === color);
  });

  protected readonly variacionSeleccionada = computed(() => {
    const p = this.producto();
    const color = this.colorId();
    const talle = this.talleId();
    if (!p || color == null || talle == null) return null;
    return p.variaciones.find((v) => v.colorId === color && v.talleId === talle) ?? null;
  });

  constructor() {
    this.cantidad.set(1);
  }

  async ngOnInit() {
    this.route.paramMap.subscribe((params) => this.cargar(Number(params.get('id'))));
  }

  async cargar(id: number) {
    this.cargando.set(true);
    this.colorId.set(null);
    this.talleId.set(null);
    this.cantidad.set(1);
    try {
      this.producto.set(await firstValueFrom(this.productoService.obtener(id)));
      firstValueFrom(this.productoService.registrarVisita(id)).catch(() => undefined);
    } catch {
      this.producto.set(null);
    } finally {
      this.cargando.set(false);
    }
  }

  selectColor(id: number) {
    this.colorId.set(id);
    this.talleId.set(null);
    this.cantidad.set(1);
  }

  selectTalle(id: number) {
    this.talleId.set(id);
    this.cantidad.set(1);
  }

  hayStock(v: ProductoVariacionDto): boolean {
    return v.stock > 0;
  }

  incrementar() {
    const v = this.variacionSeleccionada();
    if (v && this.cantidad() < v.stock) this.cantidad.update((c) => c + 1);
  }

  decrementar() {
    this.cantidad.update((c) => Math.max(1, c - 1));
  }

  async agregarAlCarrito() {
    const p = this.producto();
    const v = this.variacionSeleccionada();
    if (!p || !v) return;
    if (v.stock === 0) {
      this.snackbar.open('Este talle no tiene stock.', 'Cerrar', { duration: 3000 });
      return;
    }
    this.agregando.set(true);
    try {
      const carrito = await firstValueFrom(this.carritoService.activo());
      await firstValueFrom(
        this.carritoService.agregarItem(carrito.id, {
          productoVariacionId: v.id,
          cantidad: Math.min(this.cantidad(), v.stock),
        }),
      );
      this.snackbar.open('Producto agregado al carrito.', 'Ver carrito', { duration: 3000 });
    } catch (err) {
      this.snackbar.open('No se pudo agregar el producto.', 'Cerrar', { duration: 3000 });
    } finally {
      this.agregando.set(false);
    }
  }
}
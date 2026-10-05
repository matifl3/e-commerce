import { Component, inject, OnInit, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductoService } from '../../../core/services/producto.service';
import { CategoriaService } from '../../../core/services/categoria.service';
import { CategoriaDto, ColorDto, ProductoResponseDto, TalleDto } from '../../../core/models';
import { formatPeso } from '../../../core/util';
import { ProductoFormData, ProductoFormDialogComponent } from './producto-form-dialog.component';

@Component({
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  selector: 'app-admin-productos',
  templateUrl: './admin-productos.component.html',
  styleUrl: './admin-productos.component.scss',
})
export class AdminProductosComponent implements OnInit {
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackbar = inject(MatSnackBar);

  protected readonly productos = signal<ProductoResponseDto[]>([]);
  protected readonly categorias = signal<CategoriaDto[]>([]);
  protected readonly colores = signal<ColorDto[]>([]);
  protected readonly talles = signal<TalleDto[]>([]);
  protected readonly cargando = signal(true);
  protected readonly columnas = ['id', 'nombre', 'categoria', 'precio', 'stock', 'estado', 'accion'];
  protected readonly formatPeso = formatPeso;

  async ngOnInit() {
    try {
      const [productos, categorias, colores, talles] = await Promise.all([
        firstValueFrom(this.productoService.listar(false)),
        firstValueFrom(this.categoriaService.listar()),
        firstValueFrom(this.productoService.listarColores()),
        firstValueFrom(this.productoService.listarTalles()),
      ]);
      this.productos.set(productos);
      this.categorias.set(categorias);
      this.colores.set(colores);
      this.talles.set(talles);
    } finally {
      this.cargando.set(false);
    }
  }

  stockTotal(p: ProductoResponseDto): number {
    return p.variaciones.reduce((acc, v) => acc + v.stock, 0);
  }

  async refrescar() {
    this.productos.set(await firstValueFrom(this.productoService.listar(false)));
  }

  abrirForm(producto?: ProductoResponseDto) {
    const data: ProductoFormData = {
      producto: producto ?? null,
      categorias: this.categorias(),
      colores: this.colores(),
      talles: this.talles(),
    };
    const ref = this.dialog.open(ProductoFormDialogComponent, {
      width: '720px',
      data,
    });
    ref.afterClosed().subscribe(async (request) => {
      if (!request) return;
      try {
        if (producto) {
          await firstValueFrom(this.productoService.actualizar(producto.id, request));
          this.snackbar.open('Producto actualizado.', 'Cerrar', { duration: 3000 });
        } else {
          await firstValueFrom(this.productoService.crear(request));
          this.snackbar.open('Producto creado.', 'Cerrar', { duration: 3000 });
        }
        await this.refrescar();
      } catch {
        this.snackbar.open('No se pudo guardar el producto.', 'Cerrar', { duration: 4000 });
      }
    });
  }

  async desactivar(p: ProductoResponseDto) {
    try {
      await firstValueFrom(this.productoService.desactivar(p.id));
      this.snackbar.open('Producto desactivado.', 'Cerrar', { duration: 3000 });
      await this.refrescar();
    } catch {
      this.snackbar.open('No se pudo desactivar el producto.', 'Cerrar', { duration: 3000 });
    }
  }
}
import { Component, computed, Inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {
  CategoriaDto,
  ColorDto,
  ProductoRequestDto,
  ProductoResponseDto,
  TalleDto,
} from '../../../core/models';

interface VariacionFila {
  colorId: number | null;
  talleId: number | null;
  stock: number;
  sku: string;
}

interface ImagenFila {
  urlImagen: string;
  esPrincipal: boolean;
}

export interface ProductoFormData {
  producto?: ProductoResponseDto | null;
  categorias: CategoriaDto[];
  colores: ColorDto[];
  talles: TalleDto[];
}

@Component({
  imports: [
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
  ],
  selector: 'app-producto-form-dialog',
  templateUrl: './producto-form-dialog.component.html',
  styleUrl: './producto-form-dialog.component.scss',
})
export class ProductoFormDialogComponent {
  protected readonly nombre = signal('');
  protected readonly categoriaId = signal<number | null>(null);
  protected readonly descripcion = signal('');
  protected readonly precioBase = signal<number | null>(null);
  protected readonly activo = signal(true);
  protected readonly variaciones = signal<VariacionFila[]>([]);
  protected readonly imagenes = signal<ImagenFila[]>([]);

  protected readonly puedeGuardar = computed(() => {
    const nombreOk = this.nombre().trim().length > 0;
    const categoriaOk = this.categoriaId() != null;
    const precioOk = (this.precioBase() ?? 0) > 0;
    const variacionOk = this.variaciones().some((v) => v.colorId != null && v.talleId != null);
    const imagenOk = this.imagenes().some((i) => i.urlImagen.trim().length > 0);
    return nombreOk && categoriaOk && precioOk && variacionOk && imagenOk;
  });

  constructor(
    public readonly dialogRef: MatDialogRef<ProductoFormDialogComponent, ProductoRequestDto | null>,
    @Inject(MAT_DIALOG_DATA) public readonly data: ProductoFormData,
  ) {
    const p = data.producto;
    this.nombre.set(p?.nombre ?? '');
    this.categoriaId.set(p?.categoria.id ?? null);
    this.descripcion.set(p?.descripcion ?? '');
    this.precioBase.set(p == null ? null : Number(p.precioBase));
    this.activo.set(p?.activo ?? true);
    this.variaciones.set(
      (p?.variaciones ?? []).map((v) => ({
        colorId: v.colorId,
        talleId: v.talleId,
        stock: v.stock,
        sku: v.sku,
      })),
    );
    if (p == null) {
      this.variaciones.set([{ colorId: null, talleId: null, stock: 0, sku: '' }]);
      this.imagenes.set([{ urlImagen: '', esPrincipal: true }]);
    } else {
      this.imagenes.set(
        p.imagenes.map((i) => ({ urlImagen: i.urlImagen, esPrincipal: i.esPrincipal })),
      );
    }
  }

  agregarVariacion() {
    this.variaciones.update((v) => [...v, { colorId: null, talleId: null, stock: 0, sku: '' }]);
  }

  quitarVariacion(index: number) {
    this.variaciones.update((v) => v.filter((_, i) => i !== index));
  }

  agregarImagen() {
    this.imagenes.update((i) => [...i, { urlImagen: '', esPrincipal: false }]);
  }

  quitarImagen(index: number) {
    this.imagenes.update((i) => i.filter((_, x) => x !== index));
  }

  guardar() {
    const validas = this.variaciones().filter((v) => v.colorId != null && v.talleId != null);
    const request: ProductoRequestDto = {
      categoriaId: this.categoriaId()!,
      nombre: this.nombre().trim(),
      descripcion: this.descripcion().trim() || undefined,
      precioBase: this.precioBase()!,
      activo: this.activo(),
      variaciones: validas.map((v) => ({
        colorId: v.colorId!,
        talleId: v.talleId!,
        stock: v.stock,
        sku: v.sku.trim() || `${this.nombre().trim()}-${v.colorId}-${v.talleId}`,
      })),
      imagenes: this.imagenes()
        .filter((i) => i.urlImagen.trim().length > 0)
        .map((i) => ({ urlImagen: i.urlImagen.trim(), esPrincipal: i.esPrincipal })),
    };
    this.dialogRef.close(request);
  }
}
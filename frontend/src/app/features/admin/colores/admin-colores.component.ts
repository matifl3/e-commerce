import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { ProductoService } from '../../../core/services/producto.service';
import { ColorDto } from '../../../core/models';

@Component({
  imports: [
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatTableModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  selector: 'app-admin-colores',
  templateUrl: './admin-colores.component.html',
  styleUrl: './admin-colores.component.scss',
})
export class AdminColoresComponent implements OnInit {
  protected readonly colores = signal<ColorDto[]>([]);
  protected readonly nombre = signal('');
  protected readonly hex = signal('#000000');
  protected readonly cargando = signal(true);
  protected readonly columnas = ['id', 'swatch', 'nombre', 'hex'];

  private readonly productoService = inject(ProductoService);
  private readonly snackbar = inject(MatSnackBar);

  async ngOnInit() {
    try {
      this.colores.set(await firstValueFrom(this.productoService.listarColores()));
    } finally {
      this.cargando.set(false);
    }
  }

  async crear() {
    if (!this.nombre().trim()) return;
    try {
      await firstValueFrom(
        this.productoService.crearColor({ nombre: this.nombre().trim(), codigoHex: this.hex() }),
      );
      this.nombre.set('');
      this.colores.set(await firstValueFrom(this.productoService.listarColores()));
      this.snackbar.open('Color creado.', 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se pudo crear el color.', 'Cerrar', { duration: 3000 });
    }
  }
}
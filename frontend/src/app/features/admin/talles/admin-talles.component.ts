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
import { TalleDto } from '../../../core/models';

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
  selector: 'app-admin-talles',
  templateUrl: './admin-talles.component.html',
  styleUrl: './admin-talles.component.scss',
})
export class AdminTallesComponent implements OnInit {
  protected readonly talles = signal<TalleDto[]>([]);
  protected readonly nombre = signal('');
  protected readonly orden = signal(0);
  protected readonly cargando = signal(true);
  protected readonly columnas = ['id', 'nombre', 'orden'];

  private readonly productoService = inject(ProductoService);
  private readonly snackbar = inject(MatSnackBar);

  async ngOnInit() {
    try {
      this.talles.set(await firstValueFrom(this.productoService.listarTalles()));
    } finally {
      this.cargando.set(false);
    }
  }

  async crear() {
    if (!this.nombre().trim()) return;
    try {
      await firstValueFrom(
        this.productoService.crearTalle({ nombre: this.nombre().trim(), orden: this.orden() }),
      );
      this.nombre.set('');
      this.orden.set(0);
      this.talles.set(await firstValueFrom(this.productoService.listarTalles()));
      this.snackbar.open('Talle creado.', 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se pudo crear el talle.', 'Cerrar', { duration: 3000 });
    }
  }
}
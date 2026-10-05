import { Component, Inject, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { CategoriaService } from '../../../core/services/categoria.service';
import { CategoriaDto } from '../../../core/models';

@Component({
  imports: [FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Editar nombre</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full">
        <mat-label>Nombre</mat-label>
        <input matInput [(ngModel)]="nombre" name="nombre" />
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]>Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()">Guardar</button>
    </mat-dialog-actions>
  `,
})
export class EditarNombreDialogComponent {
  protected nombre = signal('');

  constructor(
    public readonly dialogRef: MatDialogRef<EditarNombreDialogComponent, string | null>,
    @Inject(MAT_DIALOG_DATA) nombre: string,
  ) {
    this.nombre.set(nombre);
  }

  guardar() {
    if (this.nombre().trim()) {
      this.dialogRef.close(this.nombre().trim());
    }
  }
}

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
  selector: 'app-admin-categorias',
  templateUrl: './admin-categorias.component.html',
  styleUrl: './admin-categorias.component.scss',
})
export class AdminCategoriasComponent {
  protected readonly categorias = signal<CategoriaDto[]>([]);
  protected readonly nueva = signal('');
  protected readonly cargando = signal(true);
  protected readonly columnas = ['id', 'nombre', 'accion'];

  private readonly categoriaService = inject(CategoriaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackbar = inject(MatSnackBar);

  constructor() {
    this.refrescar().then(() => this.cargando.set(false));
  }

  async refrescar() {
    this.categorias.set(await firstValueFrom(this.categoriaService.listar()));
  }

  async crear() {
    const nombre = this.nueva().trim();
    if (!nombre) return;
    try {
      await firstValueFrom(this.categoriaService.crear({ nombre }));
      this.nueva.set('');
      await this.refrescar();
      this.snackbar.open('Categoría creada.', 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se pudo crear la categoría.', 'Cerrar', { duration: 3000 });
    }
  }

  editar(c: CategoriaDto) {
    const ref = this.dialog.open(EditarNombreDialogComponent, {
      width: '320px',
      data: c.nombre,
    });
    ref.afterClosed().subscribe(async (nombre) => {
      if (nombre == null) return;
      try {
        await firstValueFrom(this.categoriaService.actualizar(c.id!, { id: c.id, nombre }));
        await this.refrescar();
        this.snackbar.open('Categoría actualizada.', 'Cerrar', { duration: 3000 });
      } catch {
        this.snackbar.open('No se pudo actualizar la categoría.', 'Cerrar', { duration: 3000 });
      }
    });
  }

  async eliminar(c: CategoriaDto) {
    try {
      await firstValueFrom(this.categoriaService.eliminar(c.id!));
      await this.refrescar();
      this.snackbar.open('Categoría eliminada.', 'Cerrar', { duration: 3000 });
    } catch {
      this.snackbar.open('No se puede eliminar una categoría con productos.', 'Cerrar', { duration: 4000 });
    }
  }
}
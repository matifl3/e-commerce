import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { StockBajoDto } from '../../../core/models';

@Component({
  imports: [FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  selector: 'app-stock-dialog',
  templateUrl: './stock-dialog.component.html',
})
export class StockDialogComponent {
  protected nuevoStock: number;

  constructor(
    public readonly dialogRef: MatDialogRef<StockDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: StockBajoDto,
  ) {
    this.nuevoStock = data.stock;
  }

  guardar() {
    if (this.nuevoStock >= 0) {
      this.dialogRef.close(this.nuevoStock);
    }
  }
}
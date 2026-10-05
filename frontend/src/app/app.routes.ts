import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./features/store/producto-list/producto-list.component').then((m) => m.ProductoListComponent),
  },
  {
    path: 'productos/:id',
    loadComponent: () =>
      import('./features/store/producto-detalle/producto-detalle.component').then(
        (m) => m.ProductoDetalleComponent,
      ),
  },
  {
    path: 'carrito',
    loadComponent: () =>
      import('./features/store/carrito/carrito.component').then((m) => m.CarritoComponent),
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/auth/registro/registro.component').then((m) => m.RegistroComponent),
  },
  {
    path: 'pedidos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/usuario/mis-pedidos/mis-pedidos.component').then((m) => m.MisPedidosComponent),
  },
  {
    path: 'pedidos/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/usuario/pedido-detalle/pedido-detalle.component').then(
        (m) => m.PedidoDetalleComponent,
      ),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'admin/productos',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/productos/admin-productos.component').then((m) => m.AdminProductosComponent),
  },
  {
    path: 'admin/categorias',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/categorias/admin-categorias.component').then((m) => m.AdminCategoriasComponent),
  },
  {
    path: 'admin/colores',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/colores/admin-colores.component').then((m) => m.AdminColoresComponent),
  },
  {
    path: 'admin/talles',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./features/admin/talles/admin-talles.component').then((m) => m.AdminTallesComponent),
  },
  { path: '**', redirectTo: '' },
];
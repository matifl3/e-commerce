export type RolUsuario = 'USUARIO' | 'ADMIN';
export type EstadoCarrito = 'ACTIVO' | 'COMPLETADO' | 'ABANDONADO';
export type EstadoPedido = 'PENDIENTE' | 'PAGADO' | 'ENVIADO' | 'ENTREGADO' | 'CANCELADO';
export type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'MERCADOPAGO';

export interface UsuarioResponseDto {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  direccion?: string;
  rol: RolUsuario;
  fechaCreacion: string;
}

export interface RegistroRequestDto {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  telefono?: string;
  direccion?: string;
}

export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface AuthResponseDto {
  token: string;
  usuario: UsuarioResponseDto;
}

export interface CategoriaDto {
  id?: number;
  nombre: string;
}

export interface ColorDto {
  id?: number;
  nombre: string;
  codigoHex?: string;
}

export interface TalleDto {
  id?: number;
  nombre: string;
  orden?: number;
}

export interface VariacionRequestDto {
  colorId: number;
  talleId: number;
  stock: number;
  sku: string;
}

export interface ImagenRequestDto {
  colorId?: number | null;
  urlImagen: string;
  orden?: number | null;
  esPrincipal?: boolean | null;
}

export interface ProductoRequestDto {
  categoriaId: number;
  nombre: string;
  descripcion?: string;
  precioBase: number;
  activo?: boolean;
  variaciones: VariacionRequestDto[];
  imagenes: ImagenRequestDto[];
}

export interface ProductoVariacionDto {
  id: number;
  productoId: number;
  colorId: number;
  colorNombre: string;
  talleId: number;
  talleNombre: string;
  stock: number;
  sku: string;
}

export interface ImagenProductoDto {
  id: number;
  productoId: number;
  colorId?: number | null;
  urlImagen: string;
  orden?: number | null;
  esPrincipal: boolean;
}

export interface ProductoResponseDto {
  id: number;
  categoria: CategoriaDto;
  nombre: string;
  descripcion?: string;
  precioBase: number | string;
  activo: boolean;
  variaciones: ProductoVariacionDto[];
  imagenes: ImagenProductoDto[];
}

export interface CarritoItemRequestDto {
  productoVariacionId: number;
  cantidad: number;
}

export interface CarritoItemResponseDto {
  id: number;
  productoVariacionId: number;
  productoNombre: string;
  colorNombre: string;
  talleNombre: string;
  cantidad: number;
  precioUnitario: number | string;
  subtotal: number | string;
}

export interface CarritoResponseDto {
  id: number;
  usuarioId?: number | null;
  sessionId?: string | null;
  estado: EstadoCarrito;
  fechaModificacion: string;
  total: number | string;
  items: CarritoItemResponseDto[];
}

export interface PedidoRequestDto {
  carritoId: number;
  direccionEnvio: string;
  metodoPago: MetodoPago;
}

export interface PedidoDetalleDto {
  id: number;
  productoVariacionId: number;
  productoNombre: string;
  colorNombre: string;
  talleNombre: string;
  cantidad: number;
  precioUnitario: number | string;
  subtotal: number | string;
}

export interface PedidoResponseDto {
  id: number;
  usuarioId: number;
  carritoId?: number | null;
  fechaPedido: string;
  total: number | string;
  estado: EstadoPedido;
  direccionEnvio: string;
  metodoPago: MetodoPago;
  detalles: PedidoDetalleDto[];
}

export interface TotalVentasDto {
  periodo: string;
  montoTotal: number | string;
  cantidadPedidos: number;
}

export interface TopProductoDto {
  productoId: number;
  nombre: string;
  unidadesVendidas: number;
  totalVendido: number | string;
}

export interface ProductoMasVistoDto {
  productoId: number;
  nombre: string;
  visitas: number;
}

export interface StockBajoDto {
  productoId: number;
  nombre: string;
  variacionId: number;
  sku: string;
  colorNombre: string;
  talleNombre: string;
  stock: number;
  umbral: number;
}

export interface UsuariosNuevosPorMesDto {
  mes: string;
  cantidad: number;
}

export interface ConversionDto {
  carritosCompletados: number;
  carritosAbandonados: number;
  tasaPorcentaje: number;
}
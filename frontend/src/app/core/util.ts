export function formatPeso(n: number | string | null | undefined): string {
  if (n == null) return '-';
  const num = typeof n === 'string' ? Number.parseFloat(n) : n;
  if (Number.isNaN(num)) return '-';
  return new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(num);
}

export function formatFecha(fecha: string | Date | null | undefined): string {
  if (!fecha) return '-';
  const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
  if (Number.isNaN(date.getTime())) return '-';
  return new Intl.DateTimeFormat('es-AR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

export const ESTADOS_PEDIDO = ['PENDIENTE', 'PAGADO', 'ENVIADO', 'ENTREGADO', 'CANCELADO'] as const;
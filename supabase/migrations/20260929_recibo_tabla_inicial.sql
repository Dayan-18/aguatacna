-- Copia en la nube de los recibos de cada usuario con cuenta de Google (feature/recibo).
-- La clave es (usuario_id, id) y no el período: así, al mover un recibo de mes se actualiza la misma fila.
-- Reutiliza public.tocar_actualizado_en(), creada en 20260921_reserva_tablas_iniciales.sql.

create table public.recibo (
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  id text not null,
  anio integer not null check (anio between 2000 and 2100),
  mes integer not null check (mes between 1 and 12),
  consumo_m3 integer not null check (consumo_m3 >= 0),
  importe_centimos bigint not null check (importe_centimos >= 0),
  fecha_emision date,
  fecha_vencimiento date,
  tipo_consumo text not null,
  lectura_anterior_m3 integer,
  lectura_actual_m3 integer,
  numero_medidor text,
  numero_recibo text,
  origen text not null,
  actualizado_en timestamptz not null default now(),
  primary key (usuario_id, id)
);

create trigger recibo_actualizado before update on public.recibo
  for each row execute function public.tocar_actualizado_en();

alter table public.recibo enable row level security;

create policy "recibo: cada usuario lo suyo" on public.recibo
  for all to authenticated
  using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);

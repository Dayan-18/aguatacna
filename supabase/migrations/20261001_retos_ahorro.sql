create table public.reto_catalogo (
  id text primary key,
  titulo text not null,
  descripcion text not null,
  litros_meta integer not null check (litros_meta > 0),
  activo boolean not null default true
);

insert into public.reto_catalogo (id, titulo, descripcion, litros_meta) values
  ('carga-completa', 'Lavar ropa solo con carga completa', 'Aprovecha cada ciclo de lavado.', 90),
  ('ducha-corta', 'Duchas de 5 minutos toda la semana', 'Reduce el tiempo bajo la ducha.', 210),
  ('reusar-agua', 'Reutilizar el agua del enjuague', 'Úsala para limpiar pisos.', 60)
on conflict (id) do update set
  titulo = excluded.titulo,
  descripcion = excluded.descripcion,
  litros_meta = excluded.litros_meta;

create table public.reto_usuario (
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  reto_id text not null references public.reto_catalogo(id) on delete cascade,
  fecha date not null,
  cumplido boolean not null,
  actualizado_en timestamptz not null default now(),
  primary key (usuario_id, reto_id, fecha)
);

alter table public.reto_catalogo enable row level security;
alter table public.reto_usuario enable row level security;

create policy "reto_catalogo: lectura pública" on public.reto_catalogo
  for select to anon, authenticated using (true);

create policy "reto_usuario: cada usuario lo suyo" on public.reto_usuario
  for all to authenticated
  using ((select auth.uid()) = usuario_id)
  with check ((select auth.uid()) = usuario_id);

create table public.reporte (
  id text primary key,
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  tipo text not null,
  descripcion text not null,
  latitud double precision,
  longitud double precision,
  tiene_foto boolean not null default false,
  pendiente_sincronizacion boolean not null default false,
  creado_en timestamptz not null default now()
);

alter table public.reporte enable row level security;

create policy "reporte: lectura comunitaria" on public.reporte
  for select to authenticated using (true);
create policy "reporte: insertar propio" on public.reporte
  for insert to authenticated with check ((select auth.uid()) = usuario_id);
create policy "reporte: actualizar propio" on public.reporte
  for update to authenticated
  using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);
create policy "reporte: eliminar propio" on public.reporte
  for delete to authenticated using ((select auth.uid()) = usuario_id);

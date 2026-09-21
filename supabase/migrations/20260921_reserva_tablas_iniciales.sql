-- Tablas en la nube de la vertical reserva (T022). Aplicada al proyecto Supabase "AguaTacna".
-- Espejo de las tablas locales de Room; cada usuario solo ve y escribe lo suyo (RLS).
create table public.perfil_hogar (
  usuario_id uuid primary key default auth.uid() references auth.users(id) on delete cascade,
  tipo_reservorio text not null,
  capacidad_litros double precision not null check (capacidad_litros > 0),
  habitantes integer not null check (habitantes > 0),
  duchas_por_dia integer not null check (duchas_por_dia >= 0),
  usa_lavadora boolean not null,
  riega_jardin boolean not null,
  consumo_por_habitos_litros_hora double precision,
  consumo_vigente_litros_hora double precision,
  actualizado_en timestamptz not null default now()
);

create table public.evento_llenado (
  id uuid primary key,
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  momento timestamp not null,
  tipo text not null,
  actualizado_en timestamptz not null default now()
);
create index evento_llenado_usuario_momento_idx on public.evento_llenado (usuario_id, momento desc);

create table public.novedad_reserva (
  id uuid primary key,
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  momento timestamp not null,
  tipo text not null check (tipo in ('SIN_LLEGADA', 'SIN_AGUA')),
  inicio_observado timestamp,
  litros_observados double precision,
  actualizado_en timestamptz not null default now()
);
create index novedad_reserva_usuario_momento_idx on public.novedad_reserva (usuario_id, momento desc);

create or replace function public.tocar_actualizado_en() returns trigger
language plpgsql set search_path = '' as $$
begin
  new.actualizado_en = now();
  return new;
end $$;

create trigger perfil_hogar_actualizado before update on public.perfil_hogar
  for each row execute function public.tocar_actualizado_en();
create trigger evento_llenado_actualizado before update on public.evento_llenado
  for each row execute function public.tocar_actualizado_en();
create trigger novedad_reserva_actualizado before update on public.novedad_reserva
  for each row execute function public.tocar_actualizado_en();

alter table public.perfil_hogar enable row level security;
alter table public.evento_llenado enable row level security;
alter table public.novedad_reserva enable row level security;

create policy "perfil_hogar: cada usuario lo suyo" on public.perfil_hogar
  for all to authenticated
  using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);
create policy "evento_llenado: cada usuario lo suyo" on public.evento_llenado
  for all to authenticated
  using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);
create policy "novedad_reserva: cada usuario lo suyo" on public.novedad_reserva
  for all to authenticated
  using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);

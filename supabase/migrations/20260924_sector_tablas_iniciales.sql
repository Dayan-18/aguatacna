-- Tablas en la nube de la vertical Sector (AguaTacna). Espejo de las tablas locales de Room.
-- Sectorización, cronogramas y cisternas son datos de referencia compartidos (lectura pública);
-- las confirmaciones son colaborativas y cada usuario solo inserta y ve las suyas (RLS, art. IX).

-- 1. Datos de referencia compartidos ------------------------------------------------
create table public.sector (
  id text primary key,
  nombre text not null,
  distrito text not null,
  latitud double precision not null,
  longitud double precision not null,
  actualizado_en timestamptz not null default now()
);

create table public.cronograma (
  id text primary key,
  sector_id text not null references public.sector(id) on delete cascade,
  fecha date not null,
  hora_inicio time not null,
  hora_fin time not null,
  tipo text not null check (tipo in ('PROGRAMADO', 'EMERGENCIA')),
  fuente text not null check (fuente in ('EPS', 'COLABORATIVA')),
  actualizado_en timestamptz not null default now(),
  check (hora_inicio < hora_fin)
);
create index cronograma_sector_fecha_idx on public.cronograma (sector_id, fecha);

create table public.punto_cisterna (
  id text primary key,
  sector_id text not null references public.sector(id) on delete cascade,
  nombre text not null,
  latitud double precision not null,
  longitud double precision not null,
  horario_inicio time not null,
  horario_fin time not null,
  estado text not null check (estado in ('ACTIVO', 'EN_RUTA', 'TERMINADO')),
  actualizado_en timestamptz not null default now()
);
create index punto_cisterna_sector_idx on public.punto_cisterna (sector_id);

-- 2. Confirmaciones colaborativas (cada usuario inserta lo suyo) ---------------------
create table public.confirmacion_horario (
  id text primary key,
  sector_id text not null references public.sector(id) on delete cascade,
  usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
  momento timestamp not null,
  tipo text not null check (tipo in ('LLEGADA', 'CORTE')),
  actualizado_en timestamptz not null default now()
);
create index confirmacion_sector_momento_idx on public.confirmacion_horario (sector_id, momento desc);

-- 3. Trigger de actualizado_en (la función ya existe desde la migración de reserva) --
create or replace function public.tocar_actualizado_en() returns trigger
language plpgsql set search_path = '' as $$
begin
  new.actualizado_en = now();
  return new;
end $$;

create trigger sector_actualizado before update on public.sector
  for each row execute function public.tocar_actualizado_en();
create trigger cronograma_actualizado before update on public.cronograma
  for each row execute function public.tocar_actualizado_en();
create trigger punto_cisterna_actualizado before update on public.punto_cisterna
  for each row execute function public.tocar_actualizado_en();
create trigger confirmacion_horario_actualizado before update on public.confirmacion_horario
  for each row execute function public.tocar_actualizado_en();

-- 4. Seguridad por fila -------------------------------------------------------------
alter table public.sector enable row level security;
alter table public.cronograma enable row level security;
alter table public.punto_cisterna enable row level security;
alter table public.confirmacion_horario enable row level security;

-- Referencia: lectura pública, sin escritura desde el cliente (se carga por SQL/panel).
create policy "sector: lectura publica" on public.sector
  for select to anon, authenticated using (true);
create policy "cronograma: lectura publica" on public.cronograma
  for select to anon, authenticated using (true);
create policy "punto_cisterna: lectura publica" on public.punto_cisterna
  for select to anon, authenticated using (true);

-- Confirmaciones: cada usuario inserta y ve solo las suyas.
create policy "confirmacion: insertar la propia" on public.confirmacion_horario
  for insert to authenticated with check ((select auth.uid()) = usuario_id);
create policy "confirmacion: ver la propia" on public.confirmacion_horario
  for select to authenticated using ((select auth.uid()) = usuario_id);

-- 5. Vista anónima para el cálculo colaborativo: expone hora y tipo, nunca quién (art. IX).
create view public.confirmacion_publica as
  select sector_id, momento, tipo from public.confirmacion_horario;
grant select on public.confirmacion_publica to anon, authenticated;

-- 6. Datos semilla: sectorización de prueba de Tacna. Se reemplaza por la oficial de EPS.
insert into public.sector (id, nombre, distrito, latitud, longitud) values
  ('CN-04', 'Ciudad Nueva 04', 'Ciudad Nueva', -17.9830, -70.2380),
  ('AA-02', 'Alto de la Alianza 02', 'Alto de la Alianza', -17.9930, -70.2520),
  ('CE-01', 'Cercado 01', 'Tacna', -18.0130, -70.2500),
  ('GA-07', 'Viñani', 'Gregorio Albarracín Lanchipa', -18.0480, -70.2530),
  ('PO-01', 'Pocollay 01', 'Pocollay', -17.9980, -70.2190),
  ('LG-06', 'Leguía 06', 'Ciudad Nueva', -17.9700, -70.2600);

insert into public.punto_cisterna (id, sector_id, nombre, latitud, longitud, horario_inicio, horario_fin, estado) values
  ('PC-01', 'GA-07', 'Viñani · Asoc. La Esperanza', -18.0495, -70.2548, '07:00', '13:00', 'ACTIVO'),
  ('PC-02', 'AA-02', 'Alto de la Alianza · Plaza', -17.9915, -70.2535, '08:00', '12:00', 'EN_RUTA'),
  ('PC-03', 'CN-04', 'Ciudad Nueva · Mercado', -17.9845, -70.2365, '06:00', '10:00', 'TERMINADO');

-- Cronogramas para ~2 semanas alrededor de hoy. Leguía 06 queda sin horario a propósito
-- (para mostrar el estado "Sector sin horario"). Se corre cerca de la fecha de presentación.
insert into public.cronograma (id, sector_id, fecha, hora_inicio, hora_fin, tipo, fuente)
select h.id || '-' || d::date, h.id, d::date, h.hi, h.hf, 'PROGRAMADO', 'EPS'
from (values
  ('CN-04', time '05:00', time '09:00'),
  ('AA-02', time '06:00', time '10:00'),
  ('CE-01', time '05:00', time '17:00'),
  ('GA-07', time '07:00', time '13:00'),
  ('PO-01', time '05:00', time '13:00')
) as h(id, hi, hf)
cross join generate_series(current_date - 3, current_date + 14, interval '1 day') as d;

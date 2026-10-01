-- El sector del domicilio viaja con el perfil del hogar, para no volver a pedirlo al entrar
-- con Google en otro teléfono. Se guarda solo el sector, nunca la coordenada de la casa.
alter table public.perfil_hogar
  add column sector_id text references public.sector(id) on delete set null;

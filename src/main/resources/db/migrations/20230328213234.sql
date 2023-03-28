-- liquibase formatted sql

-- changeset gigi:1680028363516-1
ALTER TABLE public.post ALTER COLUMN content TYPE VARCHAR(255) USING (content::VARCHAR(255));

-- changeset gigi:1680028363516-2
ALTER TABLE public.post ALTER COLUMN created_at TYPE TIMESTAMP(6) WITHOUT TIME ZONE USING (created_at::TIMESTAMP(6) WITHOUT TIME ZONE);

-- changeset gigi:1680028363516-3
ALTER TABLE public.tag ALTER COLUMN name TYPE VARCHAR(255) USING (name::VARCHAR(255));

-- changeset gigi:1680028363516-4
ALTER TABLE public."user" ALTER COLUMN nickname TYPE VARCHAR(255) USING (nickname::VARCHAR(255));

-- changeset gigi:1680028363516-5
ALTER TABLE public.post ALTER COLUMN title TYPE VARCHAR(100) USING (title::VARCHAR(100));


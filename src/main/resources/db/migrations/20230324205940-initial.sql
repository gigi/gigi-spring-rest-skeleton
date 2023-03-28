-- liquibase formatted sql

-- changeset gigi:1679684390263-1
CREATE TABLE public.post (id UUID NOT NULL, content VARCHAR(255), created_at TIMESTAMP(6) WITHOUT TIME ZONE, title VARCHAR(100) NOT NULL, author_id UUID, CONSTRAINT "postPK" PRIMARY KEY (id));

-- changeset gigi:1679684390263-2
CREATE TABLE public.post_tag (post_id UUID NOT NULL, tag_id UUID NOT NULL, CONSTRAINT "post_tagPK" PRIMARY KEY (post_id, tag_id));

-- changeset gigi:1679684390263-3
CREATE TABLE public.tag (id UUID NOT NULL, name VARCHAR(255), CONSTRAINT "tagPK" PRIMARY KEY (id));

-- changeset gigi:1679684390263-4
CREATE TABLE public."user" (id UUID NOT NULL, nickname VARCHAR(255), CONSTRAINT "userPK" PRIMARY KEY (id));

-- changeset gigi:1679684390263-5
ALTER TABLE public."user" ADD CONSTRAINT user_nickname_idx UNIQUE (nickname);

-- changeset gigi:1679684390263-6
CREATE INDEX tag_name_idx ON public.tag(name);

-- changeset gigi:1679684390263-7
ALTER TABLE public.post_tag ADD CONSTRAINT "FKac1wdchd2pnur3fl225obmlg0" FOREIGN KEY (tag_id) REFERENCES public.tag (id);

-- changeset gigi:1679684390263-8
ALTER TABLE public.post_tag ADD CONSTRAINT "FKc2auetuvsec0k566l0eyvr9cs" FOREIGN KEY (post_id) REFERENCES public.post (id);

-- changeset gigi:1679684390263-9
ALTER TABLE public.post ADD CONSTRAINT "FKj8ntmheqdjt8hvo4td0wixygs" FOREIGN KEY (author_id) REFERENCES public."user" (id);


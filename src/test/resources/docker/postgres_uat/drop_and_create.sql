DROP TABLE IF exists public.stock;

DROP SEQUENCE IF exists public.stock_id_seq;


CREATE SEQUENCE public.stock_id_seq
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1;



CREATE TABLE public.stock (
  id int8 NOT NULL DEFAULT nextval('stock_id_seq'::regclass),
  "date" timestamp NULL,
  "name" varchar(255) NULL,
  symbol varchar(255) NULL,
  value numeric(19,2) NULL,
  CONSTRAINT stock_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
) ;

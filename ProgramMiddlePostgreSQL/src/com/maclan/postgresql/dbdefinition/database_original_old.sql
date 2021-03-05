CREATE OR REPLACE FUNCTION get_lo_size(oid oid)
  RETURNS bigint AS
$BODY$DECLARE
    fd integer;
    sz bigint;
BEGIN
    fd := lo_open($1, x'40000'::int);
    PERFORM lo_lseek(fd, 0, 2);
    sz := lo_tell(fd);
    PERFORM lo_close(fd);
    RETURN sz;
END;$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;
  
-- DIVIDER

CREATE OR REPLACE FUNCTION get_localized_text(
    _text_id bigint,
    _language_id bigint)
  RETURNS text AS
$BODY$
  DECLARE local_text text;
  BEGIN
	SELECT text INTO local_text FROM localized_text WHERE text_id = _text_id AND language_id = _language_id;
	IF local_text IS NOT NULL THEN
		RETURN local_text;
	ELSE
		local_text = NULL;
		SELECT text INTO local_text FROM localized_text WHERE text_id = _text_id AND language_id = 0;
		RETURN local_text;
	END IF;
  END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
-- DIVIDER

CREATE OR REPLACE FUNCTION getunknwnstr()
  RETURNS text AS
$BODY$
BEGIN
	RETURN 'UNKNOWN';
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
-- DIVIDER
  
CREATE SEQUENCE area_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 12102
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE area_resource_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 912
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE area_slot_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 32243
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE constructor_group_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 301
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE constructor_holder_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 422
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE description_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1619
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE enumeration_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 18
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE enumeration_value_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 126
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE is_subarea_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 12603
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE language_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 90
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE mime_type_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 39684
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE namespace_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 248
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE resource_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1620
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE text_id_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 26642
  CACHE 1;
  
-- DIVIDER
  
CREATE SEQUENCE version_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 10
  CACHE 1;
  
-- DIVIDER
  
CREATE TABLE area
(
  id bigint NOT NULL DEFAULT nextval('area_id'::regclass),
  start_resource bigint,
  description_id bigint,
  visible boolean NOT NULL DEFAULT true,
  alias text,
  read_only boolean NOT NULL DEFAULT true,
  help text,
  localized boolean NOT NULL DEFAULT true,
  filename text,
  version_id bigint,
  folder text,
  start_resource_not_localized boolean,
  constructors_group_id bigint,
  related_area_id bigint,
  constructor_holder_id bigint,
  file_extension text,
  can_import boolean,
  project_root boolean
);

-- DIVIDER

CREATE TABLE area_resource
(
  area_id bigint NOT NULL,
  resource_id bigint NOT NULL,
  local_description text NOT NULL DEFAULT ''::text,
  id bigint NOT NULL DEFAULT nextval('area_resource_id'::regclass)
);

-- DIVIDER

CREATE TABLE area_slot
(
  alias text NOT NULL,
  area_id bigint NOT NULL,
  localized_text_value_id bigint,
  text_value text,
  integer_value bigint,
  real_value double precision,
  id bigint NOT NULL DEFAULT nextval('area_slot_id'::regclass),
  access character(1) NOT NULL DEFAULT 'T'::bpchar,
  hidden boolean NOT NULL DEFAULT false,
  boolean_value boolean,
  enumeration_value_id bigint,
  color bigint,
  description_id bigint,
  is_default boolean NOT NULL DEFAULT false,
  name text,
  value_meaning character(3),
  user_defined boolean,
  preferred boolean,
  special_value text
);

-- DIVIDER

CREATE TABLE constructor_group
(
  id bigint NOT NULL DEFAULT nextval('constructor_group_id'::regclass),
  extension_area_id bigint,
  alias text
);

-- DIVIDER

CREATE TABLE constructor_holder
(
  id bigint NOT NULL DEFAULT nextval('constructor_holder_id'::regclass),
  area_id bigint NOT NULL,
  group_id bigint NOT NULL,
  subgroup_id bigint,
  name text NOT NULL,
  inheritance boolean NOT NULL DEFAULT false,
  sub_relation_name text,
  super_relation_name text,
  is_sub_reference boolean,
  ask_related_area boolean NOT NULL DEFAULT false,
  subgroup_aliases text,
  invisible boolean DEFAULT false,
  alias text,
  set_home boolean
);

-- DIVIDER

CREATE TABLE description
(
  id bigint NOT NULL DEFAULT nextval('description_id'::regclass),
  description text NOT NULL
);

-- DIVIDER

CREATE TABLE enumeration
(
  id bigint NOT NULL DEFAULT nextval('enumeration_id'::regclass),
  description text NOT NULL
);

-- DIVIDER

CREATE TABLE enumeration_value
(
  enumeration_id bigint NOT NULL,
  id bigint NOT NULL DEFAULT nextval('enumeration_value_id'::regclass),
  enum_value text NOT NULL,
  description text
);

-- DIVIDER

CREATE TABLE is_subarea
(
  area_id bigint NOT NULL,
  subarea_id bigint NOT NULL,
  inheritance boolean NOT NULL DEFAULT false,
  id bigint DEFAULT nextval('is_subarea_id'::regclass),
  priority_sub integer NOT NULL DEFAULT 0,
  priority_super integer NOT NULL DEFAULT 0,
  name_sub text,
  name_super text,
  hide_sub boolean NOT NULL DEFAULT false,
  recursion boolean NOT NULL DEFAULT false
);

-- DIVIDER

CREATE TABLE language
(
  id bigint NOT NULL DEFAULT nextval('language_id'::regclass),
  description text NOT NULL,
  alias text NOT NULL,
  icon bytea,
  priority integer NOT NULL DEFAULT 0
);

-- DIVIDER

CREATE TABLE localized_text
(
  text_id bigint NOT NULL,
  language_id bigint NOT NULL,
  text text
);

-- DIVIDER

CREATE TABLE mime_type
(
  id bigint NOT NULL DEFAULT nextval('mime_type_id'::regclass),
  extension text NOT NULL,
  type text NOT NULL,
  preference boolean NOT NULL DEFAULT false
);

-- DIVIDER

CREATE TABLE namespace
(
  id bigint NOT NULL DEFAULT nextval('namespace_id'::regclass),
  description text NOT NULL DEFAULT getunknwnstr(),
  parent_id bigint NOT NULL
);

-- DIVIDER

CREATE TABLE resource
(
  id bigint NOT NULL DEFAULT nextval('resource_id'::regclass),
  namespace_id bigint NOT NULL DEFAULT 0,
  description text,
  mime_type_id bigint NOT NULL DEFAULT 0,
  visible boolean,
  blob oid,
  text text,
  protected boolean NOT NULL DEFAULT false
);

-- DIVIDER

CREATE TABLE start_area
(
  area_id bigint NOT NULL
);

-- DIVIDER

CREATE TABLE start_language
(
  language_id bigint NOT NULL
);

-- DIVIDER

CREATE TABLE text_id
(
  id bigint NOT NULL DEFAULT nextval('text_id_id'::regclass)
);

-- DIVIDER

CREATE TABLE version
(
  id bigint NOT NULL DEFAULT nextval('version_id'::regclass),
  alias text,
  description_id bigint
);

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_id_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE area_resource ADD CONSTRAINT area_resource_pkey PRIMARY KEY (area_id, resource_id, local_description);

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_pkey PRIMARY KEY (alias, area_id);

-- DIVIDER

ALTER TABLE constructor_group ADD CONSTRAINT constructor_group_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE constructor_holder ADD CONSTRAINT constructor_holder_id_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE description ADD CONSTRAINT description_id_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE enumeration ADD CONSTRAINT enumeration_description_pkey PRIMARY KEY (description);

-- DIVIDER

ALTER TABLE enumeration_value ADD CONSTRAINT enumeration_values_pkey PRIMARY KEY (enumeration_id, enum_value);

-- DIVIDER

ALTER TABLE is_subarea ADD CONSTRAINT is_subarea_pkey PRIMARY KEY (area_id, subarea_id);

-- DIVIDER

ALTER TABLE language ADD CONSTRAINT language_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE localized_text ADD CONSTRAINT localized_text_pkey PRIMARY KEY (text_id, language_id);

-- DIVIDER

ALTER TABLE mime_type ADD CONSTRAINT mime_type_pkey PRIMARY KEY (extension, type);

-- DIVIDER

ALTER TABLE namespace ADD CONSTRAINT namespace_pkey PRIMARY KEY (description, parent_id);

-- DIVIDER

ALTER TABLE resource ADD CONSTRAINT resource_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE start_area ADD CONSTRAINT start_area_pkey PRIMARY KEY (area_id);

-- DIVIDER

ALTER TABLE start_language ADD CONSTRAINT start_language_pkey PRIMARY KEY (language_id);

-- DIVIDER

ALTER TABLE text_id ADD CONSTRAINT text_id_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE version ADD CONSTRAINT version_id_pkey PRIMARY KEY (id);

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_constructor_holder_id_fkey FOREIGN KEY (constructor_holder_id)
      REFERENCES constructor_holder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_constructors_group_id_fkey FOREIGN KEY (constructors_group_id)
      REFERENCES constructor_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_description_id_fkey FOREIGN KEY (description_id)
      REFERENCES text_id (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_related_area_id_fkey FOREIGN KEY (related_area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_start_resource_fkey FOREIGN KEY (start_resource)
      REFERENCES resource (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_version_id_fkey FOREIGN KEY (version_id)
      REFERENCES version (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area ADD CONSTRAINT area_check CHECK (NOT (start_resource IS NULL AND version_id IS NOT NULL));

-- DIVIDER

ALTER TABLE area_resource ADD CONSTRAINT area_resource_a_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area_resource ADD CONSTRAINT area_resource_r_fkey FOREIGN KEY (resource_id)
      REFERENCES resource (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area_resource ADD CONSTRAINT area_resource_id_unique UNIQUE (id);

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_area_id_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_description_id_fkey FOREIGN KEY (description_id)
      REFERENCES description (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE enumeration_value ADD CONSTRAINT enumeration_values_id_unique UNIQUE (id);

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_enumeration_value_id_fkey FOREIGN KEY (enumeration_value_id)
      REFERENCES enumeration_value (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_text_value_id_fkey FOREIGN KEY (localized_text_value_id)
      REFERENCES text_id (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE area_slot ADD CONSTRAINT area_slot_value_check CHECK (text_value IS NULL AND localized_text_value_id IS NOT NULL OR text_value IS NOT NULL AND localized_text_value_id IS NULL OR text_value IS NULL AND localized_text_value_id IS NULL);

-- DIVIDER

ALTER TABLE constructor_group ADD CONSTRAINT constructor_group_extension_area_id_fkey FOREIGN KEY (extension_area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE constructor_holder ADD CONSTRAINT constructor_holder_area_id_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
-- DIVIDER

ALTER TABLE constructor_holder ADD CONSTRAINT constructor_holder_group_id_fkey FOREIGN KEY (group_id)
      REFERENCES constructor_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
-- DIVIDER

ALTER TABLE constructor_holder ADD CONSTRAINT constructor_holder_subgroup_id_fkey FOREIGN KEY (subgroup_id)
      REFERENCES constructor_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
-- DIVIDER
      
ALTER TABLE enumeration ADD CONSTRAINT enumeration_id_unique UNIQUE (id);
      
-- DIVIDER
   
ALTER TABLE enumeration_value ADD CONSTRAINT enumeration_values_id_fkey FOREIGN KEY (enumeration_id)
      REFERENCES enumeration (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE is_subarea ADD CONSTRAINT is_subarea_area_id_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE is_subarea ADD CONSTRAINT is_subarea_subarea_id_fkey FOREIGN KEY (subarea_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE is_subarea ADD CONSTRAINT is_subarea_recursion_check CHECK (recursion AND NOT inheritance AND hide_sub OR NOT recursion);

-- DIVIDER

ALTER TABLE localized_text ADD CONSTRAINT localized_text_language_id_fkey FOREIGN KEY (language_id)
      REFERENCES language (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE localized_text ADD CONSTRAINT localized_text_text_id_fkey FOREIGN KEY (text_id)
      REFERENCES text_id (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE mime_type ADD CONSTRAINT mime_type_unique UNIQUE (id);

-- DIVIDER

ALTER TABLE namespace ADD CONSTRAINT namespace_id_unique UNIQUE (id);

-- DIVIDER

ALTER TABLE namespace ADD CONSTRAINT namespace_parent_id_fkey FOREIGN KEY (parent_id)
      REFERENCES namespace (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE resource ADD CONSTRAINT resource_fkey FOREIGN KEY (namespace_id)
      REFERENCES namespace (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE resource ADD CONSTRAINT resource_mime_fkey FOREIGN KEY (mime_type_id)
      REFERENCES mime_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE start_area ADD CONSTRAINT start_area_area_id_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE start_language ADD CONSTRAINT start_laguage_language_id_fkey FOREIGN KEY (language_id)
      REFERENCES language (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE version ADD CONSTRAINT version_description_id_fkey FOREIGN KEY (description_id)
      REFERENCES text_id (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- DIVIDER

ALTER TABLE version ADD CONSTRAINT version_alias_unique UNIQUE (alias);

-- DIVIDER

CREATE INDEX area_index
  ON area
  USING btree
  (id);

-- DIVIDER

CREATE INDEX fki_area_constructor_holder_id_fkey
  ON area
  USING btree
  (constructor_holder_id);

-- DIVIDER

CREATE INDEX fki_area_constructors_group_id_fkey
  ON area
  USING btree
  (constructors_group_id);

-- DIVIDER

CREATE INDEX fki_area_description_id_fkey
  ON area
  USING btree
  (description_id);

-- DIVIDER

CREATE INDEX fki_area_related_area_id_fkey
  ON area
  USING btree
  (related_area_id);

-- DIVIDER

CREATE INDEX fki_area_start_resource_fkey
  ON area
  USING btree
  (start_resource);

-- DIVIDER

CREATE INDEX fki_area_version_id_fkey
  ON area
  USING btree
  (version_id);

-- DIVIDER

CREATE INDEX area_slot_index
  ON area_slot
  USING btree
  (alias COLLATE pg_catalog."default", area_id);

-- DIVIDER

CREATE INDEX fki_area_slot_area_id_fkey
  ON area_slot
  USING btree
  (area_id);

-- DIVIDER

CREATE INDEX fki_area_slot_description_id_fkey
  ON area_slot
  USING btree
  (description_id);

-- DIVIDER

CREATE INDEX fki_area_slot_enumeration_value_id_fkey
  ON area_slot
  USING btree
  (enumeration_value_id);

-- DIVIDER

CREATE INDEX fki_area_slot_text_value_id_fkey
  ON area_slot
  USING btree
  (localized_text_value_id);

-- DIVIDER

CREATE INDEX fki_constructor_group_extension_area_id_fkey
  ON constructor_group
  USING btree
  (extension_area_id);

-- DIVIDER

CREATE INDEX fki_constructor_holder_area_id_fkey
  ON constructor_holder
  USING btree
  (area_id);

-- DIVIDER

CREATE INDEX fki_constructor_holder_group_id_fkey
  ON constructor_holder
  USING btree
  (group_id);

-- DIVIDER

CREATE INDEX fki_constructor_holder_subgroup_id_fkey
  ON constructor_holder
  USING btree
  (subgroup_id);

-- DIVIDER

CREATE INDEX is_subarea_index1
  ON is_subarea
  USING btree
  (area_id, subarea_id);

-- DIVIDER

CREATE INDEX language_id_index
  ON language
  USING btree
  (id);

-- DIVIDER

CREATE INDEX localized_text_id
  ON localized_text
  USING btree
  (text_id);

-- DIVIDER

CREATE INDEX localized_text_language_id
  ON localized_text
  USING btree
  (language_id);

-- DIVIDER

COMMENT ON TABLE namespace
  IS 'Table has a root and subclasses.';

-- DIVIDER

CREATE INDEX fki_start_area_area_id_fkey
  ON start_area
  USING btree
  (area_id);

-- DIVIDER

CREATE INDEX fki_version_description_id_fkey
  ON version
  USING btree
  (description_id);

-- DIVIDER

INSERT INTO text_id (id) VALUES (0);

-- DIVIDER

INSERT INTO text_id (id) VALUES (1);

-- DIVIDER

INSERT INTO language (id, description, alias, priority) VALUES (0, 'Èeština', 'cz', 0);

-- DIVIDER

INSERT INTO localized_text (text_id, language_id, text) VALUES (0, 0, 'Global Area');

-- DIVIDER

INSERT INTO localized_text (text_id, language_id, text) VALUES (1, 0, 'Default');

-- DIVIDER

INSERT INTO version (id, alias, description_id) VALUES (0, 'def', 1);

-- DIVIDER

INSERT INTO namespace (id, description, parent_id) VALUES (0, '/', 0);

-- DIVIDER

INSERT INTO start_language (language_id) VALUES (0);

-- DIVIDER

INSERT INTO area (id, description_id, visible, read_only, localized, can_import) VALUES (0, 0, FALSE, TRUE, FALSE, TRUE);

-- DIVIDER

INSERT INTO start_area (area_id) VALUES (0);

-- DIVIDER

CREATE TABLE area_sources
(
  area_id bigint NOT NULL,
  resource_id bigint NOT NULL,
  version_id bigint NOT NULL,
  not_localized boolean NOT NULL DEFAULT false,
  CONSTRAINT area_sources_pkey PRIMARY KEY (area_id, resource_id, version_id),
  CONSTRAINT area_sources_area_id_fkey FOREIGN KEY (area_id)
      REFERENCES area (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT area_sources_resource_id_fkey FOREIGN KEY (resource_id)
      REFERENCES resource (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT area_sources_version_id_fkey FOREIGN KEY (version_id)
      REFERENCES version (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE area_sources
  OWNER TO postgres;

-- DIVIDER

CREATE INDEX fki_area_sources_area_id_fkey
  ON area_sources
  USING btree
  (area_id);

-- DIVIDER

CREATE INDEX fki_area_sources_resource_id_fkey
  ON area_sources
  USING btree
  (resource_id);

-- DIVIDER

CREATE INDEX fki_area_sources_version_id_fkey
  ON area_sources
  USING btree
  (version_id);

-- DIVIDER

CREATE INDEX pki_area_sources
  ON area_sources
  USING btree
  (area_id, resource_id, version_id);

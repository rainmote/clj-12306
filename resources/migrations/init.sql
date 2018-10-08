DROP TABLE IF EXISTS cdnnodes;
DROP TABLE IF EXISTS tags;

CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    name varchar(255) UNIQUE
);

CREATE TABLE cdnnodes (
    ip inet PRIMARY KEY,
    country varchar(255),
    country_code varchar(255),
    region varchar(255),
    region_name varchar(255),
    city varchar(255),
    isp varchar(255),
    org varchar(255),
    as_info varchar(255),
    domain varchar(128),
    tag_id integer DEFAULT NULL,
    longitude real,
    latitude real,
    last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_useable TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT cdnnodes_tag_id_fkey FOREIGN KEY(tag_id) REFERENCES tags(id) ON DELETE SET NULL
);
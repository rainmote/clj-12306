DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS cdn_nodes;

CREATE TABLE tags (
    id SERIAL PRIMARY KEY NOT NULL,
    name varchar(255) UNIQUE
);

CREATE TABLE cdn_nodes (
    ip inet PRIMARY KEY NOT NULL,
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
    last_useable TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

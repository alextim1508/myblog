CREATE TABLE image(
                     id bigserial PRIMARY KEY,
                     data BYTEA NOT NULL,
                     file_name VARCHAR(256) NOT NULL,
                     size INTEGER NOT NULL,
                     post_id BIGINT UNIQUE REFERENCES post(id)
);
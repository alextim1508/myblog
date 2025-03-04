CREATE TABLE tag(
    id bigserial PRIMARY KEY,
    title VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE post_tag(
    post_id bigint REFERENCES post(id),
    tag_id bigint REFERENCES tag(id)
);

CREATE TABLE comment(
    id bigint PRIMARY KEY,
    content text NOT NULL,
    post_id bigint REFERENCES post(id) NOT NULL
);
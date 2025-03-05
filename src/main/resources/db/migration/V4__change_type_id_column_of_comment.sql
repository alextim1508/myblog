DROP TABLE comment;

CREATE TABLE comment(
    id bigserial PRIMARY KEY,
    content text NOT NULL,
    post_id bigint REFERENCES post (id) NOT NULL
);
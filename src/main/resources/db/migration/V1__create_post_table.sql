CREATE TABLE post(
    id bigserial PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    content text NOT NULL,
    likeCount INTEGER NOT NULL
);
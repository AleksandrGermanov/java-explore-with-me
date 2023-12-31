DROP TABLE IF EXISTS users, categories, compilations, events, compilations_events, participation_requests, comments;

CREATE TABLE IF NOT EXISTS users(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name VARCHAR(256) NOT NULL,
email VARCHAR(256) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS compilations(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
title VARCHAR(64) NOT NULL UNIQUE,
pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS events(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
annotation VARCHAR(2000) NOT NULL,
category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
confirmed_requests BIGINT,
created_on TIMESTAMP NOT NULL,
description VARCHAR(7000),
event_date TIMESTAMP NOT NULL,
initiator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
location_lat FLOAT NOT NULL,
location_lon FLOAT NOT NULL,
paid BOOLEAN  NOT NULL,
participant_limit INT,
published_on TIMESTAMP,
request_moderation BOOLEAN,
state VARCHAR(16),
title VARCHAR(120) NOT NULL,
permit_comments BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events(
compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
CONSTRAINT pk PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS participation_requests(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
created TIMESTAMP NOT NULL,
event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
status VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
created_on TIMESTAMP NOT NULL,
event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
commentator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
text VARCHAR(2000) NOT NULL,
user_state VARCHAR(16) NOT NULL,
comment_state VARCHAR(16) NOT NULL
);
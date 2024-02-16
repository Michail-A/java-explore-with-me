create table if not exists category
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT PK_CATEGORY PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

create table if not exists users
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create table if not exists events
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id INTEGER NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id INTEGER NOT NULL,
    lat NUMERIC NOT NULL,
    lon NUMERIC NOT NULL,
    paid BOOLEAN DEFAULT FALSE,
    request_moderation BOOLEAN DEFAULT TRUE,
    participant_limit INTEGER DEFAULT 0,
    confirmed_requests INTEGER DEFAULT 0,
    views BIGINT,
    state VARCHAR,
    create_date TIMESTAMP WITHOUT TIME ZONE,
    published_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_EVENT PRIMARY KEY (id),
    CONSTRAINT FK_EVENT_USER
        FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT FK_EVENT_CATEGORY
        FOREIGN KEY (category_id) REFERENCES category (id) ON UPDATE CASCADE
);

create table if not exists request
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id INTEGER NOT NULL,
    requester_id INTEGER NOT NULL,
    create_date TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR,
    CONSTRAINT PK_REQUEST PRIMARY KEY (id),
    CONSTRAINT FK_REQUEST_USER
        FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT FK_REQUEST_EVENT
        FOREIGN KEY (event_id) REFERENCES events (id)
);

create table if not exists compilation
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN DEFAULT FALSE NOT NULL,
    title  VARCHAR NOT NULL,
    CONSTRAINT PK_COMPILATION PRIMARY KEY (id),
    CONSTRAINT UQ_COMPILATION_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS event_compilation
(
    event_id INTEGER NOT NULL,
    compilation_id INTEGER NOT NULL,
    CONSTRAINT PK_EVENT_COMPILATION PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT FK_EVENT_EVENT_COMPILATION
        FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT FK_COMPILATION_EVENT_COMPILATION
        FOREIGN KEY (compilation_id) REFERENCES compilation (id)
);
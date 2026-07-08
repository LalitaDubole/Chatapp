CREATE DATABASE IF NOT EXISTS chatapp_db;
USE chatapp_db;

CREATE TABLE IF NOT EXISTS users (
                                     user_id     INT AUTO_INCREMENT PRIMARY KEY,
                                     username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login  DATETIME
    );

CREATE TABLE IF NOT EXISTS messages (
                                        message_id  INT AUTO_INCREMENT PRIMARY KEY,
                                        sender      VARCHAR(50) NOT NULL,
    content     TEXT        NOT NULL,
    sent_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender) REFERENCES users(username)
    ON DELETE CASCADE ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS user_logs (
                                         log_id      INT AUTO_INCREMENT PRIMARY KEY,
                                         username    VARCHAR(50) NOT NULL,
    event_type  ENUM('JOIN','LEAVE') NOT NULL,
    event_time  DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- Sample test users
INSERT IGNORE INTO users (username, password, email) VALUES
    ('alice',   'alice123',   'alice@example.com'),
    ('bob',     'bob123',     'bob@example.com'),
    ('charlie', 'charlie123', 'charlie@example.com');
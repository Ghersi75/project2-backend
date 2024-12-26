CREATE TABLE
  users (
    account_id SERIAL PRIMARY KEY, -- Unique identifier for each user (auto-incremented)
    username VARCHAR(255) NOT NULL UNIQUE, -- Username must be unique and not null
    password VARCHAR(255) NOT NULL, -- Password (hashed in production)
    role VARCHAR(50) NOT NULL -- Role assigned to the user
  );
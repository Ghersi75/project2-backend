CREATE TABLE
  users (
    id SERIAL PRIMARY KEY, -- Unique identifier for each user (auto-incremented)
    display_name VARCHAR(255) NOT NULL, -- Display name of the user
    username VARCHAR(255) NOT NULL UNIQUE, -- Username must be unique and not null
    password VARCHAR(255) NOT NULL, -- Password (hashed in production)
    user_role VARCHAR(50) NOT NULL -- Role assigned to the user
    favorite_games INTEGER[];
  );
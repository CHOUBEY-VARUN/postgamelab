CREATE TABLE game_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title VARCHAR(160) NOT NULL,
    home_team VARCHAR(80) NOT NULL,
    away_team VARCHAR(80) NOT NULL,
    game_date DATE NOT NULL,

    video_url TEXT,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
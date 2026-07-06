# PostGameLab Database Schema

## Overview

PostGameLab uses PostgreSQL as the primary database.

The schema is designed around users creating game breakdowns. Each breakdown belongs to a user, references a game, and contains multiple key moments. Each moment can have multiple tags. Users can vote and comment on public breakdowns.

## Tables

## users

Stores registered users.

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(40) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## games

Stores basic game information.

```sql
CREATE TABLE games (
    id UUID PRIMARY KEY,
    league VARCHAR(20) NOT NULL DEFAULT 'NBA',
    season_year INT,
    home_team VARCHAR(80) NOT NULL,
    away_team VARCHAR(80) NOT NULL,
    home_score INT,
    away_score INT,
    game_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## breakdowns

Stores user-created game breakdowns.

```sql
CREATE TABLE breakdowns (
    id UUID PRIMARY KEY,
    creator_id UUID NOT NULL REFERENCES users(id),
    game_id UUID NOT NULL REFERENCES games(id),
    title VARCHAR(180) NOT NULL,
    slug VARCHAR(220) UNIQUE NOT NULL,
    summary TEXT,
    visibility VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    source_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

Visibility values:

```txt
DRAFT
PUBLIC
UNLISTED
```

## moments

Stores timestamped key moments inside a breakdown.

```sql
CREATE TABLE moments (
    id UUID PRIMARY KEY,
    breakdown_id UUID NOT NULL REFERENCES breakdowns(id) ON DELETE CASCADE,
    quarter VARCHAR(10),
    game_clock VARCHAR(20),
    video_timestamp_seconds INT,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    why_it_mattered TEXT,
    impact_score INT,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## tags

Stores reusable tags for classifying moments.

```sql
CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(80) UNIQUE NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL
);
```

## moment_tags

Join table for the many-to-many relationship between moments and tags.

```sql
CREATE TABLE moment_tags (
    moment_id UUID NOT NULL REFERENCES moments(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (moment_id, tag_id)
);
```

## votes

Stores user votes on breakdowns and moments.

```sql
CREATE TABLE votes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    target_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL,
    vote_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, target_type, target_id, vote_type)
);
```

Possible target types:

```txt
BREAKDOWN
MOMENT
```

Possible vote types:

```txt
UPVOTE
KEY_POSSESSION
```

## comments

Stores discussion comments.

```sql
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    breakdown_id UUID NOT NULL REFERENCES breakdowns(id) ON DELETE CASCADE,
    moment_id UUID REFERENCES moments(id) ON DELETE CASCADE,
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

If `moment_id` is null, the comment belongs to the overall breakdown.

If `moment_id` is present, the comment belongs to a specific moment.

## Relationships

```txt
users 1 → many breakdowns
games 1 → many breakdowns
breakdowns 1 → many moments
moments many → many tags
users 1 → many votes
users 1 → many comments
breakdowns 1 → many comments
moments 1 → many comments
```

## Indexes to Add Later

Useful indexes for performance:

```sql
CREATE INDEX idx_breakdowns_creator_id ON breakdowns(creator_id);
CREATE INDEX idx_breakdowns_slug ON breakdowns(slug);
CREATE INDEX idx_breakdowns_visibility ON breakdowns(visibility);
CREATE INDEX idx_moments_breakdown_id ON moments(breakdown_id);
CREATE INDEX idx_comments_breakdown_id ON comments(breakdown_id);
CREATE INDEX idx_votes_target ON votes(target_type, target_id);
```

## Schema Notes

* UUIDs are used for primary keys.
* Flyway should manage all schema changes.
* `ddl-auto` should be set to `validate`, not `update`, once migrations are in use.
* Moments are deleted when their parent breakdown is deleted.
* Tags are reusable across all breakdowns.

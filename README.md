# PostGameLab

PostGameLab is a fan-focused basketball post-game analysis platform for NBA fans who want to understand games beyond the box score and highlight reel.

The product helps fans create and share structured post-game breakdowns using timestamped key moments, tags, tactical notes, momentum swings, clutch possessions, coaching decisions, controversial plays, and fan discussion.

PostGameLab is not a coaching app. It is designed as a shareable post-game film room and discussion companion for basketball fans.

## Product Idea

After a big game, fans often ask:

- What actually decided the game?
- Which possession changed the momentum?
- Was that shot a bad decision?
- Did the coach make the right adjustment?
- Which defensive mistake mattered most?
- Was the controversial call actually game-changing?

PostGameLab turns those scattered discussions into clean, structured, shareable breakdown pages.

## MVP Goal

The MVP goal is to let a user:

1. Register and log in
2. Create a game breakdown
3. Add key moments with timestamps
4. Tag moments by play type
5. Publish a public shareable breakdown page
6. Allow fans to discuss and vote on key moments

The core product promise is:

> Create a clean “what decided the game?” breakdown in around 10 minutes and share it with other fans.

## Tech Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

### Frontend

- React
- TypeScript
- Vite
- Axios
- React Router DOM

### Database

- PostgreSQL
- Flyway migrations

## Repository Structure

```txt
postgamelab/
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── docs/
│   ├── product-brief.md
│   ├── api-plan.md
│   └── database-schema.md
├── .github/
│   ├── ISSUE_TEMPLATE/
│   └── workflows/
├── .gitignore
└── README.md
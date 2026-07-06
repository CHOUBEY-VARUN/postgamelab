# PostGameLab API Plan

## Base URL

Local development:

```txt
http://localhost:8080/api
```

## Auth Endpoints

### Register

```txt
POST /auth/register
```

Request:

```json
{
  "username": "varun",
  "email": "varun@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "user": {
    "id": "uuid",
    "username": "varun",
    "email": "varun@example.com"
  }
}
```

### Login

```txt
POST /auth/login
```

Request:

```json
{
  "email": "varun@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "token": "jwt-token",
  "user": {
    "id": "uuid",
    "username": "varun",
    "email": "varun@example.com"
  }
}
```

### Current User

```txt
GET /auth/me
```

Requires authentication.

Response:

```json
{
  "id": "uuid",
  "username": "varun",
  "email": "varun@example.com"
}
```

## Breakdown Endpoints

### Create Breakdown

```txt
POST /breakdowns
```

Requires authentication.

Request:

```json
{
  "title": "How Denver won the final five minutes",
  "homeTeam": "Denver Nuggets",
  "awayTeam": "Los Angeles Lakers",
  "homeScore": 112,
  "awayScore": 108,
  "gameDate": "2026-07-06",
  "summary": "Denver controlled the final stretch through better shot selection and defensive execution.",
  "sourceUrl": "https://example.com"
}
```

### Get My Breakdowns

```txt
GET /breakdowns/my
```

Requires authentication.

### Get Breakdown by ID

```txt
GET /breakdowns/{id}
```

Requires authentication if the breakdown is private.

### Update Breakdown

```txt
PUT /breakdowns/{id}
```

Requires owner authentication.

### Delete Breakdown

```txt
DELETE /breakdowns/{id}
```

Requires owner authentication.

### Publish Breakdown

```txt
PATCH /breakdowns/{id}/publish
```

Requires owner authentication.

### Get Public Breakdown by Slug

```txt
GET /breakdowns/slug/{slug}
```

Public endpoint.

### Get Public Breakdowns

```txt
GET /breakdowns/public?page=0&size=10
```

Public endpoint.

## Moment Endpoints

### Add Moment

```txt
POST /breakdowns/{breakdownId}/moments
```

Requires owner authentication.

Request:

```json
{
  "quarter": "4Q",
  "gameClock": "02:14",
  "videoTimestampSeconds": 5230,
  "title": "Murray attacks the switch and forces help",
  "description": "Murray rejects the screen and gets downhill before the defense can reset.",
  "whyItMattered": "This forced the weak-side defender to help and opened the corner three.",
  "impactScore": 9,
  "tagIds": ["uuid-1", "uuid-2"]
}
```

### Get Moments for Breakdown

```txt
GET /breakdowns/{breakdownId}/moments
```

### Update Moment

```txt
PUT /moments/{momentId}
```

Requires owner authentication.

### Delete Moment

```txt
DELETE /moments/{momentId}
```

Requires owner authentication.

## Tag Endpoints

### Get Tags

```txt
GET /tags
```

Public endpoint.

Response:

```json
[
  {
    "id": "uuid",
    "name": "Clutch play",
    "slug": "clutch-play"
  }
]
```

## Voting Endpoints

### Upvote Moment

```txt
POST /moments/{momentId}/upvote
```

Requires authentication.

### Remove Moment Upvote

```txt
DELETE /moments/{momentId}/upvote
```

Requires authentication.

### Mark Key Possession

```txt
POST /moments/{momentId}/key-possession
```

Requires authentication.

### Remove Key Possession Vote

```txt
DELETE /moments/{momentId}/key-possession
```

Requires authentication.

## Comment Endpoints

### Create Comment

```txt
POST /breakdowns/{breakdownId}/comments
```

Requires authentication.

Request:

```json
{
  "body": "This was definitely the possession that changed the game.",
  "momentId": "optional-moment-id"
}
```

### Get Comments

```txt
GET /breakdowns/{breakdownId}/comments
```

Public endpoint.

### Delete Comment

```txt
DELETE /comments/{commentId}
```

Requires comment owner authentication.

## Analytics Endpoints

### Get Breakdown Analytics

```txt
GET /breakdowns/{breakdownId}/analytics
```

Response:

```json
{
  "totalMoments": 7,
  "topTags": [
    {
      "tag": "Momentum swing",
      "count": 3
    }
  ],
  "highestImpactMoment": {
    "title": "4Q 01:24 - Defensive miscommunication",
    "impactScore": 9
  },
  "fanVotedKeyPossession": {
    "title": "4Q 00:41 - Missed corner rotation",
    "votes": 8
  }
}
```

## API Design Notes

* All protected endpoints require a JWT in the Authorization header.
* Public breakdown pages should be readable without login.
* Users can only edit or delete their own breakdowns and moments.
* Draft breakdowns should not be visible publicly.
* Public and unlisted breakdowns can be accessed by slug.

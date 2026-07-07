ALTER TABLE game_sessions RENAME TO breakdowns;

ALTER TABLE breakdowns
ADD COLUMN slug VARCHAR(180),
ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE';

UPDATE breakdowns
SET slug = lower(
    regexp_replace(
        regexp_replace(title, '[^a-zA-Z0-9]+', '-', 'g'),
        '(^-|-$)',
        '',
        'g'
    )
)
WHERE slug IS NULL;

ALTER TABLE breakdowns
ALTER COLUMN slug SET NOT NULL;

CREATE UNIQUE INDEX idx_breakdowns_slug ON breakdowns(slug);
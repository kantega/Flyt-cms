ALTER TABLE content ADD ContentType INTEGER;
UPDATE content SET ContentType = Type;
ALTER TABLE content DROP COLUMN Type;
ALTER TABLE multimedia ADD HasImageMap int NULL;
UPDATE multimedia SET HasImageMap = 0;
UPDATE multimedia SET HasImageMap = 1 WHERE id IN (SELECT DISTINCT (MultimediaId) FROM multimediaimagemap);
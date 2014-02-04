ALTER TABLE multimedia ADD NoUsages int NULL;

-- Update count for all existing Multimedia objects
UPDATE multimedia
SET NoUsages = ( SELECT COUNT(*) FROM multimediausage WHERE multimediausage.MultimediaId = multimedia.Id );
ALTER TABLE sites ADD IsDefault int;
UPDATE sites SET IsDefault = 0;
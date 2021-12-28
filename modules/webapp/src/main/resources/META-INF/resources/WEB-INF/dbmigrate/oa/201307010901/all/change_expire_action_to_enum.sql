UPDATE content SET ExpireAction = 'HIDE' WHERE ExpireAction = '0';
UPDATE content SET ExpireAction = 'REMIND' WHERE ExpireAction = '1';
UPDATE content SET ExpireAction = 'DELETE' WHERE ExpireAction = '2';
UPDATE content SET ExpireAction = 'ARCHIVE' WHERE ExpireAction = '3';
CREATE TABLE ratings (
  UserId VARCHAR(255),
  ObjectId VARCHAR(255) NOT NULL,
  Context VARCHAR(255) NOT NULL,
  Rating INT NOT NULL,
  RatingDate DATETIME NOT NULL
);

INSERT INTO ratings VALUES ('krisel', '1', 'content', 1, '2009-01-01');
INSERT INTO ratings VALUES ('andska', '2', 'content', 2, '2009-01-01');
INSERT INTO ratings VALUES ('krisel', '3', 'content', 3, '2009-01-01');
INSERT INTO ratings VALUES ('andska', '4', 'content', 4, '2009-01-01');

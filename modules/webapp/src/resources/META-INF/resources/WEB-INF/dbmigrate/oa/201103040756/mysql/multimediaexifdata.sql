CREATE TABLE multimediaexifdata
(
    Id INTEGER NOT NULL AUTO_INCREMENT,
    MultimediaId INTEGER NOT NULL,
    Directory VARCHAR(255) NOT NULL,
    ValueKey VARCHAR(255) NOT NULL,
    Value LONGTEXT NULL,
    PRIMARY KEY (Id)
) ;

CREATE INDEX idx_multimediaexifdata ON multimediaexifdata (MultimediaId);

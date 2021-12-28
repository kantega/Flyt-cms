CREATE TABLE multimediaexifdata
(
    Id INT NOT NULL IDENTITY (1,1) ,
    MultimediaId INT NOT NULL,
    Directory VARCHAR(255) NOT NULL,
    ValueKey VARCHAR(255) NOT NULL,
    Value TEXT,
    PRIMARY KEY (Id)
);

CREATE INDEX idx_multimediaexifdata ON multimediaexifdata (MultimediaId);

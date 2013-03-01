CREATE TABLE formsubmission (FormSubmissionId INT, FormId INT NOT NULL, SubmittedBy VARCHAR(255), AuthenticatedIdentity VARCHAR(255), Password VARCHAR(255), Email VARCHAR(255), SubmittedDate DATE NOT NULL);
INSERT INTO formsubmission VALUES (1, 1, 'Donald Duck', 'donald', null, 'donald@duck.com', '2009-01-01');
INSERT INTO formsubmission VALUES (2, 1, 'Huey Duck', 'huey', null, 'huey@duck.com', '2009-01-01');
INSERT INTO formsubmission VALUES (3, 1, 'Dewey Duck', 'dewey', null, 'dewey@duck.com', '2009-01-01');
INSERT INTO formsubmission VALUES (4, 1, 'Louie Duck', 'louie', null, 'louie@duck.com', '2009-01-01');

CREATE TABLE formsubmissionvalues (FormSubmissionId INT, FieldNumber INT, FieldName VARCHAR(255), FieldValue VARCHAR(255));

INSERT INTO formsubmissionvalues VALUES (1, 1, 'name', 'Donald Duck');
INSERT INTO formsubmissionvalues VALUES (1, 2, 'age', '31');
INSERT INTO formsubmissionvalues VALUES (1, 3, 'girlfriend', 'Dolly Duck');

INSERT INTO formsubmissionvalues VALUES (2, 1, 'name', 'Huey Duck');
INSERT INTO formsubmissionvalues VALUES (2, 2, 'age', '8');

INSERT INTO formsubmissionvalues VALUES (3, 1, 'name', 'Dewey Duck');
INSERT INTO formsubmissionvalues VALUES (3, 2, 'age', '8');

INSERT INTO formsubmissionvalues VALUES (4, 1, 'name', 'Louie Duck');
INSERT INTO formsubmissionvalues VALUES (4, 2, 'age', '8');
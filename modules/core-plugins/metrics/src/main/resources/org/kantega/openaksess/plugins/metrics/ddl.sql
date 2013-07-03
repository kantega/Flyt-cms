CREATE TABLE metrics(
  capturetime timestamp,
  memoryInit double,
  memoryMax double,
  memoryUsed double,
  memoryCommitted double,
  heapInit double,
  heapMax double,
  heapUsed double,
  heapCommitted double,
  heapUsage double,
  nonHeapUsage double,
  activeRequests integer,
  maxDbConnections integer,
  idleDbConnections integer,
  openDbConnections integer,
  badRequests integer,
  ok integer,
  serverError integer,
  notFound integer
);

CREATE INDEX capturetime_index ON metrics(capturetime);
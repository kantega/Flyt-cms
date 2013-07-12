
HOW TO USE:

*********
1) Creating SQL DDL scripts for various databases:
*********
mvn ddlutils:ddl2db -Ddbtype=mssql
mvn ddlutils:ddl2db -Ddbtype=oracle10
mvn ddlutils:ddl2db -Ddbtype=mysql

Generated DDLs are put in the directory target/schemas

*********
2) Creating SQL DDL scripts with diffs for various databases
*********
mvn ddlutils:ddl2db -Ddbtype=mssql -Pdbdiff
etc..

(Connection details for the database to create diff against is configured in pom.xml, look for profile "dbdiff")

*********
3) Creating XML DB Schema from live database
*********
mvn ddlutils:db2ddl -Ddbtype=mssql -Pdbcreateschema

(Connection details for the database can be found in pom.xml, look for profile "dbcreateschema")


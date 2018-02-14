# Database Integration GUI

A desktop application that connects to a Microsoft SQL Server database (NW_Traders used by default) that provides database integration features for managing data, custom authentication features with data managed from within the SQL database, and audit history features to read and manage admin and user interaction with the database as data is changed or accessed with/without permissions. 

The application is built on the JavaFX GUI library, uses Maven as the build manager, and the SQL JDBC-4 API for accessing the SQL Server database locally/remotely.

Program built ~2015 and is not maintained so the application could be drastically improved.

## Setup

1. SQL Server database needs to be initialized locally or remotely on AWS RDS instance or a similar cloud or remote service. To use the program as is with the example database, download NWTraders database (if it can still be downloaded from the Microsoft website). 

2. To use the default example users in the repo, run the SQL scripts in [SQL_Statements](./src/main/java/Database_Integration/SQL_Statements) and default login credentials, certificates, and additional tables for auditing users' history will alter the database that is being used.

3. Once the database is synchronized with the example login credentials, run Main.java from the Database_Integration.Main classpath.

4. Maven commands can be used to re-build, test, and deploy project once ready for production.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details
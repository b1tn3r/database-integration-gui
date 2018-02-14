-- The logins on the server and the users in the database can either be created manually with ensuring that each of the logins and users have a default username and starting password of:
-- Login/User - Password
-- myAdmin     - testPassword
-- sWaters    - testPassword
-- aJohnson   - testPassword
-- cAllens    - testPassword
-- pLangley   - testPassword
-- rHayes     - testPassword
-- sMeyers    - testPassword

-- the passwords can be changed after the user has logged into the program. 
-- these users can be added to custom roles such as udr_db_reademployees, udr_db_readupdateemployees, udr_db_elevatedaccess, etc.
-- or they can be added automatically with the below statements:

-- create logins first: select ONLY sql between comment lines and execute ___________________________________________________________________________________________
USE master
GO
CREATE LOGIN myAdmin
    WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF
GRANT SELECT ALL USER SECURABLES TO myAdmin;

CREATE LOGIN sWaters
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

CREATE LOGIN cAllens
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

CREATE LOGIN pLangley
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

CREATE LOGIN rHayes
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

CREATE LOGIN sMeyers
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

CREATE LOGIN aJohnson
	WITH PASSWORD = 'testPassword',
	DEFAULT_DATABASE = NWTraders,
	CHECK_EXPIRATION=OFF,
    CHECK_POLICY=OFF;

-- ________________________________________________________________________________________________________________________________________________________________



-- create users: select ONLY the sql between the comment lines and execute ________________________________________________________________________________________
USE NWTraders
GO
CREATE USER myAdmin FOR LOGIN myAdmin
	WITH DEFAULT_SCHEMA = db_accessadmin;
GO
CREATE USER cAllens FOR LOGIN cAllens
	WITH DEFAULT_SCHEMA = db_datareader
GO
CREATE USER sWaters FOR LOGIN sWaters
	WITH DEFAULT_SCHEMA = db_writer
GO
CREATE USER aJohnson FOR LOGIN aJohnson
	WITH DEFAULT_SCHEMA = db_securityadmin
GO
CREATE USER pLangley FOR LOGIN pLangley
	WITH DEFAULT_SCHEMA = db_backupoperator
GO
CREATE USER rHayes FOR LOGIN rHayes
	WITH DEFAULT_SCHEMA = db_datawriter
GO
CREATE USER sMeyers FOR LOGIN sMeyers	
	WITH DEFAULT_SCHEMA = db_datareader
GO
--_________________________________________________________________________________________________________________________________________________________________
USE NWTraders
GO
-- create master encryption key
CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'testPassword';

OPEN MASTER KEY DECRYPTION BY PASSWORD = 'testPassword';
GO
-- create certificate to protect the encryption key
CREATE CERTIFICATE KeyProtectionCertificate
WITH SUBJECT = 'to protect symmetric encryption keys';

-- create symmetric key
OPEN MASTER KEY DECRYPTION BY PASSWORD = 'testPassword';
GO
CREATE SYMMETRIC KEY UsersNameKey
WITH ALGORITHM = AES_256,
     KEY_SOURCE = '4frT-7FGHFDfTh98#6erZ3dq#�',
     IDENTITY_VALUE = 'l�Fg{(ZEfd@23fz4fqeRHY&4efVql'
ENCRYPTION BY CERTIFICATE KeyProtectionCertificate; 


CREATE TABLE dbo.Users
    (
    UserId int NOT NULL IDENTITY(1,1) PRIMARY KEY,
    Username nvarchar(max) NOT NULL,
    Password nvarchar(max) NOT NULL,
	LastName nvarchar(max) NULL,
	FirstName nvarchar(max) NULL
    );

CREATE TABLE dbo.AuditHistory
	(
	AuditID int NOT NULL IDENTITY(1,1) PRIMARY KEY,
	UserID int NULL,
	EmployeeID int NULL,
	Action nvarchar(MAX) NULL,
	ActionDateTime datetime NULL,
	CONSTRAINT FK_AuditHistory_Users FOREIGN KEY (UserID) 
		REFERENCES dbo.Users (UserID) 
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT FK_AuditHistory_Employees FOREIGN KEY (EmployeeID)
		REFERENCES HumanResources.Employees (EmployeeID)
		ON DELETE CASCADE
		ON UPDATE CASCADE
	);


-- inserting encrypted data... Password field's value is created from the hash value from statement: SELECT HASHBYTES('SHA2_512', 'testPassword')
OPEN SYMMETRIC KEY UsersNameKey
DECRYPTION BY CERTIFICATE KeyProtectionCertificate; 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)             
VALUES ('myAdmin', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'),
					EncryptByKey(Key_Guid('UsersNameKey'), 'Mike'), EncryptByKey(Key_Guid('UsersNameKey'), 'Anderson'));

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('sWaters', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Samuel'), EncryptByKey(Key_Guid('UsersNameKey'), 'Waters')); 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('cAllens', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Christian'), EncryptByKey(Key_Guid('UsersNameKey'), 'Allens')); 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('pLangley', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Peter'), EncryptByKey(Key_Guid('UsersNameKey'), 'Langley')); 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('rHayes', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Rebecca'), EncryptByKey(Key_Guid('UsersNameKey'), 'Hayes')); 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('sMeyers', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Sasha'), EncryptByKey(Key_Guid('UsersNameKey'), 'Meyers')); 

INSERT INTO dbo.Users (Username, Password, LastName, FirstName)                  
VALUES ('aJohnson', EncryptByKey(Key_Guid('UsersNameKey'), '8A5B8B4611DEE46B3DAF3531FABB2A73A93A2BE376EAA240DC115DD5818BD24A533EEEE9A46AAA27C8064516E489E60B75533506E774E1979228428C910AF275'), 
					EncryptByKey(Key_Guid('UsersNameKey'), 'Anthony'), EncryptByKey(Key_Guid('UsersNameKey'), 'Johnson')); 

CLOSE SYMMETRIC KEY UsersNameKey;



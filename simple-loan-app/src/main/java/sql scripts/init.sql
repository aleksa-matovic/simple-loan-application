
CREATE DATABASE LEANPAY_DB;


CREATE TABLE REQUESTS (
  ID INT NOT NULL AUTO_INCREMENT,
  METHOD VARCHAR (255) NOT NULL,
  REQUEST_URL VARCHAR (255) NOT NULL,
  REQUEST_URI VARCHAR (255) NOT NULL,
  PARAMETERS VARCHAR (500),
  CREATED_AT DATETIME NOT NULL,
  PRIMARY KEY (ID));
 

CREATE TABLE LOANS (
  ID INT NOT NULL AUTO_INCREMENT,
  LOAN_AMOUNT INT NOT NULL,
  INTEREST_RATE DECIMAL(27,2) NOT NULL,
  LOAN_TERM INT,
  MONTHS INT,
  CREATED_AT DATETIME NOT NULL,
  PRIMARY KEY (ID));
  
  
CREATE TABLE PAYMENT_PLANS (
  ID INT NOT NULL AUTO_INCREMENT,
  LOAN_ID INT NOT NULL,
  RATE INT NOT NULL,
  MONTHLY_PAYMENT DECIMAL(27,2) NOT NULL,
  PRINCIPAL_PAYMENT DECIMAL(27,2) NOT NULL,
  REMAINING_DEBT DECIMAL(27,2) NOT NULL,
  TOTAL_INTEREST DECIMAL(27,2) NOT NULL,
  PRIMARY KEY (ID));
  
  
ALTER TABLE PAYMENT_PLANS ADD CONSTRAINT FK_PYPL_LOAN_ID FOREIGN KEY (LOAN_ID) REFERENCES LOANS (ID);
  
COMMIT;
  
    
    
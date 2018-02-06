CREATE TABLE IF NOT EXISTS  settlement 
(id  numeric NOT NULL  DEFAULT NEXTVAL('settlement_id_seq'),
 ID_HOLDER_ACCOUNT_OPERATION_PK numeric,
 SETTLEMENT_AMOUNT         		numeric,
 SETTLED_QUANTITY   			numeric,
 REGISTER_DATE					timestamp NOT NULL ,
 UPDATE_DATE					timestamp ,
 STATE							integer DEFAULT 1
 CONSTRAINT id_pk PRIMARY KEY(id));
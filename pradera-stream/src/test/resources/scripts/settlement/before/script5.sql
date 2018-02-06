CREATE TABLE IF NOT EXISTS  settlement 
(id  bigint NOT NULL  DEFAULT NEXTVAL('settlement_id_seq'),
 ID_HOLDER_ACCOUNT_OPERATION_PK bigint ,
 SETTLEMENT_AMOUNT         		bigint ,
 SETTLED_QUANTITY   			bigint ,
 CONSTRAINT id_pk PRIMARY KEY(id));
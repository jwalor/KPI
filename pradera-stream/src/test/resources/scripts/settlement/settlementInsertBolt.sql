insert into kpi_stream.settlement(id,ID_HOLDER_ACCOUNT_OPERATION_PK,SETTLEMENT_AMOUNT,SETTLED_QUANTITY)
values (NEXTVAL('kpi_stream.settlement_id_seq'),?,?,?)
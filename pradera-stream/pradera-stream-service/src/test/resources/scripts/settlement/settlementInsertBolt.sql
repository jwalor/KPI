insert into kpi_stream.settlement(id,ID_HOLDER_ACCOUNT_OPERATION_PK,SETTLEMENT_AMOUNT,SETTLED_QUANTITY,REGISTER_DATE)
values (NEXTVAL('kpi_stream.settlement_id_seq'),
        $payload.ID_HOLDER_ACCOUNT_OPERATION_PK.getVal(),
        $payload.SETTLEMENT_AMOUNT.getVal(),
        $payload.SETTLED_QUANTITY.getVal(),
        to_timestamp('$DateUtil.getSystemTimestamp()', 'YYYY/MM/DD HH24:MI:SS.MS.US'))
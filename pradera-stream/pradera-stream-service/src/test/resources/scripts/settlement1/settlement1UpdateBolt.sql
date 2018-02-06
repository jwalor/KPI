UPDATE kpi_stream.settlement SET STATE = 0 , UPDATE_DATE = to_timestamp('$payload.UPDATE_DATE.getVal()', 'YYYY/MM/DD HH24:MI:SS.MS.US')
WHERE ID = $payload.ID.getVal()

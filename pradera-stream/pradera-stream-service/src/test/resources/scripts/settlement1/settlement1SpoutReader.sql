select setelement1.*
      from 
      (  select  
               (select count(1) from holder_account_operation
                where rownum <= 2 and to_char(LAST_MODIFY_DATE,'yyyy')= :YEAR ) as TOTAL,
               rownum as CODE , 
               ID_HOLDER_ACCOUNT_OPERATION_PK,
               ID_MECHANISM_OPERATION_FK,
               ID_HOLDER_ACCOUNT_FK , 
               OPERATION_PART,
               SETTLEMENT_AMOUNT,
               SETTLED_QUANTITY,
               LAST_MODIFY_DATE
               from holder_account_operation
                where rownum <= 2 and to_char(LAST_MODIFY_DATE,'yyyy')= :YEAR 
            order by code asc
      ) setelement1
              order by setelement1.code

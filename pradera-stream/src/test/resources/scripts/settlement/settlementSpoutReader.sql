 select *  from (  
 		select rownum as rownumber, T.* from( 
			select ID_HOLDER_ACCOUNT_OPERATION_PK,
					ID_MECHANISM_OPERATION_FK,
					ID_HOLDER_ACCOUNT_FK , 
					OPERATION_PART,SETTLEMENT_AMOUNT,
					SETTLED_QUANTITY,
					LAST_MODIFY_DATE
			from holder_account_operation
    		where ID_HOLDER_ACCOUNT_OPERATION_PK = ? order by LAST_MODIFY_DATE desc 
    	) T 
    ) R where rownumber < 2
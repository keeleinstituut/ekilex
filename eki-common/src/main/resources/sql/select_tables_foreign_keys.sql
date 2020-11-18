select col.table_name pk_table_name,
       col.column_name pk_column_name,
       kcu.table_name fk_table_name,
       kcu.column_name fk_column_name
from information_schema.columns col,
     information_schema.table_constraints tc,
     information_schema.key_column_usage kcu,
     information_schema.constraint_column_usage ccu
where tc.constraint_name = kcu.constraint_name
and   tc.constraint_name = ccu.constraint_name
and   tc.constraint_type = :constraintTypeFk
and   col.table_name = ccu.table_name
and   col.column_name = ccu.column_name
and   kcu.column_name not in (:ignoreFks)
and   col.table_name in (:tableNames)
order by col.table_name,
         col.ordinal_position

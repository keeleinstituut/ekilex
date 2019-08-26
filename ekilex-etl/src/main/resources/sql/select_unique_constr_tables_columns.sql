select ccu.table_name,
       ccu.column_name
from information_schema.table_constraints tc,
     information_schema.constraint_column_usage ccu,
     information_schema.columns col
where tc.constraint_type = :constraintTypeUnique
and   tc.constraint_name = ccu.constraint_name
and   ccu.table_name = col.table_name
and   ccu.column_name = col.column_name
and   ccu.table_name in (:tableNames)
order by col.table_name,
         col.ordinal_position

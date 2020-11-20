select col.table_name,
       col.column_name,
       col.data_type,
       col.character_maximum_length,
       case
         when (col.column_name = pksubq.pk_column_name) then true
         else false
       end is_primary_key,
       case
         when col.is_nullable = 'YES' then true
         when col.is_nullable = 'NO' then false
       end is_nullable,
       case
         when col.column_default is not null then true
         else false
       end default_exists,
       case
         when col.column_default like 'nextval(%' then true
         else false
       end default_is_sequence,
       fksubq.fk_table_name,
       fksubq.fk_column_name
from information_schema.columns col
  left outer join (select kcu.table_name,
                          kcu.column_name,
                          kcu.column_name pk_column_name
                   from information_schema.table_constraints tc,
                        information_schema.key_column_usage kcu
                   where tc.constraint_name = kcu.constraint_name
                   and   tc.constraint_type = :constraintTypePk) pksubq on pksubq.table_name = col.table_name and pksubq.column_name = col.column_name
  left outer join (select kcu.table_name,
                          kcu.column_name,
                          ccu.table_name fk_table_name,
                          ccu.column_name fk_column_name
                   from information_schema.table_constraints tc,
                        information_schema.key_column_usage kcu,
                        information_schema.constraint_column_usage ccu
                   where tc.constraint_name = kcu.constraint_name
                   and   tc.constraint_name = ccu.constraint_name
                   and   kcu.column_name like ('%' || ccu.column_name)
                   and   tc.constraint_type = :constraintTypeFk) fksubq on fksubq.table_name = col.table_name and fksubq.column_name = col.column_name
where col.table_name in (:tableNames)
order by col.table_name,
         col.ordinal_position

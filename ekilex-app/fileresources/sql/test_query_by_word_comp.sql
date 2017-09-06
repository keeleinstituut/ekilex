-- search words by components prefix
select w.value word,
       w.components
from (select w.id,
             unnest(w.components) component
      from word w) unw,
     word w
where w.id = unw.id
and   unw.component like :compPrefix
order by w.value;

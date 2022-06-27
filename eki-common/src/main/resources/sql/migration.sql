-- keelendi ja tähenduse linkide formaat
update definition set value_prese = replace(value_prese, ' id=', ' data-link-id=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' link-id=', ' data-link-id=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' link-type=', ' data-link-type=') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' data-link-type=''meaning_link''', ' data-link-type="meaning"') where value_prese like '%eki-link%';
update definition set value_prese = replace(value_prese, ' data-link-type="meaning_link"', ' data-link-type="meaning"') where value_prese like '%eki-link%';

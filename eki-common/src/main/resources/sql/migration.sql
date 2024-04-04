-- estermi laadimine

update dataset set code = 'esterm' where code = 'est_test';

update
	domain cl
set
	datasets = array_append(cl.datasets, 'esterm')
where
	'est_test' = any(cl.datasets);

update
	domain cl
set
	datasets = array_remove(cl.datasets, 'est_test')
where
	'est_test' = any(cl.datasets);

-- puuduvad allikate indeksid

create index source_value_idx on source(value);
create index source_value_lower_idx on source(lower(value));
create index source_value_lower_prefix_idx on source(lower(value) text_pattern_ops);
create index source_name_lower_prefix_idx on source(lower(name) text_pattern_ops);
analyze source;

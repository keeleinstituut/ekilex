create index collocation_value_idx on collocation(value);
analyze collocation;

update word_rel_type_label set value = 'algvõrre' where code = 'posit' and lang = 'est';
update word_rel_type_label set value = 'keskvõrre' where code = 'komp' and lang = 'est';
update word_rel_type_label set value = 'ülivõrre' where code = 'superl' and lang = 'est';

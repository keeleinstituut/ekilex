-- upgrade from ver 1.15.0 to 1.16.0

update word_rel_type_label set value = 'algvõrre' where code = 'posit' and lang = 'est';
update word_rel_type_label set value = 'keskvõrre' where code = 'komp' and lang = 'est';
update word_rel_type_label set value = 'ülivõrre' where code = 'superl' and lang = 'est';

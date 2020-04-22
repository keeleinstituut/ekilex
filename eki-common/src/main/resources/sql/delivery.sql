-- upgrade from ver 1.16.0 to 1.17.0

create index collocation_value_idx on collocation(value);
analyze collocation;

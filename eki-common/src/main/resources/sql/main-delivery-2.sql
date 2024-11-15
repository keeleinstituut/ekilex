-- upgrade from ver 1.38.* to 1.39.0 #3

-- ekilexi tegevuslogi vabastamine arhiivist

alter table activity_log drop column prev_data cascade;
alter table activity_log drop column curr_data cascade;
analyze activity_log;

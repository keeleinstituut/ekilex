drop table if exists lexical_decision_result;
drop table if exists lexical_decision_data;

create table lexical_decision_data 
(
  id bigserial primary key,
  word text,
  is_word boolean
);

create table lexical_decision_result
(
  id bigserial primary key,
  data_id bigint references lexical_decision_data(id) not null,
  remote_addr text not null,
  local_addr text not null,
  session_id text not null,
  answer boolean not null,
  delay bigint not null,
  created timestamp not null default statement_timestamp()
);

insert into lexical_decision_data (word, is_word)
select * from
dblink(
  'host=localhost user=ekilex password=3kil3x dbname=ekilex',
  'select * from view_ww_lexical_decision_data') as lexical_decision_data(
  word text,
  is_word boolean
);

-- tähenduse sildid
alter table tag add column type varchar(10) not null default 'LEX';
alter table tag alter column type drop default;

create table meaning_tag
(
  id bigserial primary key,
  meaning_id bigint references meaning(id) on delete cascade not null,
  tag_name varchar(100) references tag(name) on delete cascade not null,
  created_on timestamp not null default statement_timestamp(),
  unique(meaning_id, tag_name)
);
alter sequence meaning_tag_id_seq restart with 10000;

create index meaning_tag_meaning_id_idx on meaning_tag(meaning_id);
create index meaning_tag_tag_name_idx on meaning_tag(tag_name);


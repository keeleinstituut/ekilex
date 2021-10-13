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

-- ilmiku siltide kolimine tähenduse siltideks
do $$
declare
  meaning_tag_names text[] := '{001, 002, 003, 004, 005, 006, 007, 008, 009, 010, 011, 012, 013, 014, 015,' ||
                              ' 016, 017, 018, 019, 020, 021, 022, 023, 024, 025, 026, 027, 028, 029, 030,' ||
                              ' 031, 032, 033, 034, 035, 036, 037, 038, 039, 040, 041, 042, 043, 044, 045,' ||
                              ' 046, 047, 048, 049, 050, 051, 052, 053, 054, 055, 056, 057, 058, 059, 060,' ||
                              ' 061, 062, 063, 064, 065, 066, 067, 068, 069, 070, 071, 072, 073, 074, 075,' ||
                              ' 076, 077, 078, 079, 080, 081, 082, 083, 084, 085, 086, 087, 088, 089, 090,' ||
                              ' 091, 092, 093, 094, 095, 096, 097, 098, 099, 100, 101, 102, 103, 104}';
begin
  insert into meaning_tag (meaning_id, tag_name)
  select l.meaning_id, lt.tag_name
  from lexeme l, lexeme_tag lt
  where lt.tag_name = any(meaning_tag_names) and l.id = lt.lexeme_id
  on conflict do nothing;

  delete from lexeme_tag where tag_name = any(meaning_tag_names);

  update tag set type = 'MEANING' where name = any(meaning_tag_names);
end $$;

-- ilmiku usaldusväärsus
alter table lexeme add column reliability integer null;

-- täiendavad indeksid tegevuslogide ajaloo päringuteks
create index activity_entity_id_idx on activity_log(entity_id);
create index activity_curr_data_word_id_idx on activity_log(cast(curr_data ->> 'wordId' as bigint));
create index activity_curr_data_meaning_id_idx on activity_log(cast(curr_data ->> 'meaningId' as bigint));
create index activity_curr_data_lexeme_id_idx on activity_log(cast(curr_data ->> 'lexemeId' as bigint));

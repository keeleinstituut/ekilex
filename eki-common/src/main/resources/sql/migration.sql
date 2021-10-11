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

-- html entity parandused
update freeform set value_text = replace(value_text, '&otilde;', 'õ') where value_text like '%&otilde;%';
update freeform set value_prese = replace(value_prese, '&otilde;', 'õ') where value_prese like '%&otilde;%';
update freeform set value_text = replace(value_text, '&Otilde;', 'Õ') where value_text like '%&Otilde;%';
update freeform set value_prese = replace(value_prese, '&Otilde;', 'Õ') where value_prese like '%&Otilde;%';
update definition set value = replace(value, '&otilde;', 'õ') where value like '%&otilde;%';
update definition set value_prese = replace(value_prese, '&otilde;', 'õ') where value_prese like '%&otilde;%';
update definition set value = replace(value, '&Otilde;', 'Õ') where value like '%&Otilde;%';
update definition set value_prese = replace(value_prese, '&Otilde;', 'Õ') where value_prese like '%&Otilde;%';

update freeform set value_text = replace(value_text, '&auml;', 'ä') where value_text like '%&auml;%';
update freeform set value_prese = replace(value_prese, '&auml;', 'ä') where value_prese like '%&auml;%';
update freeform set value_text = replace(value_text, '&Auml;', 'Ä') where value_text like '%&Auml;%';
update freeform set value_prese = replace(value_prese, '&Auml;', 'Ä') where value_prese like '%&Auml;%';
update definition set value = replace(value, '&auml;', 'ä') where value like '%&auml;%';
update definition set value_prese = replace(value_prese, '&auml;', 'ä') where value_prese like '%&auml;%';
update definition set value = replace(value, '&Auml;', 'Ä') where value like '%&Auml;%';
update definition set value_prese = replace(value_prese, '&Auml;', 'Ä') where value_prese like '%&Auml;%';

update freeform set value_text = replace(value_text, '&ouml;', 'ö') where value_text like '%&ouml;%';
update freeform set value_prese = replace(value_prese, '&ouml;', 'ö') where value_prese like '%&ouml;%';
update freeform set value_text = replace(value_text, '&Ouml;', 'Ö') where value_text like '%&Ouml;%';
update freeform set value_prese = replace(value_prese, '&Ouml;', 'Ö') where value_prese like '%&Ouml;%';
update definition set value = replace(value, '&ouml;', 'ö') where value like '%&ouml;%';
update definition set value_prese = replace(value_prese, '&ouml;', 'ö') where value_prese like '%&ouml;%';
update definition set value = replace(value, '&Ouml;', 'Ö') where value like '%&Ouml;%';
update definition set value_prese = replace(value_prese, '&Ouml;', 'Ö') where value_prese like '%&Ouml;%';

update freeform set value_text = replace(value_text, '&uuml;', 'ü') where value_text like '%&uuml;%';
update freeform set value_prese = replace(value_prese, '&uuml;', 'ü') where value_prese like '%&uuml;%';
update freeform set value_text = replace(value_text, '&Uuml;', 'Ü') where value_text like '%&Uuml;%';
update freeform set value_prese = replace(value_prese, '&Uuml;', 'Ü') where value_prese like '%&Uuml;%';
update definition set value = replace(value, '&uuml;', 'ü') where value like '%&uuml;%';
update definition set value_prese = replace(value_prese, '&uuml;', 'ü') where value_prese like '%&uuml;%';
update definition set value = replace(value, '&Uuml;', 'Ü') where value like '%&Uuml;%';
update definition set value_prese = replace(value_prese, '&Uuml;', 'Ü') where value_prese like '%&Uuml;%';

update freeform set value_text = replace(value_text, '&scaron;', 'š') where value_text like '%&scaron;%';
update freeform set value_prese = replace(value_prese, '&scaron;', 'š') where value_prese like '%&scaron;%';
update freeform set value_text = replace(value_text, '&Scaron;', 'Š') where value_text like '%&Scaron;%';
update freeform set value_prese = replace(value_prese, '&Scaron;', 'Š') where value_prese like '%&Scaron;%';
update definition set value = replace(value, '&scaron;', 'š') where value like '%&scaron;%';
update definition set value_prese = replace(value_prese, '&scaron;', 'š') where value_prese like '%&scaron;%';
update definition set value = replace(value, '&Scaron;', 'Š') where value like '%&Scaron;%';
update definition set value_prese = replace(value_prese, '&Scaron;', 'Š') where value_prese like '%&Scaron;%';

update freeform set value_text = replace(value_text, '&ntilde;', 'ñ') where value_text like '%&ntilde;%';
update freeform set value_prese = replace(value_prese, '&ntilde;', 'ñ') where value_prese like '%&ntilde;%';
update definition set value = replace(value, '&ntilde;', 'ñ') where value like '%&ntilde;%';
update definition set value_prese = replace(value_prese, '&ntilde;', 'ñ') where value_prese like '%&ntilde;%';

update freeform set value_text = replace(value_text, '&quot;', '"') where value_text like '%&quot;%';
update freeform set value_prese = replace(value_prese, '&quot;', '"') where value_prese like '%&quot;%';
update definition set value = replace(value, '&quot;', '"') where value like '%&quot;%';
update definition set value_prese = replace(value_prese, '&quot;', '"') where value_prese like '%&quot;%';

update freeform set value_text = replace(value_text, '&ndash;', '–') where value_text like '%&ndash;%';
update freeform set value_prese = replace(value_prese, '&ndash;', '–') where value_prese like '%&ndash;%';
update definition set value = replace(value, '&ndash;', '–') where value like '%&ndash;%';
update definition set value_prese = replace(value_prese, '&ndash;', '–') where value_prese like '%&ndash;%';

update freeform set value_text = replace(value_text, '&gt;', '>') where value_text like '%&gt;%';
update freeform set value_prese = replace(value_prese, '&gt;', '>') where value_prese like '%&gt;%';
update definition set value = replace(value, '&gt;', '>') where value like '%&gt;%';
update definition set value_prese = replace(value_prese, '&gt;', '>') where value_prese like '%&gt;%';

update freeform set value_text = replace(value_text, '&lt;', '<') where value_text like '%&lt;%';
update freeform set value_prese = replace(value_prese, '&lt;', '<') where value_prese like '%&lt;%';
update definition set value = replace(value, '&lt;', '<') where value like '%&lt;%';
update definition set value_prese = replace(value_prese, '&lt;', '<') where value_prese like '%&lt;%';

update freeform set value_text = replace(value_text, '&#39;', '''') where value_text like '%&#39;%';
update freeform set value_prese = replace(value_prese, '&#39;', '''') where value_prese like '%&#39;%';
update definition set value = replace(value, '&#39;', '''') where value like '%&#39;%';
update definition set value_prese = replace(value_prese, '&#39;', '''') where value_prese like '%&#39;%';

update freeform set value_text = replace(value_text, '&ldquo;', '“') where value_text like '%&ldquo;%';
update freeform set value_prese = replace(value_prese, '&ldquo;', '“') where value_prese like '%&ldquo;%';
update definition set value = replace(value, '&ldquo;', '“') where value like '%&ldquo;%';
update definition set value_prese = replace(value_prese, '&ldquo;', '“') where value_prese like '%&ldquo;%';

update freeform set value_text = replace(value_text, '&rdquo;', '”') where value_text like '%&rdquo;%';
update freeform set value_prese = replace(value_prese, '&rdquo;', '”') where value_prese like '%&rdquo;%';
update definition set value = replace(value, '&rdquo;', '”') where value like '%&rdquo;%';
update definition set value_prese = replace(value_prese, '&rdquo;', '”') where value_prese like '%&rdquo;%';

update freeform set value_text = replace(value_text, '&lsquo;', '‘') where value_text like '%&lsquo;%';
update freeform set value_prese = replace(value_prese, '&lsquo;', '‘') where value_prese like '%&lsquo;%';
update definition set value = replace(value, '&lsquo;', '‘') where value like '%&lsquo;%';
update definition set value_prese = replace(value_prese, '&lsquo;', '‘') where value_prese like '%&lsquo;%';

update freeform set value_text = replace(value_text, '&rsquo;', '’') where value_text like '%&rsquo;%';
update freeform set value_prese = replace(value_prese, '&rsquo;', '’') where value_prese like '%&rsquo;%';
update definition set value = replace(value, '&rsquo;', '’') where value like '%&rsquo;%';
update definition set value_prese = replace(value_prese, '&rsquo;', '’') where value_prese like '%&rsquo;%';

update freeform set value_text = replace(value_text, '&laquo;', '«') where value_text like '%&laquo;%';
update freeform set value_prese = replace(value_prese, '&laquo;', '«') where value_prese like '%&laquo;%';
update definition set value = replace(value, '&laquo;', '«') where value like '%&laquo;%';
update definition set value_prese = replace(value_prese, '&laquo;', '«') where value_prese like '%&laquo;%';

update freeform set value_text = replace(value_text, '&raquo;', '»') where value_text like '%&raquo;%';
update freeform set value_prese = replace(value_prese, '&raquo;', '»') where value_prese like '%&raquo;%';
update definition set value = replace(value, '&raquo;', '»') where value like '%&raquo;%';
update definition set value_prese = replace(value_prese, '&raquo;', '»') where value_prese like '%&raquo;%';

update freeform set value_text = replace(value_text, '&bdquo;', '„') where value_text like '%&bdquo;%';
update freeform set value_prese = replace(value_prese, '&bdquo;', '„') where value_prese like '%&bdquo;%';
update definition set value = replace(value, '&bdquo;', '„') where value like '%&bdquo;%';
update definition set value_prese = replace(value_prese, '&bdquo;', '„') where value_prese like '%&bdquo;%';

update freeform set value_text = replace(value_text, '&nbsp;', ' ') where value_text like '%&nbsp;%';
update freeform set value_prese = replace(value_prese, '&nbsp;', ' ') where value_prese like '%&nbsp;%';
update definition set value = replace(value, '&nbsp;', ' ') where value like '%&nbsp;%';
update definition set value_prese = replace(value_prese, '&nbsp;', ' ') where value_prese like '%&nbsp;%';
update word set value = replace(value, '&nbsp;', ' ') where value like '%&nbsp;%';
update word set value_prese = replace(value_prese, '&nbsp;', ' ') where value_prese like '%&nbsp;%';

-- eemaldab tühikud keelendi ees ja lõpus
update word set value = trim(value), value_prese = trim(value_prese);
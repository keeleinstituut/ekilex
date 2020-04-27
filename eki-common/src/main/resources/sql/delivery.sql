-- upgrade from ver 1.16.0 to 1.17.0

create index collocation_value_idx on collocation(value);
analyze collocation;

-- muudab sellised detailsed ilmikud lihtsaks, millel leidub lihtsaid kasutusnäiteid
update lexeme l
set complexity = 'SIMPLE'
where l.dataset_code = 'sss'
  and l.type = 'PRIMARY'
  and l.complexity like 'DETAIL%'
  and exists(select w.id
             from word w
             where w.id = l.word_id
               and w.lang in ('est', 'rus'))
  and exists(select ff.id
             from freeform ff,
                  lexeme_freeform lff
             where lff.lexeme_id = l.id
               and lff.freeform_id = ff.id
               and ff.type = 'USAGE'
               and ff.complexity in ('SIMPLE', 'SIMPLE1'));

update freeform
set value_prese = value_prese || '.jpg'
where type = 'IMAGE_FILE'
  and value_prese not like '%.%';

update freeform
set value_text = value_text || '.jpg'
where type = 'IMAGE_FILE'
  and value_text not like '%.%';
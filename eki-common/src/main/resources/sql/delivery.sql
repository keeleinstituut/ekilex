-- upgrade from ver 1.31.0 to 1.31.1

-- Keelendite ühendamise tõttu muutunud militerm ilmikute avalikkuse taastamine
insert into tag (name, type) values ('mil muudetud mitteavalikuks', 'LEX');

insert into lexeme_tag (lexeme_id, tag_name)
select l1.id, 'mil muudetud mitteavalikuks'
from activity_log al,
     lexeme l1
where al.funct_name = 'joinWords'
  and al.owner_id = l1.word_id
  and l1.dataset_code = 'mil'
  and l1.is_public = true
  and exists (select l2.id
              from lexeme l2
              where l2.meaning_id = l1.meaning_id
                and l2.dataset_code = 'mil'
                and l2.is_public = false)
group by l1.id;

update lexeme l
set is_public = false
where exists(select lt.id
             from lexeme_tag lt
             where lt.lexeme_id = l.id
               and lt.tag_name = 'mil muudetud mitteavalikuks');
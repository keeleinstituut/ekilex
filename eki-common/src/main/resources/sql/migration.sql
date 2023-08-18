-- recreate types and views

-- Kasutusnäidete allikaviidete puuduvate väärtuste taastamine
update freeform_source_link ffsl
set value = s.source_name
from (select sff.source_id,
             array_to_string(array_agg(distinct snff.value_text), ', ', '*') source_name
      from source_freeform sff,
           freeform snff
      where sff.freeform_id = snff.id
        and snff.type = 'SOURCE_NAME'
      group by sff.source_id) s
where ffsl.value is null
  and ffsl.source_id = s.source_id;

-- Etümoloogia topelt kirjete ühendamine
insert into etymology_type (code, datasets) values ('ühtlustamisel', '{}');
insert into tag (name, type) values ('etümoloogia üle vaadata', 'LEX');

do $$
declare
  word_etym_row             record;
  duplicate_word_etym_row   record;
  etymology_type_codes      varchar(100)[];
  word_etym_comment         text;
  word_etym_comment_prese   text;
  is_word_etym_questionable boolean;
  etym_word_first_lexeme_id bigint;
begin
  for word_etym_row in
    (select we.id,
            we.word_id,
            we.etymology_type_code,
            we.comment,
            we.comment_prese,
            we.is_questionable
     from word_etymology we
     where exists (select we2.id
                   from word_etymology we2
                   where we2.word_id = we.word_id
                     and we2.order_by > we.order_by)
       and not exists(select we3.id
                      from word_etymology we3
                      where we3.word_id = we.word_id
                        and we3.order_by < we.order_by))
    loop
      etymology_type_codes := array []::text[];
      word_etym_comment = word_etym_row.comment;
      word_etym_comment_prese = word_etym_row.comment_prese;
      is_word_etym_questionable = word_etym_row.is_questionable;

      if word_etym_row.etymology_type_code is not null then
        etymology_type_codes := array_append(etymology_type_codes, word_etym_row.etymology_type_code);
      end if;

      for duplicate_word_etym_row in
        (select we.id,
                we.word_id,
                we.etymology_type_code,
                we.comment,
                we.comment_prese,
                we.is_questionable
         from word_etymology we
         where word_etym_row.word_id = we.word_id
           and word_etym_row.id != we.id)
        loop

          if duplicate_word_etym_row.comment is not null then
            word_etym_comment := concat(word_etym_comment, '; ', duplicate_word_etym_row.comment);
            word_etym_comment_prese := concat(word_etym_comment_prese, '; ', duplicate_word_etym_row.comment_prese);
          end if;

          if duplicate_word_etym_row.etymology_type_code is not null then
            if duplicate_word_etym_row.etymology_type_code != any (etymology_type_codes) then
              etymology_type_codes := array_append(etymology_type_codes, duplicate_word_etym_row.etymology_type_code);
            end if;
          end if;

          if duplicate_word_etym_row.is_questionable is true then
            is_word_etym_questionable := true;
          end if;

          delete from word_etymology where id = duplicate_word_etym_row.id;
        end loop;

      if array_length(etymology_type_codes, 1) = 1 then
        update word_etymology set etymology_type_code = etymology_type_codes[1] where id = word_etym_row.id;
      end if;

      if array_length(etymology_type_codes, 1) > 1 then
        word_etym_comment := concat(word_etym_comment, '; Päritolu liigid: ', array_to_string(etymology_type_codes, ', '));
        word_etym_comment_prese := concat(word_etym_comment_prese, '; Päritolu liigid: ', array_to_string(etymology_type_codes, ', '));
        update word_etymology set etymology_type_code = 'ühtlustamisel' where id = word_etym_row.id;
      end if;

      update word_etymology set comment = word_etym_comment where id = word_etym_row.id;
      update word_etymology set comment_prese = word_etym_comment_prese where id = word_etym_row.id;
      update word_etymology set is_questionable = is_word_etym_questionable where id = word_etym_row.id;

      select l.id
      from lexeme l, dataset d
      where l.word_id = word_etym_row.word_id and l.dataset_code = d.code
      order by d.order_by, l.level1, l.level2
      limit 1
      into etym_word_first_lexeme_id;

      insert into lexeme_tag (lexeme_id, tag_name) values (etym_word_first_lexeme_id, 'etümoloogia üle vaadata');
    end loop;
end $$;
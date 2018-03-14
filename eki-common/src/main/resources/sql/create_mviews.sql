drop materialized view if exists mview_ww_word cascade;
drop materialized view if exists mview_ww_form cascade;
drop materialized view if exists mview_ww_meaning cascade;

create materialized view mview_ww_word
as
select w.word_id,
       w.word,
       w.homonym_nr,
       w.lang,
       w.morph_code,
       w.display_morph_code,
       (select array_agg(distinct ld.dataset_code)
        from lexeme ld
        where ld.word_id = w.word_id
        group by w.word_id) as dataset_codes,
       mc.meaning_count,
       mw.meaning_words
from (select w.id as word_id,
             array_to_string(array_agg(distinct f.value),',','*') as word,
             w.homonym_nr,
             w.lang,
             w.morph_code,
             w.display_morph_code
      from word as w
        join paradigm as p on p.word_id = w.id
        join form as f on f.paradigm_id = p.id and f.is_word = true
      where exists (select ld.id
                    from lexeme as ld
                    where (ld.word_id = w.id and ld.dataset_code in ('qq2', 'psv', 'ss1', 'kol')))
      group by w.id) as w
  inner join (select mc.word_id,
                     count(mc.meaning_id) meaning_count
              from (select l.word_id,
                           l.meaning_id
                    from lexeme l
                    where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
                    group by l.word_id,
                             l.meaning_id) mc
              group by mc.word_id) mc on mc.word_id = w.word_id
  left outer join (select l1.word_id,
                          array_agg(distinct f2.value) meaning_words
                   from lexeme l1,
                        lexeme l2,
                        form f2,
                        paradigm p2,
                        meaning m
                   where l1.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
                   and   l1.meaning_id = m.id
                   and   l2.meaning_id = m.id
                   --and   l1.word_id != l2.word_id
                   and   p2.word_id = l2.word_id
                   and   f2.paradigm_id = p2.id
                   and   f2.is_word = true
                   group by l1.word_id) mw on mw.word_id = w.word_id;

create materialized view mview_ww_form
as
select p.word_id,
       p.id paradigm_id,
       f.id form_id,
       f.value form,
       f.morph_code,
       f.components,
       f.display_form,
       f.vocal_form,
       f.sound_file,
       f.is_word
from paradigm p,
     form f
where f.paradigm_id = p.id
and   exists (select ld.id
              from lexeme as ld
              where (ld.word_id = p.word_id and ld.dataset_code in ('qq2', 'psv', 'ss1', 'kol')))
order by f.id;

create materialized view mview_ww_meaning
as
select l.word_id,
       l.meaning_id,
       l.id lexeme_id,
       d.id definition_id,
       l.dataset_code,
       l.level1,
       l.level2,
       l.level3,
       l_type.code lexeme_type_code,
       l_type.value lexeme_type_value,
       l_type.lang lexeme_type_lang,
       reg.code register_code,
       reg.value register_value,
       reg.lang register_lang,
       pos.code pos_code,
       pos.value pos_value,
       pos.lang pos_lang,
       der.code deriv_code,
       der.value deriv_value,
       der.lang deriv_lang,
       dom.origin domain_origin,
       dom.code domain_code,
       dom.value domain_value,
       dom.lang domain_lang,
       d.value definition,
       d.lang definition_lang
from lexeme l
  left outer join lexeme_type_label l_type on l.type_code = l_type.code and l_type.type = 'descrip'
  left outer join lexeme_register l_reg on l_reg.lexeme_id = l.id
  left outer join register_label reg on reg.code = l_reg.register_code and reg.type = 'descrip'
  left outer join lexeme_pos l_pos on l_pos.lexeme_id = l.id
  left outer join pos_label pos on pos.code = l_pos.pos_code and pos.type = 'descrip'
  left outer join lexeme_deriv l_der on l_der.lexeme_id = l.id
  left outer join deriv_label der on der.code = l_der.deriv_code and der.type = 'descrip'
  inner join meaning m on l.meaning_id = m.id
  left outer join meaning_domain m_dom on m_dom.meaning_id = m.id
  left outer join domain_label dom on dom.origin = m_dom.domain_origin and dom.code = m_dom.domain_code and dom.type = 'descrip'
  left outer join definition d on d.meaning_id = m.id
where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
order by l.word_id,
         l.meaning_id,
         d.id,
         l.id,
         l_reg.order_by,
         l_pos.order_by,
         m_dom.order_by;

-- not really required at ekilex
create index mview_ww_word_word_id_idx on mview_ww_word (word_id);
create index mview_ww_form_word_id_idx on mview_ww_form (word_id);
create index mview_ww_form_value_idx on mview_ww_form (form);
create index mview_ww_form_value_lower_idx on mview_ww_form (lower(form));
create index mview_ww_meaning_word_id_idx on mview_ww_meaning (word_id);
create index mview_ww_meaning_meaning_id_idx on mview_ww_meaning (meaning_id);
create index mview_ww_meaning_lexeme_id_idx on mview_ww_meaning (lexeme_id);
create index mview_ww_meaning_lexeme_type_lang_idx on mview_ww_meaning (lexeme_type_lang);
create index mview_ww_meaning_register_lang_idx on mview_ww_meaning (register_lang);
create index mview_ww_meaning_pos_lang_idx on mview_ww_meaning (pos_lang);
create index mview_ww_meaning_deriv_lang_idx on mview_ww_meaning (deriv_lang);
create index mview_ww_meaning_domain_lang_idx on mview_ww_meaning (domain_lang);
create index mview_ww_meaning_definition_lang_idx on mview_ww_meaning (definition_lang);

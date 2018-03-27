create type type_definition as (value text, lang char(3));

create view view_ww_word 
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
       mw.meaning_words,
       wd.definitions
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
  left outer join (select mw.word_id,
                          array_agg(mw.meaning_word order by mw.order_by) meaning_words
                   from (select l1.word_id,
                                (l1.word_id || '_' || l1.dataset_code || '_' || l1.level1 || '_' || l1.level2 || '_' || l1.level3) order_by,
                                f2.value meaning_word
                         from lexeme l1,
                              lexeme l2,
                              form f2,
                              paradigm p2,
                              meaning m
                         where l1.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
                         and   l1.meaning_id = m.id
                         and   l2.meaning_id = m.id
                         and   l1.word_id != l2.word_id
                         and   p2.word_id = l2.word_id
                         and   f2.paradigm_id = p2.id
                         and   f2.is_word = true) mw
                   group by mw.word_id) mw on mw.word_id = w.word_id
  left outer join (select wd.word_id,
                          array_agg(row (wd.value,wd.lang)::type_definition) definitions
                   from (select l.word_id,
                                d.value,
                                d.lang
                         from lexeme l,
                              meaning m,
                              definition d
                         where l.meaning_id = m.id
                         and   d.meaning_id = m.id
                         order by l.dataset_code,
                                  l.level1,
                                  l.level2,
                                  l.level3,
                                  d.order_by) wd
                   group by wd.word_id) wd on wd.word_id = w.word_id;

create view view_ww_form
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

create type type_domain as (origin varchar(100), code varchar(100));

create view view_ww_meaning
  as
    select l.word_id,
      l.meaning_id,
      l.id lexeme_id,
      d.id definition_id,
      l.dataset_code,
      l.level1,
      l.level2,
      l.level3,
      (select array_agg(l_reg.register_code order by l_reg.order_by)
       from lexeme_register l_reg
       where l_reg.lexeme_id = l.id
       group by l_reg.lexeme_id) register_codes,
      (select array_agg(l_pos.pos_code order by l_pos.order_by)
       from lexeme_pos l_pos
       where l_pos.lexeme_id = l.id
       group by l_pos.lexeme_id) pos_codes,
      (select array_agg(l_der.deriv_code)
       from lexeme_deriv l_der
       where l_der.lexeme_id = l.id
       group by l_der.lexeme_id) deriv_codes,
      (select array_agg(row (m_dom.domain_origin,m_dom.domain_code)::type_domain order by m_dom.order_by)
       from meaning_domain m_dom
       where m_dom.meaning_id = m.id
       group by m_dom.meaning_id) domain_codes,
      d.value definition,
      d.lang definition_lang
    from lexeme l
      left outer join lexeme_deriv l_der on l_der.lexeme_id = l.id
      inner join meaning m on l.meaning_id = m.id
      left outer join definition d on d.meaning_id = m.id
    where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
    order by l.word_id,
      l.meaning_id,
      d.id,
      l.id;

create type type_usage as (usage text, usage_author text, usage_translator text);

create view view_ww_lexeme 
  as
    select l.id lexeme_id,
           l.word_id,
           l.meaning_id,
           anote.advice_notes,
           pnote.public_notes,
           gramm.grammars,
           gov.government_id,
           gov.government,
           gov.usage_meaning_id,
           gov.usage_meaning_type_code,
           gov.usages,
           gov.usage_translations,
           gov.usage_definitions
    from lexeme l
      left outer join (select lf.lexeme_id,
                              array_agg(ff.value_text order by ff.order_by) advice_notes
                       from lexeme_freeform lf,
                            freeform ff
                       where lf.freeform_id = ff.id
                       and   ff.type = 'ADVICE_NOTE'
                       group by lf.lexeme_id) anote on anote.lexeme_id = l.id
      left outer join (select lf.lexeme_id,
                              array_agg(ff.value_text order by ff.order_by) public_notes
                       from lexeme_freeform lf,
                            freeform ff
                       where lf.freeform_id = ff.id
                       and   ff.type = 'PUBLIC_NOTE'
                       group by lf.lexeme_id) pnote on pnote.lexeme_id = l.id
      left outer join (select lf.lexeme_id,
                              array_agg(ff.value_text order by ff.order_by) grammars
                       from lexeme_freeform lf,
                            freeform ff
                       where lf.freeform_id = ff.id
                       and   ff.type = 'GRAMMAR'
                       group by lf.lexeme_id) gramm on gramm.lexeme_id = l.id
      left outer join (select lf.lexeme_id,
                              gov.id government_id,
                              gov.value_text government,
                              umn.id usage_meaning_id,
                              umnt.classif_code usage_meaning_type_code,
                              usge.usages,
                              utrl.usage_translations,
                              udef.usage_definitions
                       from lexeme_freeform lf
                         inner join freeform gov on lf.freeform_id = gov.id and gov.type = 'GOVERNMENT'
                         left outer join freeform umn on umn.parent_id = gov.id and umn.type = 'USAGE_MEANING'
                         left outer join freeform umnt on umnt.parent_id = umn.id and umnt.type = 'USAGE_TYPE'
                         left outer join (select usm.parent_id usage_meaning_id,
                                                 array_agg(row (usm.usage,usm.usage_author,usm.usage_translator)::type_usage order by usm.order_by) usages
                                          from (select usm.parent_id,
                                                       usm.value_text usage,
                                                       usmau.value_text usage_author,
                                                       usmtr.value_text usage_translator,
                                                       usm.order_by
                                                from freeform usm
                                                  -- usage author context will be coming from ref link
                                                  left outer join freeform usmau on usmau.parent_id = usm.id and usmau.type = 'USAGE_AUTHOR'
                                                  left outer join freeform usmtr on usmtr.parent_id = usm.id and usmtr.type = 'USAGE_TRANSLATOR'
                                                where usm.type = 'USAGE') usm
                                          group by usm.parent_id) usge on usge.usage_meaning_id = umn.id
                         left outer join (select usm.parent_id usage_meaning_id,
                                                 array_agg(usm.value_text order by usm.order_by) usage_translations
                                          from freeform usm
                                          where usm.type = 'USAGE_TRANSLATION'
                                          group by usm.parent_id) utrl on utrl.usage_meaning_id = umn.id
                         left outer join (select usm.parent_id usage_meaning_id,
                                                 array_agg(usm.value_text order by usm.order_by) usage_definitions
                                          from freeform usm
                                          where usm.type = 'USAGE_DEFINITION'
                                          group by usm.parent_id) udef on udef.usage_meaning_id = umn.id) gov on gov.lexeme_id = l.id
    where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
    order by l.id;

create view view_ww_dataset
  as
    (select
       code,
       name,
       description,
       'est' as lang
     from dataset
     where code in ('qq2', 'psv', 'ss1', 'kol')
    );

create view view_ww_classifier
  as
    (select 'MORPH' as name,
            null as origin,
       code,
       value,
       lang
     from morph_label
     union all
     select 'POS' as name,
            null as origin,
       code,
       value,
       lang
     from pos_label
     union all
     select 'REGISTER' as name,
            null as origin,
       code,
       value,
       lang
     from register_label
     union all
     select 'DERIV' as name,
            null as origin,
       code,
       value,
       lang
     from deriv_label
     union all
     select 'DOMAIN' as name,
       origin,
       code,
       value,
       lang
     from domain_label);

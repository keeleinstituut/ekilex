create type type_word as (value text, lang char(3));
create type type_definition as (value text, lang char(3));
create type type_domain as (origin varchar(100), code varchar(100));
create type type_usage as (usage text, usage_lang char(3), usage_type_code varchar(100), usage_translations text array, usage_definitions text array, usage_authors text array);
create type type_colloc_member as (lexeme_id bigint, word_id bigint, word text);
create type type_word_relation as (word_id bigint, word text, word_lang char(3), word_rel_type_code varchar(100));
create type type_lexeme_relation as (lexeme_id bigint, word_id bigint, word text, word_lang char(3), lex_rel_type_code varchar(100));
create type type_meaning_relation as (meaning_id bigint, lexeme_id bigint, word_id bigint, word text, word_lang char(3), meaning_rel_type_code varchar(100));

-- words
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
                          array_agg(row(mw.meaning_word_value, mw.meaning_word_lang)::type_word order by mw.order_by) meaning_words
                   from (select distinct
                                l1.word_id,
                                f2.value meaning_word_value,
                                w2.lang meaning_word_lang,
                                (l2.dataset_code || '_' || l2.level1 || '_' || l2.level2 || '_' || l2.level3 || '_' || w2.id) order_by
                         from lexeme l1,
                              lexeme l2,
                              form f2,
                              paradigm p2,
                              word w2
                         where l1.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
                         and   l1.meaning_id = l2.meaning_id
                         and   l1.word_id != w2.id
                         and   l2.word_id = w2.id
                         and   p2.word_id = w2.id
                         and   f2.paradigm_id = p2.id
                         and   f2.is_word = true) mw
                   group by mw.word_id) mw on mw.word_id = w.word_id
  left outer join (select wd.word_id,
                          array_agg(row (wd.value,wd.lang)::type_definition order by wd.dataset_code, wd.level1, wd.level2, wd.level3, wd.order_by) definitions
                   from (select l.word_id,
                                l.dataset_code,
                                l.level1,
                                l.level2,
                                l.level3,
                                d.value,
                                d.lang,
                                d.order_by
                         from lexeme l,
                              definition d
                         where l.meaning_id = d.meaning_id
                         order by l.dataset_code,
                                  l.level1,
                                  l.level2,
                                  l.level3,
                                  d.order_by) wd
                   group by wd.word_id) wd on wd.word_id = w.word_id;

-- word forms
create view view_ww_form
  as
    select
      w.id word_id,
      fw.value word,
      w.lang,
      p.id paradigm_id,
      ff.id form_id,
      ff.value form,
      ff.morph_code,
      ff.components,
      ff.display_form,
      ff.vocal_form,
      ff.sound_file,
      ff.is_word,
      (select
        array_agg(ld.dataset_code) 
      from
        lexeme ld
      where
        ld.word_id = w.id
        group by ld.word_id
      ) dataset_codes
    from
      word w,
      paradigm p,
      form ff,
      form fw
    where p.word_id = w.id
          and ff.paradigm_id = p.id
          and fw.paradigm_id = p.id
          and fw.is_word = true
          and exists (select ld.id
                        from lexeme as ld
                        where (ld.word_id = w.id and ld.dataset_code in ('qq2', 'psv', 'ss1', 'kol')))
    order by ff.id;

-- lexeme meanings
create view view_ww_meaning 
  as
    select l.word_id,
           l.meaning_id,
           l.id lexeme_id,
           l.dataset_code,
           l.level1,
           l.level2,
           l.level3,
           l_reg.register_codes,
           l_pos.pos_codes,
           l_der.deriv_codes,
           m_dom.domain_codes,
           m_img.image_files,
           m_spp.systematic_polysemy_patterns,
           m_smt.semantic_types,
           m_lcm.learner_comments,
           d.definitions
    from lexeme l
      left outer join (select l_reg.lexeme_id,
                              array_agg(l_reg.register_code order by l_reg.order_by) register_codes
                       from lexeme_register l_reg
                       group by l_reg.lexeme_id) l_reg on l_reg.lexeme_id = l.id
      left outer join (select l_pos.lexeme_id,
                              array_agg(l_pos.pos_code order by l_pos.order_by) pos_codes
                       from lexeme_pos l_pos
                       group by l_pos.lexeme_id) l_pos on l_pos.lexeme_id = l.id
      left outer join (select l_der.lexeme_id,
                              array_agg(l_der.deriv_code) deriv_codes
                       from lexeme_deriv l_der
                       group by l_der.lexeme_id) l_der on l_der.lexeme_id = l.id
      left outer join (select m_dom.meaning_id,
                              array_agg(row (m_dom.domain_origin,m_dom.domain_code)::type_domain order by m_dom.order_by) domain_codes
                       from meaning_domain m_dom
                       group by m_dom.meaning_id) m_dom on m_dom.meaning_id = l.meaning_id
      left outer join (select d.meaning_id,
                              array_agg(row (d.value,d.lang)::type_definition order by d.order_by) definitions
                       from definition d
                       group by d.meaning_id) d on d.meaning_id = l.meaning_id
      left outer join (select mf.meaning_id,
                              array_agg(ff.value_text order by ff.order_by) image_files
                       from meaning_freeform mf,
                            freeform ff
                       where mf.freeform_id = ff.id
                       and   ff.type = 'IMAGE_FILE'
                       group by mf.meaning_id) m_img on m_img.meaning_id = l.meaning_id
      left outer join (select mf.meaning_id,
                              array_agg(ff.value_text order by ff.order_by) systematic_polysemy_patterns
                       from meaning_freeform mf,
                            freeform ff
                       where mf.freeform_id = ff.id
                       and   ff.type = 'SYSTEMATIC_POLYSEMY_PATTERN'
                       group by mf.meaning_id) m_spp on m_spp.meaning_id = l.meaning_id
      left outer join (select mf.meaning_id,
                              array_agg(ff.value_text order by ff.order_by) semantic_types
                       from meaning_freeform mf,
                            freeform ff
                       where mf.freeform_id = ff.id
                       and   ff.type = 'SEMANTIC_TYPE'
                       group by mf.meaning_id) m_smt on m_smt.meaning_id = l.meaning_id
      left outer join (select mf.meaning_id,
                              array_agg(ff.value_text order by ff.order_by) learner_comments
                       from meaning_freeform mf,
                            freeform ff
                       where mf.freeform_id = ff.id
                       and   ff.type = 'LEARNER_COMMENT'
                       group by mf.meaning_id) m_lcm on m_lcm.meaning_id = l.meaning_id
    where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
    order by l.word_id,
             l.meaning_id,
             l.id;

-- lexeme details
create view view_ww_lexeme 
  as
    select l.id lexeme_id,
           l.word_id,
           l.meaning_id,
           anote.advice_notes,
           pnote.public_notes,
           gramm.grammars,
           gov.governments,
           usg.usages
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
                              array_agg(ff.value_text order by ff.order_by) governments
                       from lexeme_freeform lf,
                            freeform ff
                       where lf.freeform_id = ff.id
                       and   ff.type = 'GOVERNMENT'
                       group by lf.lexeme_id) gov on gov.lexeme_id = l.id
      left outer join (select u.lexeme_id,
                              array_agg(row (u.usage,u.usage_lang,u.usage_type_code,u.usage_translations,u.usage_definitions,u.usage_authors)::type_usage order by u.order_by) usages
                       from (select lf.lexeme_id,
                                    u.value_text usage,
                                    u.lang usage_lang,
                                    u.order_by,
                                    utp.classif_code usage_type_code,
                                    ut.usage_translations,
                                    ud.usage_definitions,
                                    ua.usage_authors
                             from lexeme_freeform lf
                               inner join freeform u on lf.freeform_id = u.id and u.type = 'USAGE'
                               left outer join freeform utp on utp.parent_id = u.id and utp.type = 'USAGE_TYPE'
                               left outer join (select ut.parent_id usage_id,
                                                       array_agg(ut.value_text order by ut.order_by) usage_translations
                                                from freeform ut
                                                where ut.type = 'USAGE_TRANSLATION'
                                                group by ut.parent_id) ut on ut.usage_id = u.id
                               left outer join (select ud.parent_id usage_id,
                                                       array_agg(ud.value_text order by ud.order_by) usage_definitions
                                                from freeform ud
                                                where ud.type = 'USAGE_DEFINITION'
                                                group by ud.parent_id) ud on ud.usage_id = u.id
                               left outer join (select uasl.freeform_id usage_id,
                                                       array_agg((uasl.type || '|' || uas.person_name) order by uasl.order_by) usage_authors
                                                from freeform_source_link uasl
                                                  inner join (select s.id,
                                                                     array_to_string(array_agg(ff.value_text),', ','*') person_name
                                                              from source s,
                                                                   source_freeform sff,
                                                                   freeform ff
                                                              where s.type = 'PERSON'
                                                              and   sff.source_id = s.id
                                                              and   sff.freeform_id = ff.id
                                                              and   ff.type = 'SOURCE_NAME'
                                                              group by s.id) uas on uas.id = uasl.source_id
                                                group by uasl.freeform_id) ua on ua.usage_id = u.id) u
                       group by u.lexeme_id) usg on usg.lexeme_id = l.id
    where l.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
    order by l.id;

-- collocations
create view view_ww_collocation 
  as
    select
      l1.id as lexeme_id,
      l1.word_id,
      l1.dataset_code,
      l1.level1,
      l1.level2,
      l1.level3,
      pgr1.id as pos_group_id,
      pgr1.pos_group_code,
      pgr1.order_by as pos_group_order_by,
      rgr1.id as rel_group_id,
      rgr1.name as rel_group_name,
      rgr1.order_by as rel_group_order_by,
      lc1.group_order as colloc_group_order,
      c.id as colloc_id,
      c.value as colloc_value,
      c.definition as colloc_definition,
      c.usages as colloc_usages,
      array_agg(row(l2.id, l2.word_id, f2.value)::type_colloc_member order by lc2.member_order) as colloc_members
    from
      collocation as c
      inner join lex_colloc as lc1 on lc1.collocation_id = c.id
      inner join lex_colloc as lc2 on lc2.collocation_id = c.id and lc2.lexeme_id <> lc1.lexeme_id
      inner join lexeme as l1 on l1.id = lc1.lexeme_id
      inner join lexeme as l2 on l2.id = lc2.lexeme_id
      inner join paradigm as p2 on p2.word_id = l2.word_id
      inner join form as f2 on f2.paradigm_id = p2.id and f2.is_word = true
      left outer join lex_colloc_rel_group as rgr1 on lc1.rel_group_id = rgr1.id
      left outer join lex_colloc_pos_group as pgr1 on pgr1.id = rgr1.pos_group_id
    group by
      l1.id,
      c.id,
      pgr1.id,
      rgr1.id,
      lc1.id
    order by
      l1.level1,
      l1.level2,
      l1.level3,
      pgr1.order_by,
      rgr1.order_by,
      lc1.group_order,
      c.id;

-- word relations
create view view_ww_word_relation 
  as
    select w1.id word_id,
           array_agg(row (w2.related_word_id,w2.related_word,w2.related_word_lang,w2.word_rel_type_code)::type_word_relation order by w2.word_rel_order_by) related_words
    from word w1
      inner join (select r.word1_id,
                         r.word2_id related_word_id,
                         r.word_rel_type_code,
                         r.order_by word_rel_order_by,
                         f2.value related_word,
                         w2.lang related_word_lang
                  from word_relation r,
                       word w2,
                       paradigm p2,
                       form f2
                  where r.word2_id = w2.id
                  and   p2.word_id = w2.id
                  and   f2.paradigm_id = p2.id
                  and   f2.is_word = true
                  and   exists (select l2.id
                                from lexeme l2
                                where l2.word_id = w2.id
                                and   l2.dataset_code in ('qq2', 'psv', 'ss1', 'kol'))) w2 on w2.word1_id = w1.id
    where exists (select l1.id
                  from lexeme l1
                  where l1.word_id = w1.id
                  and   l1.dataset_code in ('qq2', 'psv', 'ss1', 'kol'))
    group by w1.id;

-- lexeme relations
create view view_ww_lexeme_relation 
  as
    select r.lexeme1_id lexeme_id,
           array_agg(row (l2.related_lexeme_id,l2.related_word_id,l2.related_word,l2.related_word_lang,r.lex_rel_type_code)::type_lexeme_relation order by r.order_by) related_lexemes
    from lex_relation r
      inner join (select l2.id related_lexeme_id,
                         w2.id related_word_id,
                         w2.lang related_word_lang,
                         f2.value related_word
                  from lexeme l2,
                       word w2,
                       paradigm p2,
                       form f2
                  where f2.is_word = true
                  and   f2.paradigm_id = p2.id
                  and   p2.word_id = w2.id
                  and   l2.word_id = w2.id) l2 on l2.related_lexeme_id = r.lexeme2_id
    group by r.lexeme1_id;

-- meaning relations
create view view_ww_meaning_relation 
  as
    select l1.id lexeme_id,
           l1.meaning_id,
           array_agg(row (m2.meaning_id,m2.lexeme_id,m2.word_id,m2.word,m2.word_lang,r.meaning_rel_type_code)::type_meaning_relation order by r.order_by) related_meanings
    from meaning_relation r,
         lexeme l1,
         (select distinct l2.meaning_id,
                 l2.id lexeme_id,
                 l2.word_id,
                 f2.value word,
                 w2.lang word_lang
          from lexeme l2,
               word w2,
               paradigm p2,
               form f2
          where f2.is_word = true
          and   f2.paradigm_id = p2.id
          and   p2.word_id = w2.id
          and   l2.word_id = w2.id
          and   l2.dataset_code in ('qq2', 'psv', 'ss1', 'kol')) m2
    where l1.dataset_code in ('qq2', 'psv', 'ss1', 'kol')
    and   r.meaning1_id = l1.meaning_id
    and   r.meaning2_id = m2.meaning_id
    group by l1.id;

-- datasets, classifiers
create view view_ww_dataset
  as
    (select
       code,
       name,
       description,
       'est' as lang,
       order_by
     from dataset
     where code in ('qq2', 'psv', 'ss1', 'kol')
     order by order_by
    );

create view view_ww_classifier
  as
    (select
       'MORPH' as name,
       null as origin,
       code,
       value,
       lang
     from morph_label
     where type = 'descrip'
     union all
     select
       'DISPLAY_MORPH' as name,
       null as origin,
       code,
       value,
       lang
     from display_morph_label
     where type = 'descrip'
     union all
     select
       'POS' as name,
       null as origin,
       code,
       value,
       lang
     from pos_label
     where type = 'descrip'
     union all
     select
       'REGISTER' as name,
       null as origin,
       code,
       value,
       lang
     from register_label
     where type = 'descrip'
     union all
     select
       'DERIV' as name,
       null as origin,
       code,
       value,
       lang
     from deriv_label
     where type = 'descrip'
     union all
     select
       'DOMAIN' as name,
       origin,
       code,
       value,
       lang
     from domain_label
     where type = 'descrip'
     union all
     select
       'USAGE_TYPE' as name,
       null as origin,
       code,
       value,
       lang
     from usage_type_label
     where type = 'descrip'
     union all
     select
       'POS_GROUP' as name,
       null as origin,
       code,
       value,
       lang
     from pos_group_label
     where type = 'descrip'
     union all
     select
       'WORD_REL_TYPE' as name,
       null as origin,
       code,
       value,
       lang
     from word_rel_type_label
     where type = 'full'
     union all
     select
       'LEX_REL_TYPE' as name,
       null as origin,
       code,
       value,
       lang
     from lex_rel_type_label
     where type = 'full'
     union all
     select
       'MEANING_REL_TYPE' as name,
       null as origin,
       code,
       value,
       lang
     from meaning_rel_type_label
     where type = 'descrip'
    );

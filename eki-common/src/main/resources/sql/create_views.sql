-- run this once:
-- create extension unaccent;

create type type_lang_complexity as (lang varchar(10), complexity varchar(100));
create type type_definition as (lexeme_id bigint, meaning_id bigint, definition_id bigint, value text, value_prese text, lang char(3), complexity varchar(100), public_notes text array);
create type type_domain as (origin varchar(100), code varchar(100));
create type type_image_file as (freeform_id bigint, image_file text, image_title text);
create type type_source_link as (
				ref_owner varchar(100),
				owner_id bigint,
				source_link_id bigint,
				source_link_type varchar(100),
				name text,
				value text,
				order_by bigint,
				source_id bigint,
				source_props text array);
create type type_usage as (
				usage_id bigint,
				usage text,
				usage_prese text,
				usage_lang char(3),
				complexity varchar(100),
				usage_type_code varchar(100),
				usage_translations text array,
				usage_definitions text array,
				od_usage_definitions text array,
				od_usage_alternatives text array);
create type type_freeform as (freeform_id bigint, type varchar(100), value text, complexity varchar(100));
create type type_colloc_member as (lexeme_id bigint, word_id bigint, word text, form text, homonym_nr integer, word_exists boolean, conjunct varchar(100), weight numeric(14,4));
create type type_meaning_word as (
				lexeme_id bigint,
				meaning_id bigint,
				mw_lexeme_id bigint,
				mw_lex_complexity varchar(100),
				mw_lex_weight numeric(5,4),
				mw_lex_governments type_freeform array,
				mw_lex_register_codes varchar(100) array,
				mw_lex_value_state_code varchar(100),
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				word_type_codes varchar(100) array,
				aspect_code varchar(100));
create type type_word_etym_relation as (word_etym_rel_id bigint, comment text, is_questionable boolean, is_compound boolean, related_word_id bigint);
create type type_word_relation as (
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				word_type_codes varchar(100) array,
				lex_complexities varchar(100) array,
				word_rel_type_code varchar(100));
create type type_lexeme_relation as (
                lexeme_id bigint,
                word_id bigint,
                word text,
                word_prese text,
                homonym_nr integer,
                lang char(3),
                word_type_codes varchar(100) array,
                complexity varchar(100),
                lex_rel_type_code varchar(100));
create type type_meaning_relation as (
				meaning_id bigint,
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array,
				complexity varchar(100),
				lex_value_state_codes varchar(100) array,
				lex_register_codes varchar(100) array,
				lex_government_values text array,
				meaning_rel_type_code varchar(100));

create view view_ww_word_search
as
select ws.sgroup,
       ws.word,
       ws.crit,
       unaccent(ws.crit) unacrit,
       ws.lang_order_by,
       wlc.lang_complexities,
       wlc.simple_exists
from (
        (select 'word' as sgroup,
                fw.value word,
                lower(fw.value) crit,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from form fw,
              paradigm p,
              word w,
              language wl
         where fw.mode = 'WORD'
           and fw.paradigm_id = p.id
           and p.word_id = w.id
           and w.lang = wl.code
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and ds.code = l.dataset_code
                and ds.is_public = true)
         group by fw.value)
      union all
        (select 'as_word' as sgroup,
                fw.value word,
                lower(faw.value) crit,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from form fw,
              form faw,
              paradigm p,
              word w,
              language wl
         where fw.mode = 'WORD'
           and faw.mode = 'AS_WORD'
           and faw.paradigm_id = p.id
           and fw.paradigm_id = p.id
           and p.word_id = w.id
           and w.lang = wl.code
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and ds.code = l.dataset_code
                and ds.is_public = true)
         group by fw.value,
                  faw.value)
      union all
        (select 'form' as sgroup,
                fw.value word,
                lower(f.value) crit,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from form fw,
              form f,
              paradigm p,
              word w,
              language wl
         where fw.mode = 'WORD'
           and f.mode = 'FORM'
           and fw.paradigm_id = p.id
           and f.paradigm_id = p.id
           and p.word_id = w.id
           and w.lang = wl.code
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and ds.code = l.dataset_code
                and ds.is_public = true)
           and fw.value != f.value
         group by fw.value,
                  f.value)) ws,
  (select lc.word,
          array_agg(distinct row (case
                                      when (lc.lang in ('est', 'rus', 'eng')) then lc.lang
                                      else 'other'
                                  end, 
                                  trim(trailing '12' from lc.complexity))::type_lang_complexity) lang_complexities,
          ('SIMPLE' = any(array_agg(lc.complexity))) simple_exists
   from (
           (select
              (select array_agg(distinct f.value)
               from paradigm p,
                    form f
               where p.word_id = l1.word_id
                 and f.paradigm_id = p.id
                 and f.mode = 'WORD')[1] as word,
                   w2.lang,
                   l2.complexity
            from lexeme l1
            inner join dataset l1ds on l1ds.code = l1.dataset_code
            inner join lexeme l2 on l2.meaning_id = l1.meaning_id
            and l2.dataset_code = l1.dataset_code
            and l2.word_id != l1.word_id
            inner join dataset l2ds on l2ds.code = l2.dataset_code
            inner join word w2 on w2.id = l2.word_id
            where l1.type = 'PRIMARY'
              and l1.process_state_code = 'avalik'
              and l1ds.is_public = true
              and l2.process_state_code = 'avalik'
              and l2ds.is_public = true)
         union all
           (select
              (select array_agg(distinct f.value)
               from paradigm p,
                    form f
               where p.word_id = l.word_id
                 and f.paradigm_id = p.id
                 and f.mode = 'WORD')[1] as word,
                   coalesce(ff.lang, w.lang) lang,
                   ff.complexity
            from word w,
                 lexeme l,
                 lexeme_freeform lff,
                 freeform ff,
                 dataset ds
            where l.type = 'PRIMARY'
              and l.process_state_code = 'avalik'
              and ds.code = l.dataset_code
              and ds.is_public = true
              and l.word_id = w.id
              and lff.lexeme_id = l.id
              and lff.freeform_id = ff.id
              and ff.type in ('USAGE',
                              'GRAMMAR',
                              'GOVERNMENT',
                              'PUBLIC_NOTE'))
         union all
           (select
              (select array_agg(distinct f.value)
               from paradigm p,
                    form f
               where p.word_id = l.word_id
                 and f.paradigm_id = p.id
                 and f.mode = 'WORD')[1] as word,
                   ut.lang,
                   u.complexity
            from lexeme l,
                 lexeme_freeform lff,
                 freeform u,
                 freeform ut,
                 dataset ds
            where l.type = 'PRIMARY'
              and l.process_state_code = 'avalik'
              and ds.code = l.dataset_code
              and ds.is_public = true
              and lff.lexeme_id = l.id
              and lff.freeform_id = u.id
              and u.type = 'USAGE'
              and ut.parent_id = u.id
              and ut.type = 'USAGE_TRANSLATION')
         union all
           (select
              (select array_agg(distinct f.value)
               from paradigm p,
                    form f
               where p.word_id = l.word_id
                 and f.paradigm_id = p.id
                 and f.mode = 'WORD')[1] as word,
                   d.lang,
                   d.complexity
            from lexeme l,
                 definition d,
                 dataset ds
            where l.type = 'PRIMARY'
              and l.process_state_code = 'avalik'
              and ds.code = l.dataset_code
              and ds.is_public = true
              and l.meaning_id = d.meaning_id)
         union all
           (select
              (select array_agg(distinct f.value)
               from paradigm p,
                    form f
               where p.word_id = r.word1_id
                 and f.paradigm_id = p.id
                 and f.mode = 'WORD')[1] as word,
                   w2.lang,
                   l2.complexity
            from word_relation r,
                 lexeme l2,
                 word w2,
                 dataset ds
            where w2.id = r.word2_id
              and l2.word_id = w2.id
              and l2.type = 'PRIMARY'
              and l2.process_state_code = 'avalik'
              and ds.code = l2.dataset_code
              and ds.is_public = true
              and r.word_rel_type_code != 'raw')) lc
   group by lc.word) wlc
where ws.word = wlc.word
order by ws.sgroup,
         ws.word,
         ws.crit;

-- words - OK
create view view_ww_word 
as
select w.word_id,
       w.word,
       w.word_prese,
       w.as_word,
       w.lang,
       w.lang_order_by,
       w.homonym_nr,
       w.word_class,
       wt.word_type_codes,
       w.morph_code,
       w.display_morph_code,
       w.aspect_code,
       w.dataset_codes,
       lc.lang_complexities,
       mw.meaning_words,
       wd.definitions,
       od_ws.od_word_recommendations,
       w.lex_dataset_exists,
       w.term_dataset_exists,
       w.forms_exist
from (select w.id as word_id,
             array_to_string(array_agg(distinct f.value),',','*') as word,
             array_to_string(array_agg(distinct f.value_prese),',','*') as word_prese,
             (select array_agg(distinct f.value)
	          from paradigm p,
	               form f
	          where p.word_id = w.id
	          and   f.paradigm_id = p.id
	          and   f.mode = 'AS_WORD')[1] as as_word,
             w.lang,
             (select lc.order_by
              from language lc
              where lc.code = w.lang
              limit 1) lang_order_by,
             w.homonym_nr,
             w.word_class,
             w.morph_code,
             w.display_morph_code,
             w.aspect_code,
             (select array_agg(distinct l.dataset_code)
              from lexeme l,
                   dataset ds
              where l.type = 'PRIMARY'
	          and   l.process_state_code = 'avalik'
	          and   l.word_id = w.id
	          and   ds.code = l.dataset_code
	          and   ds.is_public = true
	          group by w.id) dataset_codes,
             (select count(l.id) > 0
	          from lexeme l,
	               dataset ds
	          where l.type = 'PRIMARY'
	          and   l.process_state_code = 'avalik'
	          and   l.word_id = w.id
	          and   ds.code = l.dataset_code
	          and   ds.is_public = true
	          and   ds.type = 'LEX') lex_dataset_exists,
	         (select count(l.id) > 0
	          from lexeme l,
	               dataset ds
	          where l.type = 'PRIMARY'
	          and   l.process_state_code = 'avalik'
	          and   l.word_id = w.id
	          and   ds.code = l.dataset_code
	          and   ds.is_public = true
	          and   ds.type = 'TERM') term_dataset_exists,
	         (select count(f.id) > 0
	          from paradigm p,
	               form f
	          where p.word_id = w.id
	          and   f.paradigm_id = p.id
	          and   f.mode = 'FORM') forms_exist
      from word as w
        join paradigm as p on p.word_id = w.id
        join form as f on f.paradigm_id = p.id and f.mode = 'WORD'
      where exists (select l.id
                    from lexeme as l,
                         dataset ds
                    where l.word_id = w.id
                    and l.type = 'PRIMARY'
                    and l.process_state_code = 'avalik'
                    and ds.code = l.dataset_code
                    and ds.is_public = true)
      group by w.id) as w
  left outer join (select wt.word_id,
                          array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                   from word_word_type wt
                   group by wt.word_id) wt on wt.word_id = w.word_id
  left outer join (select mw.word_id,
                          array_agg(row (
                          	mw.lexeme_id,
                          	mw.meaning_id,
                          	mw.mw_lex_id,
                          	mw.mw_lex_complexity,
                            mw.mw_lex_weight,
                          	null,
                            null,
                            null,
                          	mw.mw_word_id,
                          	mw.mw_word,
                          	mw.mw_word_prese,
                          	mw.mw_homonym_nr,
                          	mw.mw_lang,
                          	mw.mw_word_type_codes,
                          	mw.mw_aspect_code
                          )::type_meaning_word
                          order by
                          mw.hw_lex_level1,
                          mw.hw_lex_level2,
                          mw.hw_lex_order_by,
                          mw.mw_lex_order_by
                          ) meaning_words
                   from (select distinct
                                l1.word_id,
                                l1.id lexeme_id,
                                l1.meaning_id,
                                l1.level1 hw_lex_level1,
                                l1.level2 hw_lex_level2,
                                l1.order_by hw_lex_order_by,
                                l2.id mw_lex_id,
                                l2.complexity mw_lex_complexity,
                                l2.weight mw_lex_weight,
                                w2.id mw_word_id,
                                f2.value mw_word,
                                f2.value_prese mw_word_prese,
                                w2.homonym_nr mw_homonym_nr,
                                w2.lang mw_lang,
                                (select array_agg(wt.word_type_code order by wt.order_by)
                                 from word_word_type wt
                                 where wt.word_id = w2.id
                                 group by wt.word_id) mw_word_type_codes,
                                w2.aspect_code mw_aspect_code,
                                l2.order_by mw_lex_order_by
                         from lexeme l1
                           inner join dataset l1ds on l1ds.code = l1.dataset_code
                           inner join lexeme l2 on l2.meaning_id = l1.meaning_id and l2.word_id != l1.word_id
                           inner join dataset l2ds on l2ds.code = l2.dataset_code
                           inner join word w2 on w2.id = l2.word_id
                           inner join paradigm p2 on p2.word_id = w2.id
                           inner join form f2 on f2.paradigm_id = p2.id and f2.mode = 'WORD'
                         where 
                         l1.type = 'PRIMARY'
                         and l1.process_state_code = 'avalik'
                         and l1ds.is_public = true
                         and l2.process_state_code = 'avalik'
                         and l2ds.is_public = true) mw
                   group by mw.word_id) mw on mw.word_id = w.word_id
  left outer join (select lc.word_id,
                          array_agg(distinct row(
                                        case when (lc.lang in ('est', 'rus', 'eng')) then lc.lang else 'other' end,
                                        trim(trailing '12' from lc.complexity))::type_lang_complexity) lang_complexities
                   from ((select l1.word_id,
                                 w2.lang,
                                 l2.complexity
                          from lexeme l1
                            inner join dataset l1ds on l1ds.code = l1.dataset_code
                            inner join lexeme l2 on l2.meaning_id = l1.meaning_id and l2.dataset_code = l1.dataset_code and l2.word_id != l1.word_id
                            inner join dataset l2ds on l2ds.code = l2.dataset_code
                            inner join word w2 on w2.id = l2.word_id
                          where 
                          l1.type = 'PRIMARY'
                          and l1.process_state_code = 'avalik'
                          and l1ds.is_public = true
                          and l2.process_state_code = 'avalik'
                          and l2ds.is_public = true)
                          union all
                          (select l.word_id,
                                  coalesce(ff.lang, w.lang) lang,
                                  ff.complexity
                          from word w,
                               lexeme l,
                               lexeme_freeform lff,
                               freeform ff,
                               dataset ds
                          where l.type = 'PRIMARY'
                          and l.process_state_code = 'avalik'
                          and ds.code = l.dataset_code
                          and ds.is_public = true
                          and l.word_id = w.id
                          and lff.lexeme_id = l.id
                          and lff.freeform_id = ff.id
                          and ff.type in ('USAGE', 'GRAMMAR', 'GOVERNMENT', 'PUBLIC_NOTE'))
                          union all
                          (select l.word_id,
                                  ut.lang,
                                  u.complexity
                          from lexeme l,
                               lexeme_freeform lff,
                               freeform u,
                               freeform ut,
                               dataset ds
                          where 
                          l.type = 'PRIMARY'
                          and l.process_state_code = 'avalik'
                          and ds.code = l.dataset_code
                          and ds.is_public = true
                          and lff.lexeme_id = l.id
                          and lff.freeform_id = u.id
                          and u.type = 'USAGE'
                          and ut.parent_id = u.id
                          and ut.type = 'USAGE_TRANSLATION')
                          union all
                          (select l.word_id,
                                  d.lang,
                                  d.complexity
                          from lexeme l,
                               definition d,
                               dataset ds
                          where 
                          l.type = 'PRIMARY'
                          and l.process_state_code = 'avalik'
                          and ds.code = l.dataset_code
                          and ds.is_public = true
                          and l.meaning_id = d.meaning_id)
                          union all
                          (select r.word1_id word_id,
                                  w2.lang,
                                  l2.complexity
                          from word_relation r,
                               lexeme l2,
                               word w2,
                               dataset ds
                          where w2.id = r.word2_id
                          and l2.word_id = w2.id
                          and l2.type = 'PRIMARY'
                          and l2.process_state_code = 'avalik'
                          and ds.code = l2.dataset_code
                          and ds.is_public = true
                          and r.word_rel_type_code != 'raw')) lc
                   group by lc.word_id) lc on lc.word_id = w.word_id
  left outer join (select wd.word_id,
                          array_agg(row (
                            wd.lexeme_id,
                            wd.meaning_id,
                            wd.definition_id,
                            wd.value,
                            wd.value_prese,
                            wd.lang,
                            wd.complexity,
                            null
                          )::type_definition 
                          order by
                          wd.level1,
                          wd.level2,
                          wd.lex_order_by,
                          wd.def_order_by
                          ) definitions
                   from (select l.word_id,
                                l.id lexeme_id,
                                l.meaning_id,
                                l.level1,
                                l.level2,
                                l.order_by lex_order_by,
                                d.id definition_id,
                                d.value,
                                d.value_prese,
                                d.lang,
                                d.complexity,
                                d.order_by def_order_by
                         from lexeme l
                           inner join dataset ds on ds.code = l.dataset_code
                           inner join definition d on d.meaning_id = l.meaning_id
                         where 
                         l.type = 'PRIMARY'
                         and l.process_state_code = 'avalik'
                         and ds.is_public = true) wd
                   group by wd.word_id) wd
               on wd.word_id = w.word_id
  left outer join (select wf.word_id,
                          array_agg(ff.value_prese order by ff.order_by) od_word_recommendations
                   from word_freeform wf,
                        freeform ff
                   where wf.freeform_id = ff.id
                   and   ff.type = 'OD_WORD_RECOMMENDATION'
                   group by wf.word_id) od_ws on od_ws.word_id = w.word_id;

-- word forms - OK
create view view_ww_form 
as
select w.id word_id,
       fw.value word,
       w.lang,
       p.id paradigm_id,
       p.inflection_type,
       ff.id form_id,
       ff.mode,
       ff.morph_group1,
       ff.morph_group2,
       ff.morph_group3,
       ff.display_level,
       ff.morph_code,
       ff.morph_exists,
       ff.value form,
       ff.components,
       ff.display_form,
       ff.vocal_form,
       ff.audio_file,
       ff.order_by
from word w,
     paradigm p,
     form ff,
     form fw
where p.word_id = w.id
and   ff.paradigm_id = p.id
and   fw.paradigm_id = p.id
and   fw.mode = 'WORD'
and   ff.display_level > 0
and   exists (select l.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id 
              and l.type = 'PRIMARY'
              and l.process_state_code = 'avalik'
              and ds.code = l.dataset_code
              and ds.is_public = true)
order by p.id,
         ff.order_by,
         ff.id;

-- lexeme meanings - OK
create view view_ww_meaning 
as
select m.id meaning_id,
       m_dom.domain_codes,
       m_img.image_files,
       m_spp.systematic_polysemy_patterns,
       m_smt.semantic_types,
       m_lcm.learner_comments,
       m_pnt.public_notes,
       d.definitions
from (select m.id
      from meaning m
      where exists (select l.id
                    from lexeme as l,
                         dataset ds
                    where l.meaning_id = m.id
                    and   l.type = 'PRIMARY'
                    and   l.process_state_code = 'avalik'
                    and   ds.code = l.dataset_code
                    and   ds.is_public = true)) m
  left outer join (select m_dom.meaning_id,
                          array_agg(row (m_dom.domain_origin, m_dom.domain_code)::type_domain order by m_dom.order_by) domain_codes
                   from meaning_domain m_dom
                   group by m_dom.meaning_id) m_dom
               on m_dom.meaning_id = m.id
  left outer join (select d.meaning_id,
                          array_agg(row (
                            null,
                            d.meaning_id,
                            d.id,
                            d.value,
                            d.value_prese,
                            d.lang,
                            d.complexity,
                            d.public_notes
                          )::type_definition
                          order by
                          d.order_by
                          ) definitions
                   from (select d.meaning_id,
                                d.id,
                                d.value,
                                d.value_prese,
                                d.lang,
                                d.complexity,
                                d.order_by,
                                (select array_agg(ff.value_prese order by ff.order_by)
                                 from definition_freeform dff,
                                      freeform ff
                                 where dff.definition_id = d.id
                                 and   ff.id = dff.freeform_id
                                 and   ff.type = 'PUBLIC_NOTE'
                                 group by dff.definition_id) public_notes
                         from definition d) d
                   group by d.meaning_id) d
               on d.meaning_id = m.id
  left outer join (select mff.meaning_id,
                          array_agg(row (
                            ff_if.id,
                            ff_if.value_text,
                            ff_it.value_text
                          )::type_image_file
                          order by
                          ff_if.order_by,
                          ff_it.order_by
                          ) image_files
                   from meaning_freeform mff
                     inner join freeform ff_if
                             on ff_if.id = mff.freeform_id
                            and ff_if.type = 'IMAGE_FILE'
                     left outer join freeform ff_it
                                  on ff_it.parent_id = ff_if.id
                                 and ff_it.type = 'IMAGE_TITLE'
                   group by mff.meaning_id) m_img on m_img.meaning_id = m.id
  left outer join (select mf.meaning_id,
                          array_agg(ff.value_text order by ff.order_by) systematic_polysemy_patterns
                   from meaning_freeform mf,
                        freeform ff
                   where mf.freeform_id = ff.id
                   and   ff.type = 'SYSTEMATIC_POLYSEMY_PATTERN'
                   group by mf.meaning_id) m_spp on m_spp.meaning_id = m.id
  left outer join (select mf.meaning_id,
                          array_agg(ff.value_text order by ff.order_by) semantic_types
                   from meaning_freeform mf,
                        freeform ff
                   where mf.freeform_id = ff.id
                   and   ff.type = 'SEMANTIC_TYPE'
                   group by mf.meaning_id) m_smt on m_smt.meaning_id = m.id
  left outer join (select mf.meaning_id,
                          array_agg(ff.value_prese order by ff.order_by) learner_comments
                   from meaning_freeform mf,
                        freeform ff
                   where mf.freeform_id = ff.id
                   and   ff.type = 'LEARNER_COMMENT'
                   group by mf.meaning_id) m_lcm on m_lcm.meaning_id = m.id
  left outer join (select mf.meaning_id,
                          array_agg(row (ff.id, ff.type, ff.value_prese, ff.complexity)::type_freeform order by ff.order_by) public_notes
                   from meaning_freeform mf,
                        freeform ff
                   where mf.freeform_id = ff.id
                   and   ff.type = 'PUBLIC_NOTE'
                   group by mf.meaning_id) m_pnt on m_pnt.meaning_id = m.id
order by m.id;

-- lexeme details - OK
create view view_ww_lexeme 
as
select l.id lexeme_id,
       l.word_id,
       l.meaning_id,
       ds.type dataset_type,
       l.dataset_code,
       l.level1,
       l.level2,
       l.weight,
       l.complexity,
       l.order_by lex_order_by,
       l_lc.lang_complexities,
       l_reg.register_codes,
       l_pos.pos_codes,
       l_der.deriv_codes,
       mw.meaning_words,
       anote.advice_notes,
       pnote.public_notes,
       gramm.grammars,
       gov.governments,
       usg.usages,
       odlr.od_lexeme_recommendations
from lexeme l
  inner join dataset ds on ds.code = l.dataset_code
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
  left outer join (select lf.lexeme_id,
                          array_agg(ff.value_text order by ff.order_by) advice_notes
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'ADVICE_NOTE'
                   group by lf.lexeme_id) anote on anote.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          array_agg(row (ff.id, ff.type, ff.value_prese,ff.complexity)::type_freeform order by ff.order_by) public_notes
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'PUBLIC_NOTE'
                   group by lf.lexeme_id) pnote on pnote.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          array_agg(row (ff.id, ff.type, ff.value_text,ff.complexity)::type_freeform order by ff.order_by) grammars
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'GRAMMAR'
                   group by lf.lexeme_id) gramm on gramm.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          array_agg(row (ff.id, ff.type, ff.value_text,ff.complexity)::type_freeform order by ff.order_by) governments
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'GOVERNMENT'
                   group by lf.lexeme_id) gov on gov.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          array_agg(ff.value_prese order by ff.order_by) od_lexeme_recommendations
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'OD_LEXEME_RECOMMENDATION'
                   group by lf.lexeme_id) odlr on odlr.lexeme_id = l.id
  left outer join (select mw.lexeme_id,
                          array_agg(row (
                          	mw.lexeme_id,
                          	mw.meaning_id,
                          	mw.mw_lex_id,
                          	mw.mw_lex_complexity,
                            mw.mw_lex_weight,
                          	mw.mw_lex_governments,
                          	mw.mw_lex_register_codes,
                          	mw.mw_lex_value_state_code,
                          	mw.mw_word_id,
                          	mw.mw_word,
                          	mw.mw_word_prese,
                          	mw.mw_homonym_nr,
                          	mw.mw_lang,
                          	mw.mw_word_type_codes,
                          	mw.mw_aspect_code
                          )::type_meaning_word
                          order by
                          mw.hw_lex_level1,
                          mw.hw_lex_level2,
                          mw.hw_lex_order_by,
                          mw.mw_lex_order_by
                          ) meaning_words
                   from (select distinct
                                l1.word_id,
                                l1.id lexeme_id,
                                l1.meaning_id,
                                l1.level1 hw_lex_level1,
                                l1.level2 hw_lex_level2,
                                l1.order_by hw_lex_order_by,
                                l2.id mw_lex_id,
                                l2.complexity mw_lex_complexity,
                                l2.weight mw_lex_weight,
                                --NB! space sym replaced by temp placeholder because nested complex type array masking failure by postgres
                                (select array_agg(row (ff.id, ff.type, replace(ff.value_text, ' ', '`'),ff.complexity)::type_freeform order by ff.order_by)
			                     from lexeme_freeform lf,
			                          freeform ff
			                     where lf.lexeme_id = l2.id
			                     and   lf.freeform_id = ff.id
			                     and   ff.type = 'GOVERNMENT'
			                     group by lf.lexeme_id) mw_lex_governments,
                                (select array_agg(l_reg.register_code order by l_reg.order_by)
                   				 from lexeme_register l_reg
                   				 where l_reg.lexeme_id = l2.id
                   				 group by l_reg.lexeme_id) mw_lex_register_codes,
                   				l2.value_state_code mw_lex_value_state_code,
                                w2.id mw_word_id,
                                f2.value mw_word,
                                f2.value_prese mw_word_prese,
                                w2.homonym_nr mw_homonym_nr,
                                w2.lang mw_lang,
                                (select array_agg(wt.word_type_code order by wt.order_by)
                                 from word_word_type wt
                                 where wt.word_id = w2.id
                                 group by wt.word_id) mw_word_type_codes,
                                w2.aspect_code mw_aspect_code,
                                l2.order_by mw_lex_order_by
                         from lexeme l1
                           inner join dataset l1ds on l1ds.code = l1.dataset_code
                           inner join lexeme l2 on l2.meaning_id = l1.meaning_id --and l2.word_id != l1.word_id
                           inner join dataset l2ds on l2ds.code = l2.dataset_code
                           inner join word w2 on w2.id = l2.word_id
                           inner join paradigm p2 on p2.word_id = w2.id
                           inner join form f2 on f2.paradigm_id = p2.id and f2.mode = 'WORD'
                         where 
                         l1.type = 'PRIMARY'
                         and l1.process_state_code = 'avalik'
                         and l1ds.is_public = true
                         and l2.process_state_code = 'avalik'
                         and l2ds.is_public = true) mw
                   group by mw.lexeme_id) mw on mw.lexeme_id = l.id
  left outer join (select u.lexeme_id,
                          array_agg(row (
                            u.usage_id,
                          	u.usage,
                          	u.usage_prese,
                          	u.usage_lang,
                          	u.complexity,
                          	u.usage_type_code,
                          	u.usage_translations,
                          	u.usage_definitions,
                          	u.od_usage_definitions,
                          	u.od_usage_alternatives
                          )::type_usage
                          order by u.order_by) usages
                   from (select lf.lexeme_id,
                                u.id usage_id,
                                u.value_text usage,
                                u.value_prese usage_prese,
                                u.lang usage_lang,
                                u.complexity,
                                u.order_by,
                                utp.classif_code usage_type_code,
                                ut.usage_translations,
                                ud.usage_definitions,
                                odud.od_usage_definitions,
                                odua.od_usage_alternatives
                         from lexeme_freeform lf
                           inner join freeform u on lf.freeform_id = u.id and u.type = 'USAGE'
                           left outer join freeform utp on utp.parent_id = u.id and utp.type = 'USAGE_TYPE'
                           left outer join (select ut.parent_id usage_id,
                                                   array_agg(ut.value_prese order by ut.order_by) usage_translations
                                            from freeform ut
                                            where ut.type = 'USAGE_TRANSLATION'
                                            -- TODO this hack is based on ralistic data and fulfils necessary prerequisite for data filtering at ww
                                            and   ut.lang = 'rus' 
                                            group by ut.parent_id) ut on ut.usage_id = u.id
                           left outer join (select ud.parent_id usage_id,
                                                   array_agg(ud.value_prese order by ud.order_by) usage_definitions
                                            from freeform ud
                                            where ud.type = 'USAGE_DEFINITION'
                                            group by ud.parent_id) ud on ud.usage_id = u.id
						   left outer join (select odud.parent_id usage_id,
                                       array_agg(odud.value_prese order by odud.order_by) od_usage_definitions
                                from freeform odud
                                where odud.type = 'OD_USAGE_DEFINITION'
                                group by odud.parent_id) odud on odud.usage_id = u.id
						   left outer join (select odua.parent_id usage_id,
                                       array_agg(odua.value_prese order by odua.order_by) od_usage_alternatives
                                from freeform odua
                                where odua.type = 'OD_USAGE_ALTERNATIVE'
                                group by odua.parent_id) odua on odua.usage_id = u.id) u
                   group by u.lexeme_id) usg on usg.lexeme_id = l.id
  left outer join (select lc.id,
                         array_agg(distinct row(
                                        case when (lc.lang in ('est', 'rus', 'eng')) then lc.lang else 'other' end,
                                        trim(trailing '12' from lc.complexity))::type_lang_complexity) lang_complexities
                  from ((select l1.id,
                                w2.lang,
                                l2.complexity
                         from lexeme l1
                           inner join dataset l1ds on l1ds.code = l1.dataset_code
                           inner join lexeme l2 on l2.meaning_id = l1.meaning_id and l2.dataset_code = l1.dataset_code and l2.word_id != l1.word_id
                           inner join dataset l2ds on l2ds.code = l2.dataset_code
                           inner join word w2 on w2.id = l2.word_id
                         where l1.type = 'PRIMARY'
                         and   l1.process_state_code = 'avalik'
                         and   l1ds.is_public = true
                         and   l2.type = 'PRIMARY'
                         and   l2.process_state_code = 'avalik'
                         and   l2ds.is_public = true)
                         union all
                         (select l.id,
                                 coalesce(ff.lang, w.lang) lang,
                                 ff.complexity
                         from word w,
                              lexeme l,
                              lexeme_freeform lff,
                              freeform ff,
                              dataset ds
                         where l.type = 'PRIMARY'
                         and   l.process_state_code = 'avalik'
                         and   ds.code = l.dataset_code
                         and   ds.is_public = true
                         and   l.word_id = w.id
                         and   lff.lexeme_id = l.id
                         and   lff.freeform_id = ff.id
                         and   ff.type in ('USAGE', 'GRAMMAR', 'GOVERNMENT', 'PUBLIC_NOTE'))
                         union all
                         (select l.id,
                                 ut.lang,
                                 u.complexity
                         from lexeme l,
                              lexeme_freeform lff,
                              freeform u,
                              freeform ut,
                              dataset ds
                         where l.type = 'PRIMARY'
                         and   l.process_state_code = 'avalik'
                         and   ds.code = l.dataset_code
                         and   ds.is_public = true
                         and   lff.lexeme_id = l.id
                         and   lff.freeform_id = u.id
                         and   u.type = 'USAGE'
                         and   ut.parent_id = u.id
                         and   ut.type = 'USAGE_TRANSLATION')
                         union all
                         (select l.id,
                                 d.lang,
                                 d.complexity
                         from lexeme l,
                              definition d,
                              dataset ds
                         where l.type = 'PRIMARY'
                         and   l.process_state_code = 'avalik'
                         and   l.meaning_id = d.meaning_id
                         and   ds.code = l.dataset_code
                         and   ds.is_public = true)
                         union all
                         (select l1.id,
                                 w2.lang,
                                 l2.complexity
                         from lex_relation r,
                              lexeme l1,
                              lexeme l2,
                              word w2,
                              dataset l1ds,
                              dataset l2ds
                         where l1.type = 'PRIMARY'
                         and   l1.process_state_code = 'avalik'
                         and   l1ds.code = l1.dataset_code
                         and   l1ds.is_public = true
                         and   r.lexeme1_id = l1.id
                         and   r.lexeme2_id = l2.id
                         and   l2.dataset_code = l1.dataset_code
                         and   l2.type = 'PRIMARY'
                         and   l2.process_state_code = 'avalik'
                         and   l2ds.code = l2.dataset_code
                         and   l2ds.is_public = true
                         and   w2.id = l2.word_id)
                         union all
                         (select l1.id,
                                 w1.lang,
                                 l1.complexity
                         from lexeme l1,
                              word w1,
                              dataset l1ds
                         where l1.type = 'PRIMARY'
                         and   l1.process_state_code = 'avalik'
                         and   l1ds.code = l1.dataset_code
                         and   l1ds.is_public = true
                         and   w1.id = l1.word_id
                         and   not exists (select l2.id
                                           from lexeme l2,
                                                dataset l2ds
                                           where l2.meaning_id = l1.meaning_id
                                           and   l2.dataset_code = l1.dataset_code
                                           and   l2.id != l1.id
                                           and   l2.type = 'PRIMARY'
                                           and   l2.process_state_code = 'avalik'
                                           and   l2ds.code = l2.dataset_code
                                           and   l2ds.is_public = true)
                         and   not exists (select d.id
                                           from definition d
                                           where d.meaning_id = l1.meaning_id)
                         and   not exists (select ff.id
                                           from lexeme_freeform lff,
                                                freeform ff
                                           where lff.lexeme_id = l1.id
                                           and   lff.freeform_id = ff.id
                                           and   ff.type in ('USAGE', 'GRAMMAR', 'GOVERNMENT', 'PUBLIC_NOTE')))) lc
            group by lc.id) l_lc on l_lc.id = l.id
where l.type = 'PRIMARY'
and   l.process_state_code = 'avalik'
and   ds.is_public = true
order by l.id;

-- collocations - ?
create view view_ww_collocation 
as
select l1.id as lexeme_id,
       l1.word_id,
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
       array_agg(row (lw2.lexeme_id,lw2.word_id,lw2.word,lc2.member_form,lw2.homonym_nr,lw2.word_exists,lc2.conjunct,lc2.weight)::type_colloc_member order by lc2.member_order) as colloc_members,
       c.complexity
from collocation as c
  inner join lex_colloc as lc1 on lc1.collocation_id = c.id
  inner join lex_colloc as lc2 on lc2.collocation_id = c.id
  inner join lexeme as l1 on l1.id = lc1.lexeme_id
  inner join dataset l1ds on l1ds.code = l1.dataset_code
  inner join (select distinct l2.id lexeme_id,
                     l2.word_id,
                     f2.value word,
                     w2.homonym_nr,
                     (f2.mode = 'WORD') word_exists
              from lexeme as l2,
                   word as w2,
                   paradigm as p2,
                   form as f2,
                   dataset as l2ds
              where l2.type = 'PRIMARY'
              and   l2.process_state_code = 'avalik'
              and   l2ds.code = l2.dataset_code
              and   l2ds.is_public = true
              and   l2.word_id = w2.id
              and   p2.word_id = w2.id
              and   f2.paradigm_id = p2.id
              and   f2.mode in ('WORD', 'UNKNOWN')) lw2 on lw2.lexeme_id = lc2.lexeme_id
  inner join lex_colloc_rel_group as rgr1 on lc1.rel_group_id = rgr1.id
  inner join lex_colloc_pos_group as pgr1 on pgr1.id = rgr1.pos_group_id
where 
l1.type = 'PRIMARY'
and l1.process_state_code = 'avalik'
and l1ds.is_public = true
group by l1.id,
         c.id,
         pgr1.id,
         rgr1.id,
         lc1.id
order by l1.level1,
         l1.level2,
         pgr1.order_by,
         rgr1.order_by,
         lc1.group_order,
         c.id;

-- etymology - OK
create view view_ww_word_etymology
as
with recursive word_etym_recursion (word_id, word_etym_word_id, word_etym_id, word_etym_rel_id, related_word_id, related_word_ids) as
(
  (
    select
      we.word_id,
      we.word_id word_etym_word_id,
      we.id word_etym_id,
      wer.id word_etym_rel_id,
      wer.related_word_id,
      array[we.word_id] as related_word_ids
    from
      word_etymology we
      left outer join word_etymology_relation wer on wer.word_etym_id = we.id
    order by
      we.order_by,
      wer.order_by
  )
  union all
    (
      select
        rec.word_id,
        we.word_id word_etym_word_id,
        we.id word_etym_id,
        wer.id word_etym_rel_id,
        wer.related_word_id,
        (
          rec.related_word_ids || we.word_id
        ) as related_word_ids
      from
        word_etym_recursion rec
        inner join word_etymology we on we.word_id = rec.related_word_id
        left outer join word_etymology_relation wer on wer.word_etym_id = we.id
      where
        rec.related_word_id != any(rec.related_word_ids)
      order by
        we.order_by,
        wer.order_by
    )
)
select
  rec.word_id,
  rec.word_etym_id,
  rec.word_etym_word_id,
  w.word,
  w.lang word_lang,
  mw2.meaning_words,
  we.etymology_type_code,
  we.etymology_year,
  we.comment_prese word_etym_comment,
  we.is_questionable word_etym_is_questionable,
  we.order_by word_etym_order_by,
  array_agg(
    row(
      wer.id,
      wer.comment_prese,
      wer.is_questionable,
      wer.is_compound,
      wer.related_word_id
    ):: type_word_etym_relation
    order by wer.order_by
  ) word_etym_relations
from
  word_etym_recursion rec
  inner join word_etymology we on we.id = rec.word_etym_id
  inner join (
    select
      w.id,
      w.lang,
      (array_agg(distinct f.value)) [1] word
    from
      word w,
      paradigm p,
      form f
    where
      p.word_id = w.id
      and f.paradigm_id = p.id
      and f.mode = 'WORD'
      and exists (
        select
          l.id
        from
          lexeme l,
          dataset ds
        where
        l.word_id = w.id
        and l.type = 'PRIMARY'
        and l.process_state_code = 'avalik'
        and ds.code = l.dataset_code
        and ds.type = 'LEX')
    group by
      w.id
  ) w on w.id = rec.word_etym_word_id
  left outer join word_etymology_relation wer on wer.id = rec.word_etym_rel_id
  left outer join (
    select
      l1.word_id,
      array_agg(distinct f2.value) meaning_words
    from
      lexeme l1,
      meaning m,
      lexeme l2,
      word w2,
      paradigm p2,
      form f2
    where
      l1.meaning_id = m.id
      and l2.meaning_id = m.id
      and l1.word_id != l2.word_id
      and l1.type = 'PRIMARY'
      and l1.process_state_code = 'avalik'
      and l2.dataset_code = 'ety'
      and l2.type = 'PRIMARY'
      and l2.process_state_code = 'avalik'
      and l2.word_id = w2.id
      and p2.word_id = w2.id
      and w2.lang = 'est'
      and f2.paradigm_id = p2.id
      and f2.mode = 'WORD'
    group by
      l1.word_id
  ) mw2 on mw2.word_id = rec.word_etym_word_id
group by
  rec.word_id,
  rec.word_etym_id,
  rec.word_etym_word_id,
  mw2.meaning_words,
  we.id,
  w.id,
  w.word,
  w.lang
order by
  rec.word_id,
  we.order_by;

-- word relations - OK
create view view_ww_word_relation 
as
select w.id word_id,
       wr.related_words,
       wg.word_group_id,
       wg.word_rel_type_code,
       wg.word_group_members
from word w
  left outer join (select w1.id word_id,
                          array_agg(row (
                            wr.related_word_id,
                            wr.related_word,
                            wr.related_word_prese,
                            wr.related_word_homonym_nr,
                            wr.related_word_lang,
                            wr.word_type_codes,
                            wr.lex_complexities,
                            wr.word_rel_type_code
                          )::type_word_relation order by wr.word_rel_order_by) related_words
                   from word w1
                     inner join (select distinct r.word1_id,
                                        r.word2_id related_word_id,
                                        r.word_rel_type_code,
                                        r.order_by word_rel_order_by,
                                        w2.word related_word,
                                        w2.word_prese related_word_prese,
                                        w2.homonym_nr related_word_homonym_nr,
                                        w2.lang related_word_lang,
                                        w2.word_type_codes,
                                        (select array_agg(distinct lc.complexity)
                                         from lexeme lc,
                                              dataset ds
                                         where lc.word_id = w2.id
                                         and lc.type = 'PRIMARY'
                                         and lc.process_state_code = 'avalik'
                                         and ds.code = lc.dataset_code
                                         and ds.is_public = true
                                         group by lc.word_id) as lex_complexities
                                 from word_relation r,
                                      (select w.id,
                                              (array_agg(distinct f.value)) [1] as word,
                                              (array_agg(distinct f.value_prese)) [1] as word_prese,
                                              w.homonym_nr,
                                              w.lang,
                                              array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                                       from word as w
                                         join paradigm as p on p.word_id = w.id
                                         join form as f on f.paradigm_id = p.id and f.mode = 'WORD'
                                         left outer join word_word_type as wt on wt.word_id = w.id
                                       where exists (select l.id
                                                     from lexeme as l,
                                                          dataset ds
                                                     where l.type = 'PRIMARY'
                                                     and   l.process_state_code = 'avalik'
                                                     and   ds.code = l.dataset_code
                                                     and   ds.is_public = true
                                                     and   l.word_id = w.id)
                                       group by w.id) as w2
                                 where r.word2_id = w2.id
                                 and r.word_rel_type_code != 'raw') wr on wr.word1_id = w1.id
                   group by w1.id) wr on wr.word_id = w.id
  left outer join (select wg.word_id,
                          wg.word_group_id,
                          wg.word_rel_type_code,
                          array_agg(row (
                            wg.group_member_word_id,
                            wg.group_member_word,
                            wg.group_member_word_prese,
                            wg.group_member_homonym_nr,
                            wg.group_member_word_lang,
                            wg.word_type_codes,
                            wg.lex_complexities,
                            wg.word_rel_type_code
                          )::type_word_relation order by wg.group_member_order_by) word_group_members
                   from (select distinct w1.id word_id,
                                wg.id word_group_id,
                                wg.word_rel_type_code,
                                w2.id group_member_word_id,
                                w2.word group_member_word,
                                w2.word_prese group_member_word_prese,
                                w2.homonym_nr group_member_homonym_nr,
                                w2.lang group_member_word_lang,
                                w2.word_type_codes,
                                (select array_agg(distinct lc.complexity)
                                 from lexeme lc,
                                      dataset ds
                                 where lc.word_id = w2.id
                                 and lc.type = 'PRIMARY'
                                 and lc.process_state_code = 'avalik'
                                 and ds.code = lc.dataset_code
                                 and ds.is_public = true
                                 group by lc.word_id) as lex_complexities,
                                wgm2.order_by group_member_order_by
                         from word w1,
                              (select w.id,
                                      (array_agg(distinct f.value)) [1] as word,
                                      (array_agg(distinct f.value_prese)) [1] as word_prese,
                                      w.homonym_nr,
                                      w.lang,
                                      array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                               from word as w
                                 join paradigm as p on p.word_id = w.id
                                 join form as f on f.paradigm_id = p.id and f.mode = 'WORD'
                                 left outer join word_word_type as wt on wt.word_id = w.id
                               where exists (select l.id
                                             from lexeme as l,
                                                  dataset ds
                                             where l.word_id = w.id
                                             and l.type = 'PRIMARY'
                                             and l.process_state_code = 'avalik'
                                             and ds.code = l.dataset_code
                                             and ds.is_public = true)
                               group by w.id) as w2,
                              word_group wg,
                              word_group_member wgm1,
                              word_group_member wgm2
                         where wgm1.word_group_id = wg.id
                         and   wgm2.word_group_id = wg.id
                         and   wgm1.word_id = w1.id
                         and   wgm2.word_id = w2.id
                         and   w1.id != w2.id) wg
                   group by wg.word_id,
                            wg.word_group_id,
                            wg.word_rel_type_code) wg on wg.word_id = w.id
where (wr.related_words is not null or wg.word_group_members is not null)
and   exists (select l.id
              from lexeme l,
                   dataset ds
              where l.word_id = w.id
              and l.type = 'PRIMARY'
              and l.process_state_code = 'avalik'
              and ds.code = l.dataset_code
              and ds.is_public = true);


-- lexeme relations - OK
create view view_ww_lexeme_relation 
as
select r.lexeme1_id lexeme_id,
       array_agg(row (
         l2.lexeme_id,
         w2.word_id,
         w2.word,
         w2.word_prese,
         w2.homonym_nr,
         w2.lang,
         w2.word_type_codes,
         l2.complexity,
         r.lex_rel_type_code
       )::type_lexeme_relation order by r.order_by) related_lexemes
from lex_relation r
  inner join (select l.id lexeme_id,
                     l.word_id,
                     l.complexity
              from lexeme l,
                   dataset lds
              where l.type = 'PRIMARY'
              and   l.process_state_code = 'avalik'
              and   lds.code = l.dataset_code
              and   lds.is_public = true) l2 on l2.lexeme_id = r.lexeme2_id
  inner join (select w.id word_id,
                     (array_agg(distinct f.value))[1] as word,
                     (array_agg(distinct f.value_prese))[1] as word_prese,
                     w.homonym_nr,
                     w.lang,
                     array_agg(wt.word_type_code order by wt.order_by) word_type_codes
              from word as w
                join paradigm as p on p.word_id = w.id
                join form as f on f.paradigm_id = p.id and f.mode = 'WORD'
                left outer join word_word_type as wt on wt.word_id = w.id
              group by w.id) as w2 on w2.word_id = l2.word_id
where exists (select l1.id
              from lexeme l1,
                   dataset l1ds
              where l1.id = r.lexeme1_id
              and   l1.type = 'PRIMARY'
              and   l1.process_state_code = 'avalik'
              and   l1ds.code = l1.dataset_code
              and   l1ds.is_public = true)
group by r.lexeme1_id;


-- meaning relations - OK
create view view_ww_meaning_relation
as
select r.m1_id meaning_id,
       array_agg(row (
         r.m2_id,
         r.word_id,
         r.word,
         r.word_prese,
         r.homonym_nr,
         r.word_lang,
         r.aspect_code,
         r.word_type_codes,
         r.complexity,
         r.lex_value_state_codes,
         r.lex_register_codes,
         r.lex_government_values,
         r.meaning_rel_type_code
       )::type_meaning_relation order by r.order_by, r.lex_order_by) related_meanings
from (select mr.meaning1_id m1_id,
             mr.meaning2_id m2_id,
             w.id word_id,
             (array_agg(distinct f.value))[1] word,
             (array_agg(distinct f.value_prese))[1] word_prese,
             w.homonym_nr,
             w.lang word_lang,
             w.aspect_code aspect_code,
             l.complexity,
             (select array_agg(wt.word_type_code)
              from word_word_type wt
              where wt.word_id = w.id
              group by w.id) word_type_codes,
             (select array_agg(distinct l.value_state_code)
              from lexeme l,
                   dataset l_ds
              where l.meaning_id = m.id
                and l.word_id = w.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and l_ds.code = l.dataset_code
                and l_ds.is_public = true
                and l.value_state_code is not null
              group by l.word_id, l.meaning_id) lex_value_state_codes,
             (select array_agg(distinct lr.register_code)
              from lexeme_register lr,
                   lexeme l,
                   dataset l_ds
              where l.meaning_id = m.id
                and l.word_id = w.id
                and lr.lexeme_id = l.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and l_ds.code = l.dataset_code
                and l_ds.is_public = true
              group by l.word_id, l.meaning_id) lex_register_codes,
             (select array_agg(ff.value_text)
              from freeform ff,
                   lexeme_freeform lff,
                   lexeme l,
                   dataset l_ds
              where l.meaning_id = m.id
                and l.word_id = w.id
                and l.type = 'PRIMARY'
                and l.process_state_code = 'avalik'
                and l_ds.code = l.dataset_code
                and l_ds.is_public = true
                and lff.lexeme_id = l.id
                and ff.id = lff.freeform_id
                and ff.type = 'GOVERNMENT'
              group by l.word_id, l.meaning_id) lex_government_values,
             l.order_by lex_order_by,
             mr.meaning_rel_type_code meaning_rel_type_code,
             mr.order_by
      from meaning_relation mr
           join meaning m on m.id = mr.meaning2_id
           join lexeme l on l.meaning_id = m.id and l.type = 'PRIMARY' and l.process_state_code = 'avalik'
           join word w on w.id = l.word_id
           join paradigm p on p.word_id = w.id
           join form f on f.paradigm_id = p.id and f.mode = 'WORD'
           join dataset l_ds on l_ds.code = l.dataset_code and l_ds.is_public = true
      where exists(select lex.id
                   from lexeme lex,
                        dataset lex_ds
                   where lex.meaning_id = mr.meaning1_id
                     and lex.type = 'PRIMARY'
                     and lex.process_state_code = 'avalik'
                     and lex_ds.code = lex.dataset_code
                     and lex_ds.is_public = true)
      group by m.id, mr.id, w.id, l.id) r
group by r.m1_id;


-- source links
create view view_ww_word_etym_source_link 
as
select we.word_id,
       array_agg(row (
         'WORD_ETYM',
         wesl.word_etym_id,
         wesl.id,
         wesl.type,
         wesl.name,
         wesl.value,
         wesl.order_by,
         s.source_id,
         s.source_props
       )::type_source_link
       order by
       we.id,
       wesl.order_by
       ) source_links
from word_etymology we,
     word_etymology_source_link wesl,
     (select s.id source_id,
             array_agg(ff.value_prese order by ff.order_by) source_props
      from source s,
           source_freeform sff,
           freeform ff
      where sff.source_id = s.id
      and   sff.freeform_id = ff.id
      and   ff.type not in ('SOURCE_FILE', 'EXTERNAL_SOURCE_ID')
      group by s.id) s
where wesl.word_etym_id = we.id
and   wesl.source_id = s.source_id
and   exists (select l.id
              from lexeme l,
                   dataset ds
              where l.word_id = we.word_id
              and   l.type = 'PRIMARY'
              and   l.process_state_code = 'avalik'
              and   ds.code = l.dataset_code
              and   ds.is_public = true)
group by we.word_id
order by we.word_id;

create view view_ww_lexeme_source_link 
as
select l.id lexeme_id,
       array_agg(row (
         'LEXEME',
         l.id,
         lsl.id,
         lsl.type,
         lsl.name,
         lsl.value,
         lsl.order_by,
         s.source_id,
         s.source_props
       )::type_source_link
       order by
       l.id,
       lsl.order_by
       ) source_links
from lexeme l,
     dataset ds,
     lexeme_source_link lsl,
     (select s.id source_id,
             array_agg(ff.value_prese order by ff.order_by) source_props
      from source s,
           source_freeform sff,
           freeform ff
      where sff.source_id = s.id
      and   sff.freeform_id = ff.id
      and   ff.type not in ('SOURCE_FILE', 'EXTERNAL_SOURCE_ID')
      group by s.id) s
where l.type = 'PRIMARY'
and   l.process_state_code = 'avalik'
and   lsl.lexeme_id = l.id
and   lsl.source_id = s.source_id
and   ds.code = l.dataset_code
and   ds.is_public = true
group by l.id
order by l.id;

create view view_ww_lexeme_freeform_source_link 
as
select l.id lexeme_id,
       array_agg(row (
         'FREEFORM',
         ffsl.freeform_id,
         ffsl.id,
         ffsl.type,
         ffsl.name,
         ffsl.value,
         ffsl.order_by,
         s.source_id,
         s.source_props
       )::type_source_link
       order by
       lff.id,
       ffsl.order_by
       ) source_links
from lexeme l,
     dataset ds,
     lexeme_freeform lff,
     freeform_source_link ffsl,
     (select s.id source_id,
             array_agg(ff.value_prese order by ff.order_by) source_props
      from source s,
           source_freeform sff,
           freeform ff
      where sff.source_id = s.id
      and   sff.freeform_id = ff.id
      and   ff.type not in ('SOURCE_FILE', 'EXTERNAL_SOURCE_ID')
      group by s.id) s
where l.type = 'PRIMARY'
and   l.process_state_code = 'avalik'
and   lff.lexeme_id = l.id
and   lff.freeform_id = ffsl.freeform_id
and   ffsl.source_id = s.source_id
and   ds.code = l.dataset_code
and   ds.is_public = true
group by l.id
order by l.id;

create view view_ww_meaning_freeform_source_link 
as
select ffsl.meaning_id,
       array_agg(row (
         'FREEFORM',
         ffsl.freeform_id,
         ffsl.source_link_id,
         ffsl.type,
         ffsl.name,
         ffsl.value,
         ffsl.order_by,
         ffsl.source_id,
         ffsl.source_props
       )::type_source_link
       order by
       ffsl.freeform_id,
       ffsl.order_by
       ) source_links
from (select mff.meaning_id,
             mff.freeform_id,
             ffsl.id source_link_id,
             ffsl.type,
             ffsl.name,
             ffsl.value,
             ffsl.order_by,
             s.source_id,
             s.source_props
      from lexeme l,
           dataset ds,
           meaning_freeform mff,
           freeform_source_link ffsl,
           (select s.id source_id,
                   array_agg(ff.value_prese order by ff.order_by) source_props
            from source s,
                 source_freeform sff,
                 freeform ff
            where sff.source_id = s.id
            and   sff.freeform_id = ff.id
            and   ff.type not in ('SOURCE_FILE', 'EXTERNAL_SOURCE_ID')
            group by s.id) s
      where l.type = 'PRIMARY'
      and   l.process_state_code = 'avalik'
      and   l.meaning_id = mff.meaning_id
      and   ffsl.freeform_id = mff.freeform_id
      and   ffsl.source_id = s.source_id
      and   ds.code = l.dataset_code
      and   ds.is_public = true
      group by mff.meaning_id,
               mff.freeform_id,
               ffsl.id,
               s.source_id,
               s.source_props) ffsl
group by ffsl.meaning_id
order by ffsl.meaning_id;

create view view_ww_definition_source_link 
as
select dsl.meaning_id,
       array_agg(row (
         'DEFINITION',
         dsl.definition_id,
         dsl.source_link_id,
         dsl.type,
         dsl.name,
         dsl.value,
         dsl.order_by,
         dsl.source_id,
         dsl.source_props
       )::type_source_link
       order by
       dsl.definition_id,
       dsl.order_by
       ) source_links
from (select d.meaning_id,
             d.id definition_id,
             dsl.id source_link_id,
             dsl.type,
             dsl.name,
             dsl.value,
             dsl.order_by,
             s.source_id,
             s.source_props
      from lexeme l,
           dataset ds,
           definition d,
           definition_source_link dsl,
           (select s.id source_id,
                   array_agg(ff.value_prese order by ff.order_by) source_props
            from source s,
                 source_freeform sff,
                 freeform ff
            where sff.source_id = s.id
            and   sff.freeform_id = ff.id
            and   ff.type not in ('SOURCE_FILE', 'EXTERNAL_SOURCE_ID')
            group by s.id) s
      where l.type = 'PRIMARY'
      and   l.process_state_code = 'avalik'
      and   l.meaning_id = d.meaning_id
      and   dsl.definition_id = d.id
      and   dsl.source_id = s.source_id
      and   ds.code = l.dataset_code
      and   ds.is_public = true
      group by d.meaning_id,
               d.id,
               dsl.id,
               s.source_id,
               s.source_props) dsl
group by dsl.meaning_id
order by dsl.meaning_id;


-- lexical decision game data - OK
create view view_ww_lexical_decision_data 
as
select w.word,
       w.lang,
       w.is_word
from ((select w.word,
              w.lang,
              true is_word
       from (select distinct f.value word,
                    w.lang
             from word w,
                  paradigm p,
                  form f
             where p.word_id = w.id
             and   f.paradigm_id = p.id
             and   f.mode = 'WORD'
             and   exists (select l.id
                           from lexeme as l,
                                dataset as ds
                           where l.word_id = w.id
                           and   l.complexity = 'SIMPLE'
                           and   l.dataset_code = 'sss'
                           and   l.type = 'PRIMARY'
                           and   l.process_state_code = 'avalik'
                           and   ds.code = l.dataset_code
                           and   ds.is_public = true)
             and   f.value not like '% %'
             and   length(f.value) > 2) w
       order by random()) 
       union all
       (select nw.word,
              nw.lang,
              false is_word
       from game_nonword nw
       order by random())) w
order by random();

-- similarity judgement game data - NOT!
create view view_ww_similarity_judgement_data
as
select
  w.meaning_id,
  w.word,
  w.lang,
  w.dataset_code
from
((select 
       w.meaning_id,
       w.word,
       w.lang,
       'qq2' dataset_code
from (select distinct l.meaning_id,
             f.value word,
             w.lang
      from word w,
           paradigm p,
           form f,
           lexeme l
      where p.word_id = w.id
      and   f.paradigm_id = p.id
      and   f.mode = 'WORD'
      and   f.value not like '% %'
      and   length(f.value) > 2
      and   l.word_id = w.id
      and   l.dataset_code = 'qq2') w
order by random())
union all 
(select 
       w.meaning_id,
       w.word,
       w.lang,
       'ev2' dataset_code
from (select distinct l.meaning_id,
             f.value word,
             w.lang
      from word w,
           paradigm p,
           form f,
           lexeme l
      where p.word_id = w.id
      and   f.paradigm_id = p.id
      and   f.mode = 'WORD'
      and   f.value not like '% %'
      and   length(f.value) > 2
      and   l.word_id = w.id
      and   l.dataset_code = 'ev2') w
where not exists (select w2.id
                  from word w2,
                       paradigm p2,
                       form f2,
                       lexeme l2
                  where p2.word_id = w2.id
                  and   f2.paradigm_id = p2.id
                  and   f2.mode = 'WORD'
                  and   l2.word_id = w2.id
                  and   l2.dataset_code = 'qq2'
                  and   w.word = f2.value)
order by random())
union all
(select 
       w.meaning_id,
       w.word,
       w.lang,
       'psv' dataset_code
from (select distinct l.meaning_id,
             f.value word,
             w.lang
      from word w,
           paradigm p,
           form f,
           lexeme l
      where p.word_id = w.id
      and   f.paradigm_id = p.id
      and   f.mode = 'WORD'
      and   f.value not like '% %'
      and   length(f.value) > 2
      and   l.word_id = w.id
      and   l.dataset_code = 'psv') w
order by random())) w;

-- datasets, classifiers
create view view_ww_dataset
  as
    (select
       code,
       name,
       description,
       order_by
     from dataset
     where dataset.is_public = true
     order by order_by
    );

create view view_ww_classifier
  as
	((select
		'LANGUAGE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		language c,
		language_label cl
	where 
		c.code = cl.code
		and cl.type in ('wordweb', 'iso2')
	order by c.order_by, cl.lang, cl.type)
	union all 
	(select
		'MORPH' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		morph c,
		morph_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'DISPLAY_MORPH' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		display_morph c,
		display_morph_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'WORD_TYPE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		word_type c,
		word_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'ASPECT' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		aspect c,
		aspect_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'POS' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		pos c,
		pos_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'REGISTER' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		register c,
		register_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'DERIV' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		deriv c,
		deriv_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'DOMAIN' as name,
		c.origin,
		c.code,
		cl.value,
		cl.lang,
		'wordweb' as type, --really?
		c.order_by
	from 
		domain c,
		domain_label cl
	where 
		c.code = cl.code
		and c.origin = cl.origin
		and cl.type = 'descrip'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'USAGE_TYPE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		usage_type c,
		usage_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'POS_GROUP' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		pos_group c,
		pos_group_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'WORD_REL_TYPE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		word_rel_type c,
		word_rel_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'LEX_REL_TYPE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		lex_rel_type c,
		lex_rel_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'MEANING_REL_TYPE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		meaning_rel_type c,
		meaning_rel_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
	union all
	(select
		'VALUE_STATE' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		meaning_rel_type c,
		meaning_rel_type_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
);


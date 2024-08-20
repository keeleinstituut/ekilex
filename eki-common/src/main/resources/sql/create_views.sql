drop view if exists view_ww_dataset_word_menu;
drop view if exists view_ww_word_search;
drop view if exists view_ww_word;
drop view if exists view_ww_form;
drop view if exists view_ww_meaning;
drop view if exists view_ww_lexeme;
drop view if exists view_ww_collocation;
drop view if exists view_ww_word_etymology;
drop view if exists view_ww_word_relation;
drop view if exists view_ww_lexeme_relation;
drop view if exists view_ww_meaning_relation;
drop view if exists view_ww_lexeme_source_link; -- to be removed later
drop view if exists view_ww_lexeme_freeform_source_link; -- to be removed later
drop view if exists view_ww_meaning_freeform_source_link; -- to be removed later
drop view if exists view_ww_word_etym_source_link; -- to be removed later
drop view if exists view_ww_definition_source_link; -- to be removed later
drop view if exists view_ww_lexical_decision_data;
drop view if exists view_ww_similarity_judgement_data;
drop view if exists view_ww_classifier;
drop view if exists view_ww_dataset;
drop view if exists view_ww_news_article;
drop type if exists type_meaning_word;
drop type if exists type_note;
drop type if exists type_freeform;
drop type if exists type_lang_complexity;
drop type if exists type_definition;
drop type if exists type_domain;
drop type if exists type_media_file;
drop type if exists type_usage;
drop type if exists type_source_link;
drop type if exists type_colloc_member;
drop type if exists type_word_etym_relation;
drop type if exists type_word_relation;
drop type if exists type_lexeme_relation;
drop type if exists type_meaning_relation;

create type type_lang_complexity as (
				lang varchar(10),
				dataset_code varchar(10),
				lex_complexity varchar(100),
				data_complexity varchar(100));
create type type_definition as (
				lexeme_id bigint,
				meaning_id bigint,
				definition_id bigint,
				value text,
				value_prese text,
				lang char(3),
				complexity varchar(100),
        		source_links json,
				notes json);
create type type_domain as (origin varchar(100), code varchar(100));
create type type_media_file as (
				id bigint,
				source_url text,
				title text,
				complexity varchar(100),
				source_links json);
create type type_source_link as (
				source_link_id bigint,
				source_link_type varchar(100),
        		source_link_name text,
				order_by bigint,
				source_id bigint,
		        source_name text,
		        source_value text,
		        source_value_prese text,
		        is_source_public boolean);
create type type_usage as (
				usage_id bigint,
				value text,
				value_prese text,
				lang char(3),
				complexity varchar(100),
				usage_translations text array,
				usage_definitions text array,
				source_links json);
create type type_note as (
				note_id bigint,
				value text,
				value_prese text,
				lang char(3),
				complexity varchar(100),
				created_by text,
				created_on timestamp,
        		modified_by text,
        		modified_on timestamp,
        		source_links json);
create type type_freeform as (
				freeform_id bigint,
				type varchar(100),
				value text,
				lang char(3),
				complexity varchar(100),
				created_by text,
				created_on timestamp,
        		modified_by text,
        		modified_on timestamp);
create type type_colloc_member as (
				lexeme_id bigint,
				word_id bigint,
				word text,
				form text,
				homonym_nr integer,
				conjunct varchar(100),
				weight numeric(14,4));
create type type_meaning_word as (
				lexeme_id bigint,
				meaning_id bigint,
				mw_lexeme_id bigint,
				mw_lex_complexity varchar(100),
				mw_lex_weight numeric(5,4),
				mw_lex_governments json, -- type_freeform
				mw_lex_register_codes varchar(100) array,
				mw_lex_value_state_code varchar(100),
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array);
create type type_word_etym_relation as (
				word_etym_rel_id bigint,
				comment text,
				is_questionable boolean,
				is_compound boolean,
				related_word_id bigint);
create type type_word_relation as (
				word_group_id bigint,
				word_rel_type_code varchar(100),
				relation_status varchar(100),
				order_by bigint,
				word_id bigint,
				word text,
				word_prese text,
				homonym_nr integer,
				homonyms_exist boolean,
				lang char(3),
				aspect_code varchar(100),
				word_type_codes varchar(100) array,
				lex_complexities varchar(100) array);
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
				weight numeric(5,4),
        inexact_syn_def text,
				lex_value_state_codes varchar(100) array,
				lex_register_codes varchar(100) array,
				lex_government_values text array,
				meaning_rel_type_code varchar(100));

create view view_ww_dataset_word_menu 
as
select w.dataset_code,
       w.first_letter,
       array_agg(w.word order by w.word) words
from (select left (w.value, 1) first_letter,
             w.value word,
             l.dataset_code
      from word w,
           lexeme l,
           dataset ds
      where w.value != ''
      and   w.is_public = true
      and   w.is_word = true
      and   l.word_id = w.id
      and   l.is_public = true
      and   l.dataset_code = ds.code
      and   ds.is_public = true
      and   ds.code not in ('ety', 'eki')) w
group by w.dataset_code,
         w.first_letter
order by w.dataset_code,
         w.first_letter;

create view view_ww_word_search
as
select ws.sgroup,
       ws.word,
       ws.crit,
       ws.langs_filt,
       ws.lang_order_by,
       wlc.lang_complexities
from (
        (select 'word' as sgroup,
                w.value word,
                lower(w.value) crit,
                array_agg(
                	case
                      when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then w.lang
                      else 'other'
                    end
                ) langs_filt,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from word w,
              language wl
         where w.lang = wl.code
           and w.is_public = true
           and w.is_word = true
           and not exists
             (select wwt.id
              from word_word_type wwt
              where wwt.word_id = w.id
                and wwt.word_type_code = 'viga')
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.is_public = true
                and ds.code = l.dataset_code
                and ds.is_public = true)
         group by w.value)
      union all
        (select 'as_word' as sgroup,
                w.value word,
                lower(w.value_as_word) crit,
                array_agg(
                	case
                      when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then w.lang
                      else 'other'
                    end
                ) langs_filt,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from word w,
              language wl
         where w.lang = wl.code
           and w.value_as_word is not null
           and w.is_public = true
           and w.is_word = true
           and not exists
             (select wwt.id
              from word_word_type wwt
              where wwt.word_id = w.id
                and wwt.word_type_code = 'viga')
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.is_public = true
                and ds.code = l.dataset_code
                and ds.is_public = true)
         group by w.value,
                  w.value_as_word)
      union all
        (select 'form' as sgroup,
                w.value word,
                lower(f.value) crit,
                array_agg(
                	case
                      when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then w.lang
                      else 'other'
                    end
                ) langs_filt,
                (array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
         from form f,
              paradigm_form pf,
              paradigm p,
              word w,
              language wl
         where f.id = pf.form_id
           and p.id = pf.paradigm_id
           and p.word_id = w.id
           and w.lang = wl.code
           and w.is_public = true
           and w.is_word = true
           and f.morph_code != '??'
           and f.value != w.value
           and f.value != '-'
           and pf.morph_exists = true
           and exists
             (select w.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
                and l.is_public = true
                and ds.code = l.dataset_code
                and ds.is_public = true)
         group by w.value,
                  f.value)) ws,
  (select lc.word,
          array_agg(distinct row (
                    case
                      when (lc.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then lc.lang
                      else 'other'
                    end,
                    lc.dataset_code,
                    lc.lex_complexity,
                    trim(trailing '12' from lc.data_complexity))::type_lang_complexity
                    ) lang_complexities
   from (
           (select
                 w2.value as word,
                 w2.lang,
                 l1.dataset_code,
                 l1.complexity lex_complexity,
                 l2.complexity data_complexity
            from lexeme l1
            inner join dataset l1ds on l1ds.code = l1.dataset_code
            inner join lexeme l2 on l2.meaning_id = l1.meaning_id
            and l2.dataset_code = l1.dataset_code
            and l2.word_id != l1.word_id
            inner join dataset l2ds on l2ds.code = l2.dataset_code
            inner join word w2 on w2.id = l2.word_id
            where l1.is_public = true
              and l1ds.is_public = true
              and l2.is_public = true
              and l2ds.is_public = true)
         union all
           (select
                 w.value as word,
                 coalesce(ff.lang, w.lang) lang,
                 l.dataset_code,
                 l.complexity lex_complexity,
                 ff.complexity data_complexity
            from word w,
                 lexeme l,
                 lexeme_freeform lff,
                 freeform ff,
                 dataset ds
            where l.is_public = true
              and ds.code = l.dataset_code
              and ds.is_public = true
              and l.word_id = w.id
              and w.is_public = true
              and w.is_word = true
              and lff.lexeme_id = l.id
              and lff.freeform_id = ff.id
              and ff.is_public = true
              and ff.type in ('GRAMMAR', 'GOVERNMENT'))
         union all
           (select
                 w.value as word,
                 coalesce(ln.lang, w.lang) lang,
                 l.dataset_code,
                 l.complexity lex_complexity,
                 ln.complexity data_complexity
            from word w,
                 lexeme l,
                 lexeme_note ln,
                 dataset ds
            where l.is_public = true
              and ds.code = l.dataset_code
              and ds.is_public = true
              and l.word_id = w.id
              and w.is_public = true
              and w.is_word = true
              and ln.lexeme_id = l.id
              and ln.is_public = true)
         union all
           (select
                 w.value as word,
                 u.lang,
                 l.dataset_code,
                 l.complexity lex_complexity,
                 u.complexity data_complexity
            from word w,
                 lexeme l,
                 usage u,
                 dataset ds
            where l.is_public = true
              and l.word_id = w.id
              and w.is_public = true
              and w.is_word = true
              and ds.code = l.dataset_code
              and ds.is_public = true
              and u.lexeme_id = l.id
              and u.is_public = true)
         union all
           (select
                 w.value as word,
                 ut.lang,
                 l.dataset_code,
                 l.complexity lex_complexity,
                 u.complexity data_complexity
            from word w,
                 lexeme l,
                 usage u,
                 usage_translation ut,
                 dataset ds
            where l.is_public = true
              and l.word_id = w.id
              and w.is_public = true
              and w.is_word = true
              and ds.code = l.dataset_code
              and ds.is_public = true
              and u.lexeme_id = l.id
              and u.is_public = true
              and ut.usage_id = u.id)
         union all
           (select
                 w.value as word,
                 d.lang,
                 l.dataset_code,
                 l.complexity lex_complexity,
                 d.complexity data_complexity
            from word w,
                 lexeme l,
                 definition d,
                 dataset ds
            where l.is_public = true
              and l.word_id = w.id
              and w.is_public = true
              and w.is_word = true
              and ds.code = l.dataset_code
              and ds.is_public = true
              and l.meaning_id = d.meaning_id
              and d.is_public = true)) lc
   group by lc.word) wlc
where ws.word = wlc.word
order by ws.sgroup,
         ws.word,
         ws.crit;
         
create view view_ww_word
as
select w.word_id,
       w.word,
       w.word_prese,
       w.as_word,
       w.lang,
       case
         when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then w.lang
         else 'other'
       end lang_filt,
       w.lang_order_by,
       w.homonym_nr,
       wt.word_type_codes,
       w.display_morph_code,
       w.gender_code,
       w.aspect_code,
       w.vocal_form,
       w.manual_event_on,
       w.last_activity_event_on,
       lc.lang_complexities,
       mw.meaning_words,
       wd.definitions,
       od_ws.od_word_recommendations,
       wf.freq_value,
       wf.freq_rank,
       w.forms_exist,
       w.min_ds_order_by,
       w.word_type_order_by
from (select w.id as word_id,
             w.value as word,
             w.value_prese as word_prese,
             w.value_as_word as as_word,
             w.lang,
             (select lc.order_by
              from language lc
              where lc.code = w.lang
              limit 1) lang_order_by,
             w.homonym_nr,
             w.display_morph_code,
             w.gender_code,
             w.aspect_code,
             w.vocal_form,
             w.manual_event_on,
             (select al.event_on
              from word_last_activity_log wlal,
                   activity_log al
              where wlal.word_id = w.id
              and   wlal.activity_log_id = al.id) last_activity_event_on,
             (select count(f.id) > 0
              from paradigm p,
                   paradigm_form pf,
                   form f
              where p.word_id = w.id
              and   pf.paradigm_id = p.id
              and   pf.form_id = f.id) forms_exist,
             (select min(ds.order_by)
              from lexeme l,
                   dataset ds
              where l.word_id = w.id
              and   l.is_public = true
              and   ds.code = l.dataset_code
              and   ds.is_public = true) min_ds_order_by,
             (select count(wt.id)
              from word_word_type wt
              where wt.word_id = w.id
              and wt.word_type_code = 'pf') word_type_order_by
      from word as w
      where w.is_public = true
      		and w.is_word = true
        and exists (select l.id
                    from lexeme as l,
                         dataset ds
                    where l.word_id = w.id
                    and   l.is_public = true
                    and   ds.code = l.dataset_code
                    and   ds.is_public = true)
      group by w.id) as w
  left outer join (select wt.word_id,
                          array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                   from word_word_type wt
                   where wt.word_type_code not in ('vv', 'yv', 'vvar')
                   group by wt.word_id) wt
               on wt.word_id = w.word_id
  left outer join (select mw.word_id,
                          json_agg(row (
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
                                mw.mw_aspect_code,
                                mw.mw_word_type_codes
                                )::type_meaning_word
                                order by
                                mw.hw_lex_level1,
                                mw.hw_lex_level2,
                                mw.hw_lex_order_by,
                                mw.mw_lex_order_by) meaning_words
                   from (select distinct l1.word_id,
                                l1.id lexeme_id,
                                l1.meaning_id,
                                l1.level1 hw_lex_level1,
                                l1.level2 hw_lex_level2,
                                l1.order_by hw_lex_order_by,
                                l2.id mw_lex_id,
                                l2.complexity mw_lex_complexity,
                                l2.weight mw_lex_weight,
                                w2.id mw_word_id,
                                w2.value mw_word,
                                w2.value_prese mw_word_prese,
                                w2.homonym_nr mw_homonym_nr,
                                w2.lang mw_lang,
                                (select array_agg(wt.word_type_code order by wt.order_by)
                                 from word_word_type wt
                                 where wt.word_id = w2.id
                                   and wt.word_type_code not in ('vv', 'yv', 'vvar')
                                 group by wt.word_id) mw_word_type_codes,
                                w2.aspect_code mw_aspect_code,
                                l2.order_by mw_lex_order_by
                         from lexeme l1
                           inner join dataset l1ds
                                   on l1ds.code = l1.dataset_code
                           inner join lexeme l2
                                   on l2.meaning_id = l1.meaning_id
                                  and l2.word_id != l1.word_id
                           inner join dataset l2ds
                                   on l2ds.code = l2.dataset_code
                           inner join word w2
                                   on w2.id = l2.word_id
                                  and w2.is_public = true
                                  and w2.is_word = true
                         where l1.is_public = true
                         and   l1ds.is_public = true
                         and   l2.is_public = true
                         and   l2ds.is_public = true
                         and   coalesce (l2.value_state_code, 'anything') != 'vigane') mw
                   group by mw.word_id) mw
               on mw.word_id = w.word_id
  inner join (select lc.word_id,
                     array_agg(distinct row (
                            case
                              when (lc.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then lc.lang
                              else 'other'
                            end,
                            lc.dataset_code,
                            lc.lex_complexity,
                            trim(trailing '12' from lc.data_complexity)
                            )::type_lang_complexity) lang_complexities
              from ((select l1.word_id,
                            w2.lang,
                            l1.dataset_code,
                            l1.complexity lex_complexity,
                            l2.complexity data_complexity
                     from lexeme l1
                       inner join dataset l1ds
                               on l1ds.code = l1.dataset_code
                       inner join lexeme l2
                               on l2.meaning_id = l1.meaning_id
                              and l2.dataset_code = l1.dataset_code
                              and l2.word_id != l1.word_id
                       inner join dataset l2ds
                               on l2ds.code = l2.dataset_code
                       inner join word w2
                               on w2.id = l2.word_id
                              and w2.is_public = true
                              and w2.is_word = true
                     where l1.is_public = true
                     and   l1ds.is_public = true
                     and   l2.is_public = true
                     and   l2ds.is_public = true)
                     union all
                     (select l.word_id,
                            coalesce(ff.lang, w.lang) lang,
                            l.dataset_code,
                            l.complexity lex_complexity,
                            ff.complexity data_complexity
                     from word w,
                          lexeme l,
                          lexeme_freeform lff,
                          freeform ff,
                          dataset ds
                     where l.is_public = true
                     and   ds.code = l.dataset_code
                     and   ds.is_public = true
                     and   l.word_id = w.id
                     and   w.is_public = true
                     and   w.is_word = true
                     and   lff.lexeme_id = l.id
                     and   lff.freeform_id = ff.id
                     and   ff.is_public = true
                     and   ff.type in ('GRAMMAR', 'GOVERNMENT'))
                     union all
                     (select l.word_id,
                            coalesce(ln.lang, w.lang) lang,
                            l.dataset_code,
                            l.complexity lex_complexity,
                            ln.complexity data_complexity
                     from word w,
                          lexeme l,
                          lexeme_note ln,
                          dataset ds
                     where l.is_public = true
                     and   ds.code = l.dataset_code
                     and   ds.is_public = true
                     and   l.word_id = w.id
                     and   w.is_public = true
                     and   w.is_word = true
                     and   ln.lexeme_id = l.id
                     and   ln.is_public = true)
                     union all
                     (select l.word_id,
                            u.lang,
                            l.dataset_code,
                            l.complexity lex_complexity,
                            u.complexity data_complexity
                     from lexeme l,
                          usage u,
                          dataset ds
                     where l.is_public = true
                     and   ds.code = l.dataset_code
                     and   ds.is_public = true
                     and   u.lexeme_id = l.id
                     and   u.is_public = true)
                     union all
                     (select l.word_id,
                            ut.lang,
                            l.dataset_code,
                            l.complexity lex_complexity,
                            u.complexity data_complexity
                     from lexeme l,
                          usage u,
                          usage_translation ut,
                          dataset ds
                     where l.is_public = true
                     and   ds.code = l.dataset_code
                     and   ds.is_public = true
                     and   u.lexeme_id = l.id
                     and   u.is_public = true
                     and   ut.usage_id = u.id)
                     union all
                     (select l.word_id,
                            d.lang,
                            l.dataset_code,
                            l.complexity lex_complexity,
                            d.complexity data_complexity
                     from lexeme l,
                          definition d,
                          dataset ds
                     where l.is_public = true
                     and   ds.code = l.dataset_code
                     and   ds.is_public = true
                     and   l.meaning_id = d.meaning_id
                     and   d.is_public = true)
                     union all
                     (select l1.word_id,
                             w1.lang,
                             l1.dataset_code,
                             l1.complexity lex_complexity,
                             l1.complexity data_complexity
                     from lexeme l1,
                          word w1,
                          dataset l1ds
                     where l1.is_public = true
                     and   l1ds.code = l1.dataset_code
                     and   l1ds.is_public = true
                     and   w1.id = l1.word_id
                     and   w1.is_public = true
                     and   w1.is_word = true
                     and   not exists (select l2.id
                                       from lexeme l2,
                                            dataset l2ds
                                       where l2.meaning_id = l1.meaning_id
                                       and   l2.dataset_code = l1.dataset_code
                                       and   l2.id != l1.id
                                       and   l2.is_public = true
                                       and   l2ds.code = l2.dataset_code
                                       and   l2ds.is_public = true)
                     and   not exists (select d.id from definition d where d.meaning_id = l1.meaning_id and d.is_public = true)
                     and   not exists (select ln.id from lexeme_note ln where ln.lexeme_id = l1.id and ln.is_public = true)
                     and   not exists (select u.id from usage u where u.lexeme_id = l1.id and u.is_public = true)
                     and   not exists (select ff.id
                                       from lexeme_freeform lff,
                                            freeform ff
                                       where lff.lexeme_id = l1.id
                                       and   lff.freeform_id = ff.id
                                       and   ff.type in ('GRAMMAR', 'GOVERNMENT')))) lc
              group by lc.word_id) lc
          on lc.word_id = w.word_id
  left outer join (select wd.word_id,
                          json_agg(row (
                                wd.lexeme_id,
                                wd.meaning_id,
                                wd.definition_id,
                                wd.value,
                                wd.value_prese,
                                wd.lang,
                                wd.complexity,
                                null,
                                null
                                )::type_definition
                                order by
                                wd.ds_order_by,
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
                                substring(d.value, 1, 200) "value",
                                substring(d.value_prese, 1, 200) value_prese,
                                d.lang,
                                d.complexity,
                                d.order_by def_order_by,
                                ds.order_by ds_order_by
                         from lexeme l
                           inner join dataset ds
                                   on ds.code = l.dataset_code
                           inner join definition d
                                   on d.meaning_id = l.meaning_id
                                  and d.is_public = true
                         where l.is_public = true
                         and   ds.is_public = true) wd
                   group by wd.word_id) wd
               on wd.word_id = w.word_id
  left outer join (select wf.word_id,
                          json_agg(row (ff.id, ff.type, ff.value_prese, null, null, null, null, ff.modified_by, ff.modified_on)::type_freeform order by ff.order_by) od_word_recommendations
                   from word_freeform wf,
                        freeform ff
                   where wf.freeform_id = ff.id
                   and   ff.type = 'OD_WORD_RECOMMENDATION'
                   group by wf.word_id) od_ws
               on od_ws.word_id = w.word_id
  left outer join (select wf.word_id,
                          wf.value freq_value,
                          wf.rank freq_rank,
                          fc.corp_date
                   from word_freq wf,
                        freq_corp fc
                   where wf.freq_corp_id = fc.id
                   and fc.is_public = true) wf
               on wf.word_id = w.word_id and wf.corp_date = (select max(fcc.corp_date)
                                                             from word_freq wff,
                                                                  freq_corp fcc
                                                             where wff.freq_corp_id = fcc.id
                                                             and fcc.is_public = true
                                                             and wff.word_id = w.word_id);

create view view_ww_form 
as
select w.id word_id,
       p.word_class,
       w.value word,
       w.lang,
       p.id paradigm_id,
       p.comment paradigm_comment,
       p.inflection_type,
       p.inflection_type_nr,
       f.id form_id,
       f.value,
       f.value_prese,
       f.morph_code,
       pf.morph_group1,
       pf.morph_group2,
       pf.morph_group3,
       pf.display_level,
       pf.display_form,
       pf.audio_file,
       pf.morph_exists,
       pf.is_questionable,
       pf.order_by,
       ff.form_freq_value,
       ff.form_freq_rank,
       ff.form_freq_rank_max,
       mf.morph_freq_value,
       mf.morph_freq_rank,
       mf.morph_freq_rank_max
from word w
  inner join paradigm p
          on p.word_id = w.id
  inner join paradigm_form pf
          on pf.paradigm_id = p.id
  inner join form f
          on f.id = pf.form_id
  left outer join (select ff.form_id,
                          ff.value form_freq_value,
                          ff.rank form_freq_rank,
                          fc.corp_date,
                          (select max(fff.rank) from form_freq fff where fff.freq_corp_id = fc.id) form_freq_rank_max
                   from form_freq ff,
                        freq_corp fc
                   where ff.freq_corp_id = fc.id
                   and fc.is_public = true) ff on ff.form_id = f.id and ff.corp_date = (select max(fcc.corp_date)
                                                                                        from form_freq fff,
                                                                                             freq_corp fcc
                                                                                        where fff.freq_corp_id = fcc.id
                                                                                        and fcc.is_public = true
                                                                                        and fff.form_id = f.id)
  left outer join (select mf.morph_code,
                          mf.value morph_freq_value,
                          mf.rank morph_freq_rank,
                          fc.corp_date,
                          (select max(mff.rank) from morph_freq mff where mff.freq_corp_id = fc.id) morph_freq_rank_max
                   from morph_freq mf,
                        freq_corp fc
                   where mf.freq_corp_id = fc.id
                   and fc.is_public = true) mf on mf.morph_code = f.morph_code and mf.corp_date = (select max(fcc.corp_date)
                                                                                                   from morph_freq mff,
                                                                                                        freq_corp fcc
                                                                                                   where mff.freq_corp_id = fcc.id
                                                                                                   and fcc.is_public = true
                                                                                                   and mff.morph_code = f.morph_code)
where w.is_public = true
      and w.is_word = true
  and exists (select l.id
              from lexeme as l,
                   dataset ds
              where l.word_id = w.id
              and   l.is_public = true
              and   ds.code = l.dataset_code
              and   ds.is_public = true)
order by w.id,
         p.id,
         f.id;

create view view_ww_meaning
as
select m.id meaning_id,
       m.manual_event_on,
       m.last_approve_or_edit_event_on,
       m_dom.domain_codes,
       m_img.meaning_images,
       m_media.media_files,
       m_spp.systematic_polysemy_patterns,
       m_smt.semantic_types,
       m_lcm.learner_comments,
       m_pnt.notes,
       d.definitions
from (select m.id,
             m.manual_event_on,
            (select al.event_on
             from meaning_last_activity_log mlal,
                  activity_log al
             where mlal.meaning_id = m.id
             and   mlal.activity_log_id = al.id
             order by mlal.type
             limit 1) last_approve_or_edit_event_on
      from meaning m
      where exists (select l.id
                    from lexeme as l,
                         dataset ds
                    where l.meaning_id = m.id
                    and   l.is_public = true
                    and   ds.code = l.dataset_code
                    and   ds.is_public = true)) m
  left outer join (select m_dom.meaning_id,
                          json_agg(row (m_dom.domain_origin, m_dom.domain_code)::type_domain order by m_dom.order_by) domain_codes
                   from meaning_domain m_dom
                   group by m_dom.meaning_id) m_dom
               on m_dom.meaning_id = m.id
  left outer join (select d.meaning_id,
                          json_agg(row (
                            null,
                            d.meaning_id,
                            d.id,
                            d.value,
                            d.value_prese,
                            d.lang,
                            d.complexity,
                            d.source_links,
                            d.notes
                          )::type_definition
                          order by
                          d.order_by
                          ) definitions
                   from (select d.meaning_id,
                                d.id,
                                substring(d.value, 1, 2000) "value",
                                substring(d.value_prese, 1, 2000) value_prese,
                                d.lang,
                                d.complexity,
                                d.order_by,
                                dsl.source_links,
                                (select json_agg(notes) as notes
                                 from (select dn.value_prese as value,
                                              json_agg(row (
                                                         dnsl.source_link_id,
                                                         dnsl.type,
                                                         dnsl.name,
                                                         dnsl.order_by,
                                                         dnsl.source_id,
                                                         dnsl.source_name,
                                                         dnsl.source_value,
                                                         dnsl.source_value_prese,
                                                         dnsl.is_source_public
                                                         )::type_source_link
                                                       order by
                                                         dnsl.order_by) source_links
                                       from definition_note dn
                                              left outer join (select dnsl.definition_note_id,
                                                                      dnsl.id source_link_id,
                                                                      dnsl.type,
                                                                      dnsl.name,
                                                                      dnsl.order_by,
                                                                      s.id source_id,
                                                                      s.name source_name,
                                                                      s.value source_value,
                                                                      s.value_prese source_value_prese,
                                                                      s.is_public is_source_public
                                                               from definition_note_source_link dnsl, source s
                                                               where dnsl.source_id = s.id) dnsl on dnsl.definition_note_id = dn.id
                                       where dn.definition_id = d.id
                                         and dn.is_public = true
                                       group by dn.id, dn.value_prese) notes) notes
                         from definition d
                         left outer join (select dsl.definition_id,
                                                 json_agg(row (
                                                           dsl.id,
                                                           dsl.type,
                                                           dsl.name,
                                                           dsl.order_by,
                                                           s.id,
                                                           s.name,
                                                           s.value,
                                                           s.value_prese,
                                                           s.is_public
                                                           )::type_source_link
                                                         order by
                                                           dsl.definition_id,
                                                           dsl.order_by) source_links
                                          from definition_source_link dsl, source s
                                          where dsl.source_id = s.id
                                          group by dsl.definition_id) dsl on dsl.definition_id = d.id
                         where d.is_public = true) d
                   group by d.meaning_id) d on d.meaning_id = m.id
  left outer join (select mi.meaning_id,
                          json_agg(row (
                            mi.id,
                            mi.url,
                            mi.title,
                            mi.complexity,
                            misl.source_links
                          )::type_media_file
                          order by
                          mi.order_by
                          ) meaning_images
                   from meaning_image mi
                   left outer join (select misl.meaning_image_id,
										   json_agg(row (
													   misl.id,
													   misl.type,
													   misl.name,
													   misl.order_by,
													   s.id,
													   s.name,
													   s.value,
													   s.value_prese,
													   s.is_public
													   )::type_source_link
													 order by
													   misl.meaning_image_id,
													   misl.order_by) source_links
										 from meaning_image_source_link misl, source s
										 where misl.source_id = s.id
										 group by misl.meaning_image_id) misl on misl.meaning_image_id = mi.id
                   group by mi.meaning_id) m_img on m_img.meaning_id = m.id
  left outer join (select mff.meaning_id,
                          json_agg(row (
                                      ff_mf.id,
                                      ff_mf.value_text,
                                      null,
                                      ff_mf.complexity,
                                      null
                                      )::type_media_file
                                    order by ff_mf.order_by
                            ) media_files
                   from meaning_freeform mff,
                   		freeform ff_mf
                   where ff_mf.id = mff.freeform_id
                   and 	 ff_mf.type = 'MEDIA_FILE'
                   group by mff.meaning_id) m_media on m_media.meaning_id = m.id
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
  left outer join (select mn.meaning_id,
                          json_agg(row (
                            mn.meaning_note_id,
                            mn.value,
                            mn.value_prese,
                            mn.lang,
                            mn.complexity,
                            mn.created_by,
                            mn.created_on,
                            mn.modified_by,
                            mn.modified_on,
                            mn.source_links)::type_note order by mn.order_by) notes
                   from (select mn.meaning_id,
                   				mn.id meaning_note_id,
                   				mn.value,
                   				mn.value_prese,
                   				mn.lang,
                   				mn.complexity,
                   				mn.created_by,
                   				mn.created_on,
                   				mn.modified_by,
                   				mn.modified_on,
                   				mn.order_by,
                   				mnsl.source_links
                   		 from meaning_note mn
                   		 left outer join (select mnsl.meaning_note_id,
												json_agg(row (
														   mnsl.id,
														   mnsl.type,
														   mnsl.name,
														   mnsl.order_by,
														   s.id,
														   s.name,
														   s.value,
														   s.value_prese,
														   s.is_public
														   )::type_source_link
														 order by
														   mnsl.meaning_note_id,
														   mnsl.order_by) source_links
										 from meaning_note_source_link mnsl, source s
										 where mnsl.source_id = s.id
										 group by mnsl.meaning_note_id) mnsl on mnsl.meaning_note_id = mn.id
                   		 where mn.is_public = true) mn
                   group by mn.meaning_id) m_pnt on m_pnt.meaning_id = m.id
order by m.id;

create view view_ww_lexeme
as
select l.id lexeme_id,
       l.word_id,
       l.meaning_id,
       l.dataset_code,
       ds.type dataset_type,
       ds.name dataset_name,
       l.value_state_code,
       l.proficiency_level_code,
       l.reliability,
       l.level1,
       l.level2,
       l.weight,
       l.complexity,
       ds.order_by dataset_order_by,
       l.order_by lexeme_order_by,
       l_lc.lang_complexities,
       l_reg.register_codes,
       l_pos.pos_codes,
       l_rgn.region_codes,
       l_der.deriv_codes,
       mw.meaning_words,
       anote.advice_notes,
       pnote.notes,
       gramm.grammars,
       gov.governments,
       usg.usages,
       lsl.source_links
from lexeme l
  inner join dataset ds on ds.code = l.dataset_code
  left outer join (select l_reg.lexeme_id, array_agg(l_reg.register_code order by l_reg.order_by) register_codes from lexeme_register l_reg group by l_reg.lexeme_id) l_reg on l_reg.lexeme_id = l.id
  left outer join (select l_pos.lexeme_id, array_agg(l_pos.pos_code order by l_pos.order_by) pos_codes from lexeme_pos l_pos group by l_pos.lexeme_id) l_pos on l_pos.lexeme_id = l.id
  left outer join (select l_rgn.lexeme_id, array_agg(l_rgn.region_code order by l_rgn.order_by) region_codes from lexeme_region l_rgn group by l_rgn.lexeme_id) l_rgn on l_rgn.lexeme_id = l.id
  left outer join (select l_der.lexeme_id, array_agg(l_der.deriv_code) deriv_codes from lexeme_deriv l_der group by l_der.lexeme_id) l_der on l_der.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          array_agg(ff.value_text order by ff.order_by) advice_notes
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'ADVICE_NOTE'
                   group by lf.lexeme_id) anote on anote.lexeme_id = l.id
  left outer join (select ln.lexeme_id,
                          json_agg(row (
                          	ln.lexeme_note_id,
                          	ln.value,
                          	ln.value_prese,
                          	ln.lang,
                          	ln.complexity,
                          	ln.created_by,
                            ln.created_on,
                            ln.modified_by,
                            ln.modified_on,
                          	ln.source_links)::type_note order by ln.order_by) notes
                   from (select ln.lexeme_id,
                   				ln.id lexeme_note_id,
                   				ln.value,
	                          	ln.value_prese,
	                          	ln.lang,
	                          	ln.complexity,
	                          	ln.created_by,
	                            ln.created_on,
	                            ln.modified_by,
	                            ln.modified_on,
	                            ln.order_by,
	                            lnsl.source_links
                   		from lexeme_note ln
                   		left outer join (select lnsl.lexeme_note_id,
												json_agg(row (
														   lnsl.id,
														   lnsl.type,
														   lnsl.name,
														   lnsl.order_by,
														   s.id,
														   s.name,
														   s.value,
														   s.value_prese,
														   s.is_public
														   )::type_source_link
														 order by
														   lnsl.lexeme_note_id,
														   lnsl.order_by) source_links
										 from lexeme_note_source_link lnsl, source s
										 where lnsl.source_id = s.id
										 group by lnsl.lexeme_note_id) lnsl on lnsl.lexeme_note_id = ln.id
                   		where ln.is_public = true) ln
                   group by ln.lexeme_id) pnote on pnote.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          json_agg(row (ff.id, ff.type, ff.value_prese, ff.lang, ff.complexity, null, null, null, null)::type_freeform order by ff.order_by) grammars
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'GRAMMAR'
                   group by lf.lexeme_id) gramm on gramm.lexeme_id = l.id
  left outer join (select lf.lexeme_id,
                          json_agg(row (ff.id, ff.type, ff.value_prese, ff.lang, ff.complexity, null, null, null, null)::type_freeform order by ff.order_by) governments
                   from lexeme_freeform lf,
                        freeform ff
                   where lf.freeform_id = ff.id
                   and   ff.type = 'GOVERNMENT'
                   group by lf.lexeme_id) gov on gov.lexeme_id = l.id
  left outer join (select mw.lexeme_id,
                          json_agg(row (
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
                                mw.mw_aspect_code,
                                mw.mw_word_type_codes
                                )::type_meaning_word
                                order by
                                mw.hw_lex_reliability,
                                mw.hw_lex_level1,
                                mw.hw_lex_level2,
                                mw.hw_lex_order_by,
                                mw.mw_lex_order_by) meaning_words
                   from (select distinct l1.word_id,
                                l1.id lexeme_id,
                                l1.meaning_id,
                                l1.level2 hw_lex_reliability,
                                l1.level1 hw_lex_level1,
                                l1.level2 hw_lex_level2,
                                l1.order_by hw_lex_order_by,
                                l2.id mw_lex_id,
                                l2.complexity mw_lex_complexity,
                                l2.weight mw_lex_weight,
                                (select jsonb_agg(row (ff.id, ff.type, ff.value_text, ff.lang, ff.complexity, null, null, null, null)::type_freeform order by ff.order_by)
                                 from lexeme_freeform lf,
                                      freeform ff
                                 where lf.lexeme_id = l2.id
                                 and   lf.freeform_id = ff.id
                                 and   ff.type = 'GOVERNMENT'
                                 group by lf.lexeme_id) mw_lex_governments,
                                (select array_agg(l_reg.register_code order by l_reg.order_by) from lexeme_register l_reg where l_reg.lexeme_id = l2.id group by l_reg.lexeme_id) mw_lex_register_codes,
                                l2.value_state_code mw_lex_value_state_code,
                                w2.id mw_word_id,
                                w2.value mw_word,
                                w2.value_prese mw_word_prese,
                                w2.homonym_nr mw_homonym_nr,
                                w2.lang mw_lang,
                                (select array_agg(wt.word_type_code order by wt.order_by)
                                 from word_word_type wt
                                 where wt.word_id = w2.id
                                 and   wt.word_type_code not in ('vv', 'yv', 'vvar')
                                 group by wt.word_id) mw_word_type_codes,
                                w2.aspect_code mw_aspect_code,
                                l2.order_by mw_lex_order_by
                         from lexeme l1
                           inner join dataset l1ds on l1ds.code = l1.dataset_code
                           inner join lexeme l2 on l2.meaning_id = l1.meaning_id
                           inner join dataset l2ds on l2ds.code = l2.dataset_code
                           inner join word w2 on w2.id = l2.word_id
                         where l1.is_public = true
                         and   l1ds.is_public = true
                         and   l2.is_public = true
                         and   w2.is_public = true
                         and   w2.is_word = true
                         and   l2ds.is_public = true
                         and   coalesce(l2.value_state_code, 'anything') != 'vigane') mw
                   group by mw.lexeme_id) mw on mw.lexeme_id = l.id
  left outer join (select u.lexeme_id,
                          json_agg(row (
                                u.usage_id,
                                u.value,
                                u.value_prese,
                                u.lang,
                                u.complexity,
                                u.usage_translations,
                                u.usage_definitions,
                                u.source_links)::type_usage
                                order by u.order_by) usages
                   from (select u.lexeme_id,
                                u.id usage_id,
                                u.value,
                                u.value_prese,
                                u.lang,
                                u.complexity,
                                u.order_by,
                                ut.usage_translations,
                                ud.usage_definitions,
                                usl.source_links
                         from usage u
	                     left outer join (select usl.usage_id,
												 json_agg(row (
													   usl.id,
													   usl.type,
													   usl.name,
													   usl.order_by,
													   s.id,
													   s.name,
													   s.value,
													   s.value_prese,
													   s.is_public
													   )::type_source_link
													 order by
													   usl.usage_id,
													   usl.order_by) source_links
										  from usage_source_link usl, source s
										  where usl.source_id = s.id
										  group by usl.usage_id) usl on usl.usage_id = u.id
	                     left outer join (select ut.usage_id,
	                                             array_agg(ut.value_prese order by ut.order_by) usage_translations
	                                      from usage_translation ut
	                                      where ut.lang = 'rus'
	                                      group by ut.usage_id) ut on ut.usage_id = u.id
	                     left outer join (select ud.usage_id,
	                                             array_agg(ud.value_prese order by ud.order_by) usage_definitions
	                                      from usage_definition ud
	                                      group by ud.usage_id) ud on ud.usage_id = u.id
	                     where u.is_public = true) u
                   group by u.lexeme_id) usg on usg.lexeme_id = l.id
  left outer join (select lsl.lexeme_id,
						  json_agg(row (
								   lsl.id,
								   lsl.type,
								   lsl.name,
								   lsl.order_by,
								   s.id,
								   s.name,
								   s.value,
								   s.value_prese,
								   s.is_public
								   )::type_source_link
								 order by
								   lsl.lexeme_id,
								   lsl.order_by) source_links
				   from lexeme_source_link lsl, source s
				   where lsl.source_id = s.id
				   group by lsl.lexeme_id) lsl on lsl.lexeme_id = l.id
  left outer join (select lc.id,
                          array_agg(distinct row (
                                 case 
                                   when (lc.lang in ('est', 'rus', 'eng', 'ukr', 'fra')) then lc.lang
                                   else 'other'
                                 end,
                                 lc.dataset_code,
                                 lc.lex_complexity,
                                 trim(trailing '12' from lc.data_complexity))::type_lang_complexity) lang_complexities
                   from ((select l1.id,
                                 w2.lang,
                                 l1.dataset_code,
                                 l1.complexity lex_complexity,
                                 l2.complexity data_complexity
                          from lexeme l1
                            inner join dataset l1ds on l1ds.code = l1.dataset_code
                            inner join lexeme l2
                                    on l2.meaning_id = l1.meaning_id
                                   and l2.dataset_code = l1.dataset_code
                                   and l2.word_id != l1.word_id
                            inner join dataset l2ds on l2ds.code = l2.dataset_code
                            inner join word w2 on w2.id = l2.word_id
                          where l1.is_public = true
                          and   l1ds.is_public = true
                          and   l2.is_public = true
                          and   l2ds.is_public = true)
                          union all
                          (select l.id,
                                 coalesce(ff.lang, w.lang) lang,
                                 l.dataset_code,
                                 l.complexity lex_complexity,
                                 ff.complexity data_complexity
                          from word w,
                               lexeme l,
                               lexeme_freeform lff,
                               freeform ff,
                               dataset ds
                          where l.is_public = true
                          and   ds.code = l.dataset_code
                          and   ds.is_public = true
                          and   l.word_id = w.id
                          and   w.is_public = true
                          and   w.is_word = true
                          and   lff.lexeme_id = l.id
                          and   lff.freeform_id = ff.id
                          and   ff.type in ('GRAMMAR', 'GOVERNMENT'))
                          union all
                          (select l.id,
                                 coalesce(ln.lang, w.lang) lang,
                                 l.dataset_code,
                                 l.complexity lex_complexity,
                                 ln.complexity data_complexity
                          from word w,
                               lexeme l,
                               lexeme_note ln,
                               dataset ds
                          where l.is_public = true
                          and   ds.code = l.dataset_code
                          and   ds.is_public = true
                          and   l.word_id = w.id
                          and   w.is_public = true
                          and   w.is_word = true
                          and   ln.lexeme_id = l.id
                          and   ln.is_public = true)
                          union all
                          (select l.id,
                                 coalesce(u.lang, w.lang) lang,
                                 l.dataset_code,
                                 l.complexity lex_complexity,
                                 u.complexity data_complexity
                          from word w,
                               lexeme l,
                               usage u,
                               dataset ds
                          where l.is_public = true
                          and   ds.code = l.dataset_code
                          and   ds.is_public = true
                          and   l.word_id = w.id
                          and   w.is_public = true
                          and   w.is_word = true
                          and   u.lexeme_id = l.id
                          and   u.is_public = true)
                          union all
                          (select l.id,
                                 ut.lang,
                                 l.dataset_code,
                                 l.complexity lex_complexity,
                                 u.complexity data_complexity
                          from lexeme l,
                               usage u,
                               usage_translation ut,
                               dataset ds
                          where l.is_public = true
                          and   ds.code = l.dataset_code
                          and   ds.is_public = true
                          and   u.lexeme_id = l.id
                          and   u.is_public = true
                          and   ut.usage_id = u.id)
                          union all
                          (select l.id,
                                 d.lang,
                                 l.dataset_code,
                                 l.complexity lex_complexity,
                                 d.complexity data_complexity
                          from lexeme l,
                               definition d,
                               dataset ds
                          where l.is_public = true
                          and   l.meaning_id = d.meaning_id
                          and   d.is_public = true
                          and   ds.code = l.dataset_code
                          and   ds.is_public = true)
                          union all
                          (select l1.id,
                                 w2.lang,
                                 l1.dataset_code,
                                 l1.complexity lex_complexity,
                                 l2.complexity data_complexity
                          from lex_relation r,
                               lexeme l1,
                               lexeme l2,
                               word w2,
                               dataset l1ds,
                               dataset l2ds
                          where l1.is_public = true
                          and   l1ds.code = l1.dataset_code
                          and   l1ds.is_public = true
                          and   r.lexeme1_id = l1.id
                          and   r.lexeme2_id = l2.id
                          and   l2.dataset_code = l1.dataset_code
                          and   l2.is_public = true
                          and   l2ds.code = l2.dataset_code
                          and   l2ds.is_public = true
                          and   w2.id = l2.word_id
                          and   w2.is_public = true
                          and   w2.is_word = true)
                          union all
                          (select l1.id,
                                 w1.lang,
                                 l1.dataset_code,
                                 l1.complexity lex_complexity,
                                 l1.complexity data_complexity
                          from lexeme l1,
                               word w1,
                               dataset l1ds
                          where l1.is_public = true
                          and   l1ds.code = l1.dataset_code
                          and   l1ds.is_public = true
                          and   w1.id = l1.word_id
                          and   w1.is_public = true
                          and   w1.is_word = true
                          and   not exists (select l2.id
	                                        from lexeme l2,
	                                             dataset l2ds
	                                        where l2.meaning_id = l1.meaning_id
	                                        and   l2.dataset_code = l1.dataset_code
	                                        and   l2.id != l1.id
	                                        and   l2.is_public = true
	                                        and   l2ds.code = l2.dataset_code
	                                        and   l2ds.is_public = true)
		                  and   not exists (select d.id from definition d where d.meaning_id = l1.meaning_id and d.is_public = true)
		                  and   not exists (select ln.id from lexeme_note ln where ln.lexeme_id = l1.id and ln.is_public = true)
		                  and   not exists (select u.id from usage u where u.lexeme_id = l1.id and u.is_public = true)
		                  and   not exists (select ff.id
	                                        from lexeme_freeform lff,
	                                        	 freeform ff
	                                        where lff.lexeme_id = l1.id
	                                        and   lff.freeform_id = ff.id
	                                        and   ff.type in ('GRAMMAR', 'GOVERNMENT')))) lc
                   group by lc.id) l_lc on l_lc.id = l.id
where l.is_public = true
and   ds.is_public = true
order by l.id;

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
       json_agg(row (
             lw2.lexeme_id,
             lw2.word_id,
             lw2.word,
             lc2.member_form,
             lw2.homonym_nr,
             lc2.conjunct,
             lc2.weight
             )::type_colloc_member
             order by
             lc2.member_order
       ) as colloc_members,
       c.complexity
from collocation as c
  inner join lex_colloc as lc1
          on lc1.collocation_id = c.id
  inner join lex_colloc as lc2
          on lc2.collocation_id = c.id
  inner join lexeme as l1
          on l1.id = lc1.lexeme_id
         and l1.is_public = true
  inner join dataset l1ds
          on l1ds.code = l1.dataset_code
         and l1ds.is_public = true
  inner join (select distinct l2.id lexeme_id,
                     l2.word_id,
                     w2.value word,
                     w2.homonym_nr
              from lexeme as l2,
                   word as w2,
                   dataset as l2ds
              where l2.is_public = true
              and   l2ds.code = l2.dataset_code
              and   l2ds.is_public = true
              and   l2.word_id = w2.id
              and   w2.is_public = true
              and   w2.is_word = true) lw2
          on lw2.lexeme_id = lc2.lexeme_id
  inner join lex_colloc_rel_group as rgr1
          on lc1.rel_group_id = rgr1.id
  inner join lex_colloc_pos_group as pgr1
          on pgr1.id = rgr1.pos_group_id
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

create view view_ww_word_etymology
as
with recursive word_etym_recursion (word_id, word_etym_word_id, word_etym_id, related_word_id, related_word_ids) as
(
  (
    select
      we.word_id,
      we.word_id word_etym_word_id,
      we.id word_etym_id,
      wer.related_word_id,
      array[we.word_id] as related_word_ids
    from
      word_etymology we
      left outer join word_etymology_relation wer on wer.word_etym_id = we.id
    where exists(select l.id
                 from lexeme l,
                      dataset ds
                 where l.word_id = we.word_id
                   and l.is_public = true
                   and ds.code = l.dataset_code
                   and ds.type = 'LEX')
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
  wer.word_etym_relations,
  wesl.source_links
from
  word_etym_recursion rec
  inner join word_etymology we on we.id = rec.word_etym_id
  inner join (select w.id,
				     w.lang,
				     w.value word
			  from word w
			  where exists (
				        select
				          l.id
				        from
				          lexeme l,
				          dataset ds
				        where
				        l.word_id = w.id
				        and l.is_public = true
				        and ds.code = l.dataset_code
				        and ds.type = 'LEX')
			  group by w.id) w on w.id = rec.word_etym_word_id
  left outer join (select wer.word_etym_id,
					      wer.related_word_id,
					      jsonb_agg(row (
					            wer.id,
					            wer.comment_prese,
					            wer.is_questionable,
					            wer.is_compound,
					            wer.related_word_id
					        	):: type_word_etym_relation
					        order by
					        	wer.order_by) word_etym_relations
					from word_etymology_relation wer
					group by wer.word_etym_id, wer.related_word_id) wer on wer.word_etym_id = rec.word_etym_id and wer.related_word_id != rec.word_id
  left outer join (select wesl.word_etym_id,
						  jsonb_agg(row (
								   wesl.id,
								   wesl.type,
								   wesl.name,
								   wesl.order_by,
								   s.id,
								   s.name,
								   s.value,
								   s.value_prese,
								   s.is_public
								   ):: type_source_link
								 order by
								   wesl.word_etym_id,
								   wesl.order_by) source_links
				   from word_etymology_source_link wesl, source s
				   where wesl.source_id = s.id
				   group by wesl.word_etym_id) wesl on wesl.word_etym_id = rec.word_etym_id
  left outer join (select l1.word_id,
					      array_agg(w2.value) meaning_words
				   from
						lexeme l1,
						meaning m,
						lexeme l2,
						word w2
				   where
						l1.meaning_id = m.id
					    and l2.meaning_id = m.id
					    and l1.word_id != l2.word_id
					    and l1.is_public = true
					    and l2.dataset_code = 'ety'
					    and l2.is_public = true
					    and l2.word_id = w2.id
					    and w2.lang = 'est'
				   group by
						l1.word_id) mw2 on mw2.word_id = rec.word_etym_word_id
group by
  rec.word_id,
  rec.word_etym_id,
  rec.word_etym_word_id,
  mw2.meaning_words,
  we.id,
  w.id,
  w.word,
  w.lang,
  wer.word_etym_relations,
  wesl.source_links
order by
  rec.word_id,
  we.order_by;
  
create view view_ww_word_relation 
as
select w.id word_id,
       wr.related_words,
       wg.word_group_members
from word w
  left outer join (select w1.id word_id,
                          json_agg(row (
                            null,
                            wr.word_rel_type_code,
                            wr.relation_status,
                            wr.word_rel_order_by,
                            wr.related_word_id,
                            wr.related_word,
                            wr.related_word_prese,
                            wr.related_word_homonym_nr,
                            wr.related_word_homonyms_exist,
                            wr.related_word_lang,
                            wr.related_word_aspect_code,
                            wr.word_type_codes,
                            wr.lex_complexities
                          )::type_word_relation order by wr.word_rel_order_by) related_words
                   from word w1
                     inner join (select distinct r.word1_id,
                                        r.word2_id related_word_id,
                                        r.word_rel_type_code,
                                        coalesce(r.relation_status, 'UNDEFINED') relation_status,
                                        r.order_by word_rel_order_by,
                                        w2.word related_word,
                                        w2.word_prese related_word_prese,
                                        w2.homonym_nr related_word_homonym_nr,
                                        (select max(wh.homonym_nr) > 1
                                         from word wh
                                         where wh.lang = w2.lang
                                         and wh.value = w2.word
                                         and exists (select l.id
                                                     from lexeme as l,
                                                          dataset ds
                                                     where l.is_public = true
                                                     and   ds.code = l.dataset_code
                                                     and   ds.is_public = true
                                                     and   l.word_id = wh.id)) as related_word_homonyms_exist,
                                        w2.lang related_word_lang,
                                        w2.aspect_code related_word_aspect_code,
                                        w2.word_type_codes,
                                        (select array_agg(distinct lc.complexity)
                                         from lexeme lc,
                                              dataset ds
                                         where lc.word_id = w2.id
                                         and lc.is_public = true
                                         and ds.code = lc.dataset_code
                                         and ds.is_public = true
                                         group by lc.word_id) as lex_complexities
                                 from word_relation r,
                                      (select w.id,
                                              w.value as word,
                                              w.value_prese as word_prese,
                                              w.homonym_nr,
                                              w.lang,
                                              w.aspect_code,
                                              array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                                       from word as w
                                         left outer join word_word_type as wt on wt.word_id = w.id and wt.word_type_code not in ('vv', 'yv', 'vvar')
                                       where exists (select l.id
                                                     from lexeme as l,
                                                          dataset ds
                                                     where l.is_public = true
                                                     and   ds.code = l.dataset_code
                                                     and   ds.is_public = true
                                                     and   l.word_id = w.id)
                                       group by w.id) as w2
                                 where r.word2_id = w2.id) wr on wr.word1_id = w1.id
                   group by w1.id) wr on wr.word_id = w.id
  left outer join (select wg.word_id,
                          json_agg(row (
                            wg.word_group_id,
                            wg.word_rel_type_code,
                            null,
                            wg.group_member_order_by,
                            wg.group_member_word_id,
                            wg.group_member_word,
                            wg.group_member_word_prese,
                            wg.group_member_homonym_nr,
                            wg.group_member_homonyms_exist,
                            wg.group_member_word_lang,
                            wg.group_member_aspect_code,
                            wg.word_type_codes,
                            wg.lex_complexities
                          )::type_word_relation order by wg.word_group_id, wg.group_member_order_by) word_group_members
                   from (select distinct w1.id word_id,
                                wg.id word_group_id,
                                wg.word_rel_type_code,
                                w2.id group_member_word_id,
                                w2.word group_member_word,
                                w2.word_prese group_member_word_prese,
                                w2.homonym_nr group_member_homonym_nr,
                                (select max(wh.homonym_nr) > 1
                                         from word wh
                                         where wh.lang = w2.lang
                                         and wh.value = w2.word
                                         and exists (select l.id
                                                     from lexeme as l,
                                                          dataset ds
                                                     where l.is_public = true
                                                     and   ds.code = l.dataset_code
                                                     and   ds.is_public = true
                                                     and   l.word_id = wh.id)) as group_member_homonyms_exist,
                                w2.lang group_member_word_lang,
                                w2.aspect_code group_member_aspect_code,
                                w2.word_type_codes,
                                (select array_agg(distinct lc.complexity)
                                 from lexeme lc,
                                      dataset ds
                                 where lc.word_id = w2.id
                                 and lc.is_public = true
                                 and ds.code = lc.dataset_code
                                 and ds.is_public = true
                                 group by lc.word_id) as lex_complexities,
                                wgm2.order_by group_member_order_by
                         from word w1,
                              (select w.id,
                                      w.value as word,
                                      w.value_prese as word_prese,
                                      w.homonym_nr,
                                      w.lang,
                                      w.aspect_code,
                                      array_agg(wt.word_type_code order by wt.order_by) word_type_codes
                               from word as w
                                 left outer join word_word_type as wt on wt.word_id = w.id and wt.word_type_code not in ('vv', 'yv', 'vvar')
                               where exists (select l.id
                                             from lexeme as l,
                                                  dataset ds
                                             where l.word_id = w.id
                                             and l.is_public = true
                                             and ds.code = l.dataset_code
                                             and ds.is_public = true)
                               group by w.id) as w2,
                              word_group wg,
                              word_group_member wgm1,
                              word_group_member wgm2
                         where wgm1.word_group_id = wg.id
                         and   wgm2.word_group_id = wg.id
                         and   wgm1.word_id = w1.id
                         and   wgm2.word_id = w2.id) wg
                   group by wg.word_id) wg on wg.word_id = w.id
where w.is_public = true
      and w.is_word = true
and   (wr.related_words is not null or wg.word_group_members is not null)
and   exists (select l.id
              from lexeme l,
                   dataset ds
              where l.word_id = w.id
              and l.is_public = true
              and ds.code = l.dataset_code
              and ds.is_public = true);


create view view_ww_lexeme_relation 
as
select r.lexeme1_id lexeme_id,
       json_agg(row (
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
              where l.is_public = true
              and   lds.code = l.dataset_code
              and   lds.is_public = true) l2 on l2.lexeme_id = r.lexeme2_id
  inner join (select w.id word_id,
                     w.value as word,
                     w.value_prese as word_prese,
                     w.homonym_nr,
                     w.lang,
                     array_agg(wt.word_type_code order by wt.order_by) word_type_codes
              from word as w
                left outer join word_word_type as wt on wt.word_id = w.id and wt.word_type_code not in ('vv', 'yv', 'vvar')
              group by w.id) as w2 on w2.word_id = l2.word_id
where exists (select l1.id
              from lexeme l1,
                   dataset l1ds
              where l1.id = r.lexeme1_id
              and   l1.is_public = true
              and   l1ds.code = l1.dataset_code
              and   l1ds.is_public = true)
group by r.lexeme1_id;


create view view_ww_meaning_relation
as
select r.m1_id meaning_id,
       json_agg(row (
         r.m2_id,
         r.word_id,
         r.word,
         r.word_prese,
         r.homonym_nr,
         r.word_lang,
         r.aspect_code,
         r.word_type_codes,
         r.complexity,
         r.weight,
         r.inexact_syn_def,
         r.lex_value_state_codes,
         r.lex_register_codes,
         r.lex_government_values,
         r.meaning_rel_type_code
       )::type_meaning_relation 
       order by 
       r.order_by,
       r.lex_order_by
       ) related_meanings
from (select mr.meaning1_id m1_id,
             mr.meaning2_id m2_id,
             w.id word_id,
             w.value word,
             w.value_prese word_prese,
             w.homonym_nr,
             w.lang word_lang,
             w.aspect_code aspect_code,
             l.complexity,
             inexact_syn_def.value inexact_syn_def,
             mr.weight,
             (select array_agg(wt.word_type_code)
              from word_word_type wt
              where wt.word_id = w.id
                and wt.word_type_code not in ('vv', 'yv', 'vvar')
              group by w.id) word_type_codes,
             (select array_agg(distinct l.value_state_code)
              from lexeme l,
                   dataset l_ds
              where l.meaning_id = m.id
                and l.word_id = w.id
                and l.is_public = true
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
                and l.is_public = true
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
                and l.is_public = true
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
           join lexeme l on l.meaning_id = m.id and l.is_public = true
           join word w on w.id = l.word_id and w.is_public = true and w.is_word = true
           join dataset l_ds on l_ds.code = l.dataset_code and l_ds.is_public = true
           left outer join definition inexact_syn_def on inexact_syn_def.meaning_id = m.id and inexact_syn_def.definition_type_code = 'kitsam/laiem tähendus teises keeles'
      where mr.meaning_rel_type_code != 'duplikaadikandidaat'
        and exists(select lex.id
                   from lexeme lex,
                        dataset lex_ds
                   where lex.meaning_id = mr.meaning1_id
                     and lex.is_public = true
                     and lex_ds.code = lex.dataset_code
                     and lex_ds.is_public = true)
      group by m.id, mr.id, w.id, l.id, inexact_syn_def.id) r
group by r.m1_id;

create view view_ww_lexical_decision_data 
as
select w.word,
       w.lang,
       w.is_word
from ((select w.word,
              w.lang,
              true is_word
       from (select distinct
                    w.value word,
                    w.lang
             from word w
             where exists (select l.id
                           from lexeme as l,
                                dataset as ds
                           where l.word_id = w.id
                           and   l.complexity = 'SIMPLE'
                           and   l.dataset_code = 'eki'
                           and   l.is_public = true
                           and   ds.code = l.dataset_code
                           and   ds.is_public = true)
             and   w.value not like '% %'
             and   length(w.value) > 2
             and   w.is_public = true
             and   w.is_word = true) w
       order by random())
       union all
       (select nw.word,
               nw.lang,
               false is_word
       from game_nonword nw
       order by random())) w
order by random();

-- outdated, do not use!
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
             w.value word,
             w.lang
      from word w,
           lexeme l
      where w.value not like '% %'
      and   length(w.value) > 2
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
             w.value word,
             w.lang
      from word w,
           lexeme l
      where w.value not like '% %'
      and   length(w.value) > 2
      and   l.word_id = w.id
      and   l.dataset_code = 'ev2') w
where not exists (select w2.id
                  from word w2,
                       lexeme l2
                  where l2.word_id = w2.id
                  and   l2.dataset_code = 'qq2'
                  and   w.word = w2.value)
order by random())
union all
(select 
       w.meaning_id,
       w.word,
       w.lang,
       'psv' dataset_code
from (select distinct l.meaning_id,
             w.value word,
             w.lang
      from word w,
           lexeme l
      where w.value not like '% %'
      and   length(w.value) > 2
      and   l.word_id = w.id
      and   l.dataset_code = 'psv') w
order by random())) w;

create view view_ww_dataset
  as
    (select
       code,
       type,
       name,
       description,
       contact,
       image_url,
       is_superior,
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
		'GENDER' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		gender c,
		gender_label cl
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
		value_state c,
    value_state_label cl
	where 
		c.code = cl.code
		and cl.type = 'wordweb'
	order by c.order_by, cl.lang, cl.type)
);

create view view_ww_news_article
  as
	(select
		na.id news_article_id,
		na.created,
		na."type",
		na.title,
		na.content,
		na.lang
	from
		news_article na
	order by
		na.created desc
	);
	


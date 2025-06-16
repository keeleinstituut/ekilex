drop view if exists view_ww_dataset_word_menu;
drop view if exists view_ww_new_word_menu;
drop view if exists view_ww_word_search;
drop view if exists view_ww_word;
drop view if exists view_ww_form;
drop view if exists view_ww_meaning;
drop view if exists view_ww_lexeme;
drop view if exists view_ww_colloc_pos_group;
drop view if exists view_ww_word_etymology;
drop view if exists view_ww_word_relation;
drop view if exists view_ww_lexeme_relation;
drop view if exists view_ww_meaning_relation;
drop view if exists view_ww_lexical_decision_data;
drop view if exists view_ww_similarity_judgement_data;
drop view if exists view_ww_classifier;
drop view if exists view_ww_dataset;
drop view if exists view_ww_news_article;
drop type if exists type_meaning_word; -- remove later
drop type if exists type_note; -- remove later
drop type if exists type_value_entity; -- remove later
drop type if exists type_freeform; -- remove later
drop type if exists type_lang_complexity; -- remove later
drop type if exists type_definition; -- remove later
drop type if exists type_media_file; -- remove later
drop type if exists type_usage; -- remove later
drop type if exists type_source_link; -- remove later
drop type if exists type_colloc_member; -- remove later
drop type if exists type_word_etym_relation; -- remove later
drop type if exists type_word_relation; -- remove later
drop type if exists type_lexeme_relation; -- remove later
drop type if exists type_meaning_relation; -- remove later
drop type if exists type_domain;
drop type if exists type_lang_dataset_publishing;

create type type_lang_dataset_publishing as (
	lang varchar(10),
	dataset_code varchar(10),
	is_ww_unif boolean,
	is_ww_lite boolean,
	is_ww_od boolean
);

create type type_domain as (
	origin varchar(100),
	code varchar(100)
);

create view view_ww_dataset_word_menu 
as
select 
	dsw.dataset_code,
	dsw.first_letter,
	array_agg(dsw.value order by dsw.value) word_values
from (
	select 
		left (w.value, 1) first_letter,
		w.value,
		l.dataset_code
	from 
		word w,
		lexeme l,
		dataset ds
	where 
		w.value != ''
		and w.is_public = true
		and l.word_id = w.id
		and l.is_public = true
		and l.is_word = true
		and l.dataset_code = ds.code
		and ds.is_public = true
		and ds.code not in ('ety', 'eki')
	) dsw
group by 
	dsw.dataset_code,
	dsw.first_letter
order by 
	dsw.dataset_code,
	dsw.first_letter;

create view view_ww_new_word_menu
as
select
	w.id word_id,
	w.value word_value,
	w.value_prese word_value_prese,
	w.homonym_nr,
	w.reg_year,
	(
	select
		array_agg(wt.word_type_code order by wt.order_by)
	from
		word_word_type wt
	where 
		wt.word_id = w.id
		and wt.word_type_code not in ('vv', 'yv', 'vvar')
	) word_type_codes
from
	word w
where
	w.reg_year is not null
	and w.is_public = true
	and w.lang = 'est'
	and exists (
		select
			1
		from
			lexeme l,
			lexeme_register lr
		where
			l.word_id = w.id
			and l.is_public = true
			and l.is_word = true
			and l.dataset_code = 'eki'
			and lr.lexeme_id = l.id
			and lr.register_code = 'uus'
	)
order by
	w.reg_year desc,
	w.id desc;

create view view_ww_word_search
as
select
	ws.sgroup,
	ws.word_value,
	ws.crit,
	ws.filt_langs,
	ws.lang_order_by,
	wldp.lang_ds_pubs
from 
	((select
		'word' as sgroup,
		w.value word_value,
		lower(w.value) crit,
		array_agg(
			case
				when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
				else 'other'
			end) filt_langs,
		(array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
	from
		word w,
		language wl
	where 
		w.lang = wl.code
		and w.is_public = true
		and not exists (
			select 
				wwt.id
			from 
				word_word_type wwt
			where 
				wwt.word_id = w.id
				and wwt.word_type_code = 'viga')
		and exists (
			select 
				w.id
			from
				lexeme as l,
				dataset ds
			where
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and ds.code = l.dataset_code
				and ds.is_public = true)
	group by w.value)
	union all
	(select
		'as_word' as sgroup,
		w.value word_value,
		lower(w.value_as_word) crit,
		array_agg(
			case
				when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
				else 'other'
			end) langs_filt,
		(array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
	from
		word w,
		language wl
	where 
		w.lang = wl.code
		and w.value_as_word is not null
		and w.is_public = true
		and not exists (
			select 
				wwt.id
			from 
				word_word_type wwt
			where 
				wwt.word_id = w.id
				and wwt.word_type_code = 'viga')
		and exists (
			select
				w.id
			from 
				lexeme as l,
				dataset ds
			where 
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and ds.code = l.dataset_code
				and ds.is_public = true)
	group by
		w.value,
		w.value_as_word)
	union all
	(select
		'form' as sgroup,
		w.value word_value,
		lower(f.value) crit,
		array_agg(
			case
				when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
				else 'other'
			end) langs_filt,
		(array_agg(wl.order_by order by wl.order_by))[1] lang_order_by
	from 
		form f,
		paradigm_form pf,
		paradigm p,
		word w,
		language wl
	where
		f.id = pf.form_id
		and p.id = pf.paradigm_id
		and p.word_id = w.id
		and w.lang = wl.code
		and w.is_public = true
		and f.morph_code != '??'
		and f.value != w.value
		and f.value != '-'
		and pf.morph_exists = true
		and exists
			(select w.id
			from 
				lexeme as l,
				dataset ds
			where 
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and ds.code = l.dataset_code
				and ds.is_public = true)
	group by 
		w.value,
		f.value)) ws,
	(select 
		w.value word_value,
		array_agg(
			distinct row (
				case
					when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
					else 'other'
				end,
				l.dataset_code,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id)
				or l.dataset_code != 'eki'),
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
				)),
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
				))
			)::type_lang_dataset_publishing
		) lang_ds_pubs
	from 
		word w,
		lexeme l,
		dataset ds
	where
		l.word_id = w.id
		and ds.code = l.dataset_code
		and w.is_public = true
		and l.is_public = true
		and ds.is_public = true
		and l.is_word = true
		and l.dataset_code != 'ety'
		and (
			exists (
				select
					p.id
				from
					publishing p 
				where
					p.entity_name = 'lexeme'
					and p.entity_id = l.id)
			or l.dataset_code != 'eki')
	group by w.value) wldp
where
	ws.word_value = wldp.word_value
order by
	ws.sgroup,
	ws.word_value,
	ws.crit;

create view view_ww_word
as
select
	w.word_id,
	w.value,
	w.value_prese,
	w.value_as_word,
	w.lang,
	case
		when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
		else 'other'
	end lang_filt,
	w.lang_order_by,
	w.homonym_nr,
	w.display_morph_code,
	w.gender_code,
	w.aspect_code,
	w.vocal_form,
	w.morph_comment,
	w.reg_year,
	w.manual_event_on,
	w.last_activity_event_on,
	wt.word_type_codes,
	mw.meaning_words,
	wd.definitions,
	wor.word_od_recommendation,
	wf.freq_value,
	wf.freq_rank,
	w.forms_exist,
	w.min_ds_order_by,
	w.word_type_order_by,
	wldp.lang_ds_pubs
from (
	select
		w.id as word_id,
		w.value,
		w.value_prese,
		w.value_as_word,
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
		w.morph_comment,
		w.reg_year,
		w.manual_event_on,
		(select
			al.event_on
		from
			word_last_activity_log wlal,
			activity_log al
		where 
			wlal.word_id = w.id
			and wlal.activity_log_id = al.id
		) last_activity_event_on,
		(select
			count(f.id) > 0
		from
			paradigm p,
			paradigm_form pf,
			form f
		where
			p.word_id = w.id
			and pf.paradigm_id = p.id
			and pf.form_id = f.id
		) forms_exist,
		(select
			min(ds.order_by)
		from
			lexeme l,
			dataset ds
		where
			l.word_id = w.id
			and l.is_public = true
			and ds.code = l.dataset_code
			and ds.is_public = true
		) min_ds_order_by,
		(select
			count(wt.id)
		from
			word_word_type wt
		where
			wt.word_id = w.id
			and wt.word_type_code = 'pf'
		) word_type_order_by
	from 
		word as w
	where
		w.is_public = true
		and exists (
			select
				l.id
			from 
				lexeme as l,
				dataset ds
			where 
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and ds.code = l.dataset_code
				and ds.is_public = true
				and l.dataset_code != 'ety'
				and (
					exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l.id)
					or l.dataset_code != 'eki')
		)
	order by w.id) as w
	inner join (
		select 
			w.id word_id,
			array_agg(
				distinct row (
					case
						when (w.lang in ('est', 'rus', 'eng', 'ukr', 'fra', 'mul')) then w.lang
						else 'other'
					end,
					l.dataset_code,
					(exists (
						select
							p.id
						from
							publishing p 
						where
							p.target_name = 'ww_unif'
							and p.entity_name = 'lexeme'
							and p.entity_id = l.id)
					or l.dataset_code != 'eki'),
					(exists (
						select
							p.id
						from
							publishing p 
						where
							p.target_name = 'ww_lite'
							and p.entity_name = 'lexeme'
							and p.entity_id = l.id
					)),
					(exists (
						select
							p.id
						from
							publishing p 
						where
							p.target_name = 'ww_od'
							and p.entity_name = 'lexeme'
							and p.entity_id = l.id
					))
				)::type_lang_dataset_publishing
			) lang_ds_pubs
		from 
			word w,
			lexeme l,
			dataset ds
		where
			l.word_id = w.id
			and ds.code = l.dataset_code
			and w.is_public = true
			and l.is_public = true
			and ds.is_public = true
			and l.is_word = true
			and l.dataset_code != 'ety'
			and (
				exists (
					select
						p.id
					from
						publishing p 
					where
						p.entity_name = 'lexeme'
						and p.entity_id = l.id)
				or l.dataset_code != 'eki')
		group by w.id 
	) wldp on wldp.word_id = w.word_id
	left outer join (
		select
			wt.word_id,
			array_agg(wt.word_type_code order by wt.order_by) word_type_codes
		from word_word_type wt
		where 
			wt.word_type_code not in ('vv', 'yv', 'vvar')
		group by wt.word_id
	) wt on wt.word_id = w.word_id
	left outer join (
		select
			mw.word_id,
			json_agg(
				json_build_object(
					'lexemeId', mw.lexeme_id,
					'meaningId', mw.meaning_id,
					'mwLexemeId', mw.mw_lexeme_id,
					'mwLexemeWeight', mw.mw_lexeme_weight,
					'wordId', mw.mw_word_id,
					'value', mw.mw_value,
					'valuePrese', mw.mw_value_prese,
					'homonymNr', mw.mw_homonym_nr,
					'lang', mw.mw_lang,
					'aspectCode', mw.mw_aspect_code,
					'wordTypeCodes', mw.mw_word_type_codes,
					'wwUnif', mw.is_ww_unif,
					'wwLite', mw.is_ww_lite,
					'wwOd', mw.is_ww_od
				)
				order by
					mw.hw_lexeme_level1,
					mw.hw_lexeme_level2,
					mw.hw_lexeme_order_by,
					mw.mw_lexeme_order_by
			) meaning_words
		from (
			select distinct
				l1.word_id,
				l1.id lexeme_id,
				l1.meaning_id,
				l1.level1 hw_lexeme_level1,
				l1.level2 hw_lexeme_level2,
				l1.order_by hw_lexeme_order_by,
				l2.id mw_lexeme_id,
				l2.weight mw_lexeme_weight,
				l2.order_by mw_lexeme_order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id)
				or l2.dataset_code != 'eki'
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)) is_ww_od,
				w2.id mw_word_id,
				w2.value mw_value,
				w2.value_prese mw_value_prese,
				w2.homonym_nr mw_homonym_nr,
				w2.lang mw_lang,
				(select 
					array_agg(wt.word_type_code order by wt.order_by)
				from 
					word_word_type wt
				where 
					wt.word_id = w2.id
					and wt.word_type_code not in ('vv', 'yv', 'vvar')
				group by wt.word_id
				) mw_word_type_codes,
				w2.aspect_code mw_aspect_code
			from 
				lexeme l1
				inner join dataset ds1 on ds1.code = l1.dataset_code
				inner join lexeme l2 on 
					l2.meaning_id = l1.meaning_id
					and l2.id != l1.id
					and l2.word_id != l1.word_id
					and coalesce (l2.value_state_code, 'anything') != 'vigane'
				inner join dataset ds2 on ds2.code = l2.dataset_code
				inner join word w2 on w2.id = l2.word_id
			where 
				l1.is_public = true
				and l1.is_word = true
				and l2.is_public = true
				and l2.is_word = true
				and ds1.is_public = true
				and ds2.is_public = true
				and w2.is_public = true
				and l1.dataset_code != 'ety'
				and l2.dataset_code != 'ety'
				and (
					exists (
						select
							p.id
						from
							publishing p 
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l1.id)
					or l1.dataset_code != 'eki')
				and (
					exists (
						select
							p.id
						from
							publishing p 
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l2.id)
					or l2.dataset_code != 'eki')
			) mw
		group by mw.word_id
	) mw on mw.word_id = w.word_id
	left outer join (
		select
			wd.word_id,
			json_agg(
				json_build_object(
					'id', wd.definition_id,
					'lexemeId', wd.lexeme_id,
					'meaningId', wd.meaning_id,
					'value', wd.value,
					'valuePrese', wd.value_prese,
					'lang', wd.lang,
					'wwUnif', wd.is_ww_unif,
					'wwLite', wd.is_ww_lite,
					'wwOd', wd.is_ww_od
				)
				order by
					wd.ds_order_by,
					wd.level1,
					wd.level2,
					wd.lex_order_by,
					wd.def_order_by
			) definitions
		from (
			select
				l.word_id,
				l.id lexeme_id,
				l.meaning_id,
				l.level1,
				l.level2,
				l.order_by lex_order_by,
				d.id definition_id,
				substring(d.value, 1, 200) "value",
				substring(d.value_prese, 1, 200) value_prese,
				d.lang,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'definition'
						and p.entity_id = d.id)
				or exists (
					select
						1
					from
						definition_dataset dd
					where
						dd.definition_id = d.id
						and dd.dataset_code != 'eki')
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'definition'
						and p.entity_id = d.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'definition'
						and p.entity_id = d.id
				)) is_ww_od,
				d.order_by def_order_by,
				ds.order_by ds_order_by
			from
				lexeme l
				inner join definition d on d.meaning_id = l.meaning_id
				inner join dataset ds on ds.code = l.dataset_code
			where
				d.is_public = true
				and l.is_public = true
				and ds.is_public = true
				and l.dataset_code != 'ety'
				and (
					exists (
						select
							p.id
						from
							publishing p 
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l.id)
					or l.dataset_code != 'eki')
				and (
					exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'definition'
							and p.entity_id = d.id)
					or exists (
						select
							1
						from
							definition_dataset dd
						where
							dd.definition_id = d.id
							and dd.dataset_code != 'eki')
					)
			) wd
		group by wd.word_id
	) wd on wd.word_id = w.word_id
	left outer join (
		select
			wor.word_id,
			json_build_object(
				'id', wor.id,
				'wordId', wor.word_id,
				'value', wor.value,
				'valuePrese', wor.value_prese,
				'optValue', wor.opt_value,
				'optValuePrese', wor.opt_value_prese,
				'createdBy', wor.created_by,
				'createdOn', wor.created_on,
				'modifiedBy', wor.modified_by,
				'modifiedOn', wor.modified_on
			) word_od_recommendation
		from 
			word_od_recommendation wor
	) wor on wor.word_id = w.word_id
	left outer join (
		select
			wf.word_id,
			wf.value freq_value,
			wf.rank freq_rank,
			fc.corp_date
		from 
			word_freq wf,
			freq_corp fc
		where 
			wf.freq_corp_id = fc.id
			and fc.is_public = true
	) wf on wf.word_id = w.word_id 
			and wf.corp_date = (
					select 
						max(fcc.corp_date)
					from 
						word_freq wff,
						freq_corp fcc
					where 
						wff.freq_corp_id = fcc.id
						and fcc.is_public = true
						and wff.word_id = w.word_id)
order by
	w.word_id;

create view view_ww_form 
as
select 
	w.id word_id,
	w.value word_value,
	w.lang,
	w.vocal_form,
	w.morph_comment,
	p.id paradigm_id,
	p.comment paradigm_comment,
	p.inflection_type,
	p.inflection_type_nr,
	p.word_class,
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
from 
	word w
	inner join paradigm p on p.word_id = w.id
	inner join paradigm_form pf on pf.paradigm_id = p.id
	inner join form f on f.id = pf.form_id
	left outer join (
		select 
			ff.form_id,
			ff.value form_freq_value,
			ff.rank form_freq_rank,
			fc.corp_date,
			(select 
				max(fff.rank) 
			from 
				form_freq fff 
			where 
				fff.freq_corp_id = fc.id
			) form_freq_rank_max
		from 
			form_freq ff,
			freq_corp fc
		where 
			ff.freq_corp_id = fc.id
			and fc.is_public = true
	) ff on 
		ff.form_id = f.id 
		and ff.corp_date = (
			select 
				max(fcc.corp_date)
			from 
				form_freq fff,
				freq_corp fcc
			where 
				fff.freq_corp_id = fcc.id
				and fcc.is_public = true
				and fff.form_id = f.id)
	left outer join (
		select 
			mf.morph_code,
			mf.value morph_freq_value,
			mf.rank morph_freq_rank,
			fc.corp_date,
			(select 
				max(mff.rank) 
			from 
				morph_freq mff 
			where 
				mff.freq_corp_id = fc.id
			) morph_freq_rank_max
		from 
			morph_freq mf,
			freq_corp fc
		where 
			mf.freq_corp_id = fc.id
			and fc.is_public = true
	) mf on 
		mf.morph_code = f.morph_code 
		and mf.corp_date = (
			select 
				max(fcc.corp_date)
			from 
				morph_freq mff,
				freq_corp fcc
			where 
				mff.freq_corp_id = fcc.id
				and fcc.is_public = true
				and mff.morph_code = f.morph_code)
where 
	w.is_public = true
	and exists (
		select 
			l.id
		from 
			lexeme l,
			dataset ds
		where 
			l.word_id = w.id
			and l.is_public = true
			and l.is_word = true
			and ds.code = l.dataset_code
			and ds.is_public = true)
order by
	w.id,
	p.id,
	f.id;

create view view_ww_meaning
as
select
	m.meaning_id,
	m.manual_event_on,
	m.last_approve_or_edit_event_on,
	m_dom.domain_codes,
	m_def.definitions,
	m_img.meaning_images,
	m_med.meaning_medias,
	m_smt.semantic_types,
	m_lcm.learner_comments,
	m_not.meaning_notes
from (
	select
		m.id meaning_id,
		m.manual_event_on,
		(select 
			al.event_on
		from 
			meaning_last_activity_log mlal,
			activity_log al
		where 
			mlal.meaning_id = m.id
			and mlal.activity_log_id = al.id
		order by 
			mlal.type
		limit 1) last_approve_or_edit_event_on
	from 
		meaning m
	where 
		exists (
			select
				l.id
			from 
				lexeme as l,
				dataset ds
			where 
				l.meaning_id = m.id
				and l.is_public = true
				and ds.code = l.dataset_code
				and ds.is_public = true
				and (
					exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l.id)
					or l.dataset_code != 'eki')
		)
	) m
	left outer join (
		select 
			m_dom.meaning_id,
			json_agg(
				row (
					m_dom.domain_origin,
					m_dom.domain_code
				)::type_domain 
				order by 
					m_dom.order_by
			) domain_codes
		from 
			meaning_domain m_dom
		group by 
			m_dom.meaning_id
	) m_dom on m_dom.meaning_id = m.meaning_id
	left outer join (
		select
			md.meaning_id,
			json_agg(
				json_build_object(
					'id', md.definition_id,
					'meaningId', md.meaning_id,
					'value', md.value,
					'valuePrese', md.value_prese,
					'lang', md.lang,
					'wwUnif', md.is_ww_unif,
					'wwLite', md.is_ww_lite,
					'wwOd', md.is_ww_od,
					'notes', (
						select
							json_agg(
								json_build_object(
									'id', dn.id,
									'value', dn.value,
									'valuePrese', dn.value_prese,
									'lang', dn.lang,
									'createdBy', dn.created_by,
									'createdOn', dn.created_on,
									'modifiedBy', dn.modified_by,
									'modifiedOn', dn.modified_on,
									'orderBy', dn.order_by,
									'sourceLinks', (
										select
											json_agg(
												json_build_object(
													'sourceLinkId', dnsl.id,
													'sourceLinkName', dnsl.name,
													'orderBy', dnsl.order_by,
													'sourceId', s.id,
													'sourceName', s.name,
													'sourceValue', s.value,
													'sourceValuePrese', s.value_prese,
													'sourcePublic', s.is_public
												)
												order by dnsl.order_by
											)
										from
											definition_note_source_link dnsl,
											"source" s
										where
											dnsl.definition_note_id = dn.id
											and dnsl.source_id = s.id
									)
								)
								order by dn.order_by
							)
						from
							definition_note dn 
						where
							dn.definition_id = md.definition_id
							and dn.is_public = true
					),
					'sourceLinks', (
						select
							json_agg(
								json_build_object(
									'sourceLinkId', dsl.id,
									'sourceLinkName', dsl.name,
									'orderBy', dsl.order_by,
									'sourceId', s.id,
									'sourceName', s.name,
									'sourceValue', s.value,
									'sourceValuePrese', s.value_prese,
									'sourcePublic', s.is_public
								)
								order by dsl.order_by
							)
						from
							definition_source_link dsl,
							"source" s
						where
							dsl.definition_id = md.definition_id
							and dsl.source_id = s.id
					)
				)
				order by
					md.order_by
			) definitions
		from (
			select
				d.id definition_id,
				d.meaning_id,
				substring(d.value, 1, 200) "value",
				substring(d.value_prese, 1, 200) value_prese,
				d.lang,
				d.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'definition'
						and p.entity_id = d.id)
				or exists (
					select
						1
					from
						definition_dataset dd
					where
						dd.definition_id = d.id
						and dd.dataset_code != 'eki')
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'definition'
						and p.entity_id = d.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'definition'
						and p.entity_id = d.id
				)) is_ww_od
			from
				definition d
			where
				d.is_public = true
				and (
					exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'definition'
							and p.entity_id = d.id)
					or exists (
						select
							1
						from
							definition_dataset dd
						where
							dd.definition_id = d.id
							and dd.dataset_code != 'eki')
					)
		) md
		group by md.meaning_id
	) m_def on m_def.meaning_id = m.meaning_id
	left outer join (
		select
			mi.meaning_id,
			json_agg(
				json_build_object(
					'id', mi.meaning_image_id,
					'meaningId', mi.meaning_id,
					'url', mi.url,
					'title', mi.title,
					'wwUnif', mi.is_ww_unif,
					'wwLite', mi.is_ww_lite,
					'wwOd', mi.is_ww_od,
					'sourceLinks', (
						select
							json_agg(
								json_build_object(
									'sourceLinkId', misl.id,
									'sourceLinkName', misl.name,
									'orderBy', misl.order_by,
									'sourceId', s.id,
									'sourceName', s.name,
									'sourceValue', s.value,
									'sourceValuePrese', s.value_prese,
									'sourcePublic', s.is_public
								)
								order by misl.order_by
							)
						from
							meaning_image_source_link misl,
							"source" s
						where
							misl.meaning_image_id = mi.meaning_image_id
							and misl.source_id = s.id
					)
				)
				order by
					mi.order_by
			) meaning_images
		from (
			select
				mi.id meaning_image_id,
				mi.meaning_id,
				mi.url,
				mi.title,
				mi.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'meaning_image'
						and p.entity_id = mi.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mi.meaning_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'meaning_image'
						and p.entity_id = mi.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'meaning_image'
						and p.entity_id = mi.id
				)) is_ww_od
			from
				meaning_image mi
			where
				(exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'meaning_image'
						and p.entity_id = mi.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mi.meaning_id
						and l.dataset_code not in ('eki', 'ety'))
				)
		) mi
		group by mi.meaning_id
	) m_img on m_img.meaning_id = m.meaning_id
	left outer join (
		select
			mm.meaning_id,
			json_agg(
				json_build_object(
					'id', mm.meaning_media_id,
					'meaningId', mm.meaning_id,
					'url', mm.url,
					'wwUnif', mm.is_ww_unif,
					'wwLite', mm.is_ww_lite,
					'wwOd', mm.is_ww_od
				)
				order by
					mm.order_by
			) meaning_medias
		from (
			select
				mm.id meaning_media_id,
				mm.meaning_id,
				mm.url,
				mm.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'meaning_media'
						and p.entity_id = mm.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mm.meaning_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'meaning_media'
						and p.entity_id = mm.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'meaning_media'
						and p.entity_id = mm.id
				)) is_ww_od
			from
				meaning_media mm
			where
				(exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'meaning_media'
						and p.entity_id = mm.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mm.meaning_id
						and l.dataset_code not in ('eki', 'ety'))
				)
		) mm
		group by mm.meaning_id
	) m_med on m_med.meaning_id = m.meaning_id
	left outer join (
		select 
			mst.meaning_id,
			array_agg(mst.semantic_type_code order by mst.order_by) semantic_types
		from 
			meaning_semantic_type mst
		group by mst.meaning_id
	) m_smt on m_smt.meaning_id = m.meaning_id
	left outer join (
		select 
			lc.meaning_id,
			array_agg(lc.value_prese order by lc.order_by) learner_comments
		from 
			learner_comment lc
		group by lc.meaning_id
	) m_lcm on m_lcm.meaning_id = m.meaning_id
	left outer join (
		select
			mn.meaning_id,
			json_agg(
				json_build_object(
					'id', mn.meaning_note_id,
					'value', mn.value,
					'valuePrese', mn.value_prese,
					'lang', mn.lang,
					'createdBy', mn.created_by,
					'createdOn', mn.created_on,
					'modifiedBy', mn.modified_by,
					'modifiedOn', mn.modified_on,
					'orderBy', mn.order_by,
					'wwUnif', mn.is_ww_unif,
					'wwLite', mn.is_ww_lite,
					'wwOd', mn.is_ww_od,
					'sourceLinks', (
						select
							json_agg(
								json_build_object(
									'sourceLinkId', mnsl.id,
									'sourceLinkName', mnsl.name,
									'orderBy', mnsl.order_by,
									'sourceId', s.id,
									'sourceName', s.name,
									'sourceValue', s.value,
									'sourceValuePrese', s.value_prese,
									'sourcePublic', s.is_public
								)
								order by mnsl.order_by
							)
						from
							meaning_note_source_link mnsl,
							"source" s
						where
							mnsl.meaning_note_id = mn.meaning_note_id
							and mnsl.source_id = s.id
					)
				)
				order by mn.order_by
			) meaning_notes
		from (
			select
				mn.id meaning_note_id,
				mn.meaning_id,
				mn.value,
				mn.value_prese,
				mn.lang,
				mn.created_by,
				mn.created_on,
				mn.modified_by,
				mn.modified_on,
				mn.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'meaning_note'
						and p.entity_id = mn.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mn.meaning_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'meaning_note'
						and p.entity_id = mn.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'meaning_note'
						and p.entity_id = mn.id
				)) is_ww_od
			from
				meaning_note mn 
			where
				mn.is_public = true
				and (exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'meaning_note'
						and p.entity_id = mn.id)
					or exists (
						select
							1
						from
							lexeme l
						where
							l.meaning_id = mn.meaning_id
							and l.dataset_code not in ('eki', 'ety'))
					)
		) mn
		group by
			mn.meaning_id
	) m_not on m_not.meaning_id = m.meaning_id
order by m.meaning_id;

create view view_ww_lexeme
as
select 
	l.id lexeme_id,
	l.word_id,
	l.meaning_id,
	l.dataset_code,
	l.dataset_name,
	l.dataset_type,
	l.value_state_code,
	l.proficiency_level_code,
	l.reliability,
	l.level1,
	l.level2,
	l.weight,
	l.order_by lexeme_order_by,
	l.dataset_order_by,
	l_reg.register_codes,
	l_pos.pos_codes,
	l_rgn.region_codes,
	l_der.deriv_codes,
	l_not.lexeme_notes,
	l_gra.grammars,
	l_gov.governments,
	l_usa.usages,
	l_src.source_links,
	l_mea.meaning_words,
	l.is_ww_unif,
	l.is_ww_lite,
	l.is_ww_od
from 
	(
	select
		l.id,
		l.word_id,
		l.meaning_id,
		l.dataset_code,
		l.value_state_code,
		l.proficiency_level_code,
		l.reliability,
		l.level1,
		l.level2,
		l.weight,
		l.order_by,
		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_unif'
				and p.entity_name = 'lexeme'
				and p.entity_id = l.id)
		or l.dataset_code != 'eki'
		) is_ww_unif,
		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_lite'
				and p.entity_name = 'lexeme'
				and p.entity_id = l.id
		)) is_ww_lite,
		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_od'
				and p.entity_name = 'lexeme'
				and p.entity_id = l.id
		)) is_ww_od,
		ds.type dataset_type,
		ds.name dataset_name,
		ds.order_by dataset_order_by
	from
		lexeme l,
		dataset ds
	where
		l.dataset_code = ds.code
		and l.is_word = true
		and l.is_public = true
		and ds.is_public = true
		and l.dataset_code != 'ety'
		and (exists (
				select
					p.id
				from
					publishing p 
				where
					p.entity_name = 'lexeme'
					and p.entity_id = l.id)
			or l.dataset_code != 'eki')
	) l
 	left outer join (
 		select
 			lr.lexeme_id,
 			array_agg(lr.register_code order by lr.order_by) register_codes 
 		from 
 			lexeme_register lr 
 		group by lr.lexeme_id
 	) l_reg on l_reg.lexeme_id = l.id
	left outer join (
		select 
			lp.lexeme_id,
			array_agg(lp.pos_code order by lp.order_by) pos_codes 
		from 
			lexeme_pos lp 
		group by lp.lexeme_id
	) l_pos on l_pos.lexeme_id = l.id
	left outer join (
		select 
			lr.lexeme_id, 
			array_agg(lr.region_code order by lr.order_by) region_codes 
		from 
			lexeme_region lr 
		group by lr.lexeme_id
	) l_rgn on l_rgn.lexeme_id = l.id
	left outer join (
		select 
			ld.lexeme_id, 
			array_agg(ld.deriv_code) deriv_codes 
		from 
			lexeme_deriv ld 
		group by ld.lexeme_id
	) l_der on l_der.lexeme_id = l.id
	left outer join (
		select
			ln.lexeme_id,
			json_agg(
				json_build_object(
					'id', ln.lexeme_note_id,
					'value', ln.value,
					'valuePrese', ln.value_prese,
					'lang', ln.lang,
					'createdBy', ln.created_by,
					'createdOn', ln.created_on,
					'modifiedBy', ln.modified_by,
					'modifiedOn', ln.modified_on,
					'orderBy', ln.order_by,
					'wwUnif', ln.is_ww_unif,
					'wwLite', ln.is_ww_lite,
					'wwOd', ln.is_ww_od,
					'sourceLinks', (
						select
							json_agg(
								json_build_object(
									'sourceLinkId', lnsl.id,
									'sourceLinkName', lnsl.name,
									'orderBy', lnsl.order_by,
									'sourceId', s.id,
									'sourceName', s.name,
									'sourceValue', s.value,
									'sourceValuePrese', s.value_prese,
									'sourcePublic', s.is_public
								)
								order by lnsl.order_by
							)
						from
							lexeme_note_source_link lnsl,
							"source" s
						where
							lnsl.lexeme_note_id = ln.lexeme_note_id
							and lnsl.source_id = s.id
					)
				)
				order by ln.order_by
			) lexeme_notes
		from (
			select
				ln.id lexeme_note_id,
				ln.lexeme_id,
				ln.value,
				ln.value_prese,
				ln.lang,
				ln.created_by,
				ln.created_on,
				ln.modified_by,
				ln.modified_on,
				ln.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme_note'
						and p.entity_id = ln.id)
				or exists (
					select 
						1
					from
						lexeme l
					where
						l.id = ln.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme_note'
						and p.entity_id = ln.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'lexeme_note'
						and p.entity_id = ln.id
				)) is_ww_od
			from
				lexeme_note ln 
			where
				ln.is_public = true
				and (exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'lexeme_note'
						and p.entity_id = ln.id)
					or exists (
						select 
							1
						from
							lexeme l
						where
							l.id = ln.lexeme_id
							and l.dataset_code not in ('eki', 'ety'))
					)
		) ln
		group by
			ln.lexeme_id
	) l_not on l_not.lexeme_id = l.id
	left outer join (
		select 
			lg.lexeme_id,
			json_agg(
				json_build_object(
					'id', lg.id,
					'value', lg.value,
					'valuePrese', lg.value_prese,
					'lang', lg.lang,
					'createdBy', lg.created_by,
					'createdOn', lg.created_on,
					'modifiedBy', lg.modified_by,
					'modifiedOn', lg.modified_on,
					'orderBy', lg.order_by,
					'wwUnif', lg.is_ww_unif,
					'wwLite', lg.is_ww_lite,
					'wwOd', lg.is_ww_od
				)
				order by lg.order_by
			) grammars
		from  (
			select
				g.id,
				g.lexeme_id,
				g.value,
				g.value_prese,
				g.lang,
				g.created_by,
				g.created_on,
				g.modified_by,
				g.modified_on,
				g.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'grammar'
						and p.entity_id = g.id)
				or exists (
					select 
						1
					from
						lexeme l
					where
						l.id = g.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'grammar'
						and p.entity_id = g.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'grammar'
						and p.entity_id = g.id
				)) is_ww_od
			from
				grammar g
			where
				(exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'grammar'
						and p.entity_id = g.id)
				or exists (
					select 
						1
					from
						lexeme l
					where
						l.id = g.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				)
		) lg
		group by lg.lexeme_id
	) l_gra on l_gra.lexeme_id = l.id
	left outer join (
		select 
			lg.lexeme_id,
			json_agg(
				json_build_object(
					'id', lg.id,
					'value', lg.value,
					'createdBy', lg.created_by,
					'createdOn', lg.created_on,
					'modifiedBy', lg.modified_by,
					'modifiedOn', lg.modified_on,
					'orderBy', lg.order_by,
					'wwUnif', lg.is_ww_unif,
					'wwLite', lg.is_ww_lite,
					'wwOd', lg.is_ww_od
				)
			order by lg.order_by) governments
		from  (
			select
				g.id,
				g.lexeme_id,
				g.value,
				g.created_by,
				g.created_on,
				g.modified_by,
				g.modified_on,
				g.order_by,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'government'
						and p.entity_id = g.id)
				or exists (
					select 
						1
					from
						lexeme l
					where
						l.id = g.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'government'
						and p.entity_id = g.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'government'
						and p.entity_id = g.id
				)) is_ww_od
			from
				government g
			where
				(exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'government'
						and p.entity_id = g.id)
				or exists (
					select 
						1
					from
						lexeme l
					where
						l.id = g.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				)
		) lg
		group by lg.lexeme_id
	) l_gov on l_gov.lexeme_id = l.id
	left outer join (
		select
			mw.lexeme_id,
			json_agg(
				json_build_object(
					'lexemeId', mw.lexeme_id,
					'meaningId', mw.meaning_id,
					'mwLexemeId', mw.mw_lexeme_id,
					'mwLexemeWeight', mw.mw_lexeme_weight,
					'mwLexemeGovernments', mw.mw_lexeme_governments,
					'mwLexemeRegisterCodes', mw.mw_lexeme_register_codes,
					'mwLexemeValueStateCode', mw.mw_lexeme_value_state_code,
					'wordId', mw.mw_word_id,
					'value', mw.mw_value,
					'valuePrese', mw.mw_value_prese,
					'homonymNr', mw.mw_homonym_nr,
					'lang', mw.mw_lang,
					'aspectCode', mw.mw_aspect_code,
					'wordTypeCodes', mw.mw_word_type_codes,
					'wwUnif', mw.is_ww_unif,
					'wwLite', mw.is_ww_lite,
					'wwOd', mw.is_ww_od
				)
				order by
					mw.hw_lexeme_reliability,
					mw.hw_lexeme_level1,
					mw.hw_lexeme_level2,
					mw.hw_lexeme_order_by,
					mw.mw_lexeme_order_by
			) meaning_words
		from (
			select
				l1.word_id,
				l1.id lexeme_id,
				l1.meaning_id,
				l1.reliability hw_lexeme_reliability,
				l1.level1 hw_lexeme_level1,
				l1.level2 hw_lexeme_level2,
				l1.order_by hw_lexeme_order_by,
				l2.id mw_lexeme_id,
				l2.value_state_code mw_lexeme_value_state_code,
				l2.weight mw_lexeme_weight,
				l2.order_by mw_lexeme_order_by,
				(select
					json_agg(
						json_build_object(
							'id', g.id,
							'value', g.value,
							'createdBy', g.created_by,
							'createdOn', g.created_on,
							'modifiedBy', g.modified_by,
							'modifiedOn', g.modified_on,
							'orderBy', g.order_by,
							'wwUnif', g.is_ww_unif,
							'wwLite', g.is_ww_lite,
							'wwOd', g.is_ww_od
						)
						order by g.order_by
					)
				from (
					select
						g.id,
						g.lexeme_id,
						g.value,
						g.created_by,
						g.created_on,
						g.modified_by,
						g.modified_on,
						g.order_by,
						(exists (
							select
								p.id
							from
								publishing p 
							where
								p.target_name = 'ww_unif'
								and p.entity_name = 'government'
								and p.entity_id = g.id)
						or exists (
							select 
								1
							from
								lexeme l
							where
								l.id = g.lexeme_id
								and l.dataset_code not in ('eki', 'ety'))
						) is_ww_unif,
						(exists (
							select
								p.id
							from
								publishing p 
							where
								p.target_name = 'ww_lite'
								and p.entity_name = 'government'
								and p.entity_id = g.id
						)) is_ww_lite,
						(exists (
							select
								p.id
							from
								publishing p 
							where
								p.target_name = 'ww_od'
								and p.entity_name = 'government'
								and p.entity_id = g.id
						)) is_ww_od
					from
						government g
					where
						g.lexeme_id = l2.id
						and (exists (
							select
								1
							from
								publishing p
							where
								p.entity_name = 'government'
								and p.entity_id = g.id)
							or exists (
								select 
									1
								from
									lexeme l
								where
									l.id = g.lexeme_id
									and l.dataset_code not in ('eki', 'ety'))
							)
					) g
				) mw_lexeme_governments,
				(select
					array_agg(lr.register_code order by lr.order_by) 
				from 
					lexeme_register lr 
				where 
					lr.lexeme_id = l2.id 
				group by 
					lr.lexeme_id
				) mw_lexeme_register_codes,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id)
				or l2.dataset_code != 'eki'
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)) is_ww_od,
				w2.id mw_word_id,
				w2.value mw_value,
				w2.value_prese mw_value_prese,
				w2.homonym_nr mw_homonym_nr,
				w2.lang mw_lang,
				w2.aspect_code mw_aspect_code,
				(select 
					array_agg(wt.word_type_code order by wt.order_by)
				from 
					word_word_type wt
				where 
					wt.word_id = w2.id
					and wt.word_type_code not in ('vv', 'yv', 'vvar')
				group by wt.word_id
				) mw_word_type_codes
			from 
				lexeme l1
				inner join dataset ds1 on ds1.code = l1.dataset_code
				inner join lexeme l2 on 
					l2.meaning_id = l1.meaning_id
					and l2.id != l1.id
					and l2.word_id != l1.word_id
					and coalesce (l2.value_state_code, 'anything') != 'vigane'
				inner join dataset ds2 on ds2.code = l2.dataset_code
				inner join word w2 on w2.id = l2.word_id
			where 
				l1.is_public = true
				and l1.is_word = true
				and l2.is_public = true
				and l2.is_word = true
				and ds1.is_public = true
				and ds2.is_public = true
				and w2.is_public = true
				and l1.dataset_code != 'ety'
				and l2.dataset_code != 'ety'
				and (exists (
					select
						p.id
					from
						publishing p 
					where
						p.entity_name = 'lexeme'
						and p.entity_id = l1.id)
					or l1.dataset_code != 'eki')
				and (exists (
					select
						p.id
					from
						publishing p 
					where
						p.entity_name = 'lexeme'
						and p.entity_id = l2.id)
					or l2.dataset_code != 'eki')
			) mw
		group by mw.lexeme_id
	) l_mea on l_mea.lexeme_id = l.id
	left outer join (
		select
			lu.lexeme_id,
			json_agg(
				json_build_object(
					'id', lu.usage_id,
					'value', lu.value,
					'valuePrese', lu.value_prese,
					'lang', lu.lang,
					'createdBy', lu.created_by,
					'createdOn', lu.created_on,
					'modifiedBy', lu.modified_by,
					'modifiedOn', lu.modified_on,
					'orderBy', lu.order_by,
					'wwUnif', lu.is_ww_unif,
					'wwLite', lu.is_ww_lite,
					'wwOd', lu.is_ww_od,
					'usageTranslationValues', lu.usage_translation_values,
					'usageDefinitionValues', lu.usage_definition_values,
					'sourceLinks', lu.source_links
				)
				order by lu.order_by
			) usages
		from (
			select
				u.id usage_id,
				u.lexeme_id,
				u.value,
				u.value_prese,
				u.lang,
				u.created_by,
				u.created_on,
				u.modified_by,
				u.modified_on,
				u.order_by,
				(select
					array_agg(ut.value_prese order by ut.order_by)
				from
					usage_translation ut
				where
					ut.usage_id = u.id
					and ut.lang = 'rus'
				) usage_translation_values,
				(select
					array_agg(ud.value_prese order by ud.order_by)
				from
					usage_definition ud
				where
					ud.usage_id = u.id
				) usage_definition_values,
				(select
					json_agg(
						json_build_object(
							'sourceLinkId', usl.id,
							'sourceLinkName', usl.name,
							'orderBy', usl.order_by,
							'sourceId', s.id,
							'sourceName', s.name,
							'sourceValue', s.value,
							'sourceValuePrese', s.value_prese,
							'sourcePublic', s.is_public
						)
						order by usl.order_by
					)
				from
					usage_source_link usl,
					"source" s
				where
					usl.usage_id = u.id
					and usl.source_id = s.id
				) source_links,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'usage'
						and p.entity_id = u.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.id = u.lexeme_id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'usage'
						and p.entity_id = u.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'usage'
						and p.entity_id = u.id
				)) is_ww_od
			from
				"usage" u
			where
				u.is_public = true
				and (exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'usage'
							and p.entity_id = u.id)
					or exists (
						select
							1
						from
							lexeme l
						where
							l.id = u.lexeme_id
							and l.dataset_code not in ('eki', 'ety'))
					)
		) lu
		group by
			lu.lexeme_id
	) l_usa on l_usa.lexeme_id = l.id
	left outer join (
		select
			lsl.lexeme_id,
			json_agg(
				json_build_object(
					'sourceLinkId', lsl.id,
					'sourceLinkName', lsl.name,
					'orderBy', lsl.order_by,
					'sourceId', s.id,
					'sourceName', s.name,
					'sourceValue', s.value,
					'sourceValuePrese', s.value_prese,
					'sourcePublic', s.is_public
				)
				order by 
					lsl.lexeme_id,
					lsl.order_by
			) source_links
		from
			lexeme_source_link lsl,
			"source" s
		where
			lsl.source_id = s.id
		group by
			lsl.lexeme_id
	) l_src on l_src.lexeme_id = l.id
order by l.id;

create view view_ww_colloc_pos_group
as
select
	l.id lexeme_id,
	w.id word_id,
	(select
		json_agg(
			json_build_object(
				'posGroupCode', pg.code,
				'relGroups', (
					select
						json_agg(
							json_build_object(
								'relGroupCode', rg.code,
								'collocations', (
									select
										json_agg(
											json_build_object(
												'lexemeId', cl.id,
												'wordId', cw.id,
												'wordValue', cw.value,
												'usages', (
													select
														json_agg(
															json_build_object(
																'id', u.id,
																'value', u.value,
																'valuePrese', u.value_prese,
																'lang', u.lang,
																'orderBy', u.order_by,
																'wwUnif', u.is_ww_unif,
																'wwLite', u.is_ww_lite,
																'wwOd', u.is_ww_od
															)
															order by u.order_by
														)
													from (
														select
															u.id,
															u.value,
															u.value_prese,
															u.lang,
															u.order_by,
															(exists (
																select
																	p.id
																from
																	publishing p 
																where
																	p.target_name = 'ww_unif'
																	and p.entity_name = 'usage'
																	and p.entity_id = u.id)
															or exists (
																select
																	1
																from
																	lexeme l
																where
																	l.id = u.lexeme_id
																	and l.dataset_code not in ('eki', 'ety'))
															) is_ww_unif,
															(exists (
																select
																	p.id
																from
																	publishing p 
																where
																	p.target_name = 'ww_lite'
																	and p.entity_name = 'usage'
																	and p.entity_id = u.id
															)) is_ww_lite,
															(exists (
																select
																	p.id
																from
																	publishing p 
																where
																	p.target_name = 'ww_od'
																	and p.entity_name = 'usage'
																	and p.entity_id = u.id
															)) is_ww_od
														from
															"usage" u 
														where
															u.lexeme_id = cl.id
															and u.is_public = true
															and (exists (
																	select
																		1
																	from
																		publishing p
																	where
																		p.entity_name = 'usage'
																		and p.entity_id = u.id)
																or exists (
																	select
																		1
																	from
																		lexeme l
																	where
																		l.id = u.lexeme_id
																		and l.dataset_code not in ('eki', 'ety'))
																)
													) u
												),
												'members', (
													select
														json_agg(
															json_build_object(
																'conjunct', cm2.conjunct,
																'lexemeId', ml.id,
																'wordId', mw.id,
																'wordValue', mw.value,
																'homonymNr', mw.homonym_nr,
																'lang', mw.lang,
																'formId', mf.id,
																'formValue', mf.value,
																'morphCode', mf.morph_code,
																'weight', cm2.weight,
																'memberOrder', cm2.member_order
															)
															order by cm2.member_order
														)
													from
														form mf,
														word mw,
														lexeme ml,
														collocation_member cm2
													where
														cm2.colloc_lexeme_id = cl.id
														and cm2.member_lexeme_id = ml.id
														and ml.word_id = mw.id
														and cm2.member_form_id = mf.id
												),
												'groupOrder', cm1.group_order
											)
											order by cm1.group_order
										)
									from
										word cw,
										lexeme cl,
										collocation_member cm1
									where
										cm1.member_lexeme_id = l.id
										and cm1.pos_group_code = pg.code
										and cm1.rel_group_code = rg.code
										and cm1.colloc_lexeme_id = cl.id
										and cl.word_id = cw.id
								)
							)
							order by rg.order_by
						)
					from
						rel_group rg 
					where
						exists (
							select
								cm.id
							from
								collocation_member cm
							where
								cm.member_lexeme_id = l.id
								and cm.pos_group_code = pg.code
								and cm.rel_group_code = rg.code
						)
					)
				)
				order by pg.order_by
			)
		from pos_group pg
	) pos_groups
from 
	word w,
	lexeme l
where
	l.word_id = w.id
	and w.is_public = true
	and l.is_public = true
	and exists (
		select 
			cm.id
		from
			collocation_member cm
		where 
			cm.member_lexeme_id = l.id
			and cm.pos_group_code is not null
	)
order by
	w.id,
	l.id
;

create view view_ww_word_etymology
as
with recursive word_etym_recursion (
	word_id,
	word_etym_word_id,
	word_etym_id,
	related_word_id,
	related_word_ids) as (
	(
	select
		we.word_id,
		we.word_id word_etym_word_id,
		we.id word_etym_id,
		wer.related_word_id,
		array[we.word_id] as related_word_ids
	from
		word_etymology we
		left outer join word_etymology_relation wer on 
			wer.word_etym_id = we.id 
			and wer.related_word_id != we.word_id
	where 
		exists (
			select 
				l.id
			from 
				lexeme l,
				dataset ds
			where 
				l.word_id = we.word_id
				and l.is_public = true
				and ds.code = l.dataset_code
				and ds.type = 'LEX'
				and exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'lexeme'
						and p.entity_id = l.id)
		)
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
		(rec.related_word_ids || we.word_id) as related_word_ids
	from
		word_etym_recursion rec
		inner join word_etymology we on we.word_id = rec.related_word_id
		left outer join word_etymology_relation wer on 
			wer.word_etym_id = we.id 
			and wer.related_word_id != we.word_id
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
	w.value word_etym_word_value,
	w.lang word_etym_word_lang,
	we.etymology_type_code,
	we.etymology_year,
	we.comment_prese word_etym_comment,
	we.is_questionable word_etym_is_questionable,
	we.order_by word_etym_order_by,
	(
	select
		array_agg(w2.value)
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
		and l1.word_id = rec.word_etym_word_id
	) word_etym_meaning_word_values,
	(
	select 
		json_agg(
			json_build_object(
				'wordEtymRelId', wer.id,
				'comment', wer.comment_prese,
				'questionable', wer.is_questionable,
				'compound', wer.is_compound,
				'relatedWordId', wer.related_word_id
			)
			order by
				wer.order_by
		)
	from
		word_etymology_relation wer
	where
		wer.word_etym_id = rec.word_etym_id
		and wer.related_word_id != rec.word_id
	) word_etym_relations,
	(
	select
		json_agg(
			json_build_object(
				'sourceLinkId', wesl.id,
				'sourceLinkName', wesl.name,
				'orderBy', wesl.order_by,
				'sourceId', s.id,
				'sourceName', s.name,
				'sourceValue', s.value,
				'sourceValuePrese', s.value_prese,
				'sourcePublic', s.is_public
			)
			order by 
				wesl.word_etym_id,
				wesl.order_by
		)
	from
		word_etymology_source_link wesl,
		"source" s
	where
		wesl.source_id = s.id
		and wesl.word_etym_id = rec.word_etym_id
	) source_links
from
	word_etym_recursion rec
	inner join word_etymology we on we.id = rec.word_etym_id
	inner join (
		select 
			w.id,
			w.lang,
			w.value
		from 
			word w
		where 
			exists (
				select
					l.id
				from
					lexeme l,
					dataset ds
				where
					l.word_id = w.id
					and l.is_public = true
					and ds.code = l.dataset_code
					and ds.type = 'LEX'
					and l.dataset_code != 'ety'
					and (exists (
							select
								1
							from
								publishing p
							where
								p.entity_name = 'lexeme'
								and p.entity_id = l.id)
						or l.dataset_code != 'eki')
			)
		group by w.id
	) w on w.id = rec.word_etym_word_id
order by
	rec.word_id,
	we.order_by;
 
create view view_ww_word_relation 
as
select 
	w.id word_id,
	wr.related_words,
	wg.word_group_members
from 
	word w
	left outer join (
		select
			wr.word1_id word_id,
			json_agg(
				json_build_object(
					'wordRelTypeCode', wr.word_rel_type_code,
					'relationStatus', wr.relation_status,
					'orderBy', wr.word_rel_order_by,
					'wordId', wr.related_word_id,
					'value', wr.related_word_value,
					'valuePrese', wr.related_word_value_prese,
					'homonymNr', wr.related_word_homonym_nr,
					'homonymsExist', wr.related_word_homonyms_exist,
					'lang', wr.related_word_lang,
					'aspectCode', wr.related_word_aspect_code,
					'wordTypeCodes', wr.related_word_type_codes,
					'wwUnif', wr.is_ww_unif,
					'wwLite', wr.is_ww_lite,
					'wwOd', wr.is_ww_od
				)
				order by wr.word_rel_order_by
			) related_words
		from (
			select
				wr.word1_id,
				wr.word2_id related_word_id,
				wr.word_rel_type_code,
				coalesce(wr.relation_status, 'UNDEFINED') relation_status,
				wr.order_by word_rel_order_by,
				w2.value related_word_value,
				w2.value_prese related_word_value_prese,
				w2.homonym_nr related_word_homonym_nr,
				(exists(
					select
						1
					from
						word wh
					where
						wh.lang = w2.lang
						and wh.value = w2.value
						and wh.id != w2.id
						and exists (
							select
								1
							from
								lexeme l,
								dataset ds
							where
								l.word_id = wh.id
								and l.dataset_code = ds.code
								and l.is_public = true
								and ds.is_public = true
								and l.dataset_code != 'ety'
								and (exists (
										select
											1
										from
											publishing p
										where
											p.entity_name = 'lexeme'
											and p.entity_id = l.id)
									or l.dataset_code != 'eki')
						)
				)) related_word_homonyms_exist,
				w2.lang related_word_lang,
				w2.aspect_code related_word_aspect_code,
				(
				select
					array_agg(wwt.word_type_code order by wwt.order_by)
				from
					word_word_type wwt
				where
					wwt.word_id = w2.id 
					and wwt.word_type_code not in ('vv', 'yv', 'vvar')
				) related_word_type_codes,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'word_relation'
						and p.entity_id = wr.id
				)) is_ww_unif,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'word_relation'
						and p.entity_id = wr.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'word_relation'
						and p.entity_id = wr.id
				)) is_ww_od
			from
				word w1,
				word_relation wr,
				word w2
			where
				wr.word1_id = w1.id
				and wr.word2_id = w2.id
				and exists (
					select
						1
					from
						lexeme l,
						dataset ds
					where
						l.word_id = w1.id
						and l.dataset_code = ds.code
						and l.is_public = true
						and ds.is_public = true
						and l.dataset_code != 'ety'
						and (exists (
								select
									1
								from
									publishing p
								where
									p.entity_name = 'lexeme'
									and p.entity_id = l.id)
							or l.dataset_code != 'eki')
				)
				and exists (
					select
						1
					from
						lexeme l,
						dataset ds
					where
						l.word_id = w2.id
						and l.dataset_code = ds.code
						and l.is_public = true
						and ds.is_public = true
						and l.dataset_code != 'ety'
						and (exists (
								select
									1
								from
									publishing p
								where
									p.entity_name = 'lexeme'
									and p.entity_id = l.id)
							or l.dataset_code != 'eki')
				)
				and exists (
					select
						1
					from
						publishing p
					where
						p.entity_name = 'word_relation'
						and p.entity_id = wr.id
				)
		) wr
		group by
			wr.word1_id
	) wr on wr.word_id = w.id
	left outer join (
		select
			wg.word_id,
			json_agg(
				json_build_object(
					'wordGroupId', wg.word_group_id,
					'wordRelTypeCode', wg.word_rel_type_code,
					'orderBy', wg.group_member_order_by,
					'wordId', wg.group_member_word_id,
					'value', wg.group_member_word_value,
					'valuePrese', wg.group_member_word_value_prese,
					'homonymNr', wg.group_member_homonym_nr,
					'homonymsExist', wg.group_member_homonyms_exist,
					'lang', wg.group_member_word_lang,
					'aspectCode', wg.group_member_aspect_code,
					'wordTypeCodes', wg.group_member_word_type_codes,
					'wwUnif', wg.is_ww_unif,
					'wwLite', wg.is_ww_lite,
					'wwOd', wg.is_ww_od
				)
				order by 
					wg.word_group_id, 
					wg.group_member_order_by
			) word_group_members
		from (
			select
				w1.id word_id,
				wg.id word_group_id,
				wg.word_rel_type_code,
				w2.id group_member_word_id,
				w2.value group_member_word_value,
				w2.value_prese group_member_word_value_prese,
				w2.homonym_nr group_member_homonym_nr,
				(exists(
					select
						1
					from
						word wh
					where
						wh.lang = w2.lang
						and wh.value = w2.value
						and wh.id != w2.id
						and exists (
							select
								1
							from
								lexeme l,
								dataset ds
							where
								l.word_id = wh.id
								and l.dataset_code = ds.code
								and l.is_public = true
								and ds.is_public = true
								and l.dataset_code != 'ety'
								and (exists (
										select
											1
										from
											publishing p
										where
											p.entity_name = 'lexeme'
											and p.entity_id = l.id)
									or l.dataset_code != 'eki')
						)
				)) group_member_homonyms_exist,
				w2.lang group_member_word_lang,
				w2.aspect_code group_member_aspect_code,
				wgm2.order_by group_member_order_by,
				(
				select
					array_agg(wwt.word_type_code order by wwt.order_by)
				from
					word_word_type wwt
				where
					wwt.word_id = w2.id 
					and wwt.word_type_code not in ('vv', 'yv', 'vvar')
				) group_member_word_type_codes,
				(exists (
					select
						p.id
					from
						lexeme l,
						publishing p 
					where
						l.word_id = w2.id
						and p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id)
				or exists (
					select
						1
					from
						lexeme l
					where
						l.word_id = w2.id
						and l.dataset_code not in ('eki', 'ety'))
				) is_ww_unif,
				(exists (
					select
						p.id
					from
						lexeme l,
						publishing p 
					where
						l.word_id = w2.id
						and p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
				)) is_ww_lite,
				(exists (
					select
						p.id
					from
						lexeme l,
						publishing p 
					where
						l.word_id = w2.id
						and p.target_name = 'ww_od'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
				)) is_ww_od
			from
				word w1,
				word w2,
				word_group wg,
				word_group_member wgm1,
				word_group_member wgm2
			where
				wgm1.word_group_id = wg.id
				and wgm2.word_group_id = wg.id
				and wgm1.word_id = w1.id
				and wgm2.word_id = w2.id
				and exists (
					select
						1
					from
						lexeme l,
						dataset ds
					where
						l.word_id = w1.id
						and l.dataset_code = ds.code
						and l.is_public = true
						and ds.is_public = true
						and l.dataset_code != 'ety'
						and (exists (
								select
									1
								from
									publishing p
								where
									p.entity_name = 'lexeme'
									and p.entity_id = l.id)
							or l.dataset_code != 'eki')
				)
				and exists (
					select
						1
					from
						lexeme l,
						dataset ds
					where
						l.word_id = w2.id
						and l.dataset_code = ds.code
						and l.is_public = true
						and ds.is_public = true
						and l.dataset_code != 'ety'
						and (exists (
								select
									1
								from
									publishing p
								where
									p.entity_name = 'lexeme'
									and p.entity_id = l.id)
							or l.dataset_code != 'eki')
				)
		) wg
		group by
			wg.word_id
	) wg on wg.word_id = w.id
where
	w.is_public = true
	and (
		wr.related_words is not null
		or wg.word_group_members is not null
	)
	and
		exists (
			select
				l.id
			from 
				lexeme l,
				dataset ds
			where
				l.word_id = w.id
				and l.is_public = true
				and l.is_word = true
				and ds.code = l.dataset_code
				and ds.is_public = true
				and l.dataset_code != 'ety'
				and (exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l.id)
					or l.dataset_code != 'eki')
		)
order by
	w.id;

create view view_ww_lexeme_relation 
as
select
	l1.id lexeme_id,
	json_agg(
		json_build_object(
			'lexemeId', l2.id,
			'lexRelTypeCode', lr.lex_rel_type_code,
			'wordId', w2.id,
			'value', w2.value,
			'valuePrese', w2.value_prese,
			'homonymNr', w2.homonym_nr,
			'lang', w2.lang,
			'aspectCode', w2.aspect_code,
			'wordTypeCodes', (
				select
					array_agg(wwt.word_type_code order by wwt.order_by)
				from
					word_word_type wwt
				where
					wwt.word_id = w2.id 
					and wwt.word_type_code not in ('vv', 'yv', 'vvar')
			),
			'wwUnif', (
				exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_unif'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)
				or l2.dataset_code != 'eki'
			),
			'wwLite', (
				exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_lite'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)
			),
			'wwOd', (
				exists (
					select
						p.id
					from
						publishing p 
					where
						p.target_name = 'ww_od'
						and p.entity_name = 'lexeme'
						and p.entity_id = l2.id
				)
			)
		)
		order by
			lr.order_by
	) related_lexemes
from
	lexeme l1,
	lexeme l2,
	lex_relation lr,
	word w2,
	dataset ds1,
	dataset ds2
where
	lr.lexeme1_id = l1.id
	and lr.lexeme2_id = l2.id
	and l2.word_id = w2.id
	and l1.dataset_code = ds1.code
	and l2.dataset_code = ds2.code
	and l1.is_public = true
	and l2.is_public = true
	and ds1.is_public = true
	and ds2.is_public = true
	and l1.dataset_code != 'ety'
	and l2.dataset_code != 'ety'
	and (exists (
			select
				1
			from
				publishing p
			where
				p.entity_name = 'lexeme'
				and p.entity_id = l1.id)
		or l1.dataset_code != 'eki')
	and (exists (
			select
				1
			from
				publishing p
			where
				p.entity_name = 'lexeme'
				and p.entity_id = l2.id)
		or l2.dataset_code != 'eki')
group by
	l1.id
order by
	l1.id
;		

create view view_ww_meaning_relation
as
select
	mr.m1_id meaning_id,
	json_agg(
		json_build_object(
			'meaningId', mr.m2_id,
			'weight', mr.weight,
			'nearSynDefinitionValue', mr.near_syn_definition_value,
			'lexValueStateCode', mr.value_state_code,
			'lexRegisterCodes', mr.lex_register_codes,
			'lexGovernmentValues', mr.lex_government_values,
			'meaningRelTypeCode', mr.meaning_rel_type_code,
			'wordId', mr.word_id,
			'value', mr.word_value,
			'valuePrese', mr.word_value_prese,
			'homonymNr', mr.homonym_nr,
			'lang', mr.word_lang,
			'aspectCode', mr.aspect_code,
			'wordTypeCodes', mr.word_type_codes,
			'wwUnif', mr.is_ww_unif,
			'wwLite', mr.is_ww_lite,
			'wwOd', mr.is_ww_od
		)
		order by 
			mr.order_by,
			mr.lex_order_by
	) related_meanings
from (
	select
		mr.meaning1_id m1_id,
		mr.meaning2_id m2_id,
		mr.weight,
		mr.meaning_rel_type_code meaning_rel_type_code,
		mr.order_by,
		l2.value_state_code,
		l2.order_by lex_order_by,
		w2.id word_id,
		w2.value word_value,
		w2.value_prese word_value_prese,
		w2.homonym_nr,
		w2.lang word_lang,
		w2.aspect_code,
		(select
			(array_agg(d.value))[1]
		from
			definition d
		where
			d.meaning_id = m2.id
			and d.definition_type_code = 'kitsam/laiem tähendus teises keeles'
		group by
			m2.id
		) near_syn_definition_value,
		(select 
			array_agg(wt.word_type_code)
		from 
			word_word_type wt
		where 
			wt.word_id = w2.id
			and wt.word_type_code not in ('vv', 'yv', 'vvar')
		group by 
			w2.id
		) word_type_codes,
		(select 
			array_agg(lr.register_code)
		from 
			lexeme_register lr
		where
			lr.lexeme_id = l2.id
		) lex_register_codes,
		(select 
			array_agg(g.value)
		from 
			government g
		where
			g.lexeme_id = l2.id
		) lex_government_values,
 		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_unif'
				and p.entity_name = 'lexeme'
				and p.entity_id = l2.id)
		or l2.dataset_code != 'eki'
		) is_ww_unif,
		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_lite'
				and p.entity_name = 'lexeme'
				and p.entity_id = l2.id
		)) is_ww_lite,
		(exists (
			select
				p.id
			from
				publishing p 
			where
				p.target_name = 'ww_od'
				and p.entity_name = 'lexeme'
				and p.entity_id = l2.id
		)) is_ww_od
	from 
		meaning_relation mr,
		meaning m2,
		lexeme l2,
		word w2,
		dataset ds2
	where
		mr.meaning2_id = m2.id
		and l2.meaning_id = m2.id
		and l2.word_id = w2.id
		and l2.dataset_code = ds2.code
		and mr.meaning_rel_type_code != 'duplikaadikandidaat'
		and w2.is_public = true
		and l2.is_public = true
		and l2.is_word = true
		and ds2.is_public = true
		and l2.dataset_code != 'ety'
		and exists (
			select
				1
			from
				lexeme l1,
				dataset ds1
			where
				l1.meaning_id = mr.meaning1_id
				and l1.dataset_code = ds1.code
				and l1.is_public = true
				and ds1.is_public = true
				and l1.dataset_code != 'ety'
				and (exists (
						select
							1
						from
							publishing p
						where
							p.entity_name = 'lexeme'
							and p.entity_id = l1.id)
					or l1.dataset_code != 'eki')
		)
		and (exists (
				select
					1
				from
					publishing p
				where
					p.entity_name = 'lexeme'
					and p.entity_id = l2.id)
			or l2.dataset_code != 'eki')
	) mr
group by mr.m1_id
order by mr.m1_id;

create view view_ww_dataset
as
select
	ds.code,
	ds.type,
	ds.name,
	ds.description,
	ds.contact,
	ds.image_url,
	ds.is_superior,
	ds.order_by
from 
	dataset ds
where 
	ds.is_public = true
order by 
	ds.order_by
;

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
		'REL_GROUP' as name,
		null as origin,
		c.code,
		cl.value,
		cl.lang,
		cl.type,
		c.order_by
	from 
		rel_group c,
		rel_group_label cl
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
select
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
;


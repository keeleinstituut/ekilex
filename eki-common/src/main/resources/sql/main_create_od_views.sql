
drop view if exists view_od_lexeme_meaning;
drop view if exists view_od_definition_idx;
drop view if exists view_od_definition;
drop view if exists view_od_word_relation_idx;
drop view if exists view_od_word_relation;
drop view if exists view_od_word_od_recommend;
drop view if exists view_od_word_od_usage_idx;
drop view if exists view_od_word_od_usage;
drop view if exists view_od_word_od_morph;
drop view if exists view_od_word;

-- word --

create view view_od_word
as
select
	w.id word_id,
	w.value,
	w.value_prese,
	w.value_as_word,
	w.homonym_nr,
	w.display_morph_code,
	(select
		array_agg(wwt.word_type_code)
	from
		word_word_type wwt
	where
		wwt.word_id = w.id
	) word_type_codes
from 
	word w
where
	w.is_public = true
	and w.lang = 'est'
	and exists (
		select
			1
		from
			lexeme l
		where
			l.word_id = w.id
			and l.is_public = true
			and l.is_word = true
			and l.dataset_code = 'eki'
			and exists (
				select
					1
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'lexeme'
					and p.entity_id = l.id
			)
	)
order by
	w.id
;

-- word od morph --

create view view_od_word_od_morph
as
select
	wom.word_id,
	wom.id word_od_morph_id,
	wom.value,
	wom.value_prese
from 
	word_od_morph wom
order by
	wom.id
;

-- word od usage (unindexed) --

create view view_od_word_od_usage
as
select
	wou.word_id,
	json_agg(
		json_build_object(
			'wordOdUsageId', wou.id,
			'value', wou.value,
			'valuePrese', wou.value_prese,
			'orderBy', wou.order_by
		)
		order by
			wou.order_by
	) word_od_usages
from
	word_od_usage wou
group by
	wou.word_id
order by
	wou.word_id
;

-- word od usage (indexed) --

create view view_od_word_od_usage_idx
as
select
	wou.word_id,
	wou.id word_od_usage_id,
	wou.value
from
	word_od_usage wou
order by
	wou.word_id,
	wou.id
;

-- word od recommend --

create view view_od_word_od_recommend
as
select 
	wor.word_id,
	wor.id word_od_recommend_id,
	wor.value,
	wor.value_prese,
	wor.opt_value,
	wor.opt_value_prese 
from 
	word_od_recommendation wor 
order by
	wor.id 
;

-- definition --

create view view_od_definition
as
select
	d.meaning_id,
	d.definition_id,
	d.value,
	d.value_prese
from (
	select
		meaning_id,
		(d.definition ->> 'definition_id')::bigint as definition_id,
		(d.definition ->> 'value') as value,
		(d.definition ->> 'value_prese') as value_prese,
		(d.definition ->> 'lang') as lang
	from (
		select
			d.meaning_id,
			(array_agg(
				json_build_object(
					'definition_id', d.id,
					'value', d.value,
					'value_prese', d.value_prese,
					'lang', d.lang
				)
				order by
					d.order_by
			))[1] definition
		from
			definition d
		where
			d.is_public = true
			and d.lang = 'est'
			and exists (
				select
					1
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'definition'
					and p.entity_id = d.id
			)
		group by
			d.meaning_id
	) d 
) d
order by
	d.meaning_id,
	d.definition_id
;

-- definition (indexed) --

create view view_od_definition_idx
as
select
	l.word_id,
	l.meaning_id,
	d.definition_id,
	d.value
from
	lexeme l,
	view_od_definition d
where
	l.meaning_id = d.meaning_id
	and l.is_public = true
	and l.is_word = true
	and l.dataset_code = 'eki'
	and exists (
		select
			1
		from
			word w
		where
			w.id = l.word_id
			and w.lang = 'est'
	)
	and exists (
		select
			1
		from
			publishing p 
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
order by
	l.word_id,
	l.id,
	l.meaning_id
;

-- lexeme meaning --

create view view_od_lexeme_meaning
as
select
	l.word_id,
	json_agg(
		json_build_object(
			'lexemeId', l.id,
			'wordId', l.word_id,
			'meaningId', l.meaning_id,
			'valueStateCode', l.value_state_code,
			'registerCodes', (
				select
					array_agg(lr.register_code)
				from
					lexeme_register lr
				where
					lr.lexeme_id = l.id
			),
			'meaning', (
				select
					json_build_object(
						'meaningId', m.id,
						'definition', (
							select
								json_build_object(
									'definitionId', d.definition_id,
									'meaningId', d.meaning_id,
									'value', d.value,
									'valuePrese', d.value_prese
								)
							from
								view_od_definition d
							where
								d.meaning_id = m.id
							limit 1
						),
						'lexemeWords', (
							select
								json_agg(
									json_build_object(
										'lexemeId', l2.id,
										'wordId', l2.word_id,
										'meaningId', l2.meaning_id,
										'valueStateCode', l2.value_state_code,
										'registerCodes', (
											select
												array_agg(lr.register_code)
											from
												lexeme_register lr
											where
												lr.lexeme_id = l2.id
										),
										'value', w2.value,
										'valuePrese', w2.value_prese,
										'homonymNr', w2.homonym_nr,
										'displayMorphCode', w2.display_morph_code,
										'wordTypeCodes', (
											select
												array_agg(wwt.word_type_code)
											from
												word_word_type wwt
											where
												wwt.word_id = w2.id
										)
									)
									order by
										l2.order_by
								)
							from
								lexeme l2,
								word w2
							where
								l2.meaning_id = m.id
								and l2.word_id = w2.id
								and l2.id != l.id
								and l2.is_public = true
								and l2.is_word = true
								and l2.dataset_code = 'eki'
								and w2.is_public = true
								and w2.lang = 'est'
								and exists (
									select
										1
									from
										publishing p 
									where
										p.target_name = 'ww_od'
										and p.entity_name = 'lexeme'
										and p.entity_id = l2.id
								)
						)
					)
				from
					meaning m
				where
					m.id = l.meaning_id
			)
		)
		order by
			l.level1,
			l.level2
	) lexeme_meanings
from
	lexeme l
where
	l.is_public = true
	and l.is_word = true
	and l.dataset_code = 'eki'
	and exists (
		select
			1
		from
			word w
		where
			w.id = l.word_id
			and w.lang = 'est'
	)
	and exists (
		select
			1
		from
			publishing p 
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
group by
	l.word_id
order by
	l.word_id
;

-- word relation (unindexed) --

create view view_od_word_relation
as
select
	w.id word_id,
	json_agg(
		json_build_object(
			'wordRelTypeCode', wrt.code,
			'relatedWords', (
				select
					json_agg(
						json_build_object(
							'wordRelationId', wr.id,
							'relatedWordId', w2.id,
							'wordRelTypeCode', wr.word_rel_type_code,
							'value', w2.value,
							'valuePrese', w2.value_prese,
							'homonymNr', w2.homonym_nr,
							'displayMorphCode', w2.display_morph_code,
							'wordTypeCodes', (
							select
								array_agg(wwt.word_type_code)
							from
								word_word_type wwt
							where
								wwt.word_id = w2.id
							)
						)
						order by
							wr.order_by
					)
				from
					word_relation wr,
					word w2
				where
					wr.word1_id = w.id
					and wr.word2_id = w2.id
					and wr.word_rel_type_code = wrt.code
					and w2.is_public = true
					and w2.lang = 'est'
					and exists (
						select
							1
						from
							publishing p 
						where
							p.target_name = 'ww_od'
							and p.entity_name = 'word_relation'
							and p.entity_id = wr.id
					)
					and exists (
						select
							1
						from
							lexeme l 
						where
							l.word_id = w2.id
							and l.is_public = true
							and l.is_word = true
							and l.dataset_code = 'eki'
					)
			)
		)
		order by
			wrt.order_by 
	) word_relation_groups
from 
	word_rel_type wrt,
	word w 
where
	w.is_public = true
	and w.lang = 'est'
	and wrt.code in (
		'ls-järelosaga',
		'ls-esiosaga',
		'deriv',
		'komp',
		'superl',
		'posit'
	)
	and exists (
		select
			1
		from
			word_relation wr,
			word w2
		where
			wr.word1_id = w.id
			and wr.word2_id = w2.id
			and wr.word_rel_type_code = wrt.code
			and w2.is_public = true
			and w2.lang = 'est'
			and exists (
				select
					1
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'word_relation'
					and p.entity_id = wr.id
			)
			and exists (
				select
					1
				from
					lexeme l 
				where
					l.word_id = w2.id
					and l.is_public = true
					and l.is_word = true
					and l.dataset_code = 'eki'
			)
	)
	and exists (
		select
			1
		from
			lexeme l 
		where
			l.word_id = w.id
			and l.is_public = true
			and l.is_word = true
			and l.dataset_code = 'eki'
			and exists (
				select
					1
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'lexeme'
					and p.entity_id = l.id
			)
	)
group by
	w.id
order by
	w.id
;

-- word relation (indexed) --

create view view_od_word_relation_idx
as
select
	w1.id word_id,
	wr.id word_relation_id,
	wr.word_rel_type_code,
	w2.id related_word_id,
	w2.value,
	w2.value_as_word
from
	word w1,
	word w2,
	word_relation wr 
where
	wr.word1_id = w1.id
	and wr.word2_id = w2.id
	and w1.lang = 'est'
	and w2.lang = 'est'
	and w1.is_public = true
	and w2.is_public = true
	and wr.word_rel_type_code in (
		'ls-järelosaga',
		'ls-esiosaga',
		'deriv',
		'komp',
		'superl',
		'posit'
	)
	and exists (
		select
			1
		from
			lexeme l 
		where
			l.word_id = w1.id
			and l.is_public = true
			and l.is_word = true
			and l.dataset_code = 'eki'
			and exists (
				select
					1
				from
					publishing p 
				where
					p.target_name = 'ww_od'
					and p.entity_name = 'lexeme'
					and p.entity_id = l.id
			)
	)
	and exists (
		select
			1
		from
			publishing p 
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'word_relation'
			and p.entity_id = wr.id
	)
order by
	w1.id,
	wr.id
;


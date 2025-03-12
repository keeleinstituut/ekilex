select
	stat.dataset_code,
	stat.dataset_name,
	stat.language_code,
	stat.language_name,
	stat.term_lang_meaning_count, -- 2.1
	stat.term_lang_meaning_edit_period_meaning_count, -- 2.2
	stat.def_lang_meaning_count, -- 3.3
	stat.def_lang_meaning_edit_period_meaning_count, -- 3.4
	stat.w_usage_term_lang_meaning_count -- 4.2
from
	(
	select
		ds.code dataset_code,
		ds."name" dataset_name,
		lng.code language_code,
		lng.value language_name,
		(
		select
			count(m.id)
		from
			meaning m
		where exists (
			select
				1
			from
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.is_word = true
				and l.is_public = true
				and l.dataset_code = ds.code
				and w.is_public = true
				and w.lang = lng.code
			)
		) term_lang_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m
		where exists (
			select
				1
			from
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.is_word = true
				and l.is_public = true
				and l.dataset_code = ds.code
				and w.is_public = true
				and w.lang = lng.code
			)
			and exists (
				select
					1
				from
					meaning_activity_log mal,
					activity_log al
				where
					mal.meaning_id = m.id
					and mal.activity_log_id = al.id
					and al.owner_name in ('MEANING', 'LEXEME')
					and al.event_on between '2024-04-01' and '2025-02-28'
				union all
				select
					1
				from
					meaning_activity_log mal,
					activity_log al
				where
					mal.meaning_id = m.id
					and mal.activity_log_id = al.id
					and al.owner_name = 'WORD'
					and al.entity_name not in (
						'GRAMMAR',
						'WORD_TYPE',
						'WORD_TAG',
						'WORD_NOTE',
						'WORD_RELATION',
						'WORD_RELATION_GROUP_MEMBER',
						'WORD_ETYMOLOGY',
						'PARADIGM',
						'FORM',
						'OD_WORD_RECOMMENDATION',
						'TAG')
					and al.funct_name not like '%join%'
					and al.event_on between '2024-04-01' and '2025-02-28'
			)
		) term_lang_meaning_edit_period_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m 
		where exists (
			select
				1
			from
				definition d,
				definition_dataset dd
			where
				d.meaning_id = m.id
				and dd.definition_id = d.id
				and dd.dataset_code = ds.code
				and d.lang = lng.code
			)
		) def_lang_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m 
		where exists (
			select
				1
			from
				definition d,
				definition_dataset dd
			where
				d.meaning_id = m.id
				and dd.definition_id = d.id
				and dd.dataset_code = ds.code
				and d.lang = lng.code
			)
			and exists (
				select
					1
				from
					meaning_activity_log mal,
					activity_log al
				where
					mal.meaning_id = m.id
					and mal.activity_log_id = al.id
					and al.owner_name in ('MEANING', 'LEXEME')
					and al.event_on between '2024-04-01' and '2025-02-28'
				union all
				select
					1
				from
					meaning_activity_log mal,
					activity_log al
				where
					mal.meaning_id = m.id
					and mal.activity_log_id = al.id
					and al.owner_name = 'WORD'
					and al.entity_name not in (
						'GRAMMAR',
						'WORD_TYPE',
						'WORD_TAG',
						'WORD_NOTE',
						'WORD_RELATION',
						'WORD_RELATION_GROUP_MEMBER',
						'WORD_ETYMOLOGY',
						'PARADIGM',
						'FORM',
						'OD_WORD_RECOMMENDATION',
						'TAG')
					and al.funct_name not like '%join%'
					and al.event_on between '2024-04-01' and '2025-02-28'
			)
		) def_lang_meaning_edit_period_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m
		where exists (
			select
				1
			from
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.is_word = true
				and l.is_public = true
				and l.dataset_code = ds.code
				and w.is_public = true
				and w.lang = lng.code
				and exists (
					select
						1
					from
						"usage" u
					where
						u.lexeme_id = l.id
						and u.is_public = true
				)
			)
		) w_usage_term_lang_meaning_count
	from
		(
		select
			ds.code,
			ds."name"
		from
			dataset ds
		where
			ds.code not in (
				'kce',
				'eki',
				'ety',
				'gal',
				'iht_200915',
				'ing',
				'konstr',
				'üliõpsõnad',
				'linguae',
				'les',
				'neen',
				'p3m_vana',
				'vrk',
				'default',
				'vkk-amet',
				'vke',
				'ÕS2025')
		) ds,
		(
		select
			lngl.code,
			lngl.value
		from
			"language" lng,
			language_label lngl
		where
			lngl.code = lng.code
			and lngl.type = 'full'
			and lngl.lang = 'est'
			and exists (
				select
					1
				from
					word w,
					lexeme l
				where
					w.lang = lng.code
					and l.word_id = w.id
					and l.is_word = true
					and l.dataset_code not in (
							'kce',
							'eki',
							'ety',
							'gal',
							'iht_200915',
							'ing',
							'konstr',
							'üliõpsõnad',
							'linguae',
							'les',
							'neen',
							'p3m_vana',
							'vrk',
							'default',
							'vkk-amet',
							'vke',
							'ÕS2025')
			)
		) lng
	group by
		ds.code,
		ds."name",
		lng.code,
		lng.value
	order by
		ds."name",
		lng.value
	) stat
where
	stat.term_lang_meaning_count > 0
	or stat.def_lang_meaning_count > 0
;

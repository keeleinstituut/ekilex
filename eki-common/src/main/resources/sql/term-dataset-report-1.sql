select
	stat.dataset_code,
	stat.dataset_name,
	stat.all_meaning_count, -- 1.1
	stat.all_term_count, -- 1.2
	stat.create_meaning_count, -- 1.3
	stat.update_meaning_count, -- 1.4
	stat.w_domain_meaning_count, -- 1.5
	(case
		when stat.all_meaning_count = 0 then 0
		else cast(((stat.w_domain_meaning_count::decimal / stat.all_meaning_count::decimal) * 100.00) as decimal(8, 2))
	end
	) w_domain_meaning_percent, -- 1.6
	stat.w_domain_update_meaning_count, -- 1.7
	(case
		when stat.update_meaning_count = 0 then 0
		else cast(((stat.w_domain_update_meaning_count::decimal / stat.update_meaning_count::decimal) * 100.00) as decimal(8, 2))
	end
	) w_domain_update_meaning_percent, -- 1.8
	stat.wo_domain_term_sample, -- 1.9
	stat.single_term_meaning_count, -- 2.3
	stat.single_term_meaning_term_sample, -- 2.4
	stat.single_lang_meaning_count, -- 2.5
	stat.single_lang_meaning_term_sample, -- 2.6
	stat.specific_char_term_count, -- 2.7
	stat.specific_char_term_sample, -- 2.8
	stat.init_cap_term_count, -- 2.9
	stat.init_cap_term_sample, -- 2.10
	stat.w_source_link_term_count, -- 2.11
	stat.w_source_link_meaning_update_term_count, -- 2.12
	stat.wo_source_link_term_count, -- 2.13
	stat.wo_source_link_meaning_update_term_count, -- 2.14
	stat.wo_source_link_meaning_update_term_sample, -- 2.15
	stat.w_def_meaning_count, -- 3.1
	stat.w_def_meaning_update_meaning_count, -- 3.2
	stat.wo_def_meaning_term_sample, -- 3.5
	stat.wo_def_meaning_update_term_sample, -- 3.6
	stat.w_punctuation_def_count, -- 3.7
	stat.w_punctuation_def_term_sample, -- 3.8
	stat.init_cap_def_count, -- 3.9
	stat.init_cap_def_term_sample, -- 3.10
	stat.init_1a_def_count, -- 3.11
	stat.init_1a_def_term_sample, -- 3.12
	stat.w_source_link_def_count, -- 3.13
	stat.w_source_link_def_meaning_update_def_count, -- 3.14
	stat.w_usage_meaning_count, -- 4.1
	stat.w_source_link_usage_count, -- 4.3
	stat.w_source_link_usage_meaning_update_usage_count, -- 4.4
	stat.wo_source_link_usage_count, -- 4.5
	stat.wo_source_link_usage_meaning_update_usage_count, -- 4.6
	stat.wo_source_link_usage_term_sample, -- 4.7
	stat.wo_source_link_usage_meaning_update_term_sample -- 4.8
from
	(
	select
		ds.code dataset_code,
		ds."name" dataset_name,
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
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
			)
		) all_meaning_count,
		(
		select
			count(w.id)
		from
			word w
		where 
			w.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
				)
		) all_term_count,
		(
		select
			count(m.id)
		from
			(
			select
				m.id,
				(
				select
					al.event_on
				from
					meaning_activity_log mal,
					activity_log al
				where
					mal.meaning_id = m.id
					and mal.activity_log_id = al.id
				order by
					al.event_on asc
				limit 1
				) first_event_on
			from
				meaning m
			where exists (
				select
					1
				from
					lexeme l 
				where
					l.meaning_id = m.id
					and l.is_word = true
					and l.dataset_code = ds.code
				)
			) m
		where 
			m.first_event_on between '2024-04-01' and '2025-02-28'
		) create_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m
		where exists (
				select
					1
				from
					lexeme l 
				where
					l.meaning_id = m.id
					and l.is_word = true
					and l.dataset_code = ds.code
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
		) update_meaning_count,
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
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
			)
			and exists (
				select
					1
				from
					meaning_domain md 
				where
					md.meaning_id = m.id
			)
		) w_domain_meaning_count,
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
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
			)
			and exists (
				select
					1
				from
					meaning_domain md 
				where
					md.meaning_id = m.id
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
		) w_domain_update_meaning_count,
		(
		select
			array_to_string(array_agg(distinct rw.word_values), ' | ')
		from
			(
			select
				(array_agg(w.value order by w.id))[1] word_values
			from
				meaning m,
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
				and w.lang = 'est'
				and not exists (
					select
						1
					from
						meaning_domain md 
					where
						md.meaning_id = m.id
				)
			group by m.id
			order by random()
			limit 3
			) rw
		) wo_domain_term_sample,
		(
		select
			count(ms.id)
		from
			(
			select
				m.id,
				count(w.id) word_count
			from
				meaning m,
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
			group by
				m.id
			) ms
		where
			ms.word_count = 1
		) single_term_meaning_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					count(w.id) word_count,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and w.is_public = true
				group by
					m.id
				) ms
			where
				ms.word_count = 1
			order by random()
			limit 3
			) ms
		) single_term_meaning_term_sample,
		(
		select
			count(ms.id) cnt
		from
			(
			select
				m.id,
				count(distinct w.lang) lang_count
			from
				meaning m,
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.dataset_code = ds.code
				and l.is_word = true
			group by
				m.id
			) ms
		where
			ms.lang_count = 1
		) single_lang_meaning_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					count(distinct w.lang) lang_count,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
				group by
					m.id
				) ms
			where
				ms.lang_count = 1
			order by random()
			limit 3
			) ms
		) single_lang_meaning_term_sample,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.value similar to '%(/|\*|1|2|;|,)%'
			and w.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
			)
		) specific_char_term_count,
		(
		select
			array_to_string(array_agg(distinct rw.word_values), ' | ')
		from
			(
			select
				(array_agg(w.value order by w.id))[1] word_values
			from
				meaning m,
				lexeme l,
				word w
			where
				l.meaning_id = m.id
				and l.word_id = w.id
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
				and w.value similar to '%(/|\*|1|2|;|,)%'
			group by m.id
			order by random()
			limit 3
			) rw
		) specific_char_term_sample,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.value similar to '[[:upper:]]%'
			and w.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
			)
		) init_cap_term_count,
		(
		select
			array_to_string(array_agg(distinct ws.value), ' | ')
		from
			(
			select 
				w.value
			from
				lexeme l,
				word w
			where
				l.word_id = w.id
				and l.dataset_code = ds.code
				and l.is_word = true
				and l.is_public = true
				and w.is_public = true
				and w.value similar to '[[:upper:]]%'
			order by random()
			limit 3
			) ws
		) init_cap_term_sample,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and exists (
						select
							1
						from
							lexeme_source_link sl
						where
							sl.lexeme_id = l.id
					)
			)
		) w_source_link_term_count,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.is_public = true
			and exists (
				select
					1
				from
					lexeme l,
					meaning m 
				where
					l.word_id = w.id
					and l.meaning_id = m.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and exists (
						select
							1
						from
							lexeme_source_link sl
						where
							sl.lexeme_id = l.id
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
			)
		) w_source_link_meaning_update_term_count,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
			)
			and not exists (
				select
					1
				from
					lexeme l
				where
					l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and exists (
						select
							1
						from
							lexeme_source_link sl
						where
							sl.lexeme_id = l.id
					)
			)
		) wo_source_link_term_count,
		(
		select 
			count(w.id)
		from
			word w
		where
			w.is_public = true
			and exists (
				select
					1
				from
					lexeme l,
					meaning m 
				where
					l.word_id = w.id
					and l.meaning_id = m.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and not exists (
						select
							1
						from
							lexeme_source_link sl
						where
							sl.lexeme_id = l.id
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
			)
		) wo_source_link_meaning_update_term_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and w.is_public = true
					and not exists (
						select
							1
						from
							lexeme_source_link sl
						where
							sl.lexeme_id = l.id
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
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) wo_source_link_meaning_update_term_sample,
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
			)
		) w_def_meaning_count,
		(
		select
			count(m.id)
		from
			meaning m
		where 
			exists (
				select
					1
				from
					definition d,
					definition_dataset dd 
				where
					d.meaning_id = m.id
					and dd.definition_id = d.id
					and dd.dataset_code = ds.code
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
		) w_def_meaning_update_meaning_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and w.lang = 'est'
					and not exists (
						select
							1
						from
							definition d,
							definition_dataset dd
						where
							d.meaning_id = m.id
							and dd.definition_id = d.id
							and dd.dataset_code = ds.code
					)
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) wo_def_meaning_term_sample,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and w.lang = 'est'
					and not exists (
						select
							1
						from
							definition d,
							definition_dataset dd
						where
							d.meaning_id = m.id
							and dd.definition_id = d.id
							and dd.dataset_code = ds.code
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
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) wo_def_meaning_update_term_sample,
		(
		select
			count(d.id)
		from
			definition d 
		where
			right(d.value, 1) = '.'
			and exists (
				select
					1
				from
					definition_dataset dd 
				where
					dd.definition_id = d.id
					and dd.dataset_code = ds.code
			)
		) w_punctuation_def_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and w.lang = 'est'
					and exists (
						select
							1
						from
							definition d,
							definition_dataset dd
						where
							d.meaning_id = m.id
							and dd.definition_id = d.id
							and dd.dataset_code = ds.code
							and right(d.value, 1) = '.'
					)
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) w_punctuation_def_term_sample,
		(
		select
			count(d.id)
		from
			definition d 
		where
			d.value similar to '[[:upper:]]%'
			and exists (
				select
					1
				from
					definition_dataset dd 
				where
					dd.definition_id = d.id
					and dd.dataset_code = ds.code
			)
		) init_cap_def_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and w.lang = 'est'
					and exists (
						select
							1
						from
							definition d,
							definition_dataset dd
						where
							d.meaning_id = m.id
							and dd.definition_id = d.id
							and dd.dataset_code = ds.code
							and d.value similar to '[[:upper:]]%'
					)
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) init_cap_def_term_sample,
		(
		select
			count(d.id)
		from
			definition d 
		where
			d.value similar to '(1\.|1\)|a\.|a\))%'
			and exists (
				select
					1
				from
					definition_dataset dd 
				where
					dd.definition_id = d.id
					and dd.dataset_code = ds.code
			)
		) init_1a_def_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and w.lang = 'est'
					and exists (
						select
							1
						from
							definition d,
							definition_dataset dd
						where
							d.meaning_id = m.id
							and dd.definition_id = d.id
							and dd.dataset_code = ds.code
							and d.value similar to '(1\.|1\)|a\.|a\))%'
					)
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) init_1a_def_term_sample,
		(
		select
			count(d.id)
		from
			definition d 
		where exists (
				select
					1
				from
					definition_dataset dd 
				where
					dd.definition_id = d.id
					and dd.dataset_code = ds.code
			)
			and exists (
				select
					1
				from
					definition_source_link dsl 
				where
					dsl.definition_id = d.id
			)
		) w_source_link_def_count,
		(
		select
			count(d.id)
		from
			definition d 
		where exists (
				select
					1
				from
					definition_dataset dd 
				where
					dd.definition_id = d.id
					and dd.dataset_code = ds.code
			)
			and exists (
				select
					1
				from
					definition_source_link dsl 
				where
					dsl.definition_id = d.id
			)
			and exists (
				select
					1
				from
					meaning m
				where
					m.id = d.meaning_id
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
					
			)
		) w_source_link_def_meaning_update_def_count,
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
					usage u,
					word w
				where
					l.meaning_id = m.id
					and l.is_word = true
					and l.is_public = true
					and l.dataset_code = ds.code
					and u.lexeme_id = l.id
					and u.is_public = true
					and l.word_id = w.id
					and w.is_public = true
			)
		) w_usage_meaning_count,
		(
		select
			count(u.id)
		from
			usage u 
		where 
			u.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.id = u.lexeme_id
					and l.is_word = true
					and l.is_public = true
					and l.dataset_code = ds.code
			)
			and exists (
				select
					1
				from
					usage_source_link usl
				where
					usl.usage_id = u.id
			)
		) w_source_link_usage_count,
		(
		select
			count(u.id)
		from
			usage u 
		where 
			u.is_public = true
			and exists (
				select
					1
				from
					lexeme l,
					meaning m
				where
					l.id = u.lexeme_id
					and l.meaning_id = m.id
					and l.is_word = true
					and l.is_public = true
					and l.dataset_code = ds.code
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
			)
			and exists (
				select
					1
				from
					usage_source_link usl
				where
					usl.usage_id = u.id
			)
		) w_source_link_usage_meaning_update_usage_count,
		(
		select
			count(u.id)
		from
			usage u 
		where 
			u.is_public = true
			and exists (
				select
					1
				from
					lexeme l
				where
					l.id = u.lexeme_id
					and l.is_word = true
					and l.is_public = true
					and l.dataset_code = ds.code
			)
			and not exists (
				select
					1
				from
					usage_source_link usl
				where
					usl.usage_id = u.id
			)
		) wo_source_link_usage_count,
		(
		select
			count(u.id)
		from
			usage u 
		where 
			u.is_public = true
			and exists (
				select
					1
				from
					lexeme l,
					meaning m
				where
					l.id = u.lexeme_id
					and l.meaning_id = m.id
					and l.is_word = true
					and l.is_public = true
					and l.dataset_code = ds.code
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
			)
			and not exists (
				select
					1
				from
					usage_source_link usl
				where
					usl.usage_id = u.id
			)
		) wo_source_link_usage_meaning_update_usage_count,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and w.is_public = true
					and w.lang = 'est'
					and exists (
						select
							1
						from
							usage u
						where
							u.lexeme_id = l.id
							and u.is_public = true
							and not exists (
								select
									1
								from
									usage_source_link usl 
								where
									usl.usage_id = u.id
							)
					)
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) wo_source_link_usage_term_sample,
		(
		select
			array_to_string(array_agg(distinct ms.word_value), ' | ')
		from
			(
			select
				ms.word_value
			from
				(
				select
					m.id,
					(array_agg(w.value))[1] word_value
				from
					meaning m,
					lexeme l,
					word w
				where
					l.meaning_id = m.id
					and l.word_id = w.id
					and l.dataset_code = ds.code
					and l.is_word = true
					and l.is_public = true
					and w.is_public = true
					and w.lang = 'est'
					and exists (
						select
							1
						from
							usage u
						where
							u.lexeme_id = l.id
							and u.is_public = true
							and not exists (
								select
									1
								from
									usage_source_link usl 
								where
									usl.usage_id = u.id
							)
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
				group by
					m.id
				) ms
			order by random()
			limit 3
			) ms
		) wo_source_link_usage_meaning_update_term_sample
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
	group by
		ds.code,
		ds."name"
	order by
		ds."name" 
	) stat
;

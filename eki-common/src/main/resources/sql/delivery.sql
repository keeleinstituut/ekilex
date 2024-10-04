-- upgrade from ver 1.37.* to 1.38.0

-- keelendi staatuse taastamine peale keelendite ja kollokatsioonide väliskuju automaatset ühendamist
update
	word w
set
	is_word = true
where
	w.is_word = false
	and exists (
		select
			l.id
		from
			lexeme l,
			lexeme_tag lt
		where
			l.word_id = w.id
			and l.dataset_code = 'eki'
			and lt.lexeme_id = l.id
			and lt.tag_name = 'Kollide kolimine'
	)
	and exists (
		select
			l.id
		from
			lexeme l
		where
			l.word_id = w.id
			and l.dataset_code != 'eki'
	)
;
analyze word;
analyze word_relation;

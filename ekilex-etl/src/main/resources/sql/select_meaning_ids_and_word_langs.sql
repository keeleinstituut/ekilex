select
	m.id,
	w.lang
from
	meaning m,
	lexeme l,
		(select
			w.id,
			w.lang
		from
			word w
		where exists(
		  select
		  	f.id
			from
				paradigm p,
				form f
			where
				p.word_id = w.id
				and f.paradigm_id = p.id
				and f.mode = 'WORD'
				and f.value = :word
		  )
			group by w.id) w
where
	l.meaning_id = m.id
	and l.word_id = w.id
	and l.dataset_code = :dataset
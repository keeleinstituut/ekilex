select
	w.*
from
	word w
where 
	w.homonym_nr = :homonymNr
	and w.lang = :lang
	and exists (
		select
			f.id 
		from 
			paradigm p,
			form f
		where
		    f.value = :word
			and f.mode = 'WORD'
			and f.paradigm_id = p.id
			and p.word_id = w.id
	)
	and exists (
		select
			wwt.id
		from
			word_word_type wwt
		where
			wwt.word_id = w.id
			and wwt.word_type_code = :wordTypeCode
	)

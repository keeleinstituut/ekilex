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
			and f.is_word = true
			and f.paradigm_id = p.id
			and p.word_id = w.id)

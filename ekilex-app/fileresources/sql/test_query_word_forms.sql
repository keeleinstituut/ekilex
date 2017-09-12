-- query word morph homonyms, paradigms and forms
select w.value word,
       wm.code word_morph_code,
       wml.value word_morph_value,
       mh.id morph_homonym_id,
       p.id paradigm_id,
       f.value form,
       fm.code form_morph_code,
       fml.value form_morph_value
from word w
     inner join morph_homonym mh on mh.word_id = w.id
     inner join paradigm p on p.morph_homonym_id = mh.id
     inner join form f on f.paradigm_id = p.id
     inner join morph wm on w.morph_code = wm.code
     inner join morph fm on f.morph_code = fm.code
     left outer join morph_label wml on wml.code = wm.code and wml.lang = :defaultLabelLang and wml.type = :defaultLabelType
     left outer join morph_label fml on fml.code = fm.code and fml.lang = :defaultLabelLang and fml.type = :defaultLabelType
where w.value = :word;
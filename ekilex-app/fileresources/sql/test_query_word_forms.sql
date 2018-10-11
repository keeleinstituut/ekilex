-- query word morph homonyms, paradigms and forms
select w.id word_id,
       fw.value word,
       wm.code word_morph_code,
       wml.value word_morph_value,
       p.id paradigm_id,
       fa.value form,
       fm.code form_morph_code,
       fml.value form_morph_value
from word w
     inner join paradigm p on p.word_id = w.id
     inner join form fa on fa.paradigm_id = p.id
     inner join form fw on fw.paradigm_id = p.id and fw.mode = 'WORD'
     inner join morph wm on w.morph_code = wm.code
     inner join morph fm on fa.morph_code = fm.code
     left outer join morph_label wml on wml.code = wm.code and wml.lang = :defaultLabelLang and wml.type = :defaultLabelType
     left outer join morph_label fml on fml.code = fm.code and fml.lang = :defaultLabelLang and fml.type = :defaultLabelType
where fw.value = :word;

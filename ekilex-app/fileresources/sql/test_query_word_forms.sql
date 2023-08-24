-- query word morph homonyms, paradigms and forms
select w.id word_id,
       w.value word,
       p.id paradigm_id,
       fa.value form,
       fm.code form_morph_code,
       fml.value form_morph_value
from word w
     inner join paradigm p on p.word_id = w.id
     inner join paradigm_form pf on pf.paradigm_id = p.id
     inner join form fa on fa.id = pf.form_id
     inner join morph fm on fa.morph_code = fm.code
     left outer join morph_label fml on fml.code = fm.code and fml.lang = :defaultLabelLang and fml.type = :defaultLabelType
where w.value = :word;

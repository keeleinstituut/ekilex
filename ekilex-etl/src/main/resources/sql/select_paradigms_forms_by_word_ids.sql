select p.word_id,
       f.paradigm_id,
       p.inflection_type_nr,
       p.inflection_type,
       f.mode,
       f.morph_group1,
       f.morph_group2,
       f.morph_group3,
       f.display_level,
       f.morph_code,
       f.morph_exists,
       f.value,
       f.components,
       f.display_form,
       f.vocal_form,
       f.sound_file,
       f.order_by
from paradigm p,
     form f
where f.paradigm_id = p.id
and   p.word_id in (:wordIds)
order by p.word_id,
         p.id,
         f.order_by,
         f.id;

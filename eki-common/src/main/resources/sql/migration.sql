-- morfofonoloogiline kuju
alter table word add column morphophono_form text null;
create index word_morphophono_form_idx on word(morphophono_form);
create index word_morphophono_form_lower_idx on word(lower(morphophono_form));

update word w
set morphophono_form = wmpf.morphophono_form
from (select p.word_id, f.display_form morphophono_form
      from form f,
           paradigm p
      where f.paradigm_id = p.id
        and f.morph_code in ('SgN', 'Sup', 'ID')
      group by p.word_id, f.display_form) wmpf
where w.id = wmpf.word_id
  and w.lang = 'est';

update word w
set morphophono_form = wmpf.morphophono_form
from (select p.word_id, f.display_form morphophono_form
      from form f,
           paradigm p
      where f.paradigm_id = p.id
        and f.morph_code in ('sing,nomn', 'masc,sing,nomn', 'INFN', 'ID')
      group by p.word_id, f.display_form) wmpf
where w.id = wmpf.word_id
  and w.lang = 'rus';

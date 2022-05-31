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

-- ÕS soovitab kuupäev
update freeform ff
set modified_on = ffmod.modified_on
from (select id freeform_id, to_date((regexp_matches(value_text, '\d\d\.\d\d\.20\d\d'))[1], 'dd.mm.yyyy') modified_on
      from freeform
      where type like 'OD_%'
        and value_text like '% (__.__.20__)%'
        and modified_on is null) ffmod
where ff.id = ffmod.freeform_id;

update freeform
set value_text = regexp_replace(value_text, '\s\(\d\d\.\d\d\.20\d\d\)', '')
where type like 'OD_%';

update freeform
set value_prese = regexp_replace(value_prese, '\s\(\d\d\.\d\d\.20\d\d\)', '')
where type like 'OD_%';

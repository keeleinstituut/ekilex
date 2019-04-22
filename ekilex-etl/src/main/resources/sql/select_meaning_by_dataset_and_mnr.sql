select (select array_agg(mnr.dataset_code)
       from meaning_nr mnr
       where mnr.meaning_id = m.id
       group by mnr.meaning_id) mnr_dataset_codes,
       m.*
from meaning m
where exists (select l.id
              from lexeme l
              where l.meaning_id = m.id
              and   l.dataset_code = :dataset)
and   exists (select mnr.id
              from meaning_nr mnr
              where mnr.meaning_id = m.id
              and   mnr.dataset_code = :dataset
              and   mnr.mnr = :mnr)

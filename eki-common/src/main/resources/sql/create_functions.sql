create type type_word_hom_nr_data_tuple as (word_id bigint, homonym_nr integer);

create or replace function adjust_homonym_nrs()
  returns void
  language plpgsql
as $$
declare
  ordered_homonym_nrs_str_pattern text := array_to_string(array(select generate_series(1, 100)), '-', '');
  word_row                        record;
  adj_word_ids                    type_word_hom_nr_data_tuple;
  homonym_nr_iter                 integer;
begin
  for word_row in
    (select w.value,
            w.lang,
            w.word_ids
     from (select w.value,
                  w.lang,
                  array_agg(row (w.id, w.homonym_nr)::type_word_hom_nr_data_tuple order by w.ds_order_by, w.af_order_by, w.homonym_nr, w.id) word_ids,
                  array_to_string(array_agg(w.homonym_nr order by w.ds_order_by, w.af_order_by, w.homonym_nr), '-', '') homonym_nrs_str
           from (select w.id,
                        w.value,
                        w.homonym_nr,
                        w.lang,
                        (select case
                                  when count(l.id) > 0 then 1
                                  else 2
                                  end
                         from lexeme l
                         where l.word_id = w.id and l.dataset_code = 'eki') ds_order_by,
                        (select case
                                  when count(wt.id) > 0 then 2
                                  else 1
                                  end
                         from word_word_type wt
                         where wt.word_id = w.id and wt.word_type_code in ('pf', 'sf')) af_order_by
                 from word w
                 where exists(select l.id from lexeme l where l.word_id = w.id)) w
           group by w.value,
                    w.lang) w
     where ordered_homonym_nrs_str_pattern not like w.homonym_nrs_str || '%'
     order by w.lang, w.value)
    loop
      homonym_nr_iter := 1;
      foreach adj_word_ids in array word_row.word_ids
        loop
          if homonym_nr_iter != adj_word_ids.homonym_nr then
            update word set homonym_nr = homonym_nr_iter where id = adj_word_ids.word_id;
          end if;
          homonym_nr_iter := homonym_nr_iter + 1;
        end loop;
    end loop;
end $$;
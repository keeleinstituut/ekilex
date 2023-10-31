-- Kasutusnäidete allikaviidete puuduvate väärtuste taastamine
update freeform_source_link ffsl
set value = s.source_name
from (select sff.source_id,
             array_to_string(array_agg(distinct snff.value_text), ', ', '*') source_name
      from source_freeform sff,
           freeform snff
      where sff.freeform_id = snff.id
        and snff.type = 'SOURCE_NAME'
      group by sff.source_id) s
where ffsl.value is null
  and ffsl.source_id = s.source_id;

-- Etümoloogia topelt kirjete ühendamine
insert into etymology_type (code, datasets) values ('ühtlustamisel', '{}');
insert into tag (name, type) values ('etümoloogia üle vaadata', 'LEX');

do $$
declare
  word_etym_row             record;
  duplicate_word_etym_row   record;
  etymology_type_codes      varchar(100)[];
  word_etym_comment         text;
  word_etym_comment_prese   text;
  is_word_etym_questionable boolean;
  etym_word_first_lexeme_id bigint;
begin
  for word_etym_row in
    (select we.id,
            we.word_id,
            we.etymology_type_code,
            we.comment,
            we.comment_prese,
            we.is_questionable
     from word_etymology we
     where exists (select we2.id
                   from word_etymology we2
                   where we2.word_id = we.word_id
                     and we2.order_by > we.order_by)
       and not exists(select we3.id
                      from word_etymology we3
                      where we3.word_id = we.word_id
                        and we3.order_by < we.order_by))
    loop
      etymology_type_codes := array []::text[];
      word_etym_comment = word_etym_row.comment;
      word_etym_comment_prese = word_etym_row.comment_prese;
      is_word_etym_questionable = word_etym_row.is_questionable;

      if word_etym_row.etymology_type_code is not null then
        etymology_type_codes := array_append(etymology_type_codes, word_etym_row.etymology_type_code);
      end if;

      for duplicate_word_etym_row in
        (select we.id,
                we.word_id,
                we.etymology_type_code,
                we.comment,
                we.comment_prese,
                we.is_questionable
         from word_etymology we
         where word_etym_row.word_id = we.word_id
           and word_etym_row.id != we.id)
        loop

          if duplicate_word_etym_row.comment is not null then
            word_etym_comment := concat(word_etym_comment, '; ', duplicate_word_etym_row.comment);
            word_etym_comment_prese := concat(word_etym_comment_prese, '; ', duplicate_word_etym_row.comment_prese);
          end if;

          if duplicate_word_etym_row.etymology_type_code is not null then
            if duplicate_word_etym_row.etymology_type_code != any (etymology_type_codes) then
              etymology_type_codes := array_append(etymology_type_codes, duplicate_word_etym_row.etymology_type_code);
            end if;
          end if;

          if duplicate_word_etym_row.is_questionable is true then
            is_word_etym_questionable := true;
          end if;

          delete from word_etymology where id = duplicate_word_etym_row.id;
        end loop;

      if array_length(etymology_type_codes, 1) = 1 then
        update word_etymology set etymology_type_code = etymology_type_codes[1] where id = word_etym_row.id;
      end if;

      if array_length(etymology_type_codes, 1) > 1 then
        word_etym_comment := concat(word_etym_comment, '; Päritolu liigid: ', array_to_string(etymology_type_codes, ', '));
        word_etym_comment_prese := concat(word_etym_comment_prese, '; Päritolu liigid: ', array_to_string(etymology_type_codes, ', '));
        update word_etymology set etymology_type_code = 'ühtlustamisel' where id = word_etym_row.id;
      end if;

      update word_etymology set comment = word_etym_comment where id = word_etym_row.id;
      update word_etymology set comment_prese = word_etym_comment_prese where id = word_etym_row.id;
      update word_etymology set is_questionable = is_word_etym_questionable where id = word_etym_row.id;

      select l.id
      from lexeme l, dataset d
      where l.word_id = word_etym_row.word_id and l.dataset_code = d.code
      order by d.order_by, l.level1, l.level2
      limit 1
      into etym_word_first_lexeme_id;

      insert into lexeme_tag (lexeme_id, tag_name) values (etym_word_first_lexeme_id, 'etümoloogia üle vaadata');
    end loop;
end $$;

-- Vormi migra (päringud võivad olla aeganõudvad)
create table paradigm_form
(
  id bigserial primary key,
  paradigm_id bigint references paradigm(id) on delete cascade not null,
  form_id bigint references form(id) on delete cascade not null,
  order_by bigserial
);
alter sequence paradigm_form_id_seq restart with 10000;

create index paradigm_form_paradigm_id_idx on paradigm_form(paradigm_id);
create index paradigm_form_form_id_idx on paradigm_form(form_id);
create index form_display_form_idx on form(display_form);
create index form_display_level_idx on form(display_level);

insert into paradigm_form (paradigm_id, form_id)
select p.id, f.id
from paradigm p, form f
where p.id = f.paradigm_id
order by p.word_id, p.id, f.order_by, f.id;

update paradigm_form pf
set form_id = ff.form1_id
from (select f1.id form1_id,
             f2.id form2_id
      from form f1,
           form f2,
           paradigm_form pf1,
           paradigm_form pf2,
           paradigm p1,
           paradigm p2
      where pf1.form_id = f1.id
        and pf1.paradigm_id = p1.id
        and pf2.form_id = f2.id
        and pf2.paradigm_id = p2.id
        and p1.word_id = p2.word_id
        and f1.id < f2.id
        and f1.display_level = f2.display_level
        and f1.morph_code = f2.morph_code
        and f1.value = f2.value
        and f1.display_form = f2.display_form
        and not exists(select f3.id
                       from form f3,
                            paradigm_form pf3,
                            paradigm p3
                       where pf3.form_id = f3.id
                         and pf3.paradigm_id = p3.id
                         and p3.word_id = p1.word_id
                         and f3.id < f1.id
                         and f3.display_level = f1.display_level
                         and f3.morph_code = f1.morph_code
                         and f3.value = f1.value
                         and f3.display_form = f1.display_form)) ff
where pf.form_id = ff.form2_id;

delete
from form f
where not exists (select pf.id
                  from paradigm_form pf
                  where pf.form_id = f.id);

alter table form drop column paradigm_id cascade, drop column order_by cascade;

-- Töömahu raporti jaoks logimise täiendamine
-- (edaspidi skript kõrvaldab fk reference constrainti)
alter table activity_log add column dataset_code varchar(10) references dataset(code) null;
create index activity_log_dataset_code_idx on activity_log(dataset_code);
-- alter table activity_log drop constraint activity_log_dataset_code_fkey;

-- word_relation_param rikutud andmete taastamine
delete
from word_relation_param wrp1
where exists (select wrp2.id
              from word_relation_param wrp2
              where wrp1.id = wrp2.id
                and wrp1.word_relation_id = wrp2.word_relation_id
                and wrp1.name = wrp2.name
                and wrp1.value = wrp2.value
                and wrp1.ctid > wrp2.ctid);

alter table word_relation_param add constraint word_relation_param_pkey primary key (id);
alter table word_relation_param add constraint word_relation_param_word_relation_id_name_key unique (word_relation_id, name);

-- tõlkevastete vaate kaudu EKI sõnakogusse mitteavalike keelendite loomise piirang ja andmete parandus
update eki_user_profile
set preferred_full_syn_candidate_dataset_code = 'ing'
where preferred_full_syn_candidate_dataset_code = 'eki';

update word w
set is_public = true,
    homonym_nr = 1
where w.is_public is false
  and w.homonym_nr = 0
  and exists (select l.id
              from lexeme l
              where l.word_id = w.id
                and l.dataset_code = 'eki'
                and l.is_public is true);

update lexeme l
set dataset_code = 'ing'
where l.is_public is false
  and l.dataset_code = 'eki'
  and exists (select w.id
              from word w
              where w.id = l.word_id
                and w.is_public is false);

-- Üleliigsete ekilexi laienduste kustutamine
drop extension dblink cascade;
drop extension pg_stat_statements cascade;
drop extension pg_trgm cascade;
drop extension unaccent cascade;

-- Keelendite ühendamise tõttu muutunud militerm ilmikute avalikkuse taastamine
insert into tag (name, type) values ('mil muudetud mitteavalikuks', 'LEX');

insert into lexeme_tag (lexeme_id, tag_name)
select l1.id, 'mil muudetud mitteavalikuks'
from activity_log al,
     lexeme l1
where al.funct_name = 'joinWords'
  and al.owner_id = l1.word_id
  and l1.dataset_code = 'mil'
  and l1.is_public = true
  and exists (select l2.id
              from lexeme l2
              where l2.meaning_id = l1.meaning_id
                and l2.dataset_code = 'mil'
                and l2.is_public = false)
group by l1.id;

update lexeme l
set is_public = false
where exists(select lt.id
             from lexeme_tag lt
             where lt.lexeme_id = l.id
               and lt.tag_name = 'mil muudetud mitteavalikuks');

-- Allikate täiendus
alter table source
add column name text null,
add column description text null,
add column comment text null,
add column is_public boolean not null default true;

create index source_name_idx on source(name);
create index source_name_lower_idx on source(lower(name));

-- tarneks kõrvalda fk constrainti loomine üldse. siis pole järgmist vaja
alter table activity_log drop constraint activity_log_dataset_code_fkey;

-- keelekoodide parandused
insert into "language" (code, datasets, order_by) select 'nds', datasets, order_by from "language" where code = 'qab';
insert into "language" (code, datasets, order_by) select 'grn', datasets, order_by from "language" where code = 'gug';
insert into "language" (code, datasets, order_by) select 'kbd', datasets, order_by from "language" where code = 'kab';
insert into "language" (code, datasets, order_by) select 'cre', datasets, order_by from "language" where code = 'aem';
insert into "language" (code, datasets, order_by) select 'orv', datasets, order_by from "language" where code = 'qbi';

update language_label set code = 'nds' where code = 'qab';
update language_label set code = 'grn' where code = 'gug';
update language_label set code = 'kbd' where code = 'kab';
update language_label set code = 'cre' where code = 'aem';
update language_label set code = 'orv' where code = 'qbi';

update language_label set value = code where code = 'nds' and "type" = 'iso2';
update language_label set value = 'gn' where code = 'grn' and "type" = 'iso2';
update language_label set value = code where code = 'kbd' and "type" = 'iso2';
update language_label set value = 'cr' where code = 'cre' and "type" = 'iso2';
update language_label set value = code where code = 'orv' and "type" = 'iso2';
update language_label set value = code where value in ('ps', 'lt', 'lu', 've') and "type" = 'iso2';

update word set lang = 'nds' where lang = 'qab';
update word set lang = 'grn' where lang = 'gug';
update word set lang = 'kbd' where lang = 'kab';
update word set lang = 'cre' where lang = 'aem';
update word set lang = 'orv' where lang = 'qbi';

update freeform set lang = 'nds' where lang = 'qab';
update freeform set lang = 'grn' where lang = 'gug';
update freeform set lang = 'kbd' where lang = 'kab';
update freeform set lang = 'cre' where lang = 'aem';
update freeform set lang = 'orv' where lang = 'qbi';

delete from "language" where code in ('qab', 'gug', 'kab', 'aem', 'qbi');

-- Loo uuesti ekilexi baasi tüübid (types) ja vaated (views)
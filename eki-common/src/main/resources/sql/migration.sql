
-- #1 --

alter table lexeme_tag add column created_by text null;

-- Olemasolevate andmete täiendamine - suurusjärgud on indikatiivsed ja ajas muutuvad

-- ca 68000
update lexeme_tag lt
set created_by = al.event_by
from activity_log al
where
	al.entity_id = lt.id
    and al.entity_name = 'TAG'
    and al.funct_name = 'createLexemeTag'
    and lt.created_by is null;

-- ca 17000
with activity_word_lex_tag_complete_matches as (
	select distinct on (lt.id)
		lt.id as lexeme_tag_id,
		al.event_by
	from
		lexeme_tag lt,
		lexeme_activity_log lal,
		activity_log al
	where
		lt.lexeme_id = lal.lexeme_id
		and lal.activity_log_id = al.id
		and al.funct_name = 'updateWordLexemesTagComplete'
		and al.owner_name = 'WORD'
		and al.entity_name = 'TAG'
		and al.entity_id = -1
		and al.event_on >= lt.created_on
		and al.event_on < lt.created_on + interval '5 seconds'
		and exists (
			select 1
			from unnest(al.curr_diffs) cd
			where cd.op in ('add', 'replace') and cd.value = lt.tag_name)
	order by lt.id, al.event_on asc
)
update lexeme_tag lt
set created_by = awltc.event_by
from activity_word_lex_tag_complete_matches awltc
where
	lt.id = awltc.lexeme_tag_id
    and lt.created_by is null;

-- ca 1000
with activity_lex_tag_create_matches as (
	select distinct on (lt.id)
		lt.id as lexeme_tag_id,
		al.event_by
	from
		lexeme_tag lt,
		lexeme_activity_log lal,
		activity_log al
	where
		lt.lexeme_id = lal.lexeme_id
		and lal.activity_log_id = al.id
		and al.funct_name = 'create'
		and al.owner_name = 'LEXEME'
		and al.entity_name = 'TAG'
		and al.entity_id = -1
		and al.event_on >= lt.created_on
		and al.event_on < lt.created_on + interval '5 seconds'
		and exists (
			select 1
			from unnest(al.curr_diffs) cd
			where cd.op in ('add') and cd.value = lt.tag_name)
	order by lt.id, al.event_on asc
)
update lexeme_tag lt
set created_by = altc.event_by
from activity_lex_tag_create_matches altc
where
	lt.id = altc.lexeme_tag_id
	and lt.created_by is null;

-- ca 211594
update lexeme_tag
set created_by = 'Laadur'
where
	created_on between '2020-08-03 12:00' and '2020-08-03 13:00'
	and created_by is null;

-- ca 301518
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name = 'Kollide kolimine'
	and created_on between '2024-07-18 16:00' and '2024-07-18 22:00'
	and created_by is null;

-- ca 2658
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name = 'tundmatu koll, 0 homon'
	and created_on between '2020-12-16 21:00' and '2020-12-16 22:00'
	and created_by is null;

-- ca 11576
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name = 'termin'
	and created_on between '2021-01-18 00:00' and '2021-01-18 01:00'
	and created_by is null;

-- ca 1931
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name in ('arhiveeritud', 'töös')
	and created_on between '2022-06-20 04:00' and '2022-06-20 05:00'
	and created_by is null;

-- ca 679
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name = 'mil muudetud mitteavalikuks'
	and created_on between '2023-10-04 12:00' and '2023-10-04 13:00'
	and created_by is null;

-- ca 358
update lexeme_tag
set created_by = 'Laadur'
where
	tag_name = 'etümoloogia üle vaadata'
	and created_on between '2023-12-08 10:00' and '2023-12-08 11:00'
	and created_by is null;

-- ca 100
update lexeme_tag
set created_by = 'n/a'
where created_by is null;

-- #2 --

create table text_content (
	id bigserial primary key,
	name text not null,
	lang char(3) references language(code) not null,
	value text not null,
	unique (name, lang)
);
alter sequence text_content_id_seq restart with 10000;

create index text_content_lang_idx on text_content(lang);

insert into text_content (name, lang, value) values ('report.description.term_dataset', 'est', 'Terminikogude raport annab ülevaate terminikogude mahust ja kvaliteedist. Näidatakse mõistete, definitsioonide, terminite jne muudatusi määratud ajavahemikul (algus- ja lõpukuupäev on kaasa arvatud) ja hetkeseisu raporti koostamise ajal. Raporti koostamiseks on vajalik terminikogu muutmise õigus.');
insert into text_content (name, lang, value) values ('report.description.term_dataset', 'eng', 'The terminology collection report provides an overview of the size and quality of terminology collections. It shows changes to concepts, definitions, terms, and other data during the selected time period (both the start and end dates are inclusive), as well as the current state of the collection at the time the report is generated. Generating the report requires permission to modify the terminology collection.');
insert into text_content (name, lang, value) values ('report.description.syn_work', 'est', 'Raport annab ülevaate sellest, kui mitmele EKI ühendsõnastiku keelendile on eri kasutajad määratud ajavahemikus lisanud ilmiku sildi "süno valmis". Algus- ja lõpukuupäev on kaasa arvatud.');
insert into text_content (name, lang, value) values ('report.description.syn_work', 'eng', 'The report provides an overview of how many words in the EKI Combined Dictionary have been assigned the lexeme tag "süno valmis" by different users during the selected time period. Both the start and end dates are inclusive.');

-- #3 --

delete
from
	language_label ll
where
	ll."type" = 'iso2'
	and ll.lang = 'est'
	and ll.code = ll.value
;




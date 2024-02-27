create index lexeme_tag_tag_name_lower_idx on lexeme_tag(lower(tag_name));
create index meaning_tag_tag_name_lower_idx on meaning_tag(lower(tag_name));

insert into domain (code, origin, datasets) values ('ajal', 'har', '{}');
insert into domain (code, origin, datasets) values ('alus', 'har', '{}');
insert into domain (code, origin, datasets) values ('andr', 'har', '{}');
insert into domain (code, origin, datasets) values ('did', 'har', '{}');
insert into domain (code, origin, datasets) values ('erip', 'har', '{}');
insert into domain (code, origin, datasets) values ('filos', 'har', '{}');
insert into domain (code, origin, datasets) values ('huvi', 'har', '{}');
insert into domain (code, origin, datasets) values ('info', 'har', '{}');
insert into domain (code, origin, datasets) values ('isik', 'har', '{}');
insert into domain (code, origin, datasets) values ('kasv', 'har', '{}');
insert into domain (code, origin, datasets) values ('kord', 'har', '{}');
insert into domain (code, origin, datasets) values ('kutse', 'har', '{}');
insert into domain (code, origin, datasets) values ('kõrg', 'har', '{}');
insert into domain (code, origin, datasets) values ('org', 'har', '{}');
insert into domain (code, origin, datasets) values ('psühh', 'har', '{}');
insert into domain (code, origin, datasets) values ('sope', 'har', '{}');
insert into domain (code, origin, datasets) values ('sots', 'har', '{}');
insert into domain (code, origin, datasets) values ('tead', 'har', '{}');
insert into domain (code, origin, datasets) values ('vah', 'har', '{}');
insert into domain_label (code, origin, value, lang, type) values ('ajal', 'har', 'pedagoogika (kasvatuse, hariduse) ajalugu', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('alus', 'har', 'alusharidus (koolieelne haridus)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('andr', 'har', 'andragoogika (täiskasvanuharidus; täiendusõpe)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('did', 'har', 'didaktika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('erip', 'har', 'eripedagoogika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('filos', 'har', 'filosoofia', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('huvi', 'har', 'huviharidus', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('info', 'har', 'informaatika, e-õpe', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('isik', 'har', 'isikud', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kasv', 'har', 'kasvatus ja areng (kasvatusmeetodid)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kord', 'har', 'koolikorraldus (hariduspoliitika, haridussüsteem, asutused)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kutse', 'har', 'kutseharidus', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kõrg', 'har', 'kõrgharidus', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('org', 'har', 'organisatsioonid, institutsioonid, sh allüksused (nt metoodikakabinet)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('psühh', 'har', 'psühholoogia, loogika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('sope', 'har', 'sotsiaalpedagoogika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('sots', 'har', 'haridussotsioloogia', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('tead', 'har', 'kasvatusteadus (uurimistööd jm)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('vah', 'har', 'õppevahendid (sh ruumid)', 'est', 'descrip');

insert into domain (code, origin, datasets) values ('arv', 'has', '{}');
insert into domain (code, origin, datasets) values ('foto', 'has', '{}');
insert into domain (code, origin, datasets) values ('kino', 'has', '{}');
insert into domain (code, origin, datasets) values ('kirj', 'has', '{}');
insert into domain (code, origin, datasets) values ('koll', 'has', '{}');
insert into domain (code, origin, datasets) values ('kunst', 'has', '{}');
insert into domain (code, origin, datasets) values ('käsi', 'has', '{}');
insert into domain (code, origin, datasets) values ('loome', 'has', '{}');
insert into domain (code, origin, datasets) values ('muus', 'has', '{}');
insert into domain (code, origin, datasets) values ('nupu', 'has', '{}');
insert into domain (code, origin, datasets) values ('selts', 'has', '{}');
insert into domain (code, origin, datasets) values ('sport', 'has', '{}');
insert into domain (code, origin, datasets) values ('tants', 'has', '{}');
insert into domain (code, origin, datasets) values ('teat', 'has', '{}');
insert into domain (code, origin, datasets) values ('tehn', 'has', '{}');
insert into domain (code, origin, datasets) values ('terv', 'has', '{}');
insert into domain (code, origin, datasets) values ('vaba', 'has', '{}');
insert into domain (code, origin, datasets) values ('video', 'has', '{}');
insert into domain (code, origin, datasets) values ('üld', 'has', '{}');
insert into domain_label (code, origin, value, lang, type) values ('arv', 'has', 'arvuti', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('foto', 'has', 'fotograafia', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kino', 'has', 'kino (amatöör-, animatsioon)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kirj', 'has', 'kirjandus', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('koll', 'has', 'kollektsioneerimine', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('kunst', 'has', 'kujutav kunst', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('käsi', 'has', 'käsitöö', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('loome', 'has', 'loometöö (nt luulelaagrid)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('muus', 'has', 'muusika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('nupu', 'has', 'nuputamine (ristsõnad, sudokud, pusled, alias jne)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('selts', 'has', 'seltskondlikud mängud (lauamängud)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('sport', 'has', 'sport (tervise-, võistlus-, ekstreem-) ja võitluskunstid', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('tants', 'has', 'tants (hiphop, ballett jne)', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('teat', 'has', 'teater', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('tehn', 'has', 'tehnika', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('terv', 'has', 'tervis', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('vaba', 'has', 'koolieelsed vabamängud', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('video', 'has', 'video- ja rollimängud', 'est', 'descrip');
insert into domain_label (code, origin, value, lang, type) values ('üld', 'has', 'üldmõisted (asutused, meetodid)', 'est', 'descrip');

-- morfoloogia muudatused

alter table paradigm_form
	add column morph_group1 text null,
	add column morph_group2 text null,
	add column morph_group3 text null,
	add column display_level integer not null default 1,
	add column display_form varchar(255) null,
	add column audio_file varchar(255) null,
	add column morph_exists boolean not null default true,
	add column is_questionable boolean not null default false;

update paradigm_form pf set
	morph_group1 = f.morph_group1,
	morph_group2 = f.morph_group2,
	morph_group3 = f.morph_group3,
	display_level = f.display_level,
	display_form = f.display_form,
	audio_file = f.audio_file,
	morph_exists = f.morph_exists,
	is_questionable = f.is_questionable
from
	form f
where
	f.id = pf.form_id;

create index paradigm_form_display_form_idx on paradigm_form(display_form);
create index paradigm_form_display_level_idx on paradigm_form(display_level);

alter table form
	drop column morph_group1 cascade,
	drop column morph_group2 cascade,
	drop column morph_group3 cascade,
	drop column display_level cascade,
	drop column display_form cascade,
	drop column audio_file cascade,
	drop column morph_exists cascade,
	drop column is_questionable cascade,
	drop column components cascade;

update
	paradigm_form pf
set
	form_id = ff.form1_id
from
	(
	select
		f1.id form1_id,
		f2.id form2_id
	from
		form f1,
		form f2,
		paradigm_form pf1,
		paradigm_form pf2,
		paradigm p1,
		paradigm p2
	where
		pf1.form_id = f1.id
		and pf1.paradigm_id = p1.id
		and pf2.form_id = f2.id
		and pf2.paradigm_id = p2.id
		and p1.word_id = p2.word_id
		and f1.id < f2.id
		and f1.morph_code = f2.morph_code
		and f1.value = f2.value
		and f1.value_prese is not null
		and not exists(
			select
				f3.id
			from
				form f3,
				paradigm_form pf3,
				paradigm p3
			where
				pf3.form_id = f3.id
				and pf3.paradigm_id = p3.id
				and p3.word_id = p1.word_id
				and f3.id < f1.id
				and f3.morph_code = f1.morph_code
				and f3.value = f1.value)) ff
where
	pf.form_id = ff.form2_id;

delete
from
	form f
where
	not exists (
		select
			pf.id
		from
			paradigm_form pf
		where
			pf.form_id = f.id);


-- upgrade from ver 1.47.* to 1.48.0

-- #1 --

delete from language_group;

delete from language where code in ('aka', 'qbq', 'qbr', 'phn', 'cel', 'gml', 'ito', 'pro', 'und');

insert into language (code, datasets) values ('aka', '{eki, ety}');
insert into language (code, datasets) values ('qbq', '{eki, ety}');
insert into language (code, datasets) values ('qbr', '{eki, ety}');
insert into language (code, datasets) values ('phn', '{eki, ety}');
insert into language (code, datasets) values ('cel', '{eki, ety}');
insert into language (code, datasets) values ('gml', '{eki, ety}');
insert into language (code, datasets) values ('ito', '{eki, ety}');
insert into language (code, datasets) values ('pro', '{eki, ety}');
insert into language (code, datasets) values ('und', '{eki, ety}');

insert into language_label (code, value, lang, type) values ('aka', 'akani keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('qbq', 'Ameerika hispaania keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('qbr', 'Ameerika inglise keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('phn', 'foiniikia keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('cel', 'keldi keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('gml', 'keskalamsaksa keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('ito', 'vanaitaalia keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('pro', 'vanaprovansi keel', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('und', 'määramata', 'est', 'descrip');

insert into language_label (code, value, lang, type) values ('aka', 'akani keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('qbq', 'Ameerika hispaania keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('qbr', 'Ameerika inglise keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('phn', 'foiniikia keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('cel', 'keldi keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('gml', 'keskalamsaksa keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('ito', 'vanaitaalia keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('pro', 'vanaprovansi keel', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('und', 'määramata', 'est', 'wordweb');

insert into language_group (name) values ('Aafrika keeled');
insert into language_group (name) values ('Austraalia keeled');
insert into language_group (name) values ('India keeled');
insert into language_group (name) values ('Indoneesia keeled');
insert into language_group (name) values ('Lääne-Aafrika keeled');
insert into language_group (name) values ('Paapua keeled');
insert into language_group (name) values ('Polüneesia keeled');
insert into language_group (name) values ('Skandinaavia keeled');
insert into language_group (name) values ('Uurali keeled');
insert into language_group (name) values ('aarja keeled');
insert into language_group (name) values ('algonkini keeled');
insert into language_group (name) values ('balti keeled');
insert into language_group (name) values ('bantu keeled');
insert into language_group (name) values ('berberi keeled');
insert into language_group (name) values ('eesti-liivi kiht');
insert into language_group (name) values ('eesti-soome kiht');
insert into language_group (name) values ('eesti-vadja kiht');
insert into language_group (name) values ('eskimo keeled');
insert into language_group (name) values ('germaani keeled');
insert into language_group (name) values ('hiina-tiibeti keeled');
insert into language_group (name) values ('indiaani keeled');
insert into language_group (name) values ('indoeuroopa keeled');
insert into language_group (name) values ('iraani keeled');
insert into language_group (name) values ('irokeesi keeled');
insert into language_group (name) values ('läänemeresoome keeled');
insert into language_group (name) values ('läänemeresoome-mordva kiht');
insert into language_group (name) values ('läänemeresoome-permi kiht');
insert into language_group (name) values ('läänemeresoome-saami kiht');
insert into language_group (name) values ('läänemeresoome-volga kiht');
insert into language_group (name) values ('romaani keeled');
insert into language_group (name) values ('semi keeled');
insert into language_group (name) values ('slaavi keeled');
insert into language_group (name) values ('soome-ugri keeled');
insert into language_group (name) values ('tiibeti keeled');
insert into language_group (name) values ('tunguusi-mandžu keeled');
insert into language_group (name) values ('tupii-guaranii keeled');
insert into language_group (name) values ('turgi keeled');

-- #2 --

delete from publishing where entity_name = 'meaning_relation';

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'meaning_relation',
	mr.id
from
	meaning_relation mr
where
	mr.meaning_rel_type_code != 'duplikaadikandidaat'
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mr.meaning1_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'meaning_relation'
			and p.entity_id = mr.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'meaning_relation',
	mr.id
from
	meaning_relation mr
where
	mr.meaning_rel_type_code != 'duplikaadikandidaat'
	and (
			(
				mr.meaning_rel_type_code = 'sarnane'
				and exists (
					select
						1
					from
						lexeme l
					where
						l.meaning_id = mr.meaning1_id
						and l.dataset_code = 'eki'
				)
			)
			or
			(
				mr.meaning_rel_type_code != 'sarnane'
				and exists (
					select
						1
					from
						lexeme l, publishing p
					where
						l.meaning_id = mr.meaning1_id
						and l.dataset_code = 'eki'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
						and p.target_name = 'ww_lite'
				)
				and exists (
					select
						1
					from
						lexeme l, publishing p
					where
						l.meaning_id = mr.meaning2_id
						and l.dataset_code = 'eki'
						and p.entity_name = 'lexeme'
						and p.entity_id = l.id
						and p.target_name = 'ww_lite'
				)
			)
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'meaning_relation'
			and p.entity_id = mr.id
	)
;

-- #3 --

create table report (
	id bigserial primary key,
	user_id bigint references eki_user(id) on delete cascade not null,
	type varchar(100) not null,
	status varchar(100) not null,
	content jsonb null,
	created_on timestamp not null default statement_timestamp(),
	completed_on timestamp null
);
alter sequence report_id_seq restart with 10000;

create index report_user_id_idx on report(user_id);
create index report_type_idx on report(type);

-- #4 --

alter index word_etym_word_id_idx rename to word_etymology_word_id_idx;
alter index word_etym_source_link_word_etym_id_idx rename to word_etymology_source_link_word_etym_id_idx;
alter index word_etym_source_link_source_id_idx rename to word_etymology_source_link_source_id_idx;

create table word_etym (
	id bigserial primary key, 
	word_id bigint references word(id) on delete cascade not null, 
	etymology_year text null,
	unique(word_id)
);
alter sequence word_etym_id_seq restart with 10000;

create index word_etym_word_id_idx on word_etym(word_id);

create table word_etym_comment (
	id bigserial primary key, 
	word_etym_id bigint references word_etym(id) on delete cascade not null,
	value text not null,
	value_prese text not null,
	orig_name text not null,
	order_by bigserial
);
alter sequence word_etym_comment_id_seq restart with 10000;

create index word_etym_comment_word_etym_id_idx on word_etym_comment(word_etym_id);

create table word_etym_note (
	id bigserial primary key, 
	word_etym_id bigint references word_etym(id) on delete cascade not null,
	value text not null,
	value_prese text not null,
	lang char(3) references language(code) not null,
	is_public boolean default true not null,
	created_by text null,
	created_on timestamp null, 
	modified_by text null, 
	modified_on timestamp null, 
	order_by bigserial
);
alter sequence word_etym_note_id_seq restart with 10000;

create index word_etym_note_word_etym_id_idx on word_etym_note(word_etym_id);

create table word_etym_source_link (
	id bigserial primary key, 
	word_etym_id bigint references word_etym(id) on delete cascade not null,
	source_id bigint references source(id) on delete cascade not null, 
	name text null, 
	value text null, 
	order_by bigserial
);
alter sequence word_etym_source_link_id_seq restart with 10000;

create index word_etym_source_link_word_etym_id_idx on word_etym_source_link(word_etym_id);
create index word_etym_source_link_source_id_idx on word_etym_source_link(source_id);

create table word_etym_group (
	id bigserial primary key, 
	group_type varchar(100) not null,
	etymology_type_code varchar(100) references etymology_type(code), 
	language_group_id bigint references language_group(id) on delete cascade,
	lang char(3) references language(code) on delete cascade,
	is_questionable boolean not null default false
);
alter sequence word_etym_group_id_seq restart with 10000;

create index word_etym_group_group_type_idx on word_etym_group(group_type);
create index word_etym_group_language_group_id_idx on word_etym_group(language_group_id);

create table word_etym_group_tree (
	id bigserial primary key,
	parent_word_etym_group_id bigint references word_etym_group(id) on delete cascade not null,
	child_word_etym_group_id bigint references word_etym_group(id) on delete cascade not null,
	order_by bigserial not null,
	unique(parent_word_etym_group_id, child_word_etym_group_id)
);
alter sequence word_etym_group_tree_id_seq restart with 10000;

create index word_etym_group_tree_parent_word_etym_group_id_idx on word_etym_group_tree(parent_word_etym_group_id);
create index word_etym_group_tree_child_word_etym_group_id_idx on word_etym_group_tree(child_word_etym_group_id);

create table word_etym_group_member (
	id bigserial primary key, 
	word_etym_group_id bigint references word_etym_group(id) on delete cascade not null,
	word_etym_id bigint references word_etym(id) on delete cascade not null,
	is_questionable boolean not null default false, 
	order_by bigserial not null
);
alter sequence word_etym_group_member_id_seq restart with 10000;

create index word_etym_group_member_word_etym_group_id_idx on word_etym_group_member(word_etym_group_id);
create index word_etym_group_member_word_etym_id_idx on word_etym_group_member(word_etym_id);

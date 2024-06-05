-- uudised

create table news_article (
	id bigserial primary key,
	created timestamp not null default statement_timestamp(),
	type varchar(100) not null,
	title text not null,
	lang char(3) references language(code) null
);

create index news_article_type_idx on news_article(type);
create index news_article_lang_idx on news_article(lang);

create table news_section (
	id bigserial primary key,
	news_article_id bigint not null references news_article(id) on delete cascade,
	content text not null
);

create index news_section_news_article_id_idx on news_section(news_article_id);

-- iso 2 keelekoodi fiks

update language_label set value = 'lt' where code = 'lit' and type = 'iso2';

-- kollokatsioonide kolimine

drop table collocation_freeform cascade;

create table rel_group
(
  code varchar(100) primary key,
  datasets varchar(10) array not null,
  order_by bigserial
);

create table rel_group_label
(
  code varchar(100) references rel_group(code) on delete cascade not null,
  value text not null,
  lang char(3) references language(code) not null,
  type varchar(10) references label_type(code) not null,
  unique(code, lang, type)
);

insert into rel_group (code, datasets)
(
	select
		name,
		'{}'
	from
		lex_colloc_rel_group
	group by
		name
	order by
		name
);

insert into rel_group_label (code, value, lang, type)
(
	select
		code,
		code,
		'est',
		'descrip'
	from 
		rel_group
	order by
		order_by
);

create table collocation_member (
	id bigserial primary key,
	colloc_lexeme_id bigint references lexeme(id) not null,
	member_lexeme_id bigint references lexeme(id) not null,
	member_form_id bigint references form(id) not null,
	pos_group_code varchar(100) references pos_group(code),
	rel_group_code varchar(100) references rel_group(code),
	conjunct varchar(100),
	weight numeric(14, 4),
	member_order integer not null,
	group_order integer,
	unique(colloc_lexeme_id, member_lexeme_id)
);
alter sequence collocation_member_id_seq restart with 10000;

create index collocation_member_colloc_lexeme_id_idx on collocation_member(colloc_lexeme_id);
create index collocation_member_member_lexeme_id_idx on collocation_member(member_lexeme_id);
create index collocation_member_member_form_id_idx on collocation_member(member_form_id);
create index collocation_member_pos_group_code_idx on collocation_member(pos_group_code);
create index collocation_member_rel_group_code_idx on collocation_member(rel_group_code);

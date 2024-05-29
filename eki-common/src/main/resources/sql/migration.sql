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

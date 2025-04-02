
-- NB! add extension fuzzystrmatch ??

-- kollokatsioonide kasutusnäidete taastamine

insert into "usage" (
	lexeme_id,
	value,
	value_prese,
	lang,
	complexity,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	nc.colloc_lexeme_id,
	oc.usage_value,
	oc.usage_value,
	'est',
	oc.complexity,
	true,
	'Kollide kolija',
	statement_timestamp(),
	'Kollide kolija',
	statement_timestamp()
from
	(
	select
		c.colloc_value,
		c.usage_value,
		c.member_lexeme_ids,
		c.complexity
	from
		(
		select
			c.id colloc_id,
			c.value colloc_value,
			unnest(c.usages) usage_value,
			(
			select
				array_agg(l.id order by l.id)
			from
				lex_colloc lc,
				lexeme l
			where
				lc.collocation_id = c.id
				and lc.lexeme_id = l.id) member_lexeme_ids,
			c.complexity
		from
			collocation c
		where
			c.usages is not null
		order by
			c.id
		) c
	group by
		c.colloc_value,
		c.usage_value,
		c.member_lexeme_ids,
		c.complexity
) oc
inner join (
	select
		w.id word_id,
		l.id colloc_lexeme_id,
		w.value colloc_value,
		array_agg(cml.id order by cml.id) member_lexeme_ids
	from
		word w,
		lexeme l,
		collocation_member cm,
		lexeme cml
	where
		l.word_id = w.id
		and l.is_collocation = true
		and cm.colloc_lexeme_id = l.id
		and cm.member_lexeme_id = cml.id
	group by
		w.id,
		l.id
) nc on
	nc.colloc_value = oc.colloc_value
	and nc.member_lexeme_ids = oc.member_lexeme_ids
where
	not exists (
		select
			u.id
		from
			"usage" u
		where
			u.lexeme_id = nc.colloc_lexeme_id
			and u.value = oc.usage_value
	);

-- allikaviidete parandus

update
	definition_source_link
set
	source_id = 19483,
	"name" = null
where
	source_id = 15800
	and "name" = 'Online'
;

update
	lexeme_source_link
set
	source_id = 19483,
	"name" = null
where
	source_id = 15800
	and "name" = 'Online'
;

-- obsoliitse allikaviidete tüübi likvideerimine

alter table freeform_source_link drop column "type" cascade;
alter table word_etymology_source_link drop column "type" cascade;
alter table meaning_note_source_link drop column "type" cascade;
alter table meaning_image_source_link drop column "type" cascade;
alter table definition_source_link drop column "type" cascade;
alter table definition_note_source_link drop column "type" cascade;
alter table lexeme_source_link drop column "type" cascade;
alter table usage_source_link drop column "type" cascade;
alter table lexeme_note_source_link drop column "type" cascade;

-- allika kommentaari otsing

create index source_comment_idx on source(comment);
create index source_comment_lower_idx on source(lower(comment));
create index source_comment_lower_prefix_idx on source(lower(comment) text_pattern_ops);

-- keelendi sildid

create table word_tag (
  id bigserial primary key, 
  word_id bigint references word(id) on delete cascade not null, 
  tag_name varchar(100) references tag(name) on delete cascade not null, 
  created_on timestamp not null default statement_timestamp(), 
  unique(word_id, tag_name)
);
alter sequence word_tag_id_seq restart with 10000;

create index word_tag_word_id_idx on word_tag(word_id);
create index word_tag_tag_name_idx on word_tag(tag_name);
create index word_tag_tag_name_lower_idx on word_tag(lower(tag_name));

-- ilma paradigmadeta vormidega kollokatsiooniliikmete kustutamine

delete
from 
	collocation_member cm
where 
	not exists (
		select
			1
		from
			form f,
			paradigm_form pf
		where
			f.id = cm.member_form_id 
			and f.id = pf.form_id
	)
;

-- keelenditite esitluskujus html asendamine märgendusega

update
	word
set
	value_prese = replace(value_prese, '<i>', '<eki-foreign>')
where
	value_prese like '%<i>%'
;

update
	word
set
	value_prese = replace(value_prese, '</i>', '</eki-foreign>')
where
	value_prese like '%</i>%'
;

-- kõigi keelte klassifikaator

insert into language (code, datasets) values ('mul', '{}');
insert into language_label (code, value, lang, type) values ('mul', 'mitmekeelne', 'est', 'descrip');
insert into language_label (code, value, lang, type) values ('mul', 'mitmekeelne', 'est', 'wordweb');
insert into language_label (code, value, lang, type) values ('mul', 'multiple languages', 'eng', 'descrip');
insert into language_label (code, value, lang, type) values ('mul', 'multiple languages', 'eng', 'wordweb');
update language set order_by = order_by + 2 where order_by > 1 and code not in ('eng', 'mul');
update language set order_by = 2 where code = 'eng';
update language set order_by = 3 where code = 'mul';

update
	language l
set
	datasets = (
		select
			array_agg(ds.code)
		from
			dataset ds
	)
where
	l.code = 'mul'
;

update
	language l
set
	order_by = ll.row_num
from
	(
	select
		lll.code,
		row_number() over (
		order by order_by asc) row_num
	from
		language lll
	order by
		lll.order_by
) ll
where
	l.code = ll.code
;

-- ÕS soovitused

delete from freeform where id = 2168187;

update freeform_type set code = 'WORD_OD_RECOMMENDATION'
where code = 'OD_WORD_RECOMMENDATION';

update activity_log set entity_name = 'WORD_OD_RECOMMENDATION'
where entity_name = 'OD_WORD_RECOMMENDATION';

create table word_od_recommendation (
  id bigserial primary key,
  word_id bigint references word(id) on delete cascade not null,
  value text not null, 
  value_prese text not null,
  opt_value text,
  opt_value_prese text,
  is_public boolean default true not null, 
  created_by text null, 
  created_on timestamp null, 
  modified_by text null, 
  modified_on timestamp null,
  unique(word_id)
);
alter sequence word_od_recommendation_id_seq restart with 10000;

create index word_od_recommendation_word_id_idx on word_od_recommendation(word_id);
create index word_od_recommendation_value_idx on word_od_recommendation(value);
create index word_od_recommendation_value_lower_idx on word_od_recommendation(lower(value));

insert into word_od_recommendation (
	word_id,
	value,
	value_prese,
	is_public,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	wf.word_id,
	f.value,
	f.value_prese,
	true,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	freeform f,
	word_freeform wf
where
	f.freeform_type_code = 'WORD_OD_RECOMMENDATION'
	and wf.freeform_id = f.id
order by f.id
;


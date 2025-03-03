
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



-- keelendite registreerimine --

update word w
set reg_year = rw.reg_year
from (
	select
		l.word_id,
		right(lr.register_code, 4)::int reg_year
	from
		lexeme l,
		lexeme_register lr
	where
		lr.lexeme_id = l.id
		and lr.register_code like 'uus%'
	group by
		l.word_id,
		lr.register_code
) rw
where
	w.id = rw.word_id
;

insert into register (code, datasets) values ('uus', '{}');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'uus', 'est', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'new', 'eng', 'wordweb');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'descrip');
insert into register_label (code, value, lang, type) values ('uus', 'новый', 'rus', 'wordweb');

insert into lexeme_register (lexeme_id, register_code)
select
	l.id,
	'uus'
from
	lexeme l
where
	exists (
		select
			1
		from
			lexeme_register lr
		where
			lr.lexeme_id = l.id
			and lr.register_code like 'uus20%'
	)
;

delete from lexeme_register where register_code like 'uus20%';
delete from register where code like 'uus20%';

-- kollokatsioonide väliskuju lekseemide detailsuse parandus --

update
	lexeme cl
set
	complexity = 'DETAIL'
where 
	cl.complexity = 'ANY'
	and cl.is_collocation = true
	and exists (
		select
			1
		from
			collocation_member cm,
			lexeme ml
		where
			cm.colloc_lexeme_id = cl.id
			and cm.member_lexeme_id = ml.id
			and ml.complexity = 'DETAIL'
		)
;

-- kollokatsioonide duplikaatide kustutamine --

alter table collocation_member
drop constraint collocation_member_colloc_lexeme_id_fkey,
add constraint collocation_member_colloc_lexeme_id_fkey foreign key (colloc_lexeme_id) references lexeme (id) on delete cascade;

-- !! 30 min !! --
delete
from
	lexeme l
where
	exists (
		select
			1
		from
			(
			select
				 (array_agg(cw1.colloc_lexeme_id order by cw1.colloc_lexeme_id))[1] orig_colloc_lexeme_id,
				 array_agg(cw1.colloc_lexeme_id order by cw1.colloc_lexeme_id) colloc_lexeme_ids
			from
				(
				select
					cl.id colloc_lexeme_id,
					cw.id colloc_word_id,
					cw.value,
					cl.complexity,
					(
					select
						array_agg(cm.member_lexeme_id order by cm.member_order)
					from
						collocation_member cm
					where
						cm.colloc_lexeme_id = cl.id
					) member_lexeme_ids,
					(
					select
						array_agg(cm.member_form_id order by cm.member_order)
					from
						collocation_member cm
					where
						cm.colloc_lexeme_id = cl.id
					) member_form_ids,
					(
					select
						coalesce(array_agg(u.value order by u.value), '{}')
					from 
						usage u
					where
						u.lexeme_id = cl.id
					) usage_values,
					(
					select
						coalesce(array_agg(d.value order by d.value), '{}')
					from
						definition d 
					where
						d.meaning_id = cl.meaning_id 
						and exists (
							select
								1
							from
								definition_dataset dd 
							where
								dd.definition_id = d.id
								and dd.dataset_code = 'eki'
						)
					) definition_values
				from
					word cw,
					lexeme cl
				where
					cl.word_id = cw.id
					and cl.is_collocation = true
					and cl.is_word = false
				) cw1
			where
				exists (
					select
						1
					from (
						select
							cl.id colloc_lexeme_id,
							cw.value,
							cl.complexity,
							(
							select
								array_agg(cm.member_lexeme_id order by cm.member_order)
							from
								collocation_member cm
							where
								cm.colloc_lexeme_id = cl.id
							) member_lexeme_ids,
							(
							select
								array_agg(cm.member_form_id order by cm.member_order)
							from
								collocation_member cm
							where
								cm.colloc_lexeme_id = cl.id
							) member_form_ids,
							(
							select
								coalesce(array_agg(u.value order by u.value), '{}')
							from 
								usage u
							where
								u.lexeme_id = cl.id
							) usage_values,
							(
							select
								coalesce(array_agg(d.value order by d.value), '{}')
							from
								definition d 
							where
								d.meaning_id = cl.meaning_id 
								and exists (
									select
										1
									from
										definition_dataset dd 
									where
										dd.definition_id = d.id
										and dd.dataset_code = 'eki'
								)
							) definition_values
						from
							word cw,
							lexeme cl
						where
							cl.word_id = cw.id
							and cl.is_collocation = true
							and cl.is_word = false
						) cw2
					where
						cw2.value = cw1.value
						and cw2.complexity = cw1.complexity
						and cw2.member_lexeme_ids = cw1.member_lexeme_ids
						and cw2.member_form_ids = cw1.member_form_ids
						and cw2.usage_values = cw1.usage_values
						and cw2.definition_values = cw1.definition_values
						and cw2.colloc_lexeme_id != cw1.colloc_lexeme_id
				)
			group by
				cw1.value,
				cw1.member_lexeme_ids,
				cw1.member_form_ids,
				cw1.usage_values,
				cw1.definition_values
			) dl
		where
			l.id = any (dl.colloc_lexeme_ids)
			and l.id != dl.orig_colloc_lexeme_id
	)
;

delete
from 
	word w 
where
	not exists (
		select
			1
		from
			lexeme l 
		where
			l.word_id = w.id
	)
;

-- vabavormidest kolimine --

insert into grammar (
	original_freeform_id,
	lexeme_id,
	value,
	value_prese,
	lang,
	complexity,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	lf.lexeme_id,
	f.value,
	f.value_prese,
	coalesce(f.lang, 'est'),
	f.complexity,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	lexeme_freeform lf,
	freeform f
where
	lf.freeform_id = f.id
	and f.freeform_type_code = 'GRAMMAR'
order by f.order_by;

insert into government (
	original_freeform_id,
	lexeme_id,
	value,
	complexity,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	lf.lexeme_id,
	f.value,
	f.complexity,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	lexeme_freeform lf,
	(
	select
		f1.id,
		f1.value,
		f1.complexity,
		f1.created_by,
		f1.created_on,
		f1.modified_by,
		f1.modified_on,
		f1.order_by
	from
		freeform f1
	where
		f1.freeform_type_code = 'GOVERNMENT'
	union all
	select
		f1.id,
		f2.value,
		f1.complexity,
		f1.created_by,
		f1.created_on,
		f1.modified_by,
		f1.modified_on,
		f2.order_by
	from
		freeform f1,
		freeform f2
	where
		f1.freeform_type_code = 'GOVERNMENT'
		and f2.parent_id = f1.id
		and f2.freeform_type_code in ('GOVERNMENT_OPTIONAL', 'GOVERNMENT_PLACEMENT', 'GOVERNMENT_VARIANT')
	) f
where
	lf.freeform_id = f.id
order by f.order_by;

insert into meaning_media (
	original_freeform_id,
	meaning_id,
	url,
	complexity,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	mf.meaning_id,
	f.value,
	f.complexity,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	meaning_freeform mf,
	freeform f
where
	mf.freeform_id = f.id
	and f.freeform_type_code = 'MEDIA_FILE'
order by f.order_by;

insert into learner_comment (
	original_freeform_id,
	meaning_id,
	value,
	value_prese,
	created_by,
	created_on,
	modified_by,
	modified_on)
select
	f.id,
	mf.meaning_id,
	f.value,
	f.value_prese,
	f.created_by,
	f.created_on,
	f.modified_by,
	f.modified_on
from
	meaning_freeform mf,
	freeform f
where
	mf.freeform_id = f.id
	and f.freeform_type_code = 'LEARNER_COMMENT'
order by f.order_by;

-- aegunud vabavormide kustutamine 

delete
from
	freeform f
where
	exists (
		select
			1
		from
			source_freeform sf
		where
			sf.freeform_id = f.id
	);

drop table source_freeform cascade;

delete
from
	freeform f
where
	f.freeform_type_code in (
	'GOVERNMENT',
	'GOVERNMENT_OPTIONAL',
	'GOVERNMENT_PLACEMENT',
	'GOVERNMENT_VARIANT',
	'GRAMMAR',
	'LEARNER_COMMENT',
	'SEMANTIC_TYPE',
	'MEDIA_FILE',
	'MEANING_IMAGE',
	'IMAGE_TITLE',
	'IMAGE_FILE',
	'NOTE',
	'USAGE',
	'USAGE_DEFINITION',
	'USAGE_TRANSLATION',
	'WORD_OD_RECOMMENDATION',
	'SYSTEMATIC_POLYSEMY_PATTERN',
	'SOURCE_EXPLANATION',
	'SOURCE_FILE');
	
delete
from 
	freeform_type ft 
where
	ft.code in (
	'GOVERNMENT',
	'GOVERNMENT_OPTIONAL',
	'GOVERNMENT_PLACEMENT',
	'GOVERNMENT_VARIANT',
	'GRAMMAR',
	'LEARNER_COMMENT',
	'SEMANTIC_TYPE',
	'MEDIA_FILE',
	'MEANING_IMAGE',
	'IMAGE_TITLE',
	'IMAGE_FILE',
	'NOTE',
	'USAGE',
	'USAGE_DEFINITION',
	'USAGE_TRANSLATION',
	'WORD_OD_RECOMMENDATION',
	'SYSTEMATIC_POLYSEMY_PATTERN');

delete
from 
	freeform_type ft 
where
	ft.code like 'SOURCE_%'
	and ft.code != 'SOURCE_NAME';
	
-- detailsuse kolimine publitseerimisse --

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'definition',
	d.id
from
	definition d
where
	d.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'definition',
	d.id
from
	definition d
where
	d.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = d.meaning_id 
			and l.complexity in ('ANY', 'SIMPLE')
	)
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_od',
	'definition',
	d.id
from
	definition d
where
	d.complexity = 'ANY'
	and d.is_public = false
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_od',
	'definition',
	d.id
from
	definition d
where
	exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'eki'
	)
	and exists (
		select
			1
		from
			definition_dataset dd
		where
			dd.definition_id = d.id
			and dd.dataset_code = 'ÕS-tehn'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'definition'
			and p.entity_id = d.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'word_relation',
	wr.id
from
	word_relation wr
where
	exists (
		select
			1
		from
			lexeme l
		where
			l.word_id = wr.word1_id 
			and l.complexity in ('ANY', 'DETAIL')
	)
	and exists (
		select
			1
		from
			lexeme l
		where
			l.word_id = wr.word2_id 
			and l.complexity in ('ANY', 'DETAIL')
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'word_relation'
			and p.entity_id = wr.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'word_relation',
	wr.id
from
	word_relation wr
where
	exists (
		select
			1
		from
			lexeme l
		where
			l.word_id = wr.word1_id 
			and l.dataset_code = 'eki'
			and l.complexity in ('ANY', 'SIMPLE')
	)
	and exists (
		select
			1
		from
			lexeme l
		where
			l.word_id = wr.word2_id 
			and l.dataset_code = 'eki'
			and l.complexity in ('ANY', 'SIMPLE')
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'word_relation'
			and p.entity_id = wr.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_od',
	'word_relation',
	wr.id
from
	word_relation wr
where
	exists (
		select
			1
		from
			lexeme l,
			lexeme_tag lt
		where
			l.word_id = wr.word1_id 
			and lt.lexeme_id = l.id
			and lt.tag_name = 'ÕSi liitsõna'
	)
	and exists (
		select
			1
		from
			lexeme l,
			lexeme_tag lt
		where
			l.word_id = wr.word2_id 
			and lt.lexeme_id = l.id
			and lt.tag_name = 'ÕSi liitsõna'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'word_relation'
			and p.entity_id = wr.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'lexeme',
	l.id
from
	lexeme l
where
	l.complexity in ('ANY', 'DETAIL')
	and l.dataset_code = 'eki'
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'lexeme',
	l.id
from
	lexeme l
where
	exists (
		select
			1
		from
			word_etymology we
		where
			we.word_id = l.word_id
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'lexeme',
	l.id
from
	lexeme l
where
	l.complexity in ('ANY', 'SIMPLE')
	and l.dataset_code = 'eki'
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_od',
	'lexeme',
	l.id
from
	lexeme l
where
	l.dataset_code = 'eki'
	and exists (
		select
			1
		from
			lexeme_tag lt
		where
			lt.lexeme_id = l.id
			and lt.tag_name = 'ÕSi sõna'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_od'
			and p.entity_name = 'lexeme'
			and p.entity_id = l.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'usage',
	u.id
from
	usage u
where
	u.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = u.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'usage'
			and p.entity_id = u.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'usage',
	u.id
from
	usage u
where
	u.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = u.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'usage'
			and p.entity_id = u.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'lexeme_note',
	ln.id
from
	lexeme_note ln
where
	ln.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = ln.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'lexeme_note'
			and p.entity_id = ln.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'lexeme_note',
	ln.id
from
	lexeme_note ln
where
	ln.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = ln.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'lexeme_note'
			and p.entity_id = ln.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'meaning_note',
	mn.id
from
	meaning_note mn
where
	mn.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mn.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'meaning_note'
			and p.entity_id = mn.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'meaning_note',
	mn.id
from
	meaning_note mn
where
	mn.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mn.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'meaning_note'
			and p.entity_id = mn.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'meaning_image',
	mi.id
from
	meaning_image mi
where
	mi.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mi.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'meaning_image'
			and p.entity_id = mi.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'meaning_image',
	mi.id
from
	meaning_image mi
where
	mi.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mi.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'meaning_image'
			and p.entity_id = mi.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'meaning_media',
	mm.id
from
	meaning_media mm
where
	mm.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mm.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'meaning_media'
			and p.entity_id = mm.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'meaning_media',
	mm.id
from
	meaning_media mm
where
	mm.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.meaning_id = mm.meaning_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'meaning_media'
			and p.entity_id = mm.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'grammar',
	g.id
from
	grammar g
where
	g.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = g.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'grammar'
			and p.entity_id = g.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'grammar',
	g.id
from
	grammar g
where
	g.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = g.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'grammar'
			and p.entity_id = g.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_unif',
	'government',
	g.id
from
	government g
where
	g.complexity in ('ANY', 'DETAIL')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = g.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_unif'
			and p.entity_name = 'government'
			and p.entity_id = g.id
	)
;

insert into publishing (event_by, target_name, entity_name, entity_id)
select
	'Laadur',
	'ww_lite',
	'government',
	g.id
from
	government g
where
	g.complexity in ('ANY', 'SIMPLE')
	and exists (
		select
			1
		from
			lexeme l
		where
			l.id = g.lexeme_id
			and l.dataset_code = 'eki'
	)
	and not exists (
		select
			1
		from
			publishing p
		where
			p.target_name = 'ww_lite'
			and p.entity_name = 'government'
			and p.entity_id = g.id
	)
;

analyze publishing;

-- obsoliitsete väljade likvideerimine --

alter table definition_note drop column complexity cascade;
alter table freeform drop column complexity cascade;
alter table freeform drop column is_public cascade;
alter table meaning_image drop column is_public cascade;

insert into eki_user (name, password) values ('Malle Paju', 'cdd78520affcda625a66a9c428327b81');

insert into word (id, value, components, lang, morph_code) values (1002, 'hall', '{hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1003, 'hallakord', '{halla, kord}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1004, 'hallaudu', '{halla, udu}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1005, 'hallaöö', '{halla, öö}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1006, 'hallasääsk', '{halla, sääsk}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1007, 'kaubahall', '{kauba, hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1008, 'linnahall', '{linna, hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1009, 'helehall', '{hele, hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1010, 'tumehall', '{tume, hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1011, 'hiirhall', '{hiir, hall}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1012, 'hiirjas', '{hiirjas}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1013, 'hiirukas', '{hiirukas}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1014, 'väär', '{väär}', 'est', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1015, 'заморозки', '{заморозки}', 'rus', 'PlN');
insert into word (id, value, components, lang, morph_code) values (1016, 'серый', '{серый}', 'rus', 'SgN');
insert into word (id, value, components, lang, morph_code) values (1017, 'холл', '{холл}', 'rus', 'SgN');


insert into meaning (id, dataset) values (2001, '{eos}');
insert into meaning (id, dataset) values (2002, '{eos}');
insert into meaning (id, dataset) values (2003, '{eos}');
insert into meaning (id, dataset) values (2004, '{ss_}');
insert into meaning (id, dataset) values (2005, '{ss_}');
insert into meaning (id, dataset) values (2006, '{ss_}');
insert into meaning (id, dataset) values (2007, '{ss_}');
insert into meaning (id, dataset) values (2008, '{ss_}');
insert into meaning (id, dataset) values (2009, '{ss_}');
insert into meaning (id, dataset) values (2010, '{ss_}');
insert into meaning (id, dataset) values (2011, '{ss_}');
insert into meaning (id, dataset) values (2012, '{sys}');
insert into meaning (id, dataset) values (2013, '{sys}');
insert into meaning (id, dataset) values (2014, '{evs}');
insert into meaning (id, dataset) values (2015, '{evs}');
insert into meaning (id, dataset) values (2016, '{evs}');
insert into meaning (id, dataset) values (2017, '{eos}');
insert into meaning (id, dataset) values (2018, '{eos}');
insert into meaning (id, dataset) values (2019, '{eos}');
insert into meaning (id, dataset) values (2020, '{eos}');
insert into meaning (id, dataset) values (2021, '{eos}');
insert into meaning (id, dataset) values (2022, '{eos}');
insert into meaning (id, dataset) values (2023, '{eos}');
insert into meaning (id, dataset) values (2024, '{eos}');
insert into meaning (id, dataset) values (2025, '{eos}');
insert into meaning (id, dataset) values (2026, '{eos}');
insert into meaning (id, dataset) values (2027, '{eos}');

insert into definition (meaning_id, value, dataset) values (2001, 'külmunud kaste maas', '{eos}');
insert into definition (meaning_id, value, dataset) values (2002, 'avar üldkasutatav hoone v ruum', '{eos}');
insert into definition (meaning_id, value, dataset) values (2004, 'temperatuuri langemisel alla 0 °C õhus olevast veeaurust tekkinud ebaühtlane jääkristallide kiht maapinnal, taimedel ja esemetel (hrl. kevadel ja sügisel)', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2005, 'värvuselt musta ja valge vahepealne', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2006, 'valge ja pimeda vahepealne, hämar; sombune', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2007, 'kaugesse minevikku ulatuv, ajaloo hämarusse kaduv', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2008, 'üksluine, ühetooniline, vaheldusetu, tuim, igav', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2009, 'see, kes v. mis on hall (1. täh.)', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2010, 'kodasaal, elamu suurem siseruum; suur köetav esik (esimesel korrusel)', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2011, 'rohkearvulisele publikule mõeldud suur saal v. hoone; suur tootmisruum v. hoone', '{ss_}');
insert into definition (meaning_id, value, dataset) values (2016, 'suur esik; saal; tootmishoone', '{evs}');
insert into definition (meaning_id, value, dataset) values (2021, 'suur selvekauplus, kus peale toiduainete müüakse ka muid igapäevakaupu', '{eos}');
insert into definition (meaning_id, value, dataset) values (2025, 'hiirekarva hall', '{eos}');
insert into definition (meaning_id, value, dataset) values (2026, 'rõdu, eriti kirikus', '{eos}');
insert into definition (meaning_id, value, dataset) values (2002, 'suur esinduslik ruum elamus, kodasaal', '{eos}');

insert into morph_homonym (id, word_id) values (10001, 1002);
insert into morph_homonym (id, word_id) values (10002, 1002);
insert into morph_homonym (id, word_id) values (10003, 1003);
insert into morph_homonym (id, word_id) values (10004, 1004);
insert into morph_homonym (id, word_id) values (10005, 1005);
insert into morph_homonym (id, word_id) values (10006, 1006);
insert into morph_homonym (id, word_id) values (10007, 1007);
insert into morph_homonym (id, word_id) values (10008, 1008);
insert into morph_homonym (id, word_id) values (10009, 1009);
insert into morph_homonym (id, word_id) values (10010, 1010);
insert into morph_homonym (id, word_id) values (10011, 1011);
insert into morph_homonym (id, word_id) values (10012, 1012);
insert into morph_homonym (id, word_id) values (10013, 1013);
insert into morph_homonym (id, word_id) values (10014, 1014);
insert into morph_homonym (id, word_id) values (10015, 1014);
insert into morph_homonym (id, word_id) values (10016, 1015);
insert into morph_homonym (id, word_id) values (10017, 1016);
insert into morph_homonym (id, word_id) values (10018, 1017);



insert into paradigm (id, morph_homonym_id, display_form, example) values (11001, 10001, 'h`all', 'tark');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11002, 10002, 'h`al''l', 'kurt');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11003, 10003, 'hallakord', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11004, 10004, 'hallaudu', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11005, 10005, 'hallaöö', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11006, 10006, 'hallasääsk', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11007, 10007, 'kaubahall', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11008, 10008, 'linnahall', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11009, 10009, 'helehall', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11010, 10010, 'tumehall', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11011, 10011, 'hiirhall', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11012, 10012, 'hiirjas', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11013, 10013, 'hiirukas', '');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11014, 10014, 'v`äär', 'riik');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11015, 10015, 'v`äär', 'külm');
insert into paradigm (id, morph_homonym_id, display_form, example) values (11016, 10015, 'v`äär', 'õpik');

insert into form (paradigm_id, morph_code, value) values (11001, 'SgN', 'hall');
insert into form (paradigm_id, morph_code, value) values (11001, 'SgG', 'halla');
insert into form (paradigm_id, morph_code, value) values (11001, 'SgP', 'h`alla');
insert into form (paradigm_id, morph_code, value) values (11002, 'SgN', 'hall');
insert into form (paradigm_id, morph_code, value) values (11002, 'SgG', 'halli');
insert into form (paradigm_id, morph_code, value) values (11002, 'SgP', 'h`alli');
insert into form (paradigm_id, morph_code, value) values (11014, 'SgN', 'v`äär');
insert into form (paradigm_id, morph_code, value) values (11014, 'SgG', 'vääri');
insert into form (paradigm_id, morph_code, value) values (11014, 'SgP', 'v`ääri');
insert into form (paradigm_id, morph_code, value) values (11015, 'SgN', 'v`äär');
insert into form (paradigm_id, morph_code, value) values (11015, 'SgG', 'väära');
insert into form (paradigm_id, morph_code, value) values (11015, 'SgP', 'v`äära');
insert into form (paradigm_id, morph_code, value) values (11016, 'SgN', 'v`äär');
insert into form (paradigm_id, morph_code, value) values (11016, 'SgG', 'v`äära');
insert into form (paradigm_id, morph_code, value) values (11016, 'SgP', 'v`äärat');

insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4001, 10002, 2001, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4002, 10002, 2002, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4003, 10002, 2003, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4004, 10001, 2004, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4005, 10002, 2005, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4006, 10002, 2006, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4007, 10002, 2007, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4008, 10002, 2008, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4009, 10002, 2009, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4010, 10002, 2010, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4011, 10002, 2011, '{ss_}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4012, 10001, 2012, '{sys}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4013, 10002, 2013, '{sys}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4014, 10001, 2014, '{evs}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4015, 10002, 2015, '{evs}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4016, 10002, 2016, '{evs}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4017, 10003, 2017, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4018, 10004, 2018, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4019, 10005, 2019, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4020, 10006, 2020, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4021, 10007, 2021, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4022, 10008, 2022, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4023, 10009, 2023, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4024, 10010, 2024, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4025, 10011, 2025, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4026, 10012, 2025, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4027, 10013, 2025, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4028, 10014, 2026, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4029, 10015, 2027, '{eos}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4030, 10015, 2014, '{evs}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4031, 10016, 2015, '{evs}');
insert into lexeme (id, morph_homonym_id, meaning_id, dataset) values (4032, 10017, 2016, '{evs}');

insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4001, 4017, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4001, 4018, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4001, 4019, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4001, 4020, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4002, 4021, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4002, 4022, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4003, 4023, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4003, 4024, 'comp', '{eos}');
insert into lex_relation (lexeme1_id, lexeme2_id, lex_rel_type_code, dataset) values (4003, 4025, 'comp', '{eos}');

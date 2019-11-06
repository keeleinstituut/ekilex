update word_freeform
set word_id = :wordId
where word_freeform.word_id = :sourceWordId
  and word_freeform.freeform_id in (:nonDublicateFreeformIds)
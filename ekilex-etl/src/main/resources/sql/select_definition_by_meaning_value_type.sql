select d.*,
       (select count(dd.dataset_code) > 0
        from definition_dataset dd
        where dd.definition_id = d.id
        and   dd.dataset_code = :datasetCode) dataset_exists
from definition d
where d.meaning_id = :meaningId
and   d.value = :value
and   definition_type_code = :definitionTypeCode

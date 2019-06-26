select source_concept_id,
	   target_concept_id,
	   cast(relation_type as unsigned) relation_type
from termeki_concept_relations
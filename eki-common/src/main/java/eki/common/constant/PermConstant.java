package eki.common.constant;

import java.util.Arrays;
import java.util.List;

public interface PermConstant {

	List<String> AUTH_OPS_CRUD = Arrays.asList(AuthorityOperation.OWN.name(), AuthorityOperation.CRUD.name());

	List<String> AUTH_OPS_READ = Arrays.asList(AuthorityOperation.OWN.name(), AuthorityOperation.CRUD.name(), AuthorityOperation.READ.name());

	String AUTH_ITEM_DATASET = AuthorityItem.DATASET.name();
}

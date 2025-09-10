package eki.ekilex.cli.runner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import eki.common.constant.GlobalConstant;
import eki.common.constant.LoaderConstant;
import eki.common.service.TextDecorationService;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.AbstractCreateUpdateEntity;
import eki.ekilex.data.DatasetPermission;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.ValueAndPrese;
import eki.ekilex.service.core.UserContext;
import eki.ekilex.service.db.CudDbService;
import eki.ekilex.service.db.MigrationDbService;
import eki.ekilex.service.util.ValueUtil;

public abstract class AbstractLoaderRunner implements GlobalConstant, LoaderConstant, SystemConstant {

	private static final String USER_NAME_LOADER = "Laadur";

	@Autowired
	protected UserContext userContext;

	@Autowired
	private TextDecorationService textDecorationService;

	@Autowired
	protected ValueUtil valueUtil;

	@Autowired
	protected CudDbService cudDbService;

	@Autowired
	protected MigrationDbService migrationDbService;

	protected void createSecurityContext() {

		EkiUser user = new EkiUser();
		user.setName(USER_NAME_LOADER);
		user.setAdmin(Boolean.TRUE);
		user.setMaster(Boolean.TRUE);
		user.setEnabled(Boolean.TRUE);

		DatasetPermission recentRole = new DatasetPermission();
		recentRole.setDatasetName(DATASET_EKI);
		recentRole.setSuperiorDataset(true);
		user.setRecentRole(recentRole);

		GrantedAuthority authority = new SimpleGrantedAuthority("import");
		AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("cmov", user, Arrays.asList(authority));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	protected void setValueAndPrese(ValueAndPrese entity) {

		String valuePrese = StringUtils.trim(entity.getValuePrese());
		String value = textDecorationService.removeEkiElementMarkup(valuePrese);
		entity.setValue(value);
		entity.setValuePrese(valuePrese);
	}

	protected void setCreateUpdate(AbstractCreateUpdateEntity entity) {

		String userName = userContext.getUserName();
		LocalDateTime now = LocalDateTime.now();
		entity.setCreatedBy(userName);
		entity.setCreatedOn(now);
		entity.setModifiedBy(userName);
		entity.setModifiedOn(now);
	}

	protected List<String> readFileLines(String filePath) throws Exception {
		InputStream fileInputStream = new FileInputStream(filePath);
		try {
			return IOUtils.readLines(fileInputStream, StandardCharsets.UTF_8);
		} finally {
			fileInputStream.close();
		}
	}
}

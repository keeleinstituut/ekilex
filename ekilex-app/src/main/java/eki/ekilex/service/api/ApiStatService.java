package eki.ekilex.service.api;

import java.util.StringTokenizer;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eki.ekilex.service.db.api.ApiStatDbService;

@Component
public class ApiStatService {

	private static final int GENERIC_PATH_TOKEN_COUNT_W_DELIM = 7;

	private static final int ERROR_MESSAGE_MAX_LENGTH = 200;

	@Autowired
	private ApiStatDbService apiStatDbService;

	@Transactional
	public void addRequest(String authName, String servletPath) {

		String genericPath = extractGenericPath(servletPath);

		apiStatDbService.createOrUpdateRequestCount(authName, genericPath);
	}

	@Transactional
	public void addError(String authName, String servletPath, String message) {

		String genericPath = extractGenericPath(servletPath);
		String cleanMessage = cutAndCleanMessage(message);

		apiStatDbService.createOrUpdateErrorCount(authName, genericPath, cleanMessage);
	}

	private String cutAndCleanMessage(String message) {

		if (StringUtils.isBlank(message)) {
			message = "N/A";
		} else {
			message = StringUtils.substring(message, 0, ERROR_MESSAGE_MAX_LENGTH);
			message = StringUtils.trim(message);
		}
		return message;
	}

	private String extractGenericPath(String servletPath) {

		final String tokenDelim = "/";
		StringTokenizer pathTokenizer = new StringTokenizer(servletPath, tokenDelim, true);
		StringBuilder genericPathBuf = new StringBuilder();
		int pathTokenCount = 0;
		while (pathTokenizer.hasMoreTokens()) {
			String pathToken = pathTokenizer.nextToken();
			if (pathTokenCount < GENERIC_PATH_TOKEN_COUNT_W_DELIM) {
				genericPathBuf.append(pathToken);
			} else {
				break;
			}
			pathTokenCount++;
		}
		String genericPath = genericPathBuf.toString();
		return genericPath;
	}
}

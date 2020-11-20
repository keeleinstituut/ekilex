package eki.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eki.common.exception.ConsolePromptException;

public final class ConsolePromptUtil {

	private static Logger logger = LoggerFactory.getLogger(ConsolePromptUtil.class);

	private static final String[] POSITIVE_REPLIES = {"y", "yes", "true", "ok", "go", "please", "j", "jah"};

	public static String getKeyValue(String key, String... args) {
		if (ArrayUtils.isEmpty(args)) {
			return null;
		}
		for (String arg : args) {
			if (StringUtils.startsWith(arg, key + "=")) {
				return StringUtils.substringAfter(arg, "=");
			}
		}
		return null;
	}

	public static boolean isPositiveReply(String value) {
		for (String positiveReplyAlternative : POSITIVE_REPLIES) {
			if (StringUtils.equalsIgnoreCase(value, positiveReplyAlternative)) {
				return true;
			}
		}
		return false;
	}

	public static String promptStringValue(String promptMessage) {

		logger.info(promptMessage);
		InputStreamReader consoleInput = new InputStreamReader(System.in);
		BufferedReader consoleReader = new BufferedReader(consoleInput);
		String strValue = null;
		try {
			strValue = consoleReader.readLine();
		} catch (IOException e) {
			// hardly..
		}
		if (StringUtils.isNotBlank(strValue)) {
			return StringUtils.trim(strValue);
		} else {
			promptMessage = "What? Please try again";
			return promptStringValue(promptMessage);
		}
	}

	public static int promptIntValue(String promptMessage) {

		logger.info(promptMessage);
		try {
			InputStreamReader consoleInput = new InputStreamReader(System.in);
			BufferedReader consoleReader = new BufferedReader(consoleInput);
			String intValueStr = consoleReader.readLine();
			int intValue = Integer.parseInt(intValueStr);
			return intValue;
		} catch (Exception e) {
			promptMessage = "Not a number value. Please try again";
			return promptIntValue(promptMessage);
		}
	}

	public static long promptLongValue(String promptMessage) {

		logger.info(promptMessage);
		try {
			InputStreamReader consoleInput = new InputStreamReader(System.in);
			BufferedReader consoleReader = new BufferedReader(consoleInput);
			String longValueStr = consoleReader.readLine();
			long longValue = Long.parseLong(longValueStr);
			return longValue;
		} catch (Exception e) {
			promptMessage = "Not a number value. Please try again";
			return promptLongValue(promptMessage);
		}
	}

	public static float promptFloatValue(String promptMessage) {

		logger.info(promptMessage);
		try {
			InputStreamReader consoleInput = new InputStreamReader(System.in);
			BufferedReader consoleReader = new BufferedReader(consoleInput);
			String floatValueStr = consoleReader.readLine();
			float floatValue = Float.parseFloat(floatValueStr);
			return floatValue;
		} catch (Exception e) {
			promptMessage = "Not a number value. Please try again";
			return promptFloatValue(promptMessage);
		}
	}

	public static boolean promptBooleanValue(String promptMessage) {

		logger.info(promptMessage);
		InputStreamReader consoleInput = new InputStreamReader(System.in);
		BufferedReader consoleReader = new BufferedReader(consoleInput);
		String booleanValueStr = null;
		try {
			booleanValueStr = consoleReader.readLine();
		} catch (IOException e) {
			// hardly..
		}
		if (StringUtils.isNotBlank(booleanValueStr)) {
			boolean booleanValue = isPositiveReply(booleanValueStr);
			return booleanValue;
		} else {
			promptMessage = "What? Please try again";
			return promptBooleanValue(promptMessage);
		}
	}

	public static String promptDataFolderPath(String promptMessage) {

		logger.info(promptMessage);
		try {
			InputStreamReader consoleInput = new InputStreamReader(System.in);
			BufferedReader consoleReader = new BufferedReader(consoleInput);
			String dataFolderPath = consoleReader.readLine();
			validateDataFolderExists(dataFolderPath);
			if (!StringUtils.endsWith(dataFolderPath, "/")) {
				dataFolderPath = dataFolderPath + "/";
			}
			return dataFolderPath;
		} catch (ConsolePromptException e) {
			promptMessage = "Incorrect path. Please try again";
			return promptDataFolderPath(promptMessage);
		} catch (Exception e) {
			promptMessage = "What? Please try again";
			return promptDataFolderPath(promptMessage);
		}
	}

	public static String promptDataFilePath(String promptMessage) {

		logger.info(promptMessage);
		try {
			InputStreamReader consoleInput = new InputStreamReader(System.in);
			BufferedReader consoleReader = new BufferedReader(consoleInput);
			String dataFilePath = consoleReader.readLine();
			validateDataFileExists(dataFilePath);
			return dataFilePath;
		} catch (ConsolePromptException e) {
			promptMessage = "Incorrect path. Please try again";
			return promptDataFilePath(promptMessage);
		} catch (Exception e) {
			promptMessage = "What? Please try again";
			return promptDataFilePath(promptMessage);
		}
	}

	private static void validateDataFolderExists(String dataFolderPath) throws ConsolePromptException {
		File dataFolder = new File(dataFolderPath);
		if (!dataFolder.exists()) {
			throw new ConsolePromptException("No such path \"" + dataFolderPath + "\"");
		}
		if (dataFolder.isFile()) {
			throw new ConsolePromptException("Not a folder path \"" + dataFolderPath + "\"");
		}
	}

	private static void validateDataFileExists(String dataFilePath) throws ConsolePromptException {
		File dataFile = new File(dataFilePath);
		if (!dataFile.exists()) {
			throw new ConsolePromptException("No such path \"" + dataFilePath + "\"");
		}
		if (!dataFile.isFile()) {
			throw new ConsolePromptException("Not a file path \"" + dataFilePath + "\"");
		}
	}
}

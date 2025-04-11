package eki.common.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserialiser extends JsonDeserializer<LocalDateTime> {

	private DateTimeFormatter formatter;

	public LocalDateTimeDeserialiser() {
		super();
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	}

	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {

		ObjectCodec codec = parser.getCodec();
		String valueStr = codec.readValue(parser, String.class);

		if (StringUtils.isBlank(valueStr)) {
			return null;
		}
		if (StringUtils.length(valueStr) > 19) {
			valueStr = StringUtils.substringBeforeLast(valueStr, ".");
		}

		LocalDateTime dateTime = LocalDateTime.parse(valueStr, formatter);
		return dateTime;
	}

}

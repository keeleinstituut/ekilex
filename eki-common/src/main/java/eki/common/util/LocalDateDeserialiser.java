package eki.common.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateDeserialiser extends JsonDeserializer<LocalDate> {

	private DateTimeFormatter formatter;

	public LocalDateDeserialiser() {
		super();
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {

		ObjectCodec codec = parser.getCodec();
		String valueStr = codec.readValue(parser, String.class);

		if (StringUtils.isBlank(valueStr)) {
			return null;
		}

		LocalDate date = LocalDate.parse(valueStr, formatter);
		return date;
	}

}

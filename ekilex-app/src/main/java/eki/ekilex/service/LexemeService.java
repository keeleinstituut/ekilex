package eki.ekilex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LexemeService {

	private static final Logger logger = LoggerFactory.getLogger(LexemeService.class);

	public Optional<Long> duplicateLexeme(Long lexemeId) {
		return Optional.empty();
	}

}

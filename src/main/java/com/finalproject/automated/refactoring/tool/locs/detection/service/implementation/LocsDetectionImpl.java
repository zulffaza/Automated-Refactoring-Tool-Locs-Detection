package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

@Service
public class LocsDetectionImpl implements LocsDetection {

    private static final Long INITIAL_COUNT = 0L;

    private static final String NEW_LINE_DELIMITER = "\n";
    private static final String STATEMENTS_DELIMITER = "(?:[;{])";

    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList("//", "/*", "*/", "*", "import", "package");

    @Override
    public Long llocDetection(@NonNull String body) {
        List<String> lines = Arrays.asList(body.split(NEW_LINE_DELIMITER));

        return lines.stream()
                .map(String::trim)
                .filter(this::isValid)
                .mapToLong(this::countStatement)
                .sum();
    }

    private Boolean isValid(String line) {
        return !line.isEmpty() && !isEscapes(line);
    }

    private Boolean isEscapes(String line) {
        Long count = ESCAPES_KEYWORDS.stream()
                .filter(line::startsWith)
                .count();

        return count > INITIAL_COUNT;
    }

    private Long countStatement(String line) {
        Pattern pattern = Pattern.compile(STATEMENTS_DELIMITER);
        Matcher matcher = pattern.matcher(line);

        return countMatch(matcher);
    }

    private Long countMatch(Matcher matcher) {
        Long count = INITIAL_COUNT;

        while (matcher.find()) {
            count++;
        }

        return count;
    }
}

package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

@Service
public class LocsDetectionImpl implements LocsDetection {

    private static final Long INITIAL_COUNT = 0L;

    private static final String NEW_LINE_DELIMITER = "\n";

    private static final Character DOUBLE_QUOTE_CHARACTER = '\"';
    private static final Character ESCAPE_CHARACTER = '\\';

    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList("//", "/*", "*/", "*", "import", "package");

    private static final List<Character> STATEMENTS_KEYWORDS = Arrays.asList(';', '{');

    @Override
    public Long llocDetection(@NonNull String body) {
        List<String> lines = Arrays.asList(body.split(NEW_LINE_DELIMITER));

        return lines.parallelStream()
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
        Stack<Character> stack = new Stack<>();
        Boolean isEscape = Boolean.FALSE;
        Long countedStatement = INITIAL_COUNT;

        for (int index = 0; index < line.length(); index++) {
            Character character = line.charAt(index);

            if (isEscape) {
                isEscape = Boolean.FALSE;
                continue;
            }

            if (character.equals(DOUBLE_QUOTE_CHARACTER)) {
                if (stack.empty())
                    stack.push(character);
                else
                    stack.pop();

                continue;
            }

            if (character.equals(ESCAPE_CHARACTER)) {
                isEscape = Boolean.TRUE;
                continue;
            }

            if (stack.empty() && STATEMENTS_KEYWORDS.contains(character))
                countedStatement++;
        }

        return countedStatement;
    }
}

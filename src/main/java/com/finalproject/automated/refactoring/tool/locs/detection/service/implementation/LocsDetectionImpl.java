package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.locs.detection.service.model.CountingStatementVA;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final Character SINGLE_QUOTE_CHARACTER = '\'';
    private static final Character OPEN_PARENTHESES_CHARACTER = '(';
    private static final Character CLOSED_PARENTHESES_CHARACTER = ')';
    private static final Character ESCAPE_CHARACTER = '\\';

    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList("//", "/*", "*/", "*", "import", "package");

    private static final List<Character> STATEMENTS_KEYWORDS = Arrays.asList(';', '{');

    private static final Map<Character, Character> ESCAPES_STATEMENTS_KEYWORDS;

    static {
        ESCAPES_STATEMENTS_KEYWORDS = new HashMap<>();
        ESCAPES_STATEMENTS_KEYWORDS.put(DOUBLE_QUOTE_CHARACTER, DOUBLE_QUOTE_CHARACTER);
        ESCAPES_STATEMENTS_KEYWORDS.put(SINGLE_QUOTE_CHARACTER, SINGLE_QUOTE_CHARACTER);
        ESCAPES_STATEMENTS_KEYWORDS.put(OPEN_PARENTHESES_CHARACTER, CLOSED_PARENTHESES_CHARACTER);
    }

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
        CountingStatementVA countingStatementVA = new CountingStatementVA();

        System.out.println(line);

        for (int index = 0; index < line.length(); index++) {
            countingStatement(line.charAt(index), countingStatementVA);
        }

        return countingStatementVA.getCountedStatement()
                .get();
    }


    private void countingStatement(Character character, CountingStatementVA countingStatementVA) {
        ifStringExpression(character, countingStatementVA);
//        ifParenthesesExpression(character, countingStatementVA.getStack());
        ifEscapeCharacter(character, countingStatementVA.getEscape());
        ifStatement(character, countingStatementVA);
    }

    private void ifStringExpression(Character character, CountingStatementVA countingStatementVA) {
        if (!countingStatementVA.getEscape().get()) {
            Boolean isValid = (!countingStatementVA.getStack().empty() &&
                    (countingStatementVA.getStack().peek().equals(DOUBLE_QUOTE_CHARACTER) ||
                            countingStatementVA.getStack().peek().equals(SINGLE_QUOTE_CHARACTER)))
                    || countingStatementVA.getStack().empty();

            System.out.println("For : " + character + " is " + isValid);

            if (isValid) {
                if (ESCAPES_STATEMENTS_KEYWORDS.containsValue(character) && !countingStatementVA.getStack().empty()) {
                    System.out.println(character);
                    countingStatementVA.getStack().pop();
                } else if (ESCAPES_STATEMENTS_KEYWORDS.containsKey(character)) {
                    System.out.println(character);
                    countingStatementVA.getStack().push(character);
                }
            }
        }
    }

    //    ("(hello {\"name\"}), (good {\"greeting\"}), (morning {\"vibes\"})")

    private Boolean isStringExpression(Character character, AtomicBoolean escape) {
        return character.equals(DOUBLE_QUOTE_CHARACTER) &&
                !escape.get();
    }

    private void ifParenthesesExpression(Character character, Stack<Character> stack) {
        if (character.equals(OPEN_PARENTHESES_CHARACTER)) {
            System.out.println(character);
            stack.push(character);
        }

        if (isClosedParentheses(character, stack)) {
            System.out.println(character);
            stack.pop();
        }
    }

    private Boolean isClosedParentheses(Character character, Stack<Character> stack) {
        return !stack.empty() &&
                character.equals(CLOSED_PARENTHESES_CHARACTER);
    }

    private void ifEscapeCharacter(Character character, AtomicBoolean escape) {
        if (escape.get()) {
            escape.set(Boolean.FALSE);
        }

        if (character.equals(ESCAPE_CHARACTER) && !escape.get()) {
            escape.set(Boolean.TRUE);
        }
    }

    private void ifStatement(Character character, CountingStatementVA countingStatementVA) {
        if (isStatement(character, countingStatementVA.getStack())) {
            System.out.println("Count...");
            countingStatementVA.getCountedStatement().getAndIncrement();
        } else if (!countingStatementVA.getStack().empty()) {
            System.out.println("Peek --> " + countingStatementVA.getStack().peek());
        }
    }

    private Boolean isStatement(Character character, Stack stack) {
        return stack.empty() &&
                STATEMENTS_KEYWORDS.contains(character);
    }
}

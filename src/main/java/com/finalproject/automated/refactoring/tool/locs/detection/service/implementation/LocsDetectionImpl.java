package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.locs.detection.service.model.CountingStatementVA;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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

    private static final List<Character> QUOTES_KEYWORDS = Arrays.asList(SINGLE_QUOTE_CHARACTER, DOUBLE_QUOTE_CHARACTER);
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
        CountingStatementVA countingStatementVA = new CountingStatementVA();

        for (int index = 0; index < line.length(); index++) {
            countingStatement(line.charAt(index), countingStatementVA);
        }

        return countingStatementVA.getCountedStatement()
                .get();
    }

    private void countingStatement(Character character, CountingStatementVA countingStatementVA) {
        searchWantedExpression(character, DOUBLE_QUOTE_CHARACTER, countingStatementVA);
        searchWantedExpression(character, SINGLE_QUOTE_CHARACTER, countingStatementVA);
        searchParenthesesExpression(character, countingStatementVA.getStack());
        searchEscapeCharacter(character, countingStatementVA.getEscape());
        searchStatement(character, countingStatementVA);
    }

    private void searchWantedExpression(Character character, Character wantedCharacter,
                                        CountingStatementVA countingStatementVA) {
        if (isWantedExpression(character, wantedCharacter, countingStatementVA.getEscape())) {
            analyzeWantedExpression(character, wantedCharacter, countingStatementVA.getStack());
        }
    }

    private Boolean isWantedExpression(Character character, Character wantedCharacter,
                                       AtomicBoolean escape) {
        return character.equals(wantedCharacter) &&
                !escape.get();
    }

    private void analyzeWantedExpression(Character character, Character wantedCharacter,
                                         Stack<Character> stack) {
        if (stack.empty()) {
            stack.push(character);
        } else if (stack.peek().equals(wantedCharacter)) {
            stack.pop();
        }
    }

    private void searchParenthesesExpression(Character character, Stack<Character> stack) {
        if (character.equals(OPEN_PARENTHESES_CHARACTER)) {
            analyzeOpenParenthesesExpression(character, stack);
        }

        if (isClosedParenthesesExpression(character, stack)) {
            stack.pop();
        }
    }

    private void analyzeOpenParenthesesExpression(Character character, Stack<Character> stack) {
        Boolean isQuotes = isQuotes(stack);
        stack.push(character);

        redoPushIfInsideQuotes(isQuotes, stack);
    }

    private Boolean isQuotes(Stack<Character> stack) {
        return !stack.empty() &&
                QUOTES_KEYWORDS.contains(stack.peek());
    }

    private void redoPushIfInsideQuotes(Boolean isQuotes, Stack stack) {
        if (isQuotes) {
            stack.pop();
        }
    }

    private Boolean isClosedParenthesesExpression(Character character, Stack<Character> stack) {
        return character.equals(CLOSED_PARENTHESES_CHARACTER) &&
                !stack.empty() &&
                stack.peek().equals(OPEN_PARENTHESES_CHARACTER);
    }

    private void searchEscapeCharacter(Character character, AtomicBoolean escape) {
        escape.set(character.equals(ESCAPE_CHARACTER) && !escape.get());
    }

    private void searchStatement(Character character, CountingStatementVA countingStatementVA) {
        if (isStatement(character, countingStatementVA.getStack())) {
            countingStatementVA.getCountedStatement().getAndIncrement();
        }
    }

    private Boolean isStatement(Character character, Stack stack) {
        return stack.empty() &&
                STATEMENTS_KEYWORDS.contains(character);
    }
}

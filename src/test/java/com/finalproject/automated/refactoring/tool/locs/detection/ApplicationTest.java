package com.finalproject.automated.refactoring.tool.locs.detection;

import com.finalproject.automated.refactoring.tool.locs.detection.service.implementation.LocsDetectionImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 8 March 2019
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private LocsDetectionImpl locsDetection;

    private static final Long LLOC_COUNT = 63L;

    private String body;

    @Before
    public void setUp() {
        body = createBody();
    }

    @Test
    public void llocDetection_success() {
        Long count = locsDetection.llocDetection(body);
        assertEquals(LLOC_COUNT, count);
    }

    @Test
    public void llocDetection_success_emptyBody() {
        Long count = locsDetection.llocDetection("");
        assertEquals(0, count.intValue());
    }

    @Test(expected = NullPointerException.class)
    public void llocDetection_success_nullBody() {
        locsDetection.llocDetection(null);
    }

    private String createBody() {
        return "package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;\n" +
                "\n" +
                "import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;\n" +
                "import com.finalproject.automated.refactoring.tool.locs.detection.service.model.CountingStatementVA;\n" +
                "import lombok.NonNull;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "import java.util.Stack;\n" +
                "import java.util.concurrent.atomic.AtomicBoolean;\n" +
                "\n" +
                "/**\n" +
                " * @author fazazulfikapp\n" +
                " * @version 1.0.0\n" +
                " * @since 24 October 2018\n" +
                " */\n" +
                "\n" +
                "@Service\n" +
                "public class LocsDetectionImpl implements LocsDetection {\n" +
                "\n" +
                "    private static final Long INITIAL_COUNT = 0L;\n" +
                "\n" +
                "    private static final String NEW_LINE_DELIMITER = \"\\n\";\n" +
                "\n" +
                "    private static final Character DOUBLE_QUOTE_CHARACTER = '\\\"';\n" +
                "    private static final Character SINGLE_QUOTE_CHARACTER = '\\'';\n" +
                "    private static final Character OPEN_PARENTHESES_CHARACTER = '(';\n" +
                "    private static final Character CLOSED_PARENTHESES_CHARACTER = ')';\n" +
                "    private static final Character ESCAPE_CHARACTER = '\\\\';\n" +
                "\n" +
                "    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList(\"//\", \"/*\", \"*/\", \"*\", \"import\", \"package\");\n" +
                "\n" +
                "    private static final List<Character> QUOTES_KEYWORDS = Arrays.asList(SINGLE_QUOTE_CHARACTER, DOUBLE_QUOTE_CHARACTER);\n" +
                "    private static final List<Character> STATEMENTS_KEYWORDS = Arrays.asList(';', '{');\n" +
                "\n" +
                "    @Override\n" +
                "    public Long llocDetection(@NonNull String body) {\n" +
                "        List<String> lines = Arrays.asList(body.split(NEW_LINE_DELIMITER));\n" +
                "\n" +
                "        return lines.parallelStream()\n" +
                "                .map(String::trim)\n" +
                "                .filter(this::isValid)\n" +
                "                .mapToLong(this::countStatement)\n" +
                "                .sum();\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isValid(String line) {\n" +
                "        return !line.isEmpty() && !isEscapes(line);\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isEscapes(String line) {\n" +
                "        Long count = ESCAPES_KEYWORDS.stream()\n" +
                "                .filter(line::startsWith)\n" +
                "                .count();\n" +
                "\n" +
                "        return count > INITIAL_COUNT;\n" +
                "    }\n" +
                "\n" +
                "    private Long countStatement(String line) {\n" +
                "        CountingStatementVA countingStatementVA = new CountingStatementVA();\n" +
                "\n" +
                "        for (int index = 0; index < line.length(); index++) {\n" +
                "            countingStatement(line.charAt(index), countingStatementVA);\n" +
                "        }\n" +
                "\n" +
                "        return countingStatementVA.getCountedStatement()\n" +
                "                .get();\n" +
                "    }\n" +
                "\n" +
                "    private void countingStatement(Character character, CountingStatementVA countingStatementVA) {\n" +
                "        searchWantedExpression(character, DOUBLE_QUOTE_CHARACTER, countingStatementVA);\n" +
                "        searchWantedExpression(character, SINGLE_QUOTE_CHARACTER, countingStatementVA);\n" +
                "        searchParenthesesExpression(character, countingStatementVA.getStack());\n" +
                "        searchEscapeCharacter(character, countingStatementVA.getEscape());\n" +
                "        searchStatement(character, countingStatementVA);\n" +
                "    }\n" +
                "\n" +
                "    private void searchWantedExpression(Character character, Character wantedCharacter,\n" +
                "                                        CountingStatementVA countingStatementVA) {\n" +
                "        if (isWantedExpression(character, wantedCharacter, countingStatementVA.getEscape())) {\n" +
                "            analyzeWantedExpression(character, wantedCharacter, countingStatementVA.getStack());\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isWantedExpression(Character character, Character wantedCharacter,\n" +
                "                                       AtomicBoolean escape) {\n" +
                "        return character.equals(wantedCharacter) &&\n" +
                "                !escape.get();\n" +
                "    }\n" +
                "\n" +
                "    private void analyzeWantedExpression(Character character, Character wantedCharacter,\n" +
                "                                         Stack<Character> stack) {\n" +
                "        if (stack.empty()) {\n" +
                "            stack.push(character);\n" +
                "        } else if (stack.peek().equals(wantedCharacter)) {\n" +
                "            stack.pop();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void searchParenthesesExpression(Character character, Stack<Character> stack) {\n" +
                "        if (character.equals(OPEN_PARENTHESES_CHARACTER)) {\n" +
                "            analyzeOpenParenthesesExpression(character, stack);\n" +
                "        }\n" +
                "\n" +
                "        if (isClosedParenthesesExpression(character, stack)) {\n" +
                "            stack.pop();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void analyzeOpenParenthesesExpression(Character character, Stack<Character> stack) {\n" +
                "        Boolean isQuotes = isQuotes(stack);\n" +
                "        stack.push(character);\n" +
                "\n" +
                "        redoPushIfInsideQuotes(isQuotes, stack);\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isQuotes(Stack<Character> stack) {\n" +
                "        return !stack.empty() &&\n" +
                "                QUOTES_KEYWORDS.contains(stack.peek());\n" +
                "    }\n" +
                "\n" +
                "    private void redoPushIfInsideQuotes(Boolean isQuotes, Stack stack) {\n" +
                "        if (isQuotes) {\n" +
                "            stack.pop();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isClosedParenthesesExpression(Character character, Stack<Character> stack) {\n" +
                "        return character.equals(CLOSED_PARENTHESES_CHARACTER) &&\n" +
                "                !stack.empty() &&\n" +
                "                stack.peek().equals(OPEN_PARENTHESES_CHARACTER);\n" +
                "    }\n" +
                "\n" +
                "    private void searchEscapeCharacter(Character character, AtomicBoolean escape) {\n" +
                "        escape.set(character.equals(ESCAPE_CHARACTER) && !escape.get());\n" +
                "    }\n" +
                "\n" +
                "    private void searchStatement(Character character, CountingStatementVA countingStatementVA) {\n" +
                "        if (isStatement(character, countingStatementVA.getStack())) {\n" +
                "            countingStatementVA.getCountedStatement().getAndIncrement();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isStatement(Character character, Stack stack) {\n" +
                "        return stack.empty() &&\n" +
                "                STATEMENTS_KEYWORDS.contains(character);\n" +
                "    }\n" +
                "}\n";
    }
}
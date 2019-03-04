package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

public class LocsDetectionImplTest {

    private static final Long LLOC_COUNT = 48L;

    private LocsDetectionImpl locsDetection;

    private String body;

    @Before
    public void setUp() {
        locsDetection = new LocsDetectionImpl();
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
                "    private static final Character OPEN_PARENTHESES_CHARACTER = '(';\n" +
                "    private static final Character CLOSED_PARENTHESES_CHARACTER = ')';\n" +
                "    private static final Character ESCAPE_CHARACTER = '\\\\';\n" +
                "\n" +
                "    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList(\"//\", \"/*\", \"*/\", \"*\", \"import\", \"package\");\n" +
                "\n" +
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
                "\n" +
                "    private void countingStatement(Character character, CountingStatementVA countingStatementVA) {\n" +
                "        ifStringExpression(character, countingStatementVA);\n" +
                "        ifParenthesesExpression(character, countingStatementVA.getStack());\n" +
                "        ifEscapeCharacter(character, countingStatementVA.getEscape());\n" +
                "        ifStatement(character, countingStatementVA);\n" +
                "    }\n" +
                "\n" +
                "    private void ifStringExpression(Character character, CountingStatementVA countingStatementVA) {\n" +
                "        if (isStringExpression(character, countingStatementVA.getEscape())) {\n" +
                "            if (countingStatementVA.getStack().empty())\n" +
                "                countingStatementVA.getStack().push(character);\n" +
                "            else\n" +
                "                countingStatementVA.getStack().pop();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isStringExpression(Character character, AtomicBoolean escape) {\n" +
                "        return character.equals(DOUBLE_QUOTE_CHARACTER) &&\n" +
                "                !escape.get();\n" +
                "    }\n" +
                "\n" +
                "    private void ifParenthesesExpression(Character character, Stack<Character> stack) {\n" +
                "        if (character.equals(OPEN_PARENTHESES_CHARACTER)) {\n" +
                "            stack.push(character);\n" +
                "        }\n" +
                "\n" +
                "        if (character.equals(CLOSED_PARENTHESES_CHARACTER)) {\n" +
                "            stack.pop();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void ifEscapeCharacter(Character character, AtomicBoolean escape) {\n" +
                "        if (escape.get()) {\n" +
                "            escape.set(Boolean.FALSE);\n" +
                "        }\n" +
                "\n" +
                "        if (character.equals(ESCAPE_CHARACTER) && !escape.get()) {\n" +
                "            escape.set(Boolean.TRUE);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void ifStatement(Character character, CountingStatementVA countingStatementVA) {\n" +
                "        if (isStatement(character, countingStatementVA.getStack()))\n" +
                "            countingStatementVA.getCountedStatement().getAndIncrement();\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isStatement(Character character, Stack stack) {\n" +
                "        return stack.empty() &&\n" +
                "                STATEMENTS_KEYWORDS.contains(character);\n" +
                "    }\n" +
                "}\n";
    }
}
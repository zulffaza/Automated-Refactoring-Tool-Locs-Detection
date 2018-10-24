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

    private static final Integer LLOC_COUNT = 27;

    private LocsDetectionImpl locsDetection;

    private String body;

    @Before
    public void setUp() {
        locsDetection = new LocsDetectionImpl();
        body = createBody();
    }

    @Test
    public void llocDetection_success() {
        Integer count = locsDetection.llocDetection(body);
        assertEquals(LLOC_COUNT, count);
    }

    @Test
    public void llocDetection_success_emptyBody() {
        Integer count = locsDetection.llocDetection("");
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
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "import java.util.List;\n" +
                "import java.util.concurrent.atomic.AtomicInteger;\n" +
                "import java.util.regex.Matcher;\n" +
                "import java.util.regex.Pattern;\n" +
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
                "    private static final String NEW_LINE_DELIMITER = \"\\n\";\n" +
                "    private static final String STATEMENTS_DELIMITER = \"(?:[;{])\";\n" +
                "\n" +
                "    private static final List<String> ESCAPES = Arrays.asList(\"//\", \"/*\", \"*/\", \"*\", \"import\", \"package\");\n" +
                "\n" +
                "    @Override\n" +
                "    public Integer llocDetection(String body) {\n" +
                "        List<String> lines = Arrays.asList(body.split(NEW_LINE_DELIMITER));\n" +
                "        AtomicInteger counter = new AtomicInteger();\n" +
                "\n" +
                "        lines.forEach(line -> detect(line, counter));\n" +
                "\n" +
                "        return counter.get();\n" +
                "    }\n" +
                "\n" +
                "    private void detect(String line, AtomicInteger atomicInteger) {\n" +
                "        line = line.trim();\n" +
                "\n" +
                "        if (isValid(line))\n" +
                "            countStatement(line, atomicInteger);\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isValid(String line) {\n" +
                "        return !isEscapes(line) && !isEmpty(line);\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isEscapes(String line) {\n" +
                "        for (String escape : ESCAPES) {\n" +
                "            if (line.startsWith(escape)) {\n" +
                "                return true;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    private Boolean isEmpty(String line) {\n" +
                "        return line.isEmpty();\n" +
                "    }\n" +
                "\n" +
                "    private void countStatement(String line, AtomicInteger atomicInteger) {\n" +
                "        Pattern pattern = Pattern.compile(STATEMENTS_DELIMITER);\n" +
                "        Matcher matcher = pattern.matcher(line);\n" +
                "\n" +
                "        while (matcher.find())\n" +
                "            atomicInteger.set(atomicInteger.get() + line.split(STATEMENTS_DELIMITER).length);\n" +
                "    }\n" +
                "}\n";
    }
}
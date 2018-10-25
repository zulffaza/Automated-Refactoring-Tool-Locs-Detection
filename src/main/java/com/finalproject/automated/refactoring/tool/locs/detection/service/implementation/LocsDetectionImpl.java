package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

@Service
public class LocsDetectionImpl implements LocsDetection {

    private static final String NEW_LINE_DELIMITER = "\n";
    private static final String STATEMENTS_DELIMITER = "(?:[;{])";

    private static final List<String> ESCAPES = Arrays.asList("//", "/*", "*/", "*", "import", "package");

    @Override
    public Long llocDetection(String body) {
        List<String> lines = Arrays.asList(body.split(NEW_LINE_DELIMITER));
        AtomicLong counter = new AtomicLong();

        lines.forEach(line -> detect(line, counter));

        return counter.get();
    }

    private void detect(String line, AtomicLong atomicLong) {
        line = line.trim();

        if (isValid(line))
            countStatement(line, atomicLong);
    }

    private Boolean isValid(String line) {
        return !isEscapes(line) && !isEmpty(line);
    }

    private Boolean isEscapes(String line) {
        for (String escape : ESCAPES) {
            if (line.startsWith(escape)) {
                return true;
            }
        }

        return false;
    }

    private Boolean isEmpty(String line) {
        return line.isEmpty();
    }

    private void countStatement(String line, AtomicLong atomicLong) {
        Pattern pattern = Pattern.compile(STATEMENTS_DELIMITER);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find())
            atomicLong.getAndIncrement();
    }
}

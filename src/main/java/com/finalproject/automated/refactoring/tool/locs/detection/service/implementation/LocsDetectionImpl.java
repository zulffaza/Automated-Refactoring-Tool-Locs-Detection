package com.finalproject.automated.refactoring.tool.locs.detection.service.implementation;

import com.finalproject.automated.refactoring.tool.locs.detection.service.LocsDetection;
import com.finalproject.automated.refactoring.tool.utils.model.request.IsStatementVA;
import com.finalproject.automated.refactoring.tool.utils.service.StatementHelper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

@Service
public class LocsDetectionImpl implements LocsDetection {

    @Autowired
    private StatementHelper statementHelper;

    private static final Long INITIAL_COUNT = 0L;

    private static final String NEW_LINE_DELIMITER = "\n";

    private static final List<String> ESCAPES_KEYWORDS = Arrays.asList("//", "/*", "*/", "*", "import", "package");

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
        IsStatementVA isStatementVA = new IsStatementVA();
        AtomicLong countedStatement = new AtomicLong();

        for (int index = 0; index < line.length(); index++) {
            countingStatement(line.charAt(index), countedStatement, isStatementVA);
        }

        return countedStatement.get();
    }

    private void countingStatement(Character character, AtomicLong countedStatement,
                                   IsStatementVA isStatementVA) {
        if (statementHelper.isStatement(character, isStatementVA)) {
            countedStatement.incrementAndGet();
        }
    }
}

package com.finalproject.automated.refactoring.tool.locs.detection.service.model;

import lombok.Data;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 4 March 2019
 */

@Data
public class CountingStatementVA {

    private Stack<Character> stack = new Stack<>();

    private AtomicBoolean escape = new AtomicBoolean();

    private AtomicLong countedStatement = new AtomicLong();
}

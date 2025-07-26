package com.minicc;

import java.util.*;

public class AssemblyInterpreter {
    private final List<String> instructions;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Map<String, Integer> memory = new HashMap<>();
    private int returnValue = 0;

    public AssemblyInterpreter(List<String> instructions) {
        this.instructions = instructions;
    }

    public void run() {
        for (String line : instructions) {
            String[] parts = line.trim().split(" ");
            String cmd = parts[0];

            switch (cmd) {
                case "LOADI": {
                    int value = Integer.parseInt(parts[1]);
                    stack.push(value);
                    break;
                }
                case "LOAD": {
                    String var = parts[1];
                    int value = memory.getOrDefault(var, 0);
                    stack.push(value);
                    break;
                }
                case "STORE": {
                    String var = parts[1];
                    int value = stack.pop();
                    memory.put(var, value);
                    break;
                }
                case "ADD": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a + b);
                    break;
                }
                case "SUB": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "MUL": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a * b);
                    break;
                }
                case "DIV": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a / b);
                    break;
                }
                case "RET": {
                    returnValue = stack.pop();
                    return;
                }
                default:
                    System.err.println("[ERROR] Unknown instruction: " + line);
            }
        }
    }

    public int getReturnValue() {
        return returnValue;
    }
}

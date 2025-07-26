package com.minicc;

import java.util.*;

public class AssemblyGenerator {
    private final List<String> tac;
    private final List<String> assembly = new ArrayList<>();

    public AssemblyGenerator(List<String> tac) {
        this.tac = tac;
    }

    public void generate() {
        for (String line : tac) {
            line = line.trim();
            if (line.startsWith("return")) {
                String val = line.substring("return".length()).trim();
                if (!val.isEmpty()) {
                    if (isNumber(val)) assembly.add("LOADI " + val);
                    else assembly.add("LOAD " + val);
                }
                assembly.add("RET");
            } else if (line.contains("=")) {
                String[] parts = line.split("=");
                String lhs = parts[0].trim();
                String rhs = parts[1].trim();

                if (!rhs.contains(" ")) {
                    if (isNumber(rhs)) assembly.add("LOADI " + rhs);
                    else assembly.add("LOAD " + rhs);
                    assembly.add("STORE " + lhs);
                } else {
                    String[] tokens = rhs.split(" ");
                    String a = tokens[0];
                    String op = tokens[1];
                    String b = tokens[2];

                    if (isNumber(a)) assembly.add("LOADI " + a);
                    else assembly.add("LOAD " + a);

                    if (isNumber(b)) assembly.add("LOADI " + b);
                    else assembly.add("LOAD " + b);

                    switch (op) {
                        case "+":
                            assembly.add("ADD");
                            break;
                        case "-":
                            assembly.add("SUB");
                            break;
                        case "*":
                            assembly.add("MUL");
                            break;
                        case "/":
                            assembly.add("DIV");
                            break;
                        default:
                            System.err.println("[ERROR] Unknown operator: " + op);
                    }

                    assembly.add("STORE " + lhs);
                }
            }
        }
    }

    public List<String> getAssembly() {
        return assembly;
    }

    private boolean isNumber(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    public int simulateExecution() {
        Map<String, Integer> registers = new HashMap<>();
        for (String line : assembly) {
            String[] parts = line.split(" ");
            switch (parts[0]) {
                case "MOV":
                    registers.put(parts[1].replace(",", ""), Integer.parseInt(parts[2]));
                    break;
                case "ADD":
                    String dest = parts[1].replace(",", "");
                    String src1 = parts[2].replace(",", "");
                    String src2 = parts[3];
                    registers.put(dest, registers.get(src1) + registers.get(src2));
                    break;
                case "RET":
                    return registers.get(parts[1]);
            }
        }
        return -1;
    }

}

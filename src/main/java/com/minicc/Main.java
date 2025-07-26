package com.minicc;

import com.minicc.CCompilerLexer;
import com.minicc.CCompilerParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("=== MiniC Compiler Front-End ===");

        String sourcePath = "example.c";
        String code = new String(Files.readAllBytes(Paths.get(sourcePath)));

        CharStream input = CharStreams.fromString(code);
        CCompilerLexer lexer = new CCompilerLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        System.out.println("\n=== Lexical Analysis ===");
        tokens.fill();
        for (Token token : tokens.getTokens()) {
            String tokenName = CCompilerLexer.VOCABULARY.getSymbolicName(token.getType());
            if (tokenName != null) {
                System.out.printf("Token: %-12s Type: %-15s Line: %d\n", token.getText(), tokenName, token.getLine());
            }
        }

        System.out.println("\n=== Syntax Analysis ===");
        CCompilerParser parser = new CCompilerParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new SyntaxErrorListener());

        CCompilerParser.CompileUnitContext tree = parser.compileUnit();

        System.out.println("\n--- Shortened Syntax (Flattened Code Text) ---");
        System.out.println(tree.getText());

        System.out.println("\n--- Full Syntax Tree (Rule Structure) ---");
        System.out.println(tree.toStringTree(parser));

        System.out.println("\n=== Semantic Analysis ===");
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.visit(tree);

        System.out.println("\n=== Intermediate Code Generation ===");
        TACGenerator tac = new TACGenerator();
        tac.visit(tree);
        for (String instr : tac.getInstructions()) {
            System.out.println(instr);
        }

        System.out.println("\n=== Final Code Generation (Assembly) ===");
        AssemblyGenerator asmGen = new AssemblyGenerator(tac.getInstructions());
        asmGen.generate();
        for (String line : asmGen.getAssembly()) {
            System.out.println(line);
        }

        System.out.println("\n=== Executing Assembly Code ===");
        AssemblyInterpreter interpreter = new AssemblyInterpreter(asmGen.getAssembly());
        interpreter.run();
        System.out.println("Result: " + interpreter.getReturnValue());
    }
}

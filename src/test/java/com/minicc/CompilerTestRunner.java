package com.minicc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CompilerTestRunner {

    @Test
    public void testFullCompilationPipeline() throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("test1.c").toURI());
        String code = Files.readString(path);

        CharStream input = CharStreams.fromString(code);
        CCompilerLexer lexer = new CCompilerLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        assertFalse(tokens.getTokens().isEmpty(), "Lexical analysis failed: No tokens found");

        CCompilerParser parser = new CCompilerParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new SyntaxErrorListener());
        ParseTree tree = parser.compileUnit();
        assertNotNull(tree, "Syntax analysis failed: Parse tree is null");

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.visit(tree);

        TACGenerator tacGenerator = new TACGenerator();
        tacGenerator.visit(tree);
        List<String> tac = tacGenerator.getInstructions();

        assertTrue(tac.contains("t0 = 10 + 20"), "TAC missing expected operation");
        assertTrue(tac.contains("x = t0"), "TAC missing expected assignment");
        assertTrue(tac.contains("return x"), "TAC missing return statement");

        AssemblyGenerator asmGen = new AssemblyGenerator(tac);
        asmGen.generate();
        List<String> assembly = asmGen.getAssembly();

        assertTrue(assembly.stream().anyMatch(s -> s.contains("MOV R0, 10")), "Assembly missing R0 = 10");
        assertTrue(assembly.stream().anyMatch(s -> s.contains("MOV R1, 20")), "Assembly missing R1 = 20");
        assertTrue(assembly.stream().anyMatch(s -> s.contains("ADD R2, R0, R1")), "Assembly missing ADD");

        int result = asmGen.simulateExecution();
        assertEquals(30, result, "Computation result incorrect");
    }
}

package com.minicc;

import com.minicc.CCompilerBaseVisitor;
import com.minicc.CCompilerParser;

import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer extends CCompilerBaseVisitor<Void> {

    private final Map<String, Integer> symbolTable = new HashMap<>();

    @Override
    public Void visitVarDecl(CCompilerParser.VarDeclContext ctx) {
        String type = ctx.basicType().getText();

        for (CCompilerParser.VarDefContext var : ctx.varDef()) {
            String varName = var.ID().getText();
            int line = var.ID().getSymbol().getLine();

            if (symbolTable.containsKey(varName)) {
                System.err.println("[ERROR] Variable '" + varName + "' redeclared at line " + line);
            } else {
                symbolTable.put(varName, line);
                System.out.println("[INFO] Declared variable: " + type + " " + varName);
            }
        }

        return null;
    }

    @Override
    public Void visitAssignStatement(CCompilerParser.AssignStatementContext ctx) {
        String varName = ctx.lVal().ID().getText();
        int line = ctx.lVal().ID().getSymbol().getLine();

        if (!symbolTable.containsKey(varName)) {
            System.err.println("[ERROR] Undeclared variable '" + varName + "' used at line " + line);
        }

        return null;
    }

    @Override
    public Void visitFuncDef(CCompilerParser.FuncDefContext ctx) {
        symbolTable.clear();
        return super.visitFuncDef(ctx);
    }
}

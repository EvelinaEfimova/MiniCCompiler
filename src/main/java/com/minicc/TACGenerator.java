
package com.minicc;

import com.minicc.CCompilerBaseVisitor;
import com.minicc.CCompilerParser;

import java.util.ArrayList;
import java.util.List;

public class TACGenerator extends CCompilerBaseVisitor<String> {

    private int tempVarCount = 0;
    private final List<String> instructions = new ArrayList<>();

    private String newTemp() {
        return "t" + (tempVarCount++);
    }

    public List<String> getInstructions() {
        return instructions;
    }

    @Override
    public String visitAssignStatement(CCompilerParser.AssignStatementContext ctx) {
        String varName = visit(ctx.lVal());
        String value = visit(ctx.expr());
        instructions.add(varName + " = " + value);
        return null;
    }

    @Override
    public String visitAdditiveExpr(CCompilerParser.AdditiveExprContext ctx) {
        String left = visit(ctx.multiplicativeExpr(0));
        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++) {
            String right = visit(ctx.multiplicativeExpr(i));
            String op = ctx.addOp(i - 1).getText();
            String temp = newTemp();
            instructions.add(temp + " = " + left + " " + op + " " + right);
            left = temp;
        }
        return left;
    }

    @Override
    public String visitMultiplicativeExpr(CCompilerParser.MultiplicativeExprContext ctx) {
        String left = visit(ctx.unaryExpr(0));
        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String right = visit(ctx.unaryExpr(i));
            String op = ctx.mulOp(i - 1).getText();
            String temp = newTemp();
            instructions.add(temp + " = " + left + " " + op + " " + right);
            left = temp;
        }
        return left;
    }

    @Override
    public String visitUnaryExpr(CCompilerParser.UnaryExprContext ctx) {
        return visit(ctx.primaryExpr());
    }

    @Override
    public String visitParenExpr(CCompilerParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public String visitLeftValue(CCompilerParser.LeftValueContext ctx) {
        return visit(ctx.lVal());
    }

    @Override
    public String visitBasicNum(CCompilerParser.BasicNumContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitLVal(CCompilerParser.LValContext ctx) {
        return ctx.ID().getText();
    }

    @Override
    public String visitVarDecl(CCompilerParser.VarDeclContext ctx) {
        return null;
    }

    @Override
    public String visitExpr(CCompilerParser.ExprContext ctx) {
        return visit(ctx.cond());
    }

    @Override
    public String visitCond(CCompilerParser.CondContext ctx) {
        return visit(ctx.logicalOrExpr());
    }

    @Override
    public String visitLogicalOrExpr(CCompilerParser.LogicalOrExprContext ctx) {
        return visit(ctx.logicalAndExpr(0));
    }

    @Override
    public String visitLogicalAndExpr(CCompilerParser.LogicalAndExprContext ctx) {
        return visit(ctx.equalityExpr(0));
    }

    @Override
    public String visitEqualityExpr(CCompilerParser.EqualityExprContext ctx) {
        return visit(ctx.relationalExpr(0));
    }

    @Override
    public String visitRelationalExpr(CCompilerParser.RelationalExprContext ctx) {
        return visit(ctx.additiveExpr(0));
    }

    @Override
    public String visitReturnStatement(CCompilerParser.ReturnStatementContext ctx) {
        if (ctx.expr() != null) {
            String value = visit(ctx.expr());
            instructions.add("return " + value);
        } else {
            instructions.add("return");
        }
        return null;
    }
}

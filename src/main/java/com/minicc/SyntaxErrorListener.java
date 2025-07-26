package com.minicc;

import org.antlr.v4.runtime.*;

public class SyntaxErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        System.err.printf("[SYNTAX ERROR] Line %d:%d - %s%n", line, charPositionInLine, msg);
    }
}

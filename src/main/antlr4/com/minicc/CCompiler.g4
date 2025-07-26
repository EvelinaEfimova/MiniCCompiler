grammar CCompiler;

// Entry point for compilation unit: multiple declarations or function definitions
compileUnit: (funcDef | decl)* EOF;

// --------------------------------------------------
// Functions
// --------------------------------------------------

funcDef: funcType ID LPAREN (funcParams)? RPAREN block;

funcType: INT | FLOAT | VOID;

funcParams: funcParam (COMMA funcParam)*;

funcParam:
    basicType ID (
        LBRACK (expr)? RBRACK (LBRACK expr RBRACK)*
    )?;

// --------------------------------------------------
// Blocks and Statements
// --------------------------------------------------

block: LBRACE blockItemList? RBRACE;

blockItemList: blockItem+;

blockItem: statement | decl;

statement:
      RETURN expr? SEMICOLON                             # returnStatement
    | lVal ASSIGN expr SEMICOLON                         # assignStatement
    | block                                              # blockStatement
    | expr? SEMICOLON                                    # expressionStatement
    | IF LPAREN cond RPAREN statement (ELSE statement)?  # ifelseStatement
    | WHILE LPAREN cond RPAREN statement                 # whileStatement
    | BREAK SEMICOLON                                    # breakStatement
    | CONTINUE SEMICOLON                                 # continueStatement;

// --------------------------------------------------
// Declarations
// --------------------------------------------------

decl: constDecl | varDecl;

basicType: INT | FLOAT;

constDecl: CONST basicType constDef (COMMA constDef)* SEMICOLON;

constDef: ID (LBRACK expr RBRACK)* ASSIGN initVal;

varDecl: basicType varDef (COMMA varDef)* SEMICOLON;

varDef: ID (LBRACK expr RBRACK)* (ASSIGN initVal)?;

// --------------------------------------------------
// Initial Values
// --------------------------------------------------

initVal:
      expr                                           # singleVal
    | LBRACE (initVal (COMMA initVal)*)? RBRACE      # multiVal;

// --------------------------------------------------
// Expressions
// --------------------------------------------------

expr: cond;

cond: logicalOrExpr;

logicalOrExpr: logicalAndExpr (OR logicalAndExpr)*;

logicalAndExpr: equalityExpr (AND equalityExpr)*;

equalityExpr: relationalExpr (equalityOp relationalExpr)*;

equalityOp: EQ | NEQ;

relationalExpr: additiveExpr (relationalOp additiveExpr)*;

relationalOp: GE | GT | LE | LT;

additiveExpr: multiplicativeExpr (addOp multiplicativeExpr)*;

addOp: ADD | SUB;

multiplicativeExpr: unaryExpr (mulOp unaryExpr)*;

mulOp: MUL | DIV | MOD;

unaryExpr: (unaryOp)* primaryExpr;

unaryOp: ADD | SUB | NOT;

funcArgs: expr (COMMA expr)*;

primaryExpr:
      LPAREN expr RPAREN                     # parenExpr
    | lVal                                   # leftValue
    | number                                 # basicNum
    | ID LPAREN funcArgs? RPAREN             # funcCall;

lVal: ID (LBRACK expr RBRACK)*;

number: INT_LITERAL | FLOAT_LITERAL;

// --------------------------------------------------
// Lexer Rules
// --------------------------------------------------

// Punctuation
SEMICOLON: ';';
LBRACE: '{';
RBRACE: '}';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
COMMA: ',';
ASSIGN: '=';
// Logical
NOT: '!';
AND: '&&';
OR: '||';
// Arithmetic
ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
// Relational
GE: '>=';
LE: '<=';
GT: '>';
LT: '<';
EQ: '==';
NEQ: '!=';

// Keywords
RETURN: 'return';
INT: 'int';
FLOAT: 'float';
VOID: 'void';
IF: 'if';
ELSE: 'else';
CONST: 'const';
WHILE: 'while';
BREAK: 'break';
CONTINUE: 'continue';

// Identifiers and literals
ID: [a-zA-Z_][a-zA-Z0-9_]*;

INT_LITERAL:
      '0' [xX][0-9a-fA-F]+
    | '0' [0-7]*
    | [1-9][0-9]*
    | '0';

FLOAT_LITERAL:
    ([0-9]+ '.' [0-9]* | '.' [0-9]+ | [0-9]+ '.') ([eE][+-]? [0-9]+)?;

// Whitespace and comments
WS: [ \r\n\t]+ -> skip;

LINE_COMMENT: '//' ~[\r\n]* -> skip;

BLOCK_COMMENT: '/*' .*? '*/' -> skip;

grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE     : 'while' ;
FUNCTION  : 'function';
VAR       : 'var';
PRINT     : 'print';

// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL      : 'true' | 'false' ;
NULL      : 'null' ;

// Symbols
MUL       : '*' ;
DIV       : '/' ;
SEPARATOR : ';' ;
ADD       : '+' ;
SUB       : '-' ;
MOD       : '%' ;
LT        : '<' ;
GT        : '>' ;
LE        : '<=';
GE        : '>=';
EQ        : '==';
SET       : '=';
ARGSEP    : ',';

// Identifiers
ID        : [a-zA-Z|_][a-zA-Z|0-9|_]* ;

// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
WS            : [ \t]+ -> skip ; // ignore whitespace
BLOCK_COMMENT : '/*'.*?'*/' -> skip ; //ignore block


// ***Paring rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block                          # whileStat
    | PRINT '(' expr ')' SEPARATOR                      # printStat
    | SEPARATOR                                         # emptyStat
    ;

expr: '(' expr ')'                                      # parens
    | FUNCTION params '{' stat* '}'                     # functionDecl
    | expr args                                         # functionAppl
    | expr op=( '*' | '/' | '%' ) expr                  # MulDivMod
    | expr op=( '+' | '-' ) expr                        # AddSub
    | expr op=( '<' | '<=' | '>' | '>=' | '==' ) expr   # Compare
    | INT                                               # int
    | VAR ID (op='=' expr)?                             # varDecl
    | ID                                                # varAppl
    | ID op='=' expr                                    # Assign
    | (INT | BOOL | NULL)                               # constant
    ;

args: '(' (expr (',' expr)*)? ')'                       # arguments
    ;

params: '(' (ID (',' ID)*)? ')'                         # parameters
    ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;


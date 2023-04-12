grammar Crux;
program
 : declList EOF
 ;

stmtBlock
 : '{' stmtList '}'
 ;

stmtList
 :stmt*
 ;

stmt
 : varDecl
 | callStmt
 | assStmt
 | ifStmt
 | loopStmt
 | breakStmt
 | contStmt
 | rtnStmt
 ;

rtnStmt
 : Return exp0 ';'
 ;

contStmt
 : Cont ';'
 ;

breakStmt
 : Break ';'
 ;

loopStmt
 : Loop stmtBlock
 ;

ifStmt
 : If exp0 stmtBlock ( Else stmtBlock )?
 ;

callStmt
 : callExp ';'
 ;

assStmt
 : designator '=' exp0 ';'
 ;

declList
 : decl*
 ;

decl
 : varDecl
 | arrDecl
 | funcDef
 ;

funcDef
 : type Identifier '(' parList ')' stmtBlock
 ;

arrDecl
 : type Identifier '[' Integer ']' ';'
 ;

varDecl
 : type Identifier ';'
 ;

parList
 : ( par (',' par*) )
 ;

par
 : type Identifier
 ;

expList
 : ( exp0 (',' exp0*) )
 ;

callExp
 : Identifier '(' expList ')'
 ;

exp3
 : '!' exp3
 | '(' exp0 ')'
 | designator
 | callExp
 | literal
 ;

exp2
 : exp3
 | exp2 op2 exp3
 ;

exp1
 : exp2
 | exp1 op1 exp2
 ;

exp0
 : exp1 ( op0 exp1 )
 ;

op2
 : '*'
 | '/'
 | '&&'
 ;

 op1
 : '+'
 | '-'
 | '||'
 ;

 op0
 : '>='
 | '<='
 | '!='
 | '=='
 | '>'
 | '<'
 ;

type
 : Identifier
 ;

designator
 : Identifier ( '[' exp0 ']' )
 ;

literal
 : Integer
 | True
 | False
 ;

SemiColon: ';';

Comma: ',';

Assign: '=';

LessThan: '<';

GreaterThan: '>';

Equal: '==';

NotEqual: '!=';

LessEqual: '<=';

GreaterEqual: '>=';

Div: '/';

Mul: '*';

Sub: '-';

Add: '+';

CloseBracket: ']';

OpenBracket: '[';

CloseBrace: '}';

OpenBrace: '{';

CloseParen: ')';

OpenParen: '(';

Integer
 : '0'
 | [1-9] [0-9]*
 ;

True: 'true';
False: 'false';

Identifier
 : [a-zA-Z] [a-zA-Z0-9_]*
 ;

WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;

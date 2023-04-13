grammar Crux;

Open_Bracket: '[';

Close_Bracket: ']';

Open_Paren: '(';

Close_Paren: ')';

Open_Brace: '{';

Close_Brace: '}';

And: '&&';

Or: '||';

If: 'if';

Else: 'else';

For: 'for';

Break: 'break';

True: 'true';

False: 'false';

Return: 'return';

Not: '!';

Add: '+';

Sub: '-';

Mul: '*';

Div: '/';

Greater_Equal: '>=';

Less_Equal: '<=';

Not_Equal: '!=';

Equal: '==';

Greater_Than: '>';

Less_Than: '<';

Assign: '=';

Comma: ',';

SemiColon: ';';



Identifier
 : [a-zA-Z] [a-zA-Z0-9_]*
 ;

Integer
 : '0'
 | [1-9] [0-9]*
 ;

WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;

literal
 : Integer
 | True
 | False
 ;

 designator
  : Identifier ( Open_Bracket expr0 Close_Bracket )?
  ;

type
 : Identifier
 ;

op0
 : Greater_Equal
 | Less_Equal
 | Not_Equal
 | Equal
 | Greater_Than
 | Less_Than
 ;

op1
 : Add
 | Sub
 | Or
 ;

op2
 : Mul
 | Div
 | And
 ;

expr0
  : expr1 ( op0 expr1 )?
  ;

expr1
 : expr2
 | expr1 op1 expr2
 ;

expr2
 : expr3
 | expr2 op2 expr3
 ;

expr3
 : Not expr3
 | Open_Paren expr0 Close_Paren
 | designator
 | callExpr
 | literal
 ;


callExpr
 : Identifier Open_Paren exprList Close_Paren
 ;

exprList
 : (expr0 (Comma expr0)* )?
 ;

param
 : type Identifier
 ;

paramList
 : (param (Comma param)* )?
 ;

varDecl
 : type Identifier SemiColon
 ;

arrayDecl
 : type Identifier Open_Bracket Integer Close_Bracket SemiColon
 ;

functionDefn
 : type Identifier Open_Paren paramList Close_Paren stmtBlock
 ;

decl
 : varDecl
 | arrayDecl
 | functionDefn
 ;

declList
 : decl*
 ;

assignStmt
 : designator Assign expr0 SemiColon
 ;

assignStmtNoSemi
 : designator Assign expr0
 ;

callStmt
 : callExpr SemiColon
 ;

ifStmt
 : If expr0 stmtBlock ( Else stmtBlock )?
 ;

forStmt
 : For Open_Paren assignStmt expr0 SemiColon assignStmtNoSemi Close_Paren stmtBlock
 ;

breakStmt
 : Break SemiColon
 ;

returnStmt
 : Return expr0 SemiColon
 ;

stmt
 : varDecl
 | callStmt
 | assignStmt
 | ifStmt
 | forStmt
 | breakStmt
 | returnStmt
 ;

stmtList
 :stmt*
 ;

stmtBlock
 : Open_Brace stmtList Close_Brace
 ;

program
 : declList EOF
 ;



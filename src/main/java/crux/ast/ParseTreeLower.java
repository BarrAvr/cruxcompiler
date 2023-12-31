package crux.ast;

import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.pt.CruxBaseVisitor;
import crux.pt.CruxParser;
import crux.ast.types.*;
import crux.ast.SymbolTable.Symbol;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class will convert the parse tree generated by ANTLR to AST It follows the visitor pattern
 * where decls will be by DeclVisitor Class Stmts will be resolved by StmtVisitor Class Exprs will
 * be resolved by ExprVisitor Class
 */

public final class ParseTreeLower {
  private final DeclVisitor declVisitor = new DeclVisitor();
  private final StmtVisitor stmtVisitor = new StmtVisitor();
  private final ExprVisitor exprVisitor = new ExprVisitor();

  private final SymbolTable symTab;

  public ParseTreeLower(PrintStream err) {
    symTab = new SymbolTable(err);
  }

  private static Position makePosition(ParserRuleContext ctx) {
    var start = ctx.start;
    return new Position(start.getLine());
  }

  /**
   *
   * @return True if any errors
   */
  public boolean hasEncounteredError() {
    return symTab.hasEncounteredError();
  }


  /**
   * Lower top-level parse tree to AST
   *
   * @return a {@link DeclarationList} object representing the top-level AST.
   */

  public DeclarationList lower(CruxParser.ProgramContext program) {
    ArrayList<Declaration> list = new ArrayList<Declaration>();
    Position position = makePosition(program);
    symTab.enter();

    for(CruxParser.DeclContext context: program.declList().decl()){
      Declaration node = context.accept(declVisitor);
      list.add(node);
    }

    symTab.exit();
    return new DeclarationList(position, list);
  }

  /**
   * Lower stmt list by lower individual stmt into AST.
   *
   * @return a {@link StatementList} AST object.
   */

  
   private StatementList lower(CruxParser.StmtListContext stmtList) {
     ArrayList<Statement> list = new ArrayList<Statement>();
     Position position = makePosition(stmtList);

     for(CruxParser.StmtContext context: stmtList.stmt()){
       Statement node = context.accept(stmtVisitor);
       list.add(node);
     }

     return new StatementList(position, list);
   }
   

  /**
   * Similar to {@link #lower(CruxParser.StmtListContext)}, but handles symbol table as well.
   *
   * @return a {@link StatementList} AST object.
   */

  
   private StatementList lower(CruxParser.StmtBlockContext stmtBlock) {

      symTab.enter();
      if(stmtBlock == null){
          return new StatementList(null, new ArrayList<Statement>());
        }
        StatementList list = lower(stmtBlock.stmtList());


      symTab.exit();
      return list;
   }
   

  /**
   * A parse tree visitor to create AST nodes derived from {@link Declaration}
   */
  private final class DeclVisitor extends CruxBaseVisitor<Declaration> {
    /**
     * Visit a parse tree var decl and create an AST {@link VariableDeclaration}
     *
     * @return an AST {@link VariableDeclaration}
     */

    @Override
    public VariableDeclaration visitVarDecl(CruxParser.VarDeclContext ctx) {
      Position position = makePosition(ctx);
      Type type;//might need to fix
      if(ctx.type().getText().equals("int")){
        type = new IntType();
      }else if(ctx.type().getText().equals("bool")){
        type = new BoolType();
      }else{
        type = null;
      }
      String name = ctx.Identifier().getText();
      Symbol symbol  = symTab.add(position, name, type);

      return new VariableDeclaration(position, symbol);
    }


    /**
     * Visit a parse tree array decl and creates an AST {@link ArrayDeclaration}
     *
     * @return an AST {@link ArrayDeclaration}
     */
    @Override
    public Declaration visitArrayDecl(CruxParser.ArrayDeclContext ctx) {
      Position position = makePosition(ctx);
      Type type; ///might need to fix
      if(ctx.type().getText().equals("int")){
        type = new ArrayType(Integer.parseInt(ctx.Integer().getText()), new IntType());
      }else if(ctx.type().getText().equals("bool")){
        type = new ArrayType(Integer.parseInt(ctx.Integer().getText()), new BoolType());
      }else{
        type = null;
      }
      //int size = Integer.parseInt(ctx.Integer().getText());
      //Type array = new ArrayType(size, type);
      String name = ctx.Identifier().getText();
      Symbol symbol  = symTab.add(position, name, type);
      return new ArrayDeclaration(position, symbol);
    }

    /**
     * Visit a parse tree function definition and create an AST {@link FunctionDefinition}
     *
     * @return an AST {@link FunctionDefinition}
     */

    @Override
    public Declaration visitFunctionDefn(CruxParser.FunctionDefnContext ctx) {
      Position position = makePosition(ctx);
      
      Type type;
      if(ctx.type().getText().equals("int")){
        type = new IntType();
      }else if(ctx.type().getText().equals("bool")){
        type = new BoolType();
      }else{
        type = new VoidType();
      }

      TypeList params = new TypeList();

      for(CruxParser.ParamContext context: ctx.paramList().param()){
        if(context.type().getText().equals("int")){
          params.append(new IntType());
        }else if(context.type().getText().equals("bool")){
          params.append(new BoolType());
        }else{
          params.append(new VoidType());
        }
      }

      String name = ctx.Identifier().getText();
      Symbol symbol  = symTab.add(position, name, new FuncType(params, type));
      symTab.enter();
      ArrayList<Symbol> list = new ArrayList<Symbol>(); 

      for(CruxParser.ParamContext context: ctx.paramList().param()){

        if(context.type().getText().equals("int")){
          type = new IntType();
        }else if(context.type().getText().equals("bool")){
          type = new BoolType();
        }else{
          type = new VoidType();
        }
      
        String paramName = context.Identifier().getText();
        Symbol paramSymbol = symTab.add(position, paramName, type );
        list.add(paramSymbol);
      }

      StatementList sList = lower(ctx.stmtBlock().stmtList());
      symTab.exit();
      return new FunctionDefinition(position, symbol, list, sList);
      
      
    }
  }


  /**
   * A parse tree visitor to create AST nodes derived from {@link Statement}
   */

  private final class StmtVisitor extends CruxBaseVisitor<Statement> {
    /**
     * Visit a parse tree var decl and create an AST {@link VariableDeclaration}. Since
     * {@link VariableDeclaration} is both {@link Declaration} and {@link Statement}, we simply
     * delegate this to {@link DeclVisitor#visitArrayDecl(CruxParser.ArrayDeclContext)} which we
     * implement earlier.
     *
     * @return an AST {@link VariableDeclaration}
     */
    @Override
    public Statement visitVarDecl(CruxParser.VarDeclContext ctx) {
      return declVisitor.visitVarDecl(ctx);
    }

    
    /**
     * Visit a parse tree assignment stmt and create an AST {@link Assignment}
     *
     * @return an AST {@link Assignment}
     */

    @Override
    public Statement visitAssignStmt(CruxParser.AssignStmtContext ctx) {
      Position position = makePosition(ctx);
      Expression location = ctx.designator().accept(exprVisitor);
      Expression value = ctx.expr0().accept(exprVisitor);
      return new Assignment(position, location, value);
    }
    
    /**
     * Visit a parse tree assignment nosemi stmt and create an AST {@link Assignment}
     *
     * @return an AST {@link Assignment}
     */
    @Override
    public Statement visitAssignStmtNoSemi(CruxParser.AssignStmtNoSemiContext ctx) {
      Position position = makePosition(ctx);
      Expression location = ctx.designator().accept(exprVisitor);
      Expression value = ctx.expr0().accept(exprVisitor);
      return new Assignment(position, location, value);
    }

    /**
     * Visit a parse tree call stmt and create an AST {@link Call}. Since {@link Call} is both
     * {@link Expression} and {@link Statement}, we simply delegate this to
     * {@link ExprVisitor#visitCallExpr(CruxParser.CallExprContext)} that we will implement later.
     *
     * @return an AST {@link Call}
     */

    
    @Override
    public Statement visitCallStmt(CruxParser.CallStmtContext ctx) {
      return (Statement) exprVisitor.visitCallExpr(ctx.callExpr());
    }
     
    
    /**
     * Visit a parse tree if-else branch and create an AST {@link IfElseBranch}. The template code
     * shows partial implementations that visit the then block and else block recursively before
     * using those returned AST nodes to construct {@link IfElseBranch} object.
     *
     * @return an AST {@link IfElseBranch}
     */
    
    @Override
    public Statement visitIfStmt(CruxParser.IfStmtContext ctx) {
      Position position = makePosition(ctx);
      Expression condition = ctx.expr0().accept(exprVisitor);
      StatementList thenBlock = lower(ctx.stmtBlock(0));
      StatementList elseBlock = lower(ctx.stmtBlock(1));
      return new IfElseBranch(position, condition, thenBlock, elseBlock);

    }
     
    
//    /**
//     * Visit a parse tree for loop and create an AST {@link For}. You'll going to use a similar
//     * techniques as {@link #visitIfStmt(CruxParser.IfStmtContext)} to decompose this construction.
//     *
//     * @return an AST {@link Loop}
//     */

    @Override
    public Statement visitForStmt(CruxParser.ForStmtContext ctx) {
      Position position = makePosition(ctx);
      Assignment init = (Assignment) ctx.assignStmt().accept(stmtVisitor);
      Expression cond = ctx.expr0().accept(exprVisitor);
      Assignment increment = (Assignment) ctx.assignStmtNoSemi().accept(stmtVisitor);
      StatementList body = lower(ctx.stmtBlock());
      return new For(position, init, cond, increment, body);
    }
     

    /**
     * Visit a parse tree return stmt and create an AST {@link Return}. Here we show a simple
     * example of how to lower a simple parse tree construction.
     *
     * @return an AST {@link Return}
     */

    
    @Override
    public Statement visitReturnStmt(CruxParser.ReturnStmtContext ctx) {
      Position position = makePosition(ctx);
      Expression value = ctx.expr0().accept(exprVisitor);
      return new Return(position, value);
    }
     
    
    /**
     * Creates a Break node
     */
    
    @Override
    public Statement visitBreakStmt(CruxParser.BreakStmtContext ctx) {
      
      Position position = makePosition(ctx);
      return new Break(position);
    }
    
  }

  private final class ExprVisitor extends CruxBaseVisitor<Expression> {
    /**
     * Parse Expr0 to OpExpr Node Parsing the expr should be exactly as described in the grammer
     */
     @Override
     public Expression visitExpr0(CruxParser.Expr0Context ctx) {

      CruxParser.Op0Context op = ctx.op0(); //can be null
      if(op == null){
        return ctx.expr1(0).accept(exprVisitor);
      }
       Position position = makePosition(ctx);
       CruxParser.Expr1Context lhsCtx = ctx.expr1(0);
       CruxParser.Expr1Context rhsCtx = ctx.expr1(1); //can be null

       Expression lhsExpr = lhsCtx.accept(exprVisitor);

       Expression rhsExpr = rhsCtx.accept(exprVisitor);
       String opStr = op.getText();
       Operation operation = null;
       switch (opStr) {
         case "==":
           operation = Operation.EQ;
           break;
         case ">=":
           operation = Operation.GE;
           break;
         case ">":
           operation = Operation.GT;
           break;
         case "<=":
           operation = Operation.LE;
           break;
         case "<":
           operation = Operation.LT;
           break;
         case "!=":
           operation = Operation.NE;
           break;
       }


       return new OpExpr(position, operation, lhsExpr, rhsExpr);
     }

    /**
     * Parse Expr1 to OpExpr Node Parsing the expr should be exactly as described in the grammer
     */
     @Override
     public Expression visitExpr1(CruxParser.Expr1Context ctx) {

      CruxParser.Op1Context op1 = ctx.op1(); //can be null
      if(op1 == null){
        return ctx.expr2().accept(exprVisitor);
      }

       Position position = makePosition(ctx);
       CruxParser.Expr1Context lhsCtx = ctx.expr1();
       CruxParser.Expr2Context rhsCtx = ctx.expr2();

       Expression lhsExpr = lhsCtx.accept(exprVisitor);


       Expression rhsExpr = rhsCtx.accept(exprVisitor);
       String opStr = op1.getText();
       Operation operation = null;
       switch (opStr) {
         case "+":
           operation = Operation.ADD;
           break;
         case "-":
           operation = Operation.SUB;
           break;
         case "||":
           operation = Operation.LOGIC_OR;
           break;
       }

       return new OpExpr(position, operation, lhsExpr, rhsExpr);
     }

    /**
     * Parse Expr2 to OpExpr Node Parsing the expr should be exactly as described in the grammer
     */
     @Override
     public Expression visitExpr2(CruxParser.Expr2Context ctx) {

      CruxParser.Op2Context op = ctx.op2();
      if(op == null){
        return ctx.expr3().accept(exprVisitor);
      }
      Position position = makePosition(ctx);
       CruxParser.Expr2Context lhsCtx = ctx.expr2();
       CruxParser.Expr3Context rhsCtx = ctx.expr3();

       Expression lhsExpr = lhsCtx.accept(exprVisitor);

       Expression rhsExpr = rhsCtx.accept(exprVisitor);
       String opStr = op.getText();
       Operation operation = null;
       switch (opStr) {
         case "*":
           operation = Operation.MULT;
           break;
         case "/":
           operation = Operation.DIV;
           break;
         case "&&":
           operation = Operation.LOGIC_AND;
           break;
       }

       return new OpExpr(position, operation, lhsExpr, rhsExpr);
     }

    /**
     * Parse Expr3 to OpExpr Node Parsing the expr should be exactly as described in the grammer
     */
     @Override
     public Expression visitExpr3(CruxParser.Expr3Context ctx) {
       if(ctx.designator() != null){
         return ctx.designator().accept(exprVisitor);
       }else if(ctx.callExpr() != null){
         return ctx.callExpr().accept(exprVisitor);
       }else if(ctx.literal() != null){
         return ctx.literal().accept(exprVisitor);
       }else if(ctx.expr0() != null){
         return ctx.expr0().accept(exprVisitor);
       }else{
         return new OpExpr(makePosition(ctx), Operation.LOGIC_NOT, ctx.expr3().accept(exprVisitor), null);
       }

     }

    /**
     * Create an Call Node
     */
    @Override
    public Call visitCallExpr(CruxParser.CallExprContext ctx) {
      Position position = makePosition(ctx);
      String name = ctx.Identifier().getText();
      Symbol symbol = symTab.lookup(position, name);
      List<Expression> args = new ArrayList<Expression>();
      for(CruxParser.Expr0Context exprList : ctx.exprList().expr0()){
        Expression node = exprVisitor.visit(exprList);
        args.add(node);
      }
      return new Call(position, symbol, args);
    }

    /**
     * visitDesignator will check for a name or ArrayAccess FYI it should account for the case when
     * the designator was dereferenced
     */
    @Override
    public Expression visitDesignator(CruxParser.DesignatorContext ctx) {
      Position position = makePosition(ctx);
      String name = ctx.Identifier().getText();
      Symbol symbol = symTab.lookup(position, name);
      if(ctx.expr0() != null){
        Expression index = ctx.expr0().accept(exprVisitor);
        return new ArrayAccess(position, symbol, index);
      }else{
        return new VarAccess(position, symbol);
      }
    }

    /**
     * Create an Literal Node
     */
     @Override
     public Expression visitLiteral(CruxParser.LiteralContext ctx) {
       Position position = makePosition(ctx);
       if(ctx.True() != null){
         return new LiteralBool(position, true);
       }else if(ctx.False() != null){
         return new LiteralBool(position, false);
       }else{
         String number = ctx.getText();
         Integer integer = Integer.valueOf(number);
         return new LiteralInt(position, integer);
       }
     }
  }
}

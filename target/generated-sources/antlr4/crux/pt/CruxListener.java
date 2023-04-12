// Generated from crux\pt\Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CruxParser}.
 */
public interface CruxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CruxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CruxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void enterStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void exitStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void enterStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void exitStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(CruxParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(CruxParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#rtnStmt}.
	 * @param ctx the parse tree
	 */
	void enterRtnStmt(CruxParser.RtnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#rtnStmt}.
	 * @param ctx the parse tree
	 */
	void exitRtnStmt(CruxParser.RtnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#contStmt}.
	 * @param ctx the parse tree
	 */
	void enterContStmt(CruxParser.ContStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#contStmt}.
	 * @param ctx the parse tree
	 */
	void exitContStmt(CruxParser.ContStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 */
	void enterLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 */
	void exitLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 */
	void enterCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 */
	void exitCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#assStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssStmt(CruxParser.AssStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#assStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssStmt(CruxParser.AssStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 */
	void enterDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 */
	void exitDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(CruxParser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(CruxParser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(CruxParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(CruxParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#arrDecl}.
	 * @param ctx the parse tree
	 */
	void enterArrDecl(CruxParser.ArrDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#arrDecl}.
	 * @param ctx the parse tree
	 */
	void exitArrDecl(CruxParser.ArrDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#parList}.
	 * @param ctx the parse tree
	 */
	void enterParList(CruxParser.ParListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#parList}.
	 * @param ctx the parse tree
	 */
	void exitParList(CruxParser.ParListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#par}.
	 * @param ctx the parse tree
	 */
	void enterPar(CruxParser.ParContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#par}.
	 * @param ctx the parse tree
	 */
	void exitPar(CruxParser.ParContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#expList}.
	 * @param ctx the parse tree
	 */
	void enterExpList(CruxParser.ExpListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#expList}.
	 * @param ctx the parse tree
	 */
	void exitExpList(CruxParser.ExpListContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#callExp}.
	 * @param ctx the parse tree
	 */
	void enterCallExp(CruxParser.CallExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#callExp}.
	 * @param ctx the parse tree
	 */
	void exitCallExp(CruxParser.CallExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#exp3}.
	 * @param ctx the parse tree
	 */
	void enterExp3(CruxParser.Exp3Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#exp3}.
	 * @param ctx the parse tree
	 */
	void exitExp3(CruxParser.Exp3Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#exp2}.
	 * @param ctx the parse tree
	 */
	void enterExp2(CruxParser.Exp2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#exp2}.
	 * @param ctx the parse tree
	 */
	void exitExp2(CruxParser.Exp2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#exp1}.
	 * @param ctx the parse tree
	 */
	void enterExp1(CruxParser.Exp1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#exp1}.
	 * @param ctx the parse tree
	 */
	void exitExp1(CruxParser.Exp1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#exp0}.
	 * @param ctx the parse tree
	 */
	void enterExp0(CruxParser.Exp0Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#exp0}.
	 * @param ctx the parse tree
	 */
	void exitExp0(CruxParser.Exp0Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 */
	void enterOp2(CruxParser.Op2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 */
	void exitOp2(CruxParser.Op2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 */
	void enterOp1(CruxParser.Op1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 */
	void exitOp1(CruxParser.Op1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 */
	void enterOp0(CruxParser.Op0Context ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 */
	void exitOp0(CruxParser.Op0Context ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(CruxParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(CruxParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 */
	void enterDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 */
	void exitDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(CruxParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(CruxParser.LiteralContext ctx);
}
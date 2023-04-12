// Generated from crux\pt\Crux.g4 by ANTLR 4.7.2
package crux.pt;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CruxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CruxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CruxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CruxParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmtBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBlock(CruxParser.StmtBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(CruxParser.StmtListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(CruxParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#rtnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRtnStmt(CruxParser.RtnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#contStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContStmt(CruxParser.ContStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(CruxParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#loopStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStmt(CruxParser.LoopStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(CruxParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallStmt(CruxParser.CallStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#assStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssStmt(CruxParser.AssStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#declList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclList(CruxParser.DeclListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(CruxParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(CruxParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#arrDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrDecl(CruxParser.ArrDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#parList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParList(CruxParser.ParListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#par}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar(CruxParser.ParContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#expList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpList(CruxParser.ExpListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#callExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallExp(CruxParser.CallExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#exp3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp3(CruxParser.Exp3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#exp2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp2(CruxParser.Exp2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#exp1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp1(CruxParser.Exp1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#exp0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp0(CruxParser.Exp0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp2(CruxParser.Op2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp1(CruxParser.Op1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#op0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp0(CruxParser.Op0Context ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CruxParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#designator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesignator(CruxParser.DesignatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CruxParser.LiteralContext ctx);
}
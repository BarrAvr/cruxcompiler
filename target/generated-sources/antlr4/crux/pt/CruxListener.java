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
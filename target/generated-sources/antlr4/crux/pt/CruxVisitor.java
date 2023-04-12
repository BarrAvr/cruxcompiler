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
	 * Visit a parse tree produced by {@link CruxParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(CruxParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CruxParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CruxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(CruxParser.LiteralContext ctx);
}
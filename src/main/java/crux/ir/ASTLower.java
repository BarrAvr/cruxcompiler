package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.OpExpr.Operation;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class InstPair {
  Instruction start;
  Instruction end;
  Value value;

  InstPair(Instruction start, Instruction end, Value value){
    this.start = start;
    this.end = end;
    this.value = value;
  }

  InstPair(Instruction start, Instruction end){
    this.start = start;
    this.end = end;
    this.value = null;
  }

  InstPair(Instruction instruction, Value value){
    this.start = instruction;
    this.end = instruction;
    this.value = value;
  }

  InstPair(Instruction instruction){
    this.start = instruction;
    this.end = instruction;
    this.value = null;
  }

  Instruction getStart(){
    return start;
  }

  Instruction getEnd(){
    return end;
  }

  Value getValue(){
    return value;
  }

  public void setStart(Instruction start) {
    this.start = start;
  }

}


/**
 * Convert AST to IR and build the CFG
 */
public final class ASTLower implements NodeVisitor<InstPair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;

  private Map<Symbol, LocalVar> mCurrentLocalVarMap = null;

  /**
   * A constructor to initialize member variables
   */
  public ASTLower() {}

  public Program lower(DeclarationList ast) {
    visit(ast);
    return mCurrentProgram;
  }

  @Override
  public InstPair visit(DeclarationList declarationList) {
    //initialize mcurrentprogram
    mCurrentProgram = new Program();

    //visit children
    for (var child: declarationList.getChildren()){
      child.accept(this);
    }

    return null;
  }

  /**
   * This visitor should create a Function instance for the functionDefinition node, add parameters
   * to the localVarMap, add the function to the program, and init the function start Instruction.
   */
  @Override
  public InstPair visit(FunctionDefinition functionDefinition) {
//    Function temp_func = new Function(null, null);
//    mCurrentFunction.getTempVar();
//    mCurrentLocalVarMap = new HashMap<Symbol, LocalVar>();

    //Initialize mCurrentFunction and mCurrentLocalVarMap.
    mCurrentFunction = new Function(functionDefinition.getSymbol().getName(), (FuncType) functionDefinition.getSymbol().getType());
    mCurrentLocalVarMap = new HashMap<>();

    //For each argument, create a temp var and map its symbol to it.
    ArrayList<LocalVar> args = new ArrayList<>();
    for(var param: functionDefinition.getParameters()){


    }
    //Set the arguments for mCurrentFunction.
    //Add mCurrentFunction to mCurrentProgram.
    //Visit the function body.
    //Set the starting instruction of mCurrentFunction.
    //Set mCurrentFunction and mCurrentLocalVarMap to null after visiting.
    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    return null;
  }

  /**
   * Declarations, could be either local or Global
   */
  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    return null;
  }

  /**
   * Create a declaration for array and connected it to the CFG
   */
  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) {
    return null;
  }

  /**
   * LookUp the name in the map(s). For globals, we should do a load to get the value to load into a
   * LocalVar.
   */
  @Override
  public InstPair visit(VarAccess name) {
    return null;
  }

  /**
   * If the location is a VarAccess to a LocalVar, copy the value to it. If the location is a
   * VarAccess to a global, store the value. If the location is ArrayAccess, store the value.
   */
  @Override
  public InstPair visit(Assignment assignment) {
    return null;
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    return null;
  }

  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {
    return null;
  }

  private InstPair visit(Expression expression) {
    return null;
  }

  /**
   * It should compute the address into the array, do the load, and return the value in a LocalVar.
   */
  @Override
  public InstPair visit(ArrayAccess access) {
    return null;
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralBool literalBool) {
    LocalVar destVar = mCurrentFunction.getTempVar(literalBool.getType());
    Value source = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    CopyInst instruction = new CopyInst(destVar, source);
    return new InstPair(instruction);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralInt literalInt) {
    LocalVar destVar = mCurrentFunction.getTempVar(literalInt.getType());
    Value source = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    CopyInst instruction = new CopyInst(destVar, source);
    return new InstPair(instruction);
  }

  /**
   * Lower a Return.
   */
  @Override
  public InstPair visit(Return ret) {
    return null;
  }

  /**
   * Break Node
   */
  @Override
  public InstPair visit(Break brk) {
    return null;
  }

  /**
   * Implement If Then Else statements.
   */
  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    return null;
  }

  /**
   * Implement for loops.
   */
  @Override
  public InstPair visit(For loop) {
    return null;
  }
}

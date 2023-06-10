package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

      LocalVar tempVar = mCurrentFunction.getTempVar(param.getType());
      mCurrentLocalVarMap.put(param, tempVar);
      args.add(tempVar);
    }
    //Set the arguments for mCurrentFunction.
    mCurrentFunction.setArguments(args);
    //Add mCurrentFunction to mCurrentProgram.
    mCurrentProgram.addFunction(mCurrentFunction);
    //Visit the function body.
    InstPair pair = functionDefinition.getStatements().accept(this);
    //Set the starting instruction of mCurrentFunction.
    mCurrentFunction.setStart(pair.start);
    //Set mCurrentFunction and mCurrentLocalVarMap to null after visiting.
    mCurrentFunction = null;
    mCurrentLocalVarMap = null;

    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    //Visit each statement and add an edge between each InstPair. Return an InstPair for the entire list.
    //Use a NopInst to make it easier.

    InstPair ret = new InstPair(new NopInst());
    List<Node> statements = statementList.getChildren();

    for(Node statement : statements){
      var temp = statement.accept(this);
      ret.getEnd().setNext(0, temp.getStart());
      ret.end = temp.getEnd();
    }

    return ret;
  }

  /**
   * Declarations, could be either local or Global
   */
  @Override
  public InstPair visit(VariableDeclaration variableDeclaration) {
    //If mCurrentFunction is null, this is a global variable. Add a GlobalDecl to mCurrentProgram.
    //Otherwise, it is a local variable. Allocate a temp var and add it to mCurrentLocalVarMap.
    if(mCurrentFunction == null) {
      var intCons = IntegerConstant.get(mCurrentProgram, 1);
      var glob = new GlobalDecl(variableDeclaration.getSymbol(), intCons);
      mCurrentProgram.addGlobalVar(glob);
    } else {
      var temp = mCurrentFunction.getTempVar(variableDeclaration.getType());
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), temp);
    }
    //No instructions need to be done. Return an InstPair of a NopInst if you don’t want to do null checks in visit(StatmentList).
    return new InstPair(new NopInst());
  }

  /**
   * Create a declaration for array and connected it to the CFG
   */
  @Override
  public InstPair visit(ArrayDeclaration arrayDeclaration) {
    //All array declarations are global. Create and add a GlobalDecl to mCurrentProgram.
    ArrayType arr = (ArrayType) arrayDeclaration.getSymbol().getType();
    var intCons = IntegerConstant.get(mCurrentProgram, arr.getExtent());
    var glob = new GlobalDecl(arrayDeclaration.getSymbol(), intCons);
    mCurrentProgram.addGlobalVar(glob);
    return null;
  }

  /**
   * LookUp the name in the map(s). For globals, we should do a load to get the value to load into a
   * LocalVar.
   */
  @Override
  public InstPair visit(VarAccess name) {
    //If the symbol is in mCurrentLocalVarMap, it is a local variable/parameter. Return the LocalVar from the map (no instructions necessary).
    //Otherwise, create a temp AddressVar and AddressAt instruction to store the address to the global variable.
    Symbol sym = name.getSymbol();
    if(mCurrentLocalVarMap.containsKey(sym)){
      return new InstPair(new NopInst(), mCurrentLocalVarMap.get(sym));
    }else{
      var tempVar = mCurrentFunction.getTempAddressVar(name.getType());
      var tempAt = new AddressAt(tempVar, name.getSymbol());
      var tempLocal = mCurrentFunction.getTempVar(name.getType());
      var tempLoad = new LoadInst(tempLocal,tempVar);
      tempAt.setNext(0, tempLoad);
      return new InstPair(tempAt, tempLoad, tempLocal);
    }
  }

  /**
   * If the location is a VarAccess to a LocalVar, copy the value to it. If the location is a
   * VarAccess to a global, store the value. If the location is ArrayAccess, store the value.
   */
  @Override
  public InstPair visit(Assignment assignment) {
    //Visit the lhs and rhs expressions.
    var lhs = assignment.getLocation().accept(this);
    var rhs = assignment.getValue().accept(this);
    Instruction temp;
    //If the lhs InstPair is a local var, use CopyInst.
    if(lhs.getValue().getClass() == LocalVar.class){
      temp = new CopyInst((LocalVar) lhs.getValue(), rhs.getValue());
    }
    //If the lhs InstPair is a global var, use StoreInst.
    else{
      if (mCurrentLocalVarMap.get(((VarAccess)assignment.getLocation()).getSymbol()) == null) {

        temp = new StoreInst(((LoadInst) rhs.end).getDst(), (AddressVar) lhs.getValue());
      }
      else {
        temp = new StoreInst((LocalVar) rhs.getValue(), (AddressVar) lhs.getValue());
      }
    }
    //add rhs add temp
    lhs.getEnd().setNext(0, rhs.getStart());
    lhs.end = rhs.getEnd();

    InstPair iPair = new InstPair(temp, temp, null);
    lhs.getEnd().setNext(0,iPair.getStart());
    lhs.end = iPair.getEnd();

    return lhs;
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    //Visit each argument to construct its CFG and add a localVar containing the argument value to the param list.
    List<LocalVar> args = new ArrayList<>();
    InstPair temp = null;
    Instruction start = null;
    Instruction end = null;
    for(var arg: call.getArguments()){
      temp = arg.accept(this);
      args.add((LocalVar) temp.getValue());
      if(start == null) {
        start = temp.getStart();
      }else{
        end.setNext(0, temp.getStart());
      }
      end = temp.getEnd();
    }
    //If function is not void, create a temp var for the return value and pass that as the InstPair’s value.
    //Construct CallInst with the function symbol.
    var tempRet = mCurrentFunction.getTempVar(((FuncType) call.getCallee().getType()).getRet());
    CallInst callInst = new CallInst(tempRet, call.getCallee(), args);
    if (temp == null){
      temp = new InstPair(start, end, null);
      temp.getEnd().setNext(0, callInst);
      return new InstPair(temp.getStart(), callInst, tempRet);
    }
    return new InstPair(callInst, callInst, callInst.getDst());
  }

  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {
    List<Node> children = operation.getChildren();
    InstPair left = children.get(0).accept(this);
    InstPair right = children.get(1).accept(this);
    left.getEnd().setNext(0, right.getStart());

    LocalVar v = mCurrentFunction.getTempVar(left.getValue().getType());
    LocalVar lhs = mCurrentFunction.getTempVar(left.getValue().getType());
    CopyInst lhs_copy = new CopyInst(lhs, left.getValue());
    LocalVar rhs = mCurrentFunction.getTempVar(right.getValue().getType());
    CopyInst rhs_copy = new CopyInst(rhs, right.getValue());
    switch(operation.getOp()) {
      case ADD:
      case SUB:
      case MULT:
      case DIV:
        BinaryOperator b = new BinaryOperator(opToBOp(operation), v, lhs, rhs); //not sure if whether to use lhs or lhs_copygetDstVar()
        right.getEnd().setNext(0, b);
        return new InstPair(left.getStart(), b, v);
      case GE:
      case GT:
      case LE:
      case LT:
      case EQ:
      case NE:
        CompareInst c = new CompareInst(v, opToCmpInstPrd(operation), lhs, rhs);
        right.getEnd().setNext(0, c);
        return new InstPair(left.getStart(), c, v);
      case LOGIC_NOT:
        UnaryNotInst u = new UnaryNotInst(v, lhs); //I think lhs should be used instead of rhs but unsure
        right.getEnd().setNext(0, u);
        return new InstPair(left.getStart(), u, v);
      case LOGIC_OR:
      case LOGIC_AND:
        //"no instruction for LOGIC_AND/OR?" - 2023 Discussion 7 Slide 43
      default:
        return null;
    }
  }

  private BinaryOperator.Op opToBOp(OpExpr op){
    switch(op.getOp()) {
      case ADD:
        return BinaryOperator.Op.Add;
      case SUB:
        return BinaryOperator.Op.Sub;
      case MULT:
        return BinaryOperator.Op.Mul;
      case DIV:
        return BinaryOperator.Op.Div;
      default:
        return null;
    }
  }

  private CompareInst.Predicate opToCmpInstPrd(OpExpr op){
    switch(op.getOp()) {
      case GE:
        return CompareInst.Predicate.GE;
      case GT:
        return CompareInst.Predicate.GT;
      case LE:
        return CompareInst.Predicate.LE;
      case LT:
        return CompareInst.Predicate.LT;
      case EQ:
        return CompareInst.Predicate.EQ;
      case NE:
        return CompareInst.Predicate.NE;
      default:
        return null;
    }
  }

  private InstPair visit(Expression expression) {
    //In the repo by mistake.
    //You can ignore this.
    return null;
  }

  /**
   * It should compute the address into the array, do the load, and return the value in a LocalVar.
   */
  @Override
  public InstPair visit(ArrayAccess access) {
    //Visit the index.
    var index = access.getIndex().accept(this);
    //Create a temp AddressVar and AddressAt to store the address to the global variable.
    var tempAdvar = mCurrentFunction.getTempAddressVar(access.getType());
    var tempAdat = new AddressAt(tempAdvar, access.getBase(), (LocalVar) index.getValue());
    index.getEnd().setNext(0,tempAdat);

    return new InstPair(index.getStart(), tempAdat, tempAdvar);
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
    //Visit the return expression.
    var exp = ret.getValue().accept(this);

    //Pass its value into a ReturnInst.
    var retInst = new ReturnInst((LocalVar) exp.getValue());
    exp.getEnd().setNext(0, retInst);
    return new InstPair(exp.getStart(), retInst);
  }

  /**
   * Break Node
   */
  @Override
  public InstPair visit(Break brk) {
    //Put the current loop exit in the instPair
    //Need to keep track of current loop (hint: a global variable could work)

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

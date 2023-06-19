package crux.ir;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NodeVisitor;
import crux.ast.types.*;
import crux.ir.insts.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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

  public Stack<Instruction> breakStack = new Stack<>();

  /**
   * A constructor to initialize member variables
   */
  public ASTLower() {
    mCurrentLocalVarMap = new HashMap<Symbol, LocalVar>();
  }

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
    mCurrentLocalVarMap = new HashMap<Symbol, LocalVar>();

    //For each argument, create a temp var and map its symbol to it.
    ArrayList<LocalVar> args = new ArrayList<LocalVar>();
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
    mCurrentFunction.setStart(pair.getStart());

    //Set mCurrentFunction and mCurrentLocalVarMap to null after visiting.
    mCurrentFunction = null;
    mCurrentLocalVarMap = null;

    return null;
  }

  @Override
  public InstPair visit(StatementList statementList) {
    //Visit each statement and add an edge between each InstPair. Return an InstPair for the entire list.
    //Use a NopInst to make it easier.

    Instruction start = new NopInst();
    Instruction last = start;
    List<Node> statements = statementList.getChildren();

    for(Node statement : statements){
      var temp = statement.accept(this);
      last.setNext(0, temp.getStart());
      last = temp.getEnd();
    }

    return new InstPair(start, last);
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
      var temp = mCurrentFunction.getTempVar(variableDeclaration.getSymbol().getType());
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
      var n = new NopInst();
      return new InstPair(n, n, mCurrentLocalVarMap.get(sym));
    }else{
      var tempVar = mCurrentFunction.getTempAddressVar(name.getSymbol().getType());
      var tempAt = new AddressAt(tempVar, name.getSymbol());
      var tempLocal = mCurrentFunction.getTempVar(name.getSymbol().getType());
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
    //do not visit location Instead check if it is an ArrayAccess or global VarAccess.
    var lhs = assignment.getLocation();
    var rhs = assignment.getValue();

    Symbol temp = null;
    //Instead check if it is an ArrayAccess or global VarAccess.
    if(lhs.getClass().equals(VarAccess.class)){
      temp = ((VarAccess) lhs).getSymbol();
      var get = mCurrentLocalVarMap.get(temp);
      if (get == null){

        var advar = mCurrentFunction.getTempAddressVar(temp.getType());

        var right = rhs.accept(this);
        AddressAt adat = new AddressAt(advar, temp);
        StoreInst store = new StoreInst((LocalVar) right.getValue(), advar);
        right.getEnd().setNext(0, adat);
        adat.setNext(0, store);
        return new InstPair(right.getStart(), store);
      }
    }else{
      temp = ((ArrayAccess)lhs).getBase();
      var get = mCurrentLocalVarMap.get(temp);
      if (get == null){

        var advar = mCurrentFunction.getTempAddressVar(temp.getType());

        var index = ((ArrayAccess) lhs).getIndex().accept(this);
        var right = rhs.accept(this);
        AddressAt adat = new AddressAt(advar, temp, (LocalVar) index.getValue());
        index.getEnd().setNext(0, right.getStart());
        right.getEnd().setNext(0, adat);
        StoreInst store = new StoreInst((LocalVar) right.getValue(), advar);
        adat.setNext(0, store);
        return new InstPair(index.getStart(), store);
      }
    }

    InstPair left = lhs.accept(this);
    InstPair right = rhs.accept(this);
    right.getEnd().setNext(0, left.getStart());
    CopyInst copy = new CopyInst((LocalVar) left.getValue(), right.getValue());
    left.getEnd().setNext(0, copy);
    return new InstPair(right.getStart(), copy);
  }

  /**
   * Lower a Call.
   */
  @Override
  public InstPair visit(Call call) {
    //Visit each argument to construct its CFG and add a localVar containing the argument value to the param list.
    NopInst start = new NopInst();
    List<LocalVar> paramList = new ArrayList<>();
    Instruction last = start;
    LocalVar local = null;

    for(var arg: call.getArguments()){
      var temp = arg.accept(this);
      if (temp.getEnd().getClass().equals(LoadInst.class)){
        local = ((LoadInst) temp.getEnd()).getDst();
      }else {
        local = ((LocalVar) temp.getValue());
      }
      last.setNext(0, temp.getStart());
      last = temp.getEnd();
      paramList.add(local);
    }
    //If function is not void, create a temp var for the return value and pass that as the InstPair’s value.
    //Construct CallInst with the function symbol.
    var callee = call.getCallee();

    if(((FuncType) callee.getType()).getRet().getClass().equals(void.class)){
      CallInst callInst = new CallInst(callee, paramList);
      last.setNext(0, callInst);
      return new InstPair(start, callInst);
    }
    LocalVar tempVal = mCurrentFunction.getTempVar(((FuncType) callee.getType()).getRet());
    CallInst callInst = new CallInst(tempVal, callee, paramList);
    last.setNext(0, callInst);
    return new InstPair(start, callInst, tempVal);
  }

  /**
   * Handle operations like arithmetics and comparisons. Also handle logical operations (and,
   * or, not).
   */
  @Override
  public InstPair visit(OpExpr operation) {
    InstPair lhs;
    InstPair rhs;
    if(operation.getRight() != null){
      lhs = operation.getLeft().accept(this);
      rhs = operation.getRight().accept(this);
    }
    else {
      lhs = operation.getLeft().accept(this);
      LocalVar myLocalVar = mCurrentFunction.getTempVar(operation.getType());
      UnaryNotInst myUnaryNotInst = new UnaryNotInst(myLocalVar, (LocalVar) lhs.value);
      lhs.end.setNext(0, myUnaryNotInst);
      return new InstPair(lhs.start, myUnaryNotInst, myLocalVar);
    }

    CompareInst.Predicate myComparePredicate = null;
    BinaryOperator.Op myBinaryOp = null;
    String myBoolOp = null;
    if(operation.getOp().toString().equals("==")){
      myComparePredicate = CompareInst.Predicate.EQ;
    }else if(operation.getOp().toString().equals("!=")){
      myComparePredicate = CompareInst.Predicate.NE;
    }else if(operation.getOp().toString().equals(">")){
      myComparePredicate = CompareInst.Predicate.GT;
    }else if(operation.getOp().toString().equals("<")){
      myComparePredicate = CompareInst.Predicate.LT;
    }else if(operation.getOp().toString().equals(">=")){
      myComparePredicate = CompareInst.Predicate.GE;
    }else if(operation.getOp().toString().equals("<=")){
      myComparePredicate = CompareInst.Predicate.LE;
    }else if(operation.getOp().toString().equals("+")){
      myBinaryOp = BinaryOperator.Op.Add;
    }else if(operation.getOp().toString().equals("-")){
      myBinaryOp = BinaryOperator.Op.Sub;
    }else if(operation.getOp().toString().equals("*")){
      myBinaryOp = BinaryOperator.Op.Mul;
    }else if(operation.getOp().toString().equals("/")){
      myBinaryOp = BinaryOperator.Op.Div;
    }else if(operation.getOp().toString().equals("&&")){
      myBoolOp = "&&";
    }else if(operation.getOp().toString().equals("||")){
      myBoolOp = "||";
    }
    if(myComparePredicate != null){
      LocalVar myLocalVar = mCurrentFunction.getTempVar(operation.getType());
      CompareInst myCompareInst = new CompareInst(myLocalVar, myComparePredicate,
              (LocalVar) lhs.value,
              (LocalVar) rhs.value);
      lhs.end.setNext(0, rhs.start);
      rhs.end.setNext(0, myCompareInst);
      return new InstPair(lhs.start, myCompareInst, myLocalVar);
    }
    else if(myBinaryOp != null){
      LocalVar myLocalVar = mCurrentFunction.getTempVar(operation.getType());
      BinaryOperator myOpInst = new BinaryOperator(myBinaryOp, myLocalVar,
              (LocalVar) lhs.value,
              (LocalVar) rhs.value);
      lhs.end.setNext(0, rhs.start);
      rhs.end.setNext(0, myOpInst);
      return new InstPair(lhs.start, myOpInst, myLocalVar);
    }
    else if(myBoolOp != null && (myBoolOp.equals("&&") || myBoolOp.equals("||"))){

      JumpInst myJump;

      NopInst myThenBranch = new NopInst();
      if(myBoolOp.equals("&&")){
        LocalVar myLocalVar = mCurrentFunction.getTempVar(operation.getType());
        myJump = new JumpInst((LocalVar) lhs.value);
        lhs.end.setNext(0, myJump);
        CopyInst myCopyInst0 = new CopyInst(myLocalVar, lhs.value);
        myJump.setNext(0, myCopyInst0);
        myJump.setNext(1, rhs.start);
        CopyInst myCopyInst1 = new CopyInst(myLocalVar, rhs.value);
        rhs.end.setNext(0, myCopyInst1);
        NopInst myMergeInst = new NopInst();
        myCopyInst0.setNext(0, myMergeInst);
        myCopyInst1.setNext(0, myMergeInst);
        return new InstPair(lhs.start, myMergeInst, myLocalVar);
      }
      else{ //"||"
        LocalVar myLocalVar = mCurrentFunction.getTempVar(operation.getType());
        myJump = new JumpInst((LocalVar) lhs.value);
        lhs.end.setNext(0, myJump);
        CopyInst myCopyInst0 = new CopyInst(myLocalVar, lhs.value);
        CopyInst myCopyInst1 = new CopyInst(myLocalVar, rhs.value);
        myJump.setNext(0, rhs.start);
        myJump.setNext(1, myCopyInst0);
        NopInst myMergeInst = new NopInst();;
        rhs.end.setNext(0, myCopyInst1);
        myCopyInst0.setNext(0, myMergeInst);
        myCopyInst1.setNext(0, myMergeInst);

        return new InstPair(lhs.start, myMergeInst, myLocalVar);
      }

    }
    return null;
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

    var local = mCurrentFunction.getTempVar(access.getType());
    var loadInst = new LoadInst(local, tempAdvar);

    tempAdat.setNext(0, loadInst);

    return new InstPair(index.getStart(), loadInst, local);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralBool literalBool) {
    LocalVar destVar = mCurrentFunction.getTempVar(new BoolType());
    Value source = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    CopyInst instruction = new CopyInst(destVar, source);
    return new InstPair(instruction, instruction, destVar);
  }

  /**
   * Copy the literal into a tempVar
   */
  @Override
  public InstPair visit(LiteralInt literalInt) {
    LocalVar destVar = mCurrentFunction.getTempVar(new IntType());
    Value source = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    CopyInst instruction = new CopyInst(destVar, source);
    return new InstPair(instruction, instruction, destVar);
  }

  /**
   * Lower a Return.s
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
    var exitLoop = breakStack.pop();

    return new InstPair(exitLoop, new NopInst());
  }

  /**
   * Implement If Then Else statements.
   */
  @Override
  public InstPair visit(IfElseBranch ifElseBranch) {
    //Visit condition.
    var temp = ifElseBranch.getCondition().accept(this);
    //Use JumpInst.
    JumpInst jump = new JumpInst((LocalVar) temp.getValue());
    temp.getEnd().setNext(0, jump);
    //Visit thenBlock and elseBlock.
    var tempElse = ifElseBranch.getElseBlock().accept(this);
    var tempThen = ifElseBranch.getThenBlock().accept(this);
    jump.setNext(0, tempElse.getStart());
    jump.setNext(1, tempThen.getStart());
    NopInst nop = new NopInst();
    //Merge the blocks into a NopInst.
    tempThen.getEnd().setNext(0,nop);
    tempElse.getEnd().setNext(0, nop);

    return new InstPair(temp.getStart(), nop);
  }

  /**
   * Implement for loops.
   */
  @Override
  public InstPair visit(For loop) {
    //Visit the loop header
    var init = loop.getInit().accept(this);

    //Create a NopInst as the loop exit, and connect header to exit. Store the exit in a global variable ( since loops can be nested, outer loop exits need to be remembered in some way ) , so that break statements in the loop can find the exit.
    NopInst exit = new NopInst();
    breakStack.push(exit);


    //Visit the loop body , and add edge from header to body
    var body = loop.getBody().accept(this);
    var cond = loop.getCond().accept(this);
    var inc = loop.getIncrement().accept(this);

    init.getEnd().setNext(0, cond.getStart());
    JumpInst jump = new JumpInst((LocalVar) cond.getValue());
    cond.getEnd().setNext(0, jump);
    //Add two edges from the body, one to the loop header and one to the loop exit
    //set jump conditions 0 = exit, 1 = loop body
    //cond -> jump -> body or exit -> inc -> cond
    jump.setNext(0, exit);
    jump.setNext(1,body.getStart());
    body.getEnd().setNext(0, inc.getStart());
    inc.getEnd().setNext(0, cond.getStart());
    //Remove the current loop exit
    if(!breakStack.isEmpty()) {
      breakStack.pop();
    }

    return new InstPair(init.getStart(), exit);
  }
}

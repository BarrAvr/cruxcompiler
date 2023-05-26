package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will associate types with the AST nodes from Stage 2
 */
public final class TypeChecker {
  private final ArrayList<String> errors = new ArrayList<>();

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void check(DeclarationList ast) {
    var inferenceVisitor = new TypeInferenceVisitor();
    inferenceVisitor.visit(ast);
  }

  /**
   * Helper function, should be used to add error into the errors array
   */
  private void addTypeError(Node n, String message) {
    errors.add(String.format("TypeError%s[%s]", n.getPosition(), message));
  }

  /**
   * Helper function, should be used to record Types if the Type is an ErrorType then it will call
   * addTypeError
   */
  private void setNodeType(Node n, Type ty) {
    ((BaseNode) n).setType(ty);
    if (ty.getClass() == ErrorType.class) {
      var error = (ErrorType) ty;
      addTypeError(n, error.getMessage());
    }
  }

  /**
   * Helper to retrieve Type from the map
   */
  public Type getType(Node n) {
    return ((BaseNode) n).getType();
  }


  /**
   * This calls will visit each AST node and try to resolve it's type with the help of the
   * symbolTable.
   */
  private final class TypeInferenceVisitor extends NullNodeVisitor<Void> {
    @Override
    public Void visit(VarAccess vaccess) {
      Type type = vaccess.getSymbol().getType();
      setNodeType(vaccess, type);

      return null;
    }

    @Override //done -- not completely sure about the setNodeType for this one
    public Void visit(ArrayDeclaration arrayDeclaration) {
      
      //get the base type of the array

      if(((ArrayType) arrayDeclaration.getSymbol().getType()).getBase().equivalent(new IntType())){
        return null;
      }
      else if(((ArrayType) arrayDeclaration.getSymbol().getType()).getBase().equivalent(new BoolType())){
        return null;
      }
      else{
        addTypeError(arrayDeclaration, "Array size must be an integer or bool type " + arrayDeclaration.getSymbol().getType().getClass());
        return null;
      }
    }

    @Override //done 
    public Void visit(Assignment assignment) {
      //get assignment children
      var child = assignment.getChildren();

      //type check children
      var lhs = child.get(0); //varAccess or ArrayAccess 
      var rhs = child.get(1); //value

      //visit children
      lhs.accept(this);
      rhs.accept(this);
    
      //get locationType from lhs
      var locationType = ((BaseNode) assignment.getLocation()).getType();

       //get valueType from rhs
      var valueType = ((BaseNode) assignment.getValue()).getType();

      //Invoke locationType.assign(valueType)
      
      locationType.assign(valueType);

      //update AST node type to ↑
      setNodeType(assignment, locationType);

      return null;
    }

    @Override //done
    public Void visit(Break brk) {
      return null;
    }

    @Override //done
    public Void visit(Call call) {  

      //type check children
      var children = call.getChildren();

      //typelist to keep track of types of children
      TypeList typeList = new TypeList();

      for(var child : children){
        child.accept(this);

        //check to see if the child is a call
        if(child.getClass() != Call.class){
          typeList.append(getType(child));
        }
        else{
          //get the return type of the call and add that to the type list 
          var callType = (Call) child;
          var ReturnType = ((FuncType) callType.getCallee().getType()).getRet();
          typeList.append(ReturnType);
        }
      }

      //compare the argument list to the type list
      var callType = (FuncType) call.getCallee().getType();
      
      if(!callType.getArgs().equivalent(typeList)){
        addTypeError(call, "Types do not match");
      }
      

      //Invoke calleeType.call(argumentListType, which is going to be TypeList)
      callType.call(new TypeList());

      //Update AST node type to ↑
      setNodeType(call, callType);

      return null;
    }


    @Override //done
    public Void visit(DeclarationList declarationList) {

      //get children
      var children = declarationList.getChildren();

      //call accept on all children
      //int i = children.size();
      for (Node child : children) {
        child.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      //check the signature of main
      var symbol = functionDefinition.getSymbol();

      //check if the function is main
      if(symbol.getName().equals("main")){
        //check if the return type is void
        
        //Type type = functionDefinition.getSymbol().getType();
        //var returnType = ((FuncType) type).getRet();
        //if(returnType.getClass() != VoidType.class){
        //  addTypeError(functionDefinition, "Main must have return type void");
        //}

        //check if the parameter list is empty
        var parameterList = functionDefinition.getParameters();
        if(!parameterList.isEmpty()){
          addTypeError(functionDefinition, "Main must have no parameters");
        }
      }

      //visit the children
      var children = functionDefinition.getStatements();
      children.accept(this);


      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {

      //Visit the condition -> now the condition node type is updated
      ifElseBranch.getCondition().accept(this);

      //Check if the condition is BoolType
      Type conditionType = ((BaseNode) ifElseBranch.getCondition()).getType();
      if(conditionType.getClass() != BoolType.class){
        addTypeError(ifElseBranch, "Condition must be a boolean");
      }
      //Visit else and then block
      ifElseBranch.getElseBlock().accept(this);
      ifElseBranch.getThenBlock().accept(this);

      return null;
    }

    @Override //done --example in the slides
    public Void visit(ArrayAccess access) {
      var base = access.getBase().getType();
      Node node = access.getIndex();
      node.accept(this);
      var index = ((BaseNode) node).getType();
      var indexType = base.index(index);
      
      setNodeType(access, indexType);

      return null;
    }

    @Override
    public Void visit(LiteralBool literalBool) {

      //Set node type to new BoolType / IntType
      setNodeType(literalBool, new BoolType());
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      //Set node type to new BoolType / IntType
      setNodeType(literalInt, new IntType());
      return null;
    }

    @Override
    public Void visit(For forloop) {
      //Check condition similar to IfElseBranch
      //visit cond first
      forloop.getCond().accept(this);
      //var condition = forloop.getCond();

      //Check if the condition is BoolType
      Type conditionType = ((BaseNode) forloop.getCond()).getType();
      if(conditionType.getClass() != BoolType.class){
        addTypeError(forloop, "Condition must be a boolean");
      }
      //Visit children (init, increment, body)
      forloop.getInit().accept(this);
      forloop.getIncrement().accept(this);
      forloop.getBody().accept(this);

      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      //Type check left and right (right is null if op == LOGIC_NOT)
      var left = op.getLeft();
      var right = op.getRight();
      var operator = op.getOp();

      left.accept(this);

      if(operator != OpExpr.Operation.LOGIC_NOT){
        right.accept(this);
      }
      
      //idk how to type check this???
      //Call corresponding method depending on operator
      if(operator == OpExpr.Operation.ADD || operator == OpExpr.Operation.SUB || operator == OpExpr.Operation.MULT || operator == OpExpr.Operation.DIV){
        setNodeType(op, new IntType());
      } else if(operator == OpExpr.Operation.LOGIC_AND || operator == OpExpr.Operation.LOGIC_OR || operator == OpExpr.Operation.LOGIC_NOT){
        setNodeType(op, new BoolType());
      }else if (operator == OpExpr.Operation.EQ || operator == OpExpr.Operation.NE || operator == OpExpr.Operation.GE || operator == OpExpr.Operation.GT || operator == OpExpr.Operation.LE || operator == OpExpr.Operation.LT){
        setNodeType(op, new BoolType());
      }else {
        addTypeError(op, "Invalid operator");
      }
      //Update AST node type to ↑
      //did this in if statements above

      return null;
    }

    @Override
    public Void visit(Return ret) {
      //Type check return value
      var returnValue = ret.getValue();
      returnValue.accept(this);

      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      //Similar to DeclarationList
      //Iterate over statements and visit
      var children = statementList.getChildren();

      //visit all children
      for (var child : children) {
        child.accept(this);
      }

      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {
      //Two possible (base) types (IntType, BoolType)
      //Set lastStatementReturns to false
      if(variableDeclaration.getSymbol().getType().equivalent(new IntType())){
        setNodeType(variableDeclaration, new IntType());
        return null;
      }
      else if(variableDeclaration.getSymbol().getType().equivalent(new BoolType())){
        setNodeType(variableDeclaration, new BoolType());
        return null;
      }
      else{
        addTypeError(variableDeclaration, "Array size must be an integer or bool");
        return null;
      }
    }
  }
}

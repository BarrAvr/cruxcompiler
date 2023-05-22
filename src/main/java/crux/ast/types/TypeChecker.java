package crux.ast.types;

import crux.ast.SymbolTable.Symbol;
import crux.ast.*;
import crux.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      if(arrayDeclaration.getSymbol().getType().equivalent(new IntType()) || arrayDeclaration.getSymbol().getType().equivalent(new BoolType())){
        setNodeType(arrayDeclaration, arrayDeclaration.getSymbol().getType());
        return null;
      }
      else{
        addTypeError(arrayDeclaration, "Array size must be an integer or bool");
        return null;
      }
    }

    @Override
    public Void visit(Assignment assignment) {
      var child = assignment.getChildren();

      var lhs = child.get(0);
      var rhs = child.get(1);

      var lhsType = getType(lhs);
      var rhsType = getType(rhs);

      if (lhsType.getClass() == ErrorType.class) {
        setNodeType(assignment, lhsType);
        return null;
      }

      if (rhsType.getClass() == ErrorType.class) {
        setNodeType(assignment, rhsType);
        return null;
      }

      //not sure if this is correct



      return null;
    }

    @Override
    public Void visit(Break brk) {

      return null;
    }

    @Override 
    public Void visit(Call call) {  //not done
      var check = call.getCallee().getType();
      TypeList typeList = new TypeList();

      for (var child : call.getArguments()) {
        child.accept(this);
        typeList.append(getType(child));
      }

      return null;
    }


    @Override
    public Void visit(DeclarationList declarationList) {
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      //check the type
      var symbol = functionDefinition.getSymbol();
      
      if(symbol.getName().equals("main")){
        if(symbol.getType().getClass() != VoidType.class){
          addTypeError(functionDefinition, "main function must be void");
        }
      }

      //check the return type
      var returnType = ((FuncType) symbol.getType()).getRet();
      if(returnType.getClass() == ErrorType.class){
        addTypeError(functionDefinition, "return type is not valid");
      }
      
      //check the parameters
      var parameters = ((FuncType) symbol.getType()).getArgs();

      var statementList = functionDefinition.getStatements();

      //check the statements
      statementList.accept(this);

      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      return null;
    }

    @Override
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
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      return null;
    }

    @Override
    public Void visit(For forloop) {
      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      var left = op.getChildren().get(0);
      var right = op.getChildren().get(1);

      return null;
    }

    @Override
    public Void visit(Return ret) {
      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {
      return null;
    }
  }
}

package crux.ast;

import crux.ast.Position;
import crux.ast.types.*;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol table will map each symbol from Crux source code to its declaration or appearance in the
 * source. The symbol table is made up of scopes, Each scope is a map which maps an identifier to
 * it's symbol. Scopes are inserted to the table starting from the first scope (Global Scope). The
 * Global scope is the first scope in each Crux program and it contains all the built in functions
 * and names. The symbol table is an ArrayList of scops.
 */
public final class SymbolTable {

  /**
   * Symbol is used to record the name and type of names in the code. Names include function names,
   * global variables, global arrays, and local variables.
   */
  static public final class Symbol implements java.io.Serializable {
    static final long serialVersionUID = 12022L;
    private final String name;
    private final Type type;
    private final String error;

    /**
     *
     * @param name String
     * @param type the Type
     */
    private Symbol(String name, Type type) {
      this.name = name;
      this.type = type;
      this.error = null;
    }

    private Symbol(String name, String error) {
      this.name = name;
      this.type = null;
      this.error = error;
    }

    /**
     *
     * @return String the name
     */
    public String getName() {
      return name;
    }

    /**
     *
     * @return the type
     */
    public Type getType() {
      return type;
    }

    @Override
    public String toString() {
      if (error != null) {
        return String.format("Symbol(%s:%s)", name, error);
      }
      return String.format("Symbol(%s:%s)", name, type);
    }

    public String toString(boolean includeType) {
      if (error != null) {
        return toString();
      }
      return includeType ? toString() : String.format("Symbol(%s)", name);
    }
  }

  private final PrintStream err;
  private final ArrayList<Map<String, Symbol>> symbolScopes = new ArrayList<>();

  private boolean encounteredError = false;

  SymbolTable(PrintStream err) {
    this.err = err;
    //initialize the global scope
    symbolScopes.add(new HashMap<String, Symbol>());

    //add built in functions to the global scope
    add(new Position(0), "readInt", new FuncType(new TypeList(), new IntType()));
    add(new Position(0), "readChar", new FuncType(new TypeList(), new IntType()));
    add(new Position(0), "printBool", new FuncType(TypeList.of(new BoolType()), new VoidType())); 
    add(new Position(0), "printInt", new FuncType(TypeList.of(new IntType()), new VoidType()));
    add(new Position(0), "printChar", new FuncType(TypeList.of(new IntType()), new VoidType()));
    add(new Position(0), "println", new FuncType(new TypeList(), new VoidType()));
  }

  boolean hasEncounteredError() {
    return encounteredError;
  }

  /**
   * Called to tell symbol table we entered a new scope.
   */

  void enter() {
    //add new hash map to the symbol table
    symbolScopes.add(new HashMap<String, Symbol>());
  }

  /**
   * Called to tell symbol table we are exiting a scope.
   */

  void exit() {
    //pop last hash map from the symbol table
    symbolScopes.remove(symbolScopes.size() - 1);
  }

  /**
   * Insert a symbol to the table at the most recent scope. if the name already exists in the
   * current scope that's a declareation error.
   */
  Symbol add(Position pos, String name, Type type) {
    //add symbol to current scope. Return error if already exists.
    
    //make sure sybmol scope exists and size > 0
    if(symbolScopes != null && symbolScopes.size() != 0){

      Map<String, Symbol> currentScope = symbolScopes.get(symbolScopes.size() - 1);

      //check if name already exists in current scope
      if(currentScope.containsKey(name)){
        err.println("DeclarationError" + pos + "Variable " + name + " already declared in this scope.");
        encounteredError = true;
        return new Symbol(name, "DeclarationError");
      }
      //in the case that we do not find an existing name in the current scope, add the new symbol to the current scope
      else{
        currentScope.put(name, new Symbol(name, type));
        return new Symbol(name, type);
      } 
    }

    return null;
  }

  /**
   * lookup a name in the SymbolTable, if the name not found in the table it shouold encounter an
   * error and return a symbol with ResolveSymbolError error. if the symbol is found then return it.
   */
  Symbol lookup(Position pos, String name) {
    var symbol = find(name);
    if (symbol == null) {
      err.printf("ResolveSymbolError%s[Could not find %s.]%n", pos, name);
      encounteredError = true;
      return new Symbol(name, "ResolveSymbolError");
    } else {
      return symbol;
    }
  }

  /**
   * Try to find a symbol in the table starting form the most recent scope.
   */
  private Symbol find(String name) {
    // Looks up and returns the symbol with matching name. Return null if doesn’t exist. Private.

    if(name != null){
      for(int i = symbolScopes.size() - 1; i >= 0; i--){
        if(symbolScopes.get(i).containsKey(name)){
          return symbolScopes.get(i).get(name);
        }
      }
    }
    return null;
  }
}

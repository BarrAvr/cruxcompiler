package crux.backend;

import crux.ast.LiteralBool;
import crux.ast.SymbolTable.Symbol;
import crux.ir.*;
import crux.ir.insts.*;
import crux.printing.IRValueFormatter;

import java.util.*;

/**
 * Convert the CFG into Assembly Instructions
 */
public final class CodeGen extends InstVisitor {
  private final Program p;
  private final CodePrinter out;

  private HashMap<Variable, Integer> varMap = new HashMap<Variable, Integer>(); //variable or localvar?
  int varcount = 0;

  Integer getStackSlot(Variable v) {
    if (!varMap.containsKey(v)){
      varcount++;
      varMap.put(v, varcount);
    }
    return varMap.get(v);
  }

  public CodeGen(Program p) {
    this.p = p;
    // Do not change the file name that is outputted or it will
    // break the grader!

    out = new CodePrinter("a.s");
  }

  /**
   * It should allocate space for globals call genCode for each Function
   */
  public void genCode() {
    //TODO
    //generate global variables
    //
    for(Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();){
      GlobalDecl g = glob_it.next();
      String name = g.getSymbol().getName();
      int size = (int) g.getNumElement().getValue(); //this is size
      out.printCode(".comm" + name + ", " + size + ", 8");
    }

    int count[] = new int[1];
    for(Iterator<Function> func_it = p.getFunctions(); func_it.hasNext();){
      Function f = func_it.next();
      genCode(f, count);
    }

    out.close();
  }
  //todo
  public void genCode(Function func, int count[]) {
    //step 1
    HashMap<Instruction, String> labels = func.assignLabels(count);
    //step 2
    out.printCode(".globl " + func.getName());
    out.printLabel(func.getName() + ":");
    //step 3 emit function prologue
    int numslots = func.getNumTempVars()+func.getNumTempAddressVars();
    //slots must be rounded up to an even number so the stack is 16 byte aligned.
    out.printCode("enter $(8 * " + numslots + "), $0");
    //step 4
    List<LocalVar> arguments = func.getArguments();
    for (int i = 0;i < arguments.size(); i++) {
      switch (i) {
        case 0:
          out.printCode("movq %rdi, -8(%rbp)");
          break;
        case 1:
          out.printCode("movq %rsi, -16(%rbp)");
          break;
        case 2:
          out.printCode("movq %rdx, -24(%rbp)");
          break;
        case 3:
          out.printCode("movq %rcx, -32(%rbp)");
          break;
        case 4:
          out.printCode("movq %r8, -40(%rbp)");
          break;
        case 5:
          out.printCode("movq %r9, -48(%rbp)");

        default:
          int overflow = (i - 7 + 2) * 8;
          out.printCode("movq" + overflow + "(%rbp)" + ", %r10");
          out.printCode("movq %r10, " + (i * -8) + "(%rbp)");
      }
      varMap.put(arguments.get(i), i+1);
    }
    //step 5/step 6
    //Generate code for function body
    //Linearize CFG using jumps and labels
    //Use DFS traversal
    //Refer to Function.assignLabels(int count[])
    Stack<Instruction> visiting = new Stack<Instruction>();
    Stack<Instruction> visited = new Stack();
    Instruction start = func.getStart();
    visiting.push(start);

    while(!visiting.isEmpty()){
      Instruction current = visiting.pop();

      if(labels.containsKey(current)){
        out.printLabel(labels.get(current) + ":");
      }

      current.accept(this);
      if(current.getNext(1) != null){
        if(!visited.contains(current.getNext(1))) {
          visiting.push(current.getNext(1));
          visited.push(current.getNext(1));
        }
      }
      if(current.getNext(0) != null){
        if(!visited.contains(current.getNext(0))){
          visiting.push(current.getNext(0));
          visited.push(current.getNext(0));
        }else{
          out.printCode("jmp " + labels.get(current.getNext(0)));
        }
      }else{
        out.printCode("leave");
        out.printCode("ret");
      }
    }
  }

  public void visit(AddressAt i) {
    var dst = i.getDst();
    var base = i.getBase();
    var off = i.getOffset();
    var name = base.getName();

    out.printCode("movq " + name + "@GOTPCREL(%rip), %r11");

    if (off != null){
      out.printCode("movq " + -8 * varMap.get(off)+"(%rbp)" + ", %r10");
      out.printCode("imulq $8, %r10");
      out.printCode("addq %r10, %r11");
    }
    out.printCode("movq %r11, -8*" + varMap.get(dst) + "(%rbp)");
  }

  public void visit(BinaryOperator i) {}


//todo
  public void visit(CompareInst i) {
    switch (i.getPredicate()){
      case LT:

        break;
      case EQ:
        break;
      case GE:
        break;
      case GT:
        break;
      case LE:
        break;
      case NE:
        break;
      default:
    }
  }

  public void visit(CopyInst i) {
    int dest_offset = varMap.get(i.getDstVar());
    out.printCode("movq $" + i.getSrcValue() + "%r10");
    out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
  }

  public void visit(JumpInst i) {}


  public void visit(LoadInst i) {
//    out.printCode("movq " + offset_1 + "(%rbp)" + "%r10");
//    out.printCode("movq " + offset_2 + "(%rbp)" + "%r10");
//    out.printCode("movq %r10" + offset_2 + "(%r11)");
  }

  public void visit(NopInst i) {
    out.printCode("NOP");
  }

  public void visit(StoreInst i) {
    int offset_1 = getStackSlot(i.getSrcValue());
    int offset_2 = getStackSlot(i.getDestAddress());
    out.printCode("movq " + offset_1 + "(%rbp)" + "%r10");
    out.printCode("movq " + offset_2 + "(%rbp)" + "%r11");
    out.printCode("movq %r10" + offset_2 + "(%r11)");
  }

  public void visit(ReturnInst i) {
    var offset = -8 *getStackSlot(i.getReturnValue());
    out.printCode("movq " + offset + "(%rbp), %rax");
    out.printCode("leave");
    out.printCode("ret");
  }

  public void visit(CallInst i) {
    //todo
    //step 1: Store parameters in designated registers or stack
    //pass arguments to callee using args registers
    //generate assembly for call instruction
    //move retyrn value (if there is one) into return register
    Symbol callee = i.getCallee();
    List<LocalVar> params = i.getParams();
    int offset = getStackSlot(params.get(0)) * 8;

    out.printCode("movq -" + offset + "%rbp, %rdi");
    //step 2:
    String calleeName = callee.getName();
    out.printCode("call " + calleeName);
  }

  public void visit(UnaryNotInst i) {
    //Just subtract the value from $1.
    //movq $1, %r11
    //subq %r11, VAL
    out.printCode("movq $1, %r11");
    out.printCode("subq -8*" + varMap.get(i.getInner()) + "(%rbp), %r11");

  }
}

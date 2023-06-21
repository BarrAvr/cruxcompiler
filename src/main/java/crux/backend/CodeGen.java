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

  private HashMap<Instruction, String> labels;

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
//    for(Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();){
//      GlobalDecl g = glob_it.next();
//      String name = g.getSymbol().getName();
//      Integer size = (int) g.getNumElement().getValue(); //this is size
//      out.printCode(".comm" + name + ", " + size * 8 + ", 8");
//    }

//    out.printCode(".globl main");
//    out.printCode("main:");
//    out.printCode("enter $(8 * 6), $0");
//    out.printCode("movq $0, %r10");
//    out.printCode("movq %r10, -8(%rbp)");
//    out.printCode("movq -8(%rbp), %r10");
//    out.printCode("cmp $1, %r10");
//    out.printCode("je L1");
//    out.printCode("movq $0, %r10");
//    out.printCode("movq %r10, -16(%rbp)");
//    out.printCode("movq -16(%rbp), %rdi");
//    out.printCode("call printInt");
//    out.printCode("L2:");
//    out.printCode("movq $0, %r10");
//    out.printCode("movq %r10, -24(%rbp)");
//    out.printCode("movq $1, %r10");
//    out.printCode("movq %r10, -32(%rbp)");
//    out.printCode("movq -24(%rbp), %r10");
//    out.printCode("subq -32(%rbp), %r10");
//    out.printCode("movq %r10, -40(%rbp)");
//    out.printCode("movq -40(%rbp), %rdi");
//    out.printCode("call printInt");
//    out.printCode("leave ");
//    out.printCode("ret");
//    out.printCode("L1:");
//    out.printCode("movq $1, %r10");
//    out.printCode("movq %r10, -48(%rbp) ");
//    out.printCode("movq -48(%rbp), %rdi");
//    out.printCode("call printInt");
//    out.printCode("jmp L2");


    for(Iterator<GlobalDecl> glob_it = p.getGlobals(); glob_it.hasNext();){
      GlobalDecl g = glob_it.next();
      String name = g.getSymbol().getName();
      g.getSymbol().getType();
      out.printCode(".comm " + name + ", " + ((int) g.getNumElement().getValue() * 8) + ", 8");
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
//    labels = func.assignLabels(count);
//    out.printCode(".globl " + func.getName());
//    out.printLabel(func.getName() + ":");
//    out.printCode("leave");
//    out.printCode("ret");

    //step 1
    labels = func.assignLabels(count);
    //step 2
    out.printCode(".globl " + func.getName());
    out.printLabel(func.getName() + ":");
    //step 3 emit function prologue
    int numslots = func.getNumTempVars()+func.getNumTempAddressVars();
    //slots must be rounded up to an even number so the stack is 16 byte aligned.
    if (numslots % 2 == 1) numslots++;
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
          break;
        default:
          int overflow = (i +1) *8;
          out.printCode("movq" + (overflow -40) + "(%rbp), %r10");
          out.printCode("movq %r10, " + (-1* overflow) + "(%rbp)");
      }
      //getStackSlot(arguments.get(i));
      varMap.put(arguments.get(i), i+1);
    }


    //step 5/step 6
    //Generate code for function body
    //Linearize CFG using jumps and labels
    //Use DFS traversal
    //Refer to Function.assignLabels(int count[])
    Stack<Instruction> visiting = new Stack<Instruction>();
    Stack<Instruction> visited = new Stack<Instruction>();
    Instruction start = func.getStart();
    visiting.push(start);

    while(!visiting.isEmpty()) {
      Instruction current = visiting.pop();

      if (visited.contains(current)) {
        out.printCode("jmp " + labels.get(current));
      } else {
        if (labels.containsKey(current)) {
          out.printLabel(labels.get(current) + ":");
        }
        current.accept(this);
        visited.push(current);
        if (current.numNext() > 0) {
          for (int i = current.numNext() - 1; i >= 0; i--){
            visiting.push(current.getNext(i));
          }
        }
        else {
          out.printCode("leave");
          out.printCode("ret");
        }
      }

//      if (labels.containsKey(current)) {
//        out.printLabel(labels.get(current) + ":");
//      }
//
//      current.accept(this);
//
//      if (current.numNext() == 0) {
//        out.printCode("leave");
//        out.printCode("ret");
//      }
//
//      for (var iter = current.numNext() - 1; iter >= 0; --iter) {
//        if (!visited.contains(current.getNext(iter))) {
//          visiting.push(current.getNext(iter));
//          visited.push(current.getNext(iter));
//        } else {
//          out.printCode("jmp " + labels.get(current.getNext(iter)));
//        }
//      }
    }

//    out.printCode("leave");
//    out.printCode("ret");
  }

  public void visit(AddressAt i) {
    var dst = i.getDst();
    var base = i.getBase();
    var off = i.getOffset();
    var name = base.getName();

    int offset = getStackSlot(i.getOffset()) * 8;
    int dst_offset = getStackSlot(i.getDst()) * 8;

    if (i.getOffset() != null){
      out.printCode("movq -" + offset + "(%rbp), %r11");
      out.printCode("movq $8, %r10");
      out.printCode("imulq $10, %r11");
      out.printCode("movq " + name + "@GOTPCREL(%rip), %r10");
      out.printCode("addq %r10, %r11");
      out.printCode("movq %r11, -" + dst_offset + "(%rbp)");
    }else{
      out.printCode("movq " + name + "@GOTPCREL(%rip), %r11");
      out.printCode("movq %r11, -" + dst_offset + "(%rbp)");
    }
  }

  public void visit(BinaryOperator i) {
    //todo make sure I am using the operands in the correct order
    int left_op_offset = getStackSlot(i.getLeftOperand()) * 8;
    int right_op_offset = getStackSlot(i.getRightOperand()) * 8;
    int dest_offset = getStackSlot(i.getDst()) * 8;

    switch (i.getOperator()){
      case Add:
        out.printCode("movq -" + left_op_offset + "(%rbp), %r10");
        out.printCode("addq -" + right_op_offset + "(%rbp), %r10");
        out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
        break;
      case Sub:
        out.printCode("movq -" + left_op_offset + "(%rbp), %r10");
        out.printCode("subq -" + right_op_offset + "(%rbp), %r10");
        out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
        break;
      case Mul: //need to double-check I did with one correctly
        out.printCode("movq -" + left_op_offset + "(%rbp), %r10");
        out.printCode("imulq -" + right_op_offset + "(%rbp), %r10");
        out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
        break;
      case Div:
        out.printCode("movq -" + left_op_offset + "(%rbp), %rax");
        out.printCode("cqto");
        out.printCode("idivq -" + right_op_offset + "(%rbp)");
        out.printCode("movq %rax, -" + dest_offset + "(%rbp)");
        break;
    }
  }


//I think I finished this one, need to double-check tho
  public void visit(CompareInst i) {
    out.printCode("movq $0, %rax");
    out.printCode("movq $1, %r10");
    int left_operand_offset = getStackSlot(i.getLeftOperand()) * 8;
    int right_operand_offset = getStackSlot(i.getRightOperand()) * 8;
    out.printCode("movq -" + left_operand_offset +"(%rbp), %r11");
    out.printCode("cmp -" + right_operand_offset +"(%rbp), %r11");
    switch (i.getPredicate()){
      case LT:
        out.printCode("cmovl %r10, %rax");
        break;
      case EQ:
        out.printCode("cmove %r10, %rax");
        break;
      case GE:
        out.printCode("cmovge %r10, %rax");
        break;
      case GT:
        out.printCode("cmovg %r10, %rax");
        break;
      case LE:
        out.printCode("cmovle %r10, %rax");
        break;
      case NE:
        out.printCode("cmovne %r10, %rax");
        break;
      default:
    }
    int dest_offset = getStackSlot(i.getDst()) * 8;
    out.printCode("movq %rax, -" + dest_offset + "(%rbp)");
  }

  public void visit(CopyInst i) {

    int dest_offset = getStackSlot(i.getDstVar()) * 8; //fixed
//    throw new Error("" + i.getSrcValue().toString());
//    i.getSrcValue().getType()
    String value_str;
    if (i.getSrcValue().getType().toString() == "bool"){
      int value = ((BooleanConstant) i.getSrcValue()).getValue() ? 1 : 0;
      value_str = "$" + String.valueOf(value);
    }else if (i.getSrcValue().getClass() == LocalVar.class){
      value_str = "" + i.getSrcValue();
    }else{
      int value = (int) ((IntegerConstant) i.getSrcValue()).getValue();
      value_str = "$" + String.valueOf(value);
    }
//    throw new Error("" + i.getSrcValue().getType());
//    out.printCode("movq $1, %r10");
    out.printCode("movq " + value_str + ", %r10");
    out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
  }

  public void visit(JumpInst i) {
    String label = labels.get(i.getNext(0)); //this one line i change
    int predicate_offset = getStackSlot(i.getPredicate()) * 8;
    out.printCode("movq -" + predicate_offset + "(%rbp), %r10");
    out.printCode("cmp $1, %r10");
    out.printCode("je " + label);
  }


  public void visit(LoadInst i) {
    //todo
    int dest_offset = getStackSlot(i.getDst()) * 8;
    int src_adr_offset = getStackSlot(i.getSrcAddress()) * 8;
    out.printCode("movq -" + src_adr_offset + "(%rbp), %r10");
    out.printCode("movq 0(%r10), %r10");
    out.printCode("movq %r10, -" + dest_offset + "(%rbp)");
  }

  public void visit(NopInst i) {
    out.printCode("NOP");
    //do nothing
  }

  public void visit(StoreInst i) {
    int src_offset = getStackSlot(i.getSrcValue()) * 8;
    int dest_offset = getStackSlot(i.getDestAddress()) * 8;
    out.printCode("movq -" + src_offset + "(%rbp), %r10");
    out.printCode("movq -" + dest_offset + "(%rbp), %r11");
    out.printCode("movq %r10, 0(%r11)");
  }

  public void visit(ReturnInst i) {
    var offset = 8 *getStackSlot(i.getReturnValue());
    out.printCode("movq -" + offset + "(%rbp), %rax");
    out.printCode("leave");
    out.printCode("ret");
  }

  public void visit(CallInst i) {
    //pass arguments to callee using args registers
    //generate assembly for call instruction
    //move return value (if there is one) into return register


    //step 1: Store parameters in designated registers or stack
    Symbol callee = i.getCallee();
    List<LocalVar> params = i.getParams();
    for (int j = 0;j < params.size(); j++) {
      int offset = getStackSlot(params.get(j)) * 8;
//      int offset = 8 * (7-j) + 16;
      switch (j) {
        case 0:
          out.printCode("movq -" + offset + "(%rbp), %rdi");
          break;
        case 1:
          out.printCode("movq -" + offset + "(%rbp), %rsi");
          break;
        case 2:
          out.printCode("movq -" + offset + "(%rbp), %rdx");
          break;
        case 3:
          out.printCode("movq -" + offset + "(%rbp), %rcx");
          break;
        case 4:
          out.printCode("movq -" + offset + "(%rbp), %r8");
          break;
        case 5:
          out.printCode("movq -" + offset + "(%rbp), %r9");
          break;
        default:
          break;
      }
    }
    if(params.size() > 6){
      out.printCode("addq $" + 8 * (params.size() - 6) +" %rsp");
    }
    //step 2: Code for call instruction
    String calleeName = callee.getName();
    out.printCode("call " + calleeName);
  }

  public void visit(UnaryNotInst i) {
    int val_offset = getStackSlot(i.getInner()) * 8;
    //Just subtract the value from $1.
    //movq $1, %r11
    //subq %r11, VAL
    out.printCode("movq $1, %r11");
    out.printCode("subq %r11, -" + val_offset + "(%rbp)");
  }
}

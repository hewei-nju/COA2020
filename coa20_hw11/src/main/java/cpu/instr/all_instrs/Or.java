package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import cpu.registers.EFlag;

/**
 * @author heweistart
 * @create 2020-12-12-9:45
 */
public class Or implements Instruction {

    // 0x0D OR eAX,Iv
    // opcode: 8bits; eAx: 8bits; Iv: 32bits length = 8 + 32
    // eAx <- eAx or Iv
    // CF <- 0
    // OF <- 0
    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0x0D) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            String eaxVal = CPU_State.eax.read();
            String ans = new ALU().or(eaxVal, operand.getVal());
            CPU_State.eax.write(ans);
            new EFlag().setCF(false);
            new EFlag().setOF(false);
            return length;
        } else {
            return -1;
        }
    }
}

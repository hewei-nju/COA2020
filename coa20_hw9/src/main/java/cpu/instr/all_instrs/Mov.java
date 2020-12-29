package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;

/**
 * @author heweistart
 * @create 2020-12-12-9:53
 */
public class Mov implements Instruction {

    // 0xb8 MOV eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits  length = 8 + 32
    // eAx <- Iv
    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0xb8) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            CPU_State.eax.write(operand.getVal());
            return length;
        } else {
            return -1;
        }
    }
}

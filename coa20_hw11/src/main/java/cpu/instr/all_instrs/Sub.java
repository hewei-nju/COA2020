package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;

/**
 * @author heweistart
 * @create 2020-12-12-9:47
 */
public class Sub implements Instruction {

    // 0x2D SUB eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits; length = 8 + 32
    // eAX <- eAX - Iv
    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0x2D) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            System.out.println(CPU_State.eax.read());
            String ans = (new ALU().sub(operand.getVal(), CPU_State.eax.read()));
            CPU_State.eax.write(ans);
            return length;
        } else {
            return -1;
        }
    }
}

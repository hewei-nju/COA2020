package cpu.instr.all_instrs;
import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import cpu.registers.EFlag;

import java.util.Arrays;

/**
 * @author heweistart
 * @create 2020-12-12-9:46
 */
public class Sbb implements Instruction {

    // 0x1D SBB eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits; length = 8 + 32
    // eAX <- eAX - (Iv + CF)
    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0x1D) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            char [] cf = new char[32];
            Arrays.fill(cf, '0');
            if (((EFlag)CPU_State.eflag).getCF()) {
                cf[31] = '1';
            }
            String ans = (new ALU().sub(new ALU().add(String.valueOf(cf), operand.getVal()), CPU_State.eax.read()));
            CPU_State.eax.write(ans);
            return length;
        } else {
            return -1;
        }
    }
}

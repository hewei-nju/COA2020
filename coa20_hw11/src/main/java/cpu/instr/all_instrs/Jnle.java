package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;


/**
 * @author heweistart
 * @create 2020-12-24-21:16
 */
public class Jnle implements Instruction {

    // 0x8f JG/GNLE rel8
    // ZF == 0 & OF == SF
    @Override
    public int exec(String eip, int opcode) {
        if (opcode == 0x7f) {
            int length = 16;
            String instr = MMU.fetchInstr(eip, length);
            if (!((EFlag) CPU_State.eflag).getZF() && ((EFlag)CPU_State.eflag).getSF() == ((EFlag)CPU_State.eflag).getOF()) {
                CPU_State.eip.write(new ALU().add(CPU_State.eip.read(), "000000000000000000000000" + instr.substring(8, 16)));
                ((EFlag) CPU_State.eflag).setZF(true);
                length = 0;
            }
            return length;
        } else {
            return -1;
        }
    }
}

package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.registers.EFlag;


/**
 * @author heweistart
 * @create 2020-12-24-19:57
 */
public class Jmp implements  Instruction{

    // 0xeb JMP rel8
    // opcode + operand 8 8 = 16

    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0xeb) {
            int length = 16;
            String instr = MMU.fetchInstr(eip, length);
            CPU_State.eip.write(new ALU().add(CPU_State.eip.read(), "000000000000000000000000" + instr.substring(8, 16)));
            ((EFlag)CPU_State.eflag).setZF(true);
            return 0;
        } else{
            return -1;
        }
    }
}

package cpu.instr.all_instrs;
import cpu.MMU;

/**
 * @author heweistart
 * @create 2020-12-17-19:15
 */
public class Hlt implements Instruction {
    @Override
    public int exec(String eip, int opcode) {
        if (opcode == 0xF4) {
            int length = 8;
            String instr = MMU.fetchInstr(eip, length);
            return -1;
        } else {
            return -1;
        }
    }
}

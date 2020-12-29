package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;

public interface Instruction {

    int exec(String eip, int opcode);
}

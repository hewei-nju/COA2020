package cpu.instr.all_instrs;


import cpu.CPU_State;
import memory.Memory;

/**
 * @author heweistart
 * @create 2020-12-12-9:52
 */
public class Push implements Instruction {

    // 0x53 PUSH eBX
    // opcode: 8bits; length = 8
    //
    @Override
    public int exec(String eip, int opcode) {
        // TODO FINISHED TRUE
        if (opcode == 0x53) {
            int length = 8;
            String logicAddr = CPU_State.esp.read();
            Memory.getMemory().pushStack(logicAddr, CPU_State.ebx.read());
            return length;
        }
        return 0;
    }
}

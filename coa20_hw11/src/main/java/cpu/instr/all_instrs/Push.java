package cpu.instr.all_instrs;


import cpu.CPU;
import cpu.CPU_State;
import cpu.alu.ALU;
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
            Memory.getMemory().pushStack(CPU_State.esp.read(), CPU_State.ebx.read());
            CPU_State.eip.write(new ALU().add(eip, "00001000"));
            CPU_State.esp.write(new ALU().sub("0100", CPU_State.esp.read()));
            return length;
        }
        return 0;
    }
}

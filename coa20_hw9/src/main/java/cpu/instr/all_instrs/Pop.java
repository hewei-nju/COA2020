package cpu.instr.all_instrs;


import cpu.CPU_State;
import memory.Memory;

/**
 * @author heweistart
 * @create 2020-12-12-9:51
 */
public class Pop implements Instruction {

    // 0x58 POP eAX
    // opcode: 8bits length = 8
    // top: addr <- ss + esp
    // eAX <- top

    // 0x59 POP eCX
    // 0x5a POP eDX
    // 下面两个就寄存器有点区别吧！
    @Override
    public int exec(String eip, int opcode) {
        // TODO FINISHED TRUE
        int length = 8;
        String logicAddr = CPU_State.esp.read();
        switch (opcode) {
            case 0x58:
                CPU_State.eax.write(Memory.getMemory().topOfStack(logicAddr));
                break;
            case 0x59:
                CPU_State.ecx.write(Memory.getMemory().topOfStack(logicAddr));
                break;
            case 0x5a:
                CPU_State.edx.write(Memory.getMemory().topOfStack(logicAddr));
                break;
            default:
                length = -1;
        }
        return length;
    }

}

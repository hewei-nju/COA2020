package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import memory.Memory;


/**
 * @author heweistart
 * @create 2020-12-12-9:53
 */
public class Mov implements Instruction {

    // 0xb8 MOV eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits  length = 8 + 32
    // eAx <- Iv

    // 0xc7 MOV Ev, Iv (opcode + ModR/M + displacement + imm)
    // 8 8 32 32 = 80
    // ModR/M = Mod + Reg + R/M 这条指令只会用到：10 000 011
    // 10: 存储在内存中; 011: displacement in EBX

    // 0x8b MOV Gv, Ev
    // 8 8 32 = 48
    // ModR/M 只会用到：10 000 011
    // Mod, R/M -> Ev: 10: 存储在内存中; 011: displacement in EBX; R/M: 000: Gv operand in EAX

    // 0x89 MOV Ev, Gv
    // 8 8 32 = 48
    // ModR/M 只会用到：10 000 011
    // Mod, R/M -> Ev: 10: 存储在内存中; 011: displacement in EBX; R/M: 000: Gv operand in EAX
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
        } else if (opcode == 0xc7){
            // TODO
            int length = 80;
            String instr = MMU.fetchInstr(eip, length);
            String mod = instr.substring(8, 16);
            if (mod.equals("10000011")) {
                String displacement = instr.substring(16, 48);
                String addr = new ALU().add(CPU_State.ebx.read(), displacement);
                Operand operand = new Operand();
                operand.setVal(instr.substring(48, 80));
                operand.setType(OperandType.OPR_IMM);
                Memory.getMemory().write(addr, 32, operand.getVal().toCharArray());
                return length;
            }
            return -1;
        } else if (opcode == 0x8b) {
            int length = 48;
            String instr = MMU.fetchInstr(eip, length);
            String mod = instr.substring(8, 16);
            if (mod.equals("10000011")) {
                String displacement = instr.substring(16, 48);
                String addr = CPU_State.ds.read() + new ALU().add(displacement, CPU_State.ebx.read());
                Operand operand = new Operand();
                operand.setVal(String.valueOf(MMU.getMMU().read(addr, 32)));
                operand.setType(OperandType.OPR_IMM);
                CPU_State.eax.write(operand.getVal());
                return length;
            } else if (mod.equals("10001011")) {
                String displacement = instr.substring(16, 48);
                String addr = CPU_State.ds.read() + new ALU().add(displacement, CPU_State.ebx.read());
                Operand operand = new Operand();
                operand.setVal(String.valueOf(MMU.getMMU().read(addr, 32)));
                operand.setType(OperandType.OPR_IMM);
                CPU_State.ecx.write(operand.getVal());
                return length;
            }
            return -1;
        } else if (opcode == 0x89) {
            int length = 48;
            String instr = MMU.fetchInstr(eip, length);
            String mod = instr.substring(8, 16);
            if (mod.equals("10000011")) {
                String displacement = instr.substring(16, 48);
                String addr = new ALU().add(CPU_State.ebx.read(), displacement);
                Operand operand = new Operand();
                operand.setVal(CPU_State.eax.read());
                operand.setType(OperandType.OPR_IMM);
                Memory.getMemory().write(addr, 32, operand.getVal().toCharArray());
                return length;
            } else if (mod.equals("10001011")) {
                String displacement = instr.substring(16, 48);
                String addr = new ALU().add(CPU_State.ebx.read(), displacement);
                Operand operand = new Operand();
                operand.setVal(CPU_State.ecx.read());
                operand.setType(OperandType.OPR_IMM);
                Memory.getMemory().write(addr, 32, operand.getVal().toCharArray());
                return length;
            }
            return -1;
        } else {
            return -1;
        }
    }

    public static void main(String [] args) {
        System.out.println("100010111000001100000000000000000000000000000000".length());
    }
}

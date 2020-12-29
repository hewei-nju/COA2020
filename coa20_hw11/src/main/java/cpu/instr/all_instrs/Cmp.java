package cpu.instr.all_instrs;

import cpu.CPU;
import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import cpu.registers.EFlag;


/**
 * @author heweistart
 * @create 2020-12-12-9:50
 */
public class Cmp implements Instruction {

    // 0x3D CMP eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits
    // Instruction: opcode + Iv; eAx -> eAx register  ----> length = 8 + 32
    // setFlag <- eAx - Iv

    // 0x39 CMP Ev, Gv
    // opcode + ModR/M 8 8 = 16
    // ModR/M 只会用到: 11 001 000
    // Mod, R/M -> Ev: 11: 存储在寄存器中; 001: operand in ECX; R/M: 000: Gv operand in EAX
    @Override
    public int exec(String eip, int opcode) {
        // TODO FINISHED TRUE
        if (opcode == 0x3D) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            String eaxVal = CPU_State.eax.read();
            String ans = new ALU().sub(eaxVal, operand.getVal());
            if (ans.equals("00000000000000000000000000000000")) {
                ((EFlag) CPU_State.eflag).setZF(true);
            }
            return length;
        } else if (opcode == 0x39) {
            // TODO
            int length = 16;
            String instr = MMU.fetchInstr(eip, length);
            String mod = instr.substring(8, 16);
            if (mod.equals("11001000")) {
                String ev = CPU_State.eax.read();
                String gv = CPU_State.ecx.read();
                String ans = new ALU().sub(gv, ev);
                if (ans.equals("00000000000000000000000000000000")) {
                    ((EFlag)CPU_State.eflag).setZF(true);
                } else {
                    ((EFlag)CPU_State.eflag).setZF(false);
                }
                if (ans.charAt(0) == '0') {
                    ((EFlag)CPU_State.eflag).setSF(false);
                } else if (ans.charAt(0) == '1') {
                    ((EFlag) CPU_State.eflag).setSF(true);
                }
                return length;
            }
            return -1;
        } else {
            return -1;
        }
    }
}

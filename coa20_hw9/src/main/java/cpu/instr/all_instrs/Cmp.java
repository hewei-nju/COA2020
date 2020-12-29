package cpu.instr.all_instrs;

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
                new EFlag().setZF(true);
            }
            return length;
        } else {
            return -1;
        }
    }
}

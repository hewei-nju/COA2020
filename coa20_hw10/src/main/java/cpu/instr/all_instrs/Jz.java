package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import cpu.registers.EFlag;
import transformer.Transformer;


/**
 * @author heweistart
 * @create 2020-12-12-9:53
 */
public class Jz implements Instruction{

    // 0x74 JZ Jb
    // opcode: 8bits; Jb: 8bits  length = 8 + 8
    // Jump if Jb == 0
    @Override
    public int exec(String eip, int opcode) {
        // TODO
        if (opcode == 0x74) {
            int length = 8 + 8;
            String instr = MMU.fetchInstr(eip, length);
            if (((EFlag)CPU_State.eflag).getZF()) {
                ((EFlag)CPU_State.eflag).setZF(true);////
                Operand operand = new Operand();
                operand.setVal("000000000000000000000000" + instr.substring(8));
                operand.setType(OperandType.OPR_IMM);
                CPU_State.eip.write(new ALU().add(eip, operand.getVal()));
                length = 0;
            } else {
                CPU_State.eip.write(new ALU().add(eip, new Transformer().intToBinary(String.valueOf(16))));
            }
            return length;
        } else {
            return -1;
        }
    }
}

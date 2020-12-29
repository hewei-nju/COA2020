package cpu.instr.all_instrs;


import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;


/**
 * @author heweistart
 * @create 2020-12-11-11:38
 */
public class Add implements Instruction{
    // 0x05 ADD eAX,Iv
    // opcode: 8bits; eAx: 16bits; Iv: 32bits
    // Instruction: opcode + Iv; eAx -> eAx register  ----> length = 8 + 32
    // eAx = eAx + Iv;
    //
    @Override
    public int exec(String eip, int opcode) {
        // TODO FINISHED
        if (opcode == 0x05) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            String eaxVal = CPU_State.eax.read();
            String ans = new ALU().add(eaxVal, operand.getVal());
            CPU_State.eax.write(ans);
            return length;
        } else {
            return -1;
        }
    }

}

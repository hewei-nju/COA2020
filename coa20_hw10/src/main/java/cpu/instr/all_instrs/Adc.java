package cpu.instr.all_instrs;

import cpu.CPU_State;
import cpu.MMU;
import cpu.alu.ALU;
import cpu.instr.decode.Operand;
import cpu.instr.decode.OperandType;
import cpu.registers.EFlag;
import cpu.registers.EIP;
import transformer.Transformer;

import java.util.Arrays;

/**
 * @author heweistart
 * @create 2020-12-12-9:46
 */
public class Adc implements Instruction {

    // 0x15 ADC eAX,Iv
    // opcode: 8bits; eAx: 32bits; Iv: 32bits
    // Instruction: opcode + Iv; eAx -> eAx register  ----> length = 8 + 32
    // eAx = eAx + Iv + CF;
    @Override
    public int exec(String eip, int opcode) {
        // TODO FINISHED
        if (opcode == 0x15) {
            int length = 8 + 32;
            String instr = MMU.fetchInstr(eip, length);
            Operand operand = new Operand();
            operand.setVal(instr.substring(8));
            operand.setType(OperandType.OPR_IMM);
            String eaxVal = CPU_State.eax.read();
            char [] cf = new char[32];
            Arrays.fill(cf, '0');
            if (((EFlag)CPU_State.eflag).getCF()) {
                cf[31] = '1';
            }
            String ans = (new ALU().add(new ALU().add(eaxVal, operand.getVal()), String.valueOf(cf)));
            CPU_State.eax.write(ans);
            return length;
        } else {
            return -1;
        }
    }
}

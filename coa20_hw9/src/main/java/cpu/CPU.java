package cpu;

import cpu.instr.all_instrs.InstrFactory;
import cpu.instr.all_instrs.Instruction;
import cpu.registers.EIP;
import transformer.Transformer;
public class CPU {

    Transformer transformer = new Transformer();
    MMU mmu = MMU.getMMU();


    /**
     * execInstr specific numbers of instructions
     *
     * @param number numbers of instructions
     */
    public int execInstr(long number) {  // numbers是要执行的指令的条数，但是在此次编程作业中为1
        // 执行过的指令的总长度
        int totalLen = 0;
        while (number > 0) {
            // TODO
            totalLen += execInstr();
            number -= 1;
        }
        return totalLen;
    }

    /**
     * execInstr a single instruction according to eip value
     */
    private int execInstr() {  // 根据eip的值，来执行单独的一条指令
        String eip = CPU_State.eip.read();
        int len = decodeAndExecute(eip);
        return len;
    }

    private int decodeAndExecute(String eip) {  // 译码并执行
        int opcode = instrFetch(eip, 1);  // 先通过获取指令的opcode
        Instruction instruction = InstrFactory.getInstr(opcode);  // 然后通过InstrFactory获取对应的指令
        assert instruction != null;

        //exec the target instruction
        int len = instruction.exec(eip, opcode);  // 执行指令，然后返回的指令的长度（字节）
        return len;
    }

    /**
     * @param eip
     * @param length opcode的字节数，本作业只使用单字节opcode
     * @return opcode
     */
    // 循环读取一个字节，知道发现某个字节为opcode，然后返回这个opcode
    private int instrFetch(String eip, int length) {
        // TODO
        /**
         * 根据CS段寄存器中的段选择符和eip寄存器的值，组成48位逻辑地址访问内存，获取只能够instr
         */
        String logicAddr = CPU_State.cs.read() + eip;
        while (true) {
            int opcode = Integer.parseInt(transformer.binaryToInt(String.valueOf(mmu.read(logicAddr, length * 8))));
            if (opcode == 0x05 || opcode == 0x0D || opcode == 0x15 || opcode == 0x1D || opcode == 0x25 || opcode == 0x2D ||
            opcode == 0x35 || opcode == 0x3D || opcode == 0x58 || opcode == 0x59 || opcode == 0x5A || opcode == 0x53 ||
            opcode == 0x74 || opcode == 0xB8) {
                CPU_State.cs.write(logicAddr.substring(0, 16));
                CPU_State.eip.write(logicAddr.substring(16));
                return opcode;
            } else {
                logicAddr = add(logicAddr.toCharArray(), "000000000000000000000000000000000000000000001000");
            }
        }
    }


    private String add(char[] base, String offsetStr) {
        char[] offset = offsetStr.toCharArray();
        StringBuilder res = new StringBuilder(  );
        char carry = '0';
        for(int i=base.length-1;i>=0;i-- ){
            res.append ((carry - '0') ^ (base[i] - '0') ^ (offset[i] - '0'));
            carry = (char) (((carry - '0') & (base[i] - '0')) | ((carry - '0') & (offset[i] - '0')) | ((base[i] - '0') & (offset[i] - '0'))+'0');
        }
        int t = res.length();
        for(int i=0;i<48-t;i++){
            res.append( "0" );
        }
        return res.reverse().toString();
    }

    public void execUntilHlt(){
        // TODO ICC
        //String eip = null;
        EIP eip = (EIP)CPU_State.eip;
        //int length = 1;
        while (true) {
            int len = execInstr();
            if (len != -1024) {
                eip.plus(8);
                break;
            } else {
                eip.plus(len);
            }
            /*
            switch (CPU_State.ICC) {
                case 0x00:  // fetch
                    int opcode = instrFetch(eip.read(), length);
                    int len = execInstr();
                    String Instr = MMU.fetchInstr(eip.read(), len);
                    if (opcode == 0xF4) {
                        CPU_State.ICC = 0x03;
                    } else {

                    }
                    break;
                case 0x01:  // indirect
                    eip.plus(8);
                    CPU_State.ICC = 0x02;
                    break;
                case 0x02:  // execute
                    execInstr();
                    eip.plus(8);
                    break;
                case 0x03:  // 停机 halt
                    return;
            }*/
        }
    }


    public static void main(String [] args) {
        //System.out.println(Integer.toHexString(111).toUpperCase());
        //char [] data = {'0', '0', '0', '0', '0', '1', '0', '1'};
        //System.out.println(String.valueOf(CPU.ToByteStream(data)));
        //System.out.println(Integer.parseInt("0D", 16));
        System.out.println(new Transformer().intToBinary(String.valueOf(0)));
        System.out.println("000000000000000000000000".length());
    }
}


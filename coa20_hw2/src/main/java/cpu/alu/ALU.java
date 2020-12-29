package cpu.alu;
/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减与逻辑运算
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";
    //public static String CF = "0";
    // 模拟寄存器中的溢出标志位
    private String OF = "0";
    //public static String OF = "0";

    public static String InvertPlusOne(String bitStr) {
        char[] bits = bitStr.toCharArray();
        for (int i = 0; i <= bitStr.length() - 1; ++i) { // 取反
            bits[i] = bits[i] == '0' ? '1' : '0';
        }
        int carry = 1;  // 加一
        for (int i = bitStr.length() - 1; i >= 0; --i) {
            if (carry == 1 && bits[i] == '1') {
                carry = 1;
                bits[i] = '0';
            } else if (carry == 1 && bits[i] == '0') {
                carry = 0;
                bits[i] = '1';
            }
            if (carry == 0) {
                break;
            }
        }
        return String.valueOf(bits);
    }

    public static String Reverse(String s) {
        String res = "";
        for (int i = s.length() - 1; i >= 0; --i) {
            res += s.charAt(i);
        }
        return res;
    }

    //add two integer
    String add(String src, String dest) {
        // TODO
        String res = "";
        boolean iSSameSign = src.charAt(31) == dest.charAt(31) ? true : false;
        char sign = '1';
        if (iSSameSign) {
            sign = src.charAt(31);
        }
        for (int i = src.length() - 1; i >= 0; --i) {
            if (CF.equals("1")) { // 进位是1
                if (src.charAt(i) == '1' && dest.charAt(i) == '1') {
                    res += '1';
                    CF = "1";
                } else if (src.charAt(i) == '0' && dest.charAt(i) == '0') {
                    res += '1';
                    CF = "0";
                } else {
                    res += '0';
                    CF = "1";
                }
            } else {
                if (src.charAt(i) == '1' && src.charAt(i) == dest.charAt(i)) {
                    res += '0';
                    CF = "1";
                } else if (src.charAt(i) == '0' && src.charAt(i) == dest.charAt(i)) {
                    res += '0';
                    CF = "0";
                } else {
                    res += '1';
                    CF = "0";
                }
            }
        }
        if (iSSameSign) {
            if (sign != res.charAt(0)) {
                OF = "1";
            }
        }
        return Reverse(res);
    }

    //sub two integer
    // dest - src
    String sub(String src, String dest) {
        // TODO
        dest = InvertPlusOne(dest);
        //System.out.println(dest);
        String res = InvertPlusOne(add(src, dest));
        return res;
    }

    String and(String src, String dest) {
        // TODO
        String res = "";
        for (int i = 0; i <= src.length() - 1; ++i) {
            if (src.charAt(i) == '0' || dest.charAt(i) == '0') {
                res += '0';
            } else {
                res += "1";
            }
        }
        return res;
    }

    String or(String src, String dest) {
        // TODO
        String res = "";
        for (int i = 0; i <= src.length() - 1; ++i) {
            if (src.charAt(i) == '1' || dest.charAt(i) == '1') {
                res += '1';
            } else {
                res += '0';
            }
        }
        return res;
    }

    String xor(String src, String dest) {
        // TODO
        String res = "";
        for (int i = 0; i <= src.length() - 1; ++i) {
            if (src.charAt(i) == dest.charAt(i)) {
                res += '0';
            } else {
                res += '1';
            }
        }
        return res;
    }

    /**public static void main(String [] args) {
     String a = "10000000000000000000000000001111";
     String b = "00000000000000000000000000001111";
     String res = sub(a, b);
     System.out.println(res);
     }*/
}

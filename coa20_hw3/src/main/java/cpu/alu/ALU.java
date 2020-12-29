package cpu.alu;

import transformer.Transformer;


/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 加减乘除
 */
public class ALU {

    // 模拟寄存器中的进位标志位
    private String CF = "0";

    // 模拟寄存器中的溢出标志位
    private String OF = "0";

    Transformer transformer = new Transformer();

    /*public static void main(String [] args) {
        Transformer transformer = new Transformer();
        System.out.println(5 % 3);
        System.out.println(5 % (-3));
        System.out.println((-5) % 3);
        System.out.println((-5) % 3);
        System.out.println(8 % 5);
        System.out.println(8 % (-5));
        System.out.println((-8) % 5);
        System.out.println((-8) % (-5));
        ALU alu = new ALU();
        System.out.println(alu.imod("00000000000000000000000000000101", "00000000000000000000000011010011"));
        //"00000000000000000000000000000001", alu.imod("00000000000000000000000000000101", "00000000000000000000000011010011"));
        //System.out.println(transformer.binaryToInt("11111111111111111111111111111010"));
    }*/
    public static String Reverse(String s) {
        String res = "";
        for (int i = s.length() - 1; i >= 0; --i) {
            res += s.charAt(i);
        }
        return res;
    }

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


    public String CompareInt(String src, String dest) {
        if (src.charAt(0) == '0' && dest.charAt(0) == '1') {
            return ">";
        } else if (src.charAt(0) == '1' && dest.charAt(0) == '0') {
            return "<";
        } else if (src.charAt(0) == '0') {
            for (int i = 1; i <= 31; ++i) {
                if (src.charAt(i) == '1' && dest.charAt(i) == '0') {
                    return ">";
                } else if (src.charAt(i) == '0' && dest.charAt(i) == '1') {
                    return "<";
                }
            }
        } else if (src.charAt(0) == '1') {
            src = InvertPlusOne(src);
            dest = InvertPlusOne(dest);
            for (int i = 1; i <= 31; ++i) {
                if (src.charAt(i) == '1' && dest.charAt(i) == '0') {
                    return "<";
                } else if (src.charAt(i) == '0' && dest.charAt(i) == '1') {
                    return ">";
                }
            }
        }
        return "==";
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

    //signed integer mod
    String imod(String src, String dest) {
        // TODO.
        Boolean isPositive = dest.charAt(0) == '0' ? true : false;
        if (dest.charAt(0) == '1') {
            dest = InvertPlusOne(dest);
        }
        if (src.charAt(0) == '1') {
            src = InvertPlusOne(src);
        }
        while (true) {
            if (CompareInt(dest, src).equals("==")) {
                return "00000000000000000000000000000000";
            } else if (CompareInt(dest, src).equals(">")) {
                dest = sub(src, dest);
            } else if (CompareInt(dest, src).equals("<")) {
                if (isPositive) {
                    return dest;
                } else {
                    return InvertPlusOne(dest);
                }
            }
        }
    }

    String shl(String src, String dest) {
        // TODO
        int cnt = Integer.parseInt(transformer.binaryToInt(src.substring(27, 32)));
        for (int i = 0; i <= cnt - 1; ++i) {
            dest += "0";
        }
        return dest.substring(cnt);
    }

    String shr(String src, String dest) {
        // TODO
        for (int i = 26; i >= 0; --i) {
            if (src.charAt(i) == '1') {
                return "00000000000000000000000000000000";
            }
        }
        String res = "";
        int cnt = Integer.parseInt(transformer.binaryToInt(src.substring(27, 32)));
        for (int i = 0; i <= cnt - 1; ++i) {
            res += "0";
        }
        res += dest;
        return res.substring(0, 32);
    }

    String sal(String src, String dest) {
        // TODO
        return shl(src, dest);
    }

    String sar(String src, String dest) {
        // TODO
        String signBit = dest.substring(0, 1);
        String res = "";
        int cnt = Integer.parseInt(transformer.binaryToInt(src.substring(27, 32)));
        for (int i = 26; i >= 0; --i) {
            if (src.charAt(i) == '1') {
                cnt = 32;
                break;
            }
        }
        for (int i = 0; i <= cnt - 1; ++i) {
            res += signBit;
        }
        res += dest;
        return res.substring(0, 32);
    }


    String rol(String src, String dest) {
        // TODO
        String res = "";
        int cnt = Integer.parseInt(transformer.binaryToInt(src.substring(27, 32)));
        for (int i = cnt; i <= 31; ++i) {
            res += dest.charAt(i);
        }
        for (int i = 0; i <= cnt - 1; ++i) {
            res += dest.charAt(i);
        }
        return res;
    }

    String ror(String src, String dest) {
        // TODO
        String res = "";
        int cnt = Integer.parseInt(transformer.binaryToInt(src.substring(27, 32)));
        for (int i = 32 - cnt; i <= 31; ++i) {
            res += dest.charAt(i);
        }
        for (int i = 0; i <= 32 - cnt - 1; ++i) {
            res += dest.charAt(i);
        }
        return res;
    }


    public static void main(String [] args) {
        Transformer t = new Transformer();
        ALU alu = new ALU();
        String a = t.intToBinary("3");
        String b = t.intToBinary("20");
        System.out.println(t.binaryToInt(alu.imod(a, b)));
    }
}

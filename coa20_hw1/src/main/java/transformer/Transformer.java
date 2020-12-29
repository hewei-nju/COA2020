package transformer;


public class Transformer {
    /**
     * Integer to binaryString
     *
     //* @param numStr to be converted
     * @return result
     */

    public String reverse(String str) {
        String res = "";
        char [] chars = str.toCharArray();
        for (int i= chars.length - 1; i >= 0; --i) {
            res += chars[i];
        }
        return res;
    }

    public String intToBinary(String numStr) {
        //TODO:
        String res = "";
        int num = Integer.parseInt(numStr);
        for (int i = 0; i <= 31; ++i) {
            res += 0x0001 & num;
            num = num >> 1;
        }
        return reverse(res);
    }

    /**
     * BinaryString to Integer
     *
     * @param binStr : Binary string in 2's complement
     * @return :result
     */
    public String binaryToInt(String binStr) {
        //TODO:
        int num = 0;
        char [] bits = binStr.toCharArray();
        String res = "";
        if (bits[0] == '1') {
            res += "-";
            for (int i = 0; i <= 31; ++i) { // 取反
                bits[i] = bits[i] == '0' ? '1' : '0';
            }
            int carry = 1;  // 加一
            for (int i = 31; i >= 0; --i) {
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
        }
        int base = 1;
        for (int i = 31; i >= 0; --i) {
            num += (bits[i] - '0') * base;
            base = base << 1;
        }
        res += String.valueOf(num);
        return res;
    }

    /**
     * Float true value to binaryString
     * @param floatStr : The string of the float true value
     * */
    public String floatToBinary(String floatStr) {
        //TODO:
        String res = "";
        if (Math.abs(Double.parseDouble(floatStr)) > Math.abs(Float.MAX_VALUE)) {
            if (floatStr.charAt(0) == '-')
                return "-Inf";
            else
                return "+Inf";
        }
        int num = Float.floatToIntBits(Float.parseFloat(floatStr));
        for (int i = 0; i <= 31; ++i) {
            res += 0x0001 & num;
            num = num >> 1;
        }
        return reverse(res);
    }

    /**
     * Binary code to its float true value
     * */
    public String binaryToFloat(String binStr) {
        //TODO:
        int index = 0;
        double res;
        char Sign = binStr.charAt(0);
        String floatStr = Sign == '0' ? "" : "-";
        String Exp = binStr.substring(1, 9);
        String Val = binStr.substring(9, 32);
        // +INF
        if (Sign == '0' && Exp.equals("11111111")) {
            return "+Inf";
        }
        // -INF
        if (Sign == '1' && Exp.equals("11111111")) {
            return "-Inf";
        }
        // 0
        if (binStr.equals("00000000000000000000000000000000")) {
            return "0.0";
        }
        for (int i = 0; i <= 7; ++i) {
            index += Integer.parseInt(Exp.substring(i, i + 1)) * Math.pow(2, 7 - i);
        }
        if (index == 0) {
            res = 0;
            for (int i = 0; i <= 22; ++i) {
                res += Integer.parseInt(Val.substring(i, i + 1)) * Math.pow(2, -1 * (i + 1));
            }
            res =  res * Math.pow(2, - 126);
        } else {
            res = 1;
            for (int i = 0; i <= 22; ++i) {
                res += Integer.parseInt(Val.substring(i, i + 1)) * Math.pow(2, -1 * (i + 1));
            }
            res =  res * Math.pow(2, index - 127);
        }
        floatStr += String.valueOf(res);
        return floatStr;
    }

    /**
     * The decimal number to its NBCD code
     * */
    public String decimalToNBCD(String decimal) {
        //TODO:
        String res = "";
        int Dec = Integer.parseInt(decimal);
        boolean Sign = Dec >= 0 ? true : false;
        Dec = Sign ? Dec : -1*Dec;
        int digit, cnt = 0;
        while (Dec != 0) {
            digit = Dec % 10;
            Dec /= 10;
            cnt += 1;
            switch (digit) {
                case 0:
                    res += "0000";
                    break;
                case 1:
                    res += "1000";
                    break;
                case 2:
                    res += "0100";
                    break;
                case 3:
                    res += "1100";
                    break;
                case 4:
                    res += "0010";
                    break;
                case 5:
                    res += "1010";
                    break;
                case 6:
                    res += "0110";
                    break;
                case 7:
                    res += "1110";
                    break;
                case 8:
                    res += "0001";
                    break;
                case 9:
                    res += "1001";
                    break;
            }
        }
        while (cnt < 7) {
            cnt += 1;
            res += "0000";
        }
        res += Sign ? "0011" : "1011";
        return reverse(res);
    }

    /**
     * NBCD code to its decimal number
     * */
    public String NBCDToDecimal(String NBCDStr) {
        //TODO:
        int res = 0, base = 1;
        NBCDStr = reverse(NBCDStr);
        String SubStr;
        for (int i = 0; i <= 24; i += 4) {
            SubStr = NBCDStr.substring(i, i+4);
            switch (SubStr) {
                case "0000":
                    res += 0;
                    break;
                case "1000":
                    res += 1 * base;
                    break;
                case "0100":
                    res += 2 * base;
                    break;
                case "1100":
                    res += 3 * base;
                    break;
                case "0010":
                    res += 4 * base;
                    break;
                case "1010":
                    res += 5 * base;
                    break;
                case "0110":
                    res += 6 * base;
                    break;
                case "1110":
                    res += 7 * base;
                    break;
                case "0001":
                    res += 8 * base;
                    break;
                case "1001":
                    res += 9 * base;
                    break;
            }
            base *= 10;
        }
        res = NBCDStr.substring(28, 32).equals("0011") ? res : -1*res;
        return String.valueOf(res);
    }



    public static void main(String [] args) {
        Transformer t = new Transformer();
        System.out.println(t.intToBinary("0"));
        System.out.println(t.intToBinary("-20"));
        //System.out.println((long)Math.pow(2, 31));
        System.out.println(t.intToBinary(String.valueOf((int)(-1 * Math.pow(2, 31)))));
    }
}

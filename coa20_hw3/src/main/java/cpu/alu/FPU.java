package cpu.alu;

import transformer.Transformer;

import java.util.ServiceConfigurationError;


/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    /**
     * compute the float add of (a + b)
     **/
    //ALU alu = new ALU();
    public int binaryToInt(String binStr) {
        int val = 0;
        int base = 1;
        for (int i = binStr.length() - 1; i >= 0; --i) {
            if (binStr.charAt(i) == '1') {
                val += base;
            }
            base *= 2;
        }
        return val;
    }

    public String intTiBinary(int num) {
        char[] res = new char[8];
        int i = 7;
        for (; i >= 0; --i) {
            res[i] = num % 2 == 0 ? '0' : '1';
            num = num >> 1;
        }
        return String.valueOf(res);
    }

    public boolean isNaN(String binStr) {
        if (binStr.substring(1, 9).equals("11111111") && !binStr.substring(9).equals("00000000000000000000000")) {
            return true;
        }
        return false;
    }

    public boolean isInf(String binStr) {
        if (binStr.equals("01111111100000000000000000000000") || binStr.equals("11111111100000000000000000000000")) {
            return true;
        }
        return false;
    }

    public boolean isZero(String binStr) {
        if (binStr.equals("00000000000000000000000000000000") || binStr.equals("10000000000000000000000000000000")) {
            return true;
        }
        return false;
    }

    public static void InvertPlusOne(char[] src, char[] guardBits) {
        for (int i = 0; i <= guardBits.length - 1; ++i) {
            guardBits[i] = guardBits[i] == '0' ? '1' : '0';
        }
        for (int i = 0; i <= src.length - 1; ++i) { // 取反
            src[i] = src[i] == '0' ? '1' : '0';
        }
        char carry = '1';  // 加一
        for (int i = guardBits.length - 1; i >= 0 && carry == '1'; --i) {
            if (guardBits[i] == '1') {
                guardBits[i] = '0';
            } else if (guardBits[i] == '0') {
                carry = '0';
                guardBits[i] = '1';
            }
        }
        for (int i = src.length - 1; i >= 0 && carry == '1'; --i) {
            if (src[i] == '1') {
                src[i] = '0';
            } else if (src[i] == '0') {
                carry = '0';
                src[i] = '1';
            }
        }
    }
    public static void InvertPlusOne(char[] src) {
        for (int i = 0; i <= src.length - 1; ++i) { // 取反
            src[i] = src[i] == '0' ? '1' : '0';
        }
        char carry = '1';  // 加一
        for (int i = src.length - 1; i >= 0 && carry == '1'; --i) {
            if (src[i] == '1') {
                src[i] = '0';
            } else if (src[i] == '0') {
                carry = '0';
                src[i] = '1';
            }
        }
    }

    String add(String a, String b) {
        // TODO
        // 先检查两个数是否有NaN
        if (isNaN(a)) {
            return a;
        } else if (isNaN(b)) {
            return b;
        }
        // 判断是否有无穷大
        if (isInf(a)) {
            return a;
        } else if (isInf(b)) {
            return b;
        }
        // 判断是否有0
        if (isZero(a)) {
            return b;
        } else if (isZero(b)) {
            return a;
        }
        // 正常计算
        /**
         * 符号位： 0
         * 阶码： 1 - 9(8 bits)
         * 有效值：9 - 32(23 bits) 要注意移位时，最前面默认有1
         * 保护位：32 - (4 bits)
         *
         * step1: 找出阶码较大的字符串，对阶，并右移有效值，若有效值为 0， 直接输出
         * step2：有效值相加
         * step3：有效值若溢出，移动阶码
         * step4；看是否用到保护位，然后返回结果
         * */
        int aE = binaryToInt(a.substring(1, 9));
        int bE = binaryToInt(b.substring(1, 9));
        int bias = aE > bE ? aE - bE : bE - aE;
        if (bE > aE) {
            String tmp = a;
            a = b;
            b = tmp;
        }
        aE = binaryToInt(a.substring(1, 9));
        char[] src = a.toCharArray();
        char[] dest = b.toCharArray();
        char[] aVal = new char[24];
        char[] bVal = new char[24];
        char[] guardBits = {'0', '0', '0', '0'};
        char CF = '0';
        //boolean overFlow = false;
        aVal[0] = '1';
        bVal[0] = '1';
        for (int i = 9; i <= 31; ++i) {
            aVal[i - 8] = src[i];
            bVal[i - 8] = dest[i];
        }
        // 对阶
        if (bias >= 24) {  // 阶码大于23， 另一个太小了，不够加
            return a;
        } else {
            while (bias > 0) {
                --bias;
                for (int i = 2; i >= 0; --i) {
                    guardBits[i + 1] = guardBits[i];
                }
                guardBits[0] = bVal[23];
                for (int i = 22; i >= 0; --i) {
                    bVal[i + 1] = bVal[i];
                }
                bVal[0] = '0';
            }
        }
        // 看a，b的符号，看是否要取反+1
        if (src[0] != dest[0]) {
            InvertPlusOne(bVal, guardBits);
            //InvertPlusOne(bVal);
        }
        // 对aVal和bVal进行加法
        for (int i = aVal.length - 1; i >= 0; --i) {
            if (CF == '1') {
                if (aVal[i] != bVal[i]) {
                    aVal[i] = '0';
                    CF = '1';
                } else if (aVal[i] == bVal[i] && aVal[i] == '0') {
                    aVal[i] = '1';
                    CF = '0';
                } else {
                    aVal[i] = '1';
                    CF = '1';
                }
            } else {
                if (aVal[i] != bVal[i]) {
                    aVal[i] = '1';
                    CF = '0';
                } else if (aVal[i] == bVal[i] && aVal[i] == '0') {
                    aVal[i] = '0';
                    CF = '0';
                } else {
                    aVal[i] = '0';
                    CF = '1';
                }
            }
        }
        if (String.valueOf(aVal).equals("000000000000000000000000")) {
            if (src[0] == '0') {
                return "00000000000000000000000000000000";
            } else {
                return "10000000000000000000000000000000";
            }
        }
        if (CF == '1') {  // 有效值上溢
            aE += 1;
            for (int i = guardBits.length - 2; i >= 0; --i) {
                guardBits[i + 1] = guardBits[i];
            }
            guardBits[0] = aVal[aVal.length - 1];
            for (int i = aVal.length - 2; i >= 0; --i) {
                aVal[i + 1] = aVal[i];
            }
            if (dest[0] == src[0]) {
                aVal[0] = '1';
            } else {
                aVal[0] = '0';
            }
            //aVal[0] = '0';
            // 特判指数是否上溢
            if (aE == 255) {
                if (src[0] == '0') {
                    return "01111111100000000000000000000000";
                } else {
                    return "11111111100000000000000000000000";
                }
            }
        }
        // 有一些复杂的情况没有考虑，测试用例没要求考虑，主要是NaN的输出没说明
        // 规格化
        int cnt = 27;
        while (aVal[0] != '1' && cnt > 0) {
            --aE;
            --cnt;
            for (int i = 0; i <= aVal.length - 2; ++i) {
                aVal[i] = aVal[i + 1];
            }
            aVal[aVal.length - 1] = guardBits[0];
            for (int i = 0; i <= guardBits.length - 2; ++i) {
                guardBits[i] = guardBits[i + 1];
            }
            guardBits[3] = '0';
        }
        if (cnt == 0) {
            if (src[0] == '0') {
                return "00000000000000000000000000000000";
            } else {
                return "10000000000000000000000000000000";
            }
        }
        char [] aEBits = intTiBinary(aE).toCharArray();
        for (int i = 0; i <= aEBits.length - 1; ++i) {
            src[i + 1] = aEBits[i];
        }
        for (int i = 1; i <= aVal.length - 1; ++i) {
            src[i + 8] = aVal[i];
        }
        return String.valueOf(src);
    }

    /**
     * compute the float add of (a - b)
     **/
    String sub(String a, String b) {
        // TODO
        char[] ans = b.toCharArray();
        ans[0] = ans[0] == '0' ? '1' : '0';
        b = String.valueOf(ans);
        return add(a, b);
    }

}

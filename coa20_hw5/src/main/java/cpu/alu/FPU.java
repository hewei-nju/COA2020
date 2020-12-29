package cpu.alu;
import util.IEEE754Float;


/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    /**
     * compute the float mul of a * b
     * 分数部分(23 bits)的计算结果直接截取前23位
     */
    String OF = "0";
    private char OFF = '0';
    private char C = '0';

    boolean isZero(String str) {
        return (str.equals(IEEE754Float.P_ZERO) || str.equals(IEEE754Float.N_ZERO));
    }

    boolean isNaN(String str) {
        return str.substring(1, 9).equals("11111111") && !str.substring(9).equals("00000000000000000000000");
    }

    boolean isInf(String str) {
        return str.equals(IEEE754Float.P_INF) || str.equals(IEEE754Float.N_INF);
    }

    int BinaryToInt(char [] str) {
        int radix = 1, res = 0;
        for (int i = str.length - 1; i >= 0; --i) {
            if (str[i] == '1') {
                res += radix;
            }
            radix *= 2;
        }
        return res;
    }

    char [] IntToBinary(int num) {
        // default no underFlow
        char [] res = new char[8];
        for (int i = 7; i >= 0; --i) {
            if (num % 2 == 0) {
                res[i] = '0';
            } else {
                res[i] = '1';
            }
            num /= 2;
        }
        return res;
    }

    public String CompareInt(char[] src, char[] dest) {
        if (src[0] == '1') {
            return ">";
        }
        for (int i = 1; i <= src.length - 1; ++i) {
            if (src[i] == '1' && dest[i - 1] == '0') {
                return ">";
            } else if (src[i] == '0' && dest[i - 1] == '1') {
                return "<";
            }
        }
        return "==";
    }

    char[] add(char[] src, char[] dest) {
        OF = "0";
        int a = BinaryToInt(src);
        int b = BinaryToInt(dest);
        int c = a + b - 127;
        return IntToBinary(c);
    }

    char [] sub(char [] src, char [] dest) {
        OF = "0";
        int a = BinaryToInt(src);
        int b = BinaryToInt(dest);
        int c = a - b + 127;
        return IntToBinary(c);
    }

    void Add(char [] A, char [] M) {
        C = '0';
        for (int i = 23; i >= 0; --i) {
            if (C == '1') { // 进位是1
                if (A[i] == '1' && M[i] == '1') {
                    A[i] = '1';
                    C = '1';
                } else if (A[i] == '0' && M[i] == '0') {
                    A[i] = '1';
                    C = '0';
                } else {
                    A[i] = '0';
                    C = '1';
                }
            } else {
                if (A[i] == '1' && A[i] == M[i]) {
                    A[i] = '0';
                    C = '1';
                } else if (A[i] == '0' && A[i] == M[i]) {
                    A[i] = '0';
                    C = '0';
                } else {
                    A[i] = '1';
                    C = '0';
                }
            }
        }
    }

    void Sub(char [] A, char [] M) {
        int a = BinaryToInt(A);
        int b = BinaryToInt(M);
        int c = a - b;
        for (int i = 28; i >= 0; --i) {
            if (c % 2 == 1) {
                A[i] = '1';
            } else {
                A[i] = '0';
            }
            c /= 2;
        }
    }

    public void sar(char[] A, char[] Q) {
        for (int i = 22; i >= 0; --i) {
            Q[i + 1] = Q[i];
        }
        Q[0] = A[23];
        for (int i = 22; i >= 0; --i) {
            A[i + 1] = A[i];
        }
        A[0] = C;
    }

    public void sal(char [] A, char [] Q) { // A共有29位 1 1 23 4
        for (int i = 0; i <= 27; ++i) {
            A[i] = A[i + 1];
        }
        A[28] = Q[0];
        for (int i = 0; i <= 26; ++i) {
            Q[i] = Q[i + 1];
        }
        Q[27] = '0';
    }

    char [] unsignedMul(char [] Q, char [] M) {
        int cnt = 24;
        char [] A = new char[24];
        for (int i = 0; i <= 23; ++i) {
            A[i] = '0';
        }
        while (cnt > 0) {
            --cnt;
            if (Q[23] == '1') {
                Add(A, M);
            }
            sar(A, Q);
        }
        return (String.valueOf(A) + String.valueOf(Q)).toCharArray();
    }

    // TODO Q/M
    char [] unsignedDiv(char [] A, char [] M) {
        int cnt = 28;
        char [] Q = new char[28];
        for (int i = 0; i <= 27; ++i) {
            Q[i] = '0';
        }
        while (cnt > 0) {
            --cnt;
            if (CompareInt(A, M).equals("<")) {
                Q[27] = '0';
            } else {
                Sub(A, M);
                Q[27] = '1';
            }
            sal(A, Q);
        }
        return (String.valueOf(A) + String.valueOf(Q)).toCharArray();
    }

    String mul(String a, String b) {
        // TODO
        String res = a.charAt(0) == b.charAt(0) ? "0" : "1";
        if (isNaN(a)) {
            return IEEE754Float.NaN;
        } else if (isZero(a)) {
            if (isInf(b) || isNaN(b)) {
                return IEEE754Float.NaN;
            } else {
                res += "0000000000000000000000000000000";
                return res;
            }
        } else if (isNaN(b)) {
            return IEEE754Float.NaN;
        } else if (isInf(a) ) {
            if (isZero(b) || isNaN(b)) {
                return IEEE754Float.NaN;
            } else {
                res += "1111111100000000000000000000000";
                return res;
            }
        } else if (isZero(b)) {
            res += "0000000000000000000000000000000";
            return res;
        }
        char [] aExp = ("0" + a.substring(1, 9)).toCharArray();
        char [] aVal = ("1" + a.substring(9)).toCharArray();
        char [] bExp = ("0" + b.substring(1, 9)).toCharArray();
        char [] bVal = ("1" + b.substring(9)).toCharArray();
        aExp = add(aExp, bExp);
        char [] val = unsignedMul(aVal, bVal);
        res += String.valueOf(aExp) + String.valueOf(val).substring(2, 25);
        return res;
    }

    /**
     * compute the float mul of a / b
     */
    String div(String a, String b) {
        // TODO
        String res = a.charAt(0) == b.charAt(0) ? "0" : "1";
        if (isNaN(a)) {
            return IEEE754Float.NaN;
        } else if (isZero(a)) {
            if (isNaN(b) || isZero(b)) {
                return IEEE754Float.NaN;
            } else {
                res += "0000000000000000000000000000000";
                return res;
            }
        } else if (isInf(a)) {
            if (isNaN(b)) {
                return IEEE754Float.NaN;
            } else if (isZero(b)) {
                throw new ArithmeticException("ArithmeticException!");
            } else {
                res += "1111111100000000000000000000000";
                return res;
            }
        } else if (isNaN(b)) {
            return IEEE754Float.NaN;
        } else if (isZero(b)) {
            throw new ArithmeticException("ArithmeticException!");
        } else if (isInf(b)) {
            res += "000000000000000000000000000000";
            return res;
        }
        char [] aExp = ("0" + a.substring(1, 9)).toCharArray();
        char [] aVal = ("01" + a.substring(9) + "0000").toCharArray(); // 前面多留了一位用作计算
        char [] bExp = ("0" + b.substring(1, 9)).toCharArray();
        char [] bVal = ("1" + b.substring(9) + "0000").toCharArray();
        aExp = sub(aExp, bExp);
        char [] val = unsignedDiv(aVal, bVal);
        System.out.println(String.valueOf(val));
        // 规格化
        res += String.valueOf(aExp) + String.valueOf(val).substring(29, 52);
        return res;
    }

    public static void main(String [] args) {
        FPU fpu = new FPU();
        String b = "100000000000000000000000";
        String a = "111000000000000000000000";
        String c = String.valueOf(fpu.unsignedDiv(a.toCharArray(), b.toCharArray()));
        System.out.println(c);
    }

}

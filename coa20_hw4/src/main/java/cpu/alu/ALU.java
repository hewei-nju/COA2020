package cpu.alu;

import transformer.Transformer;
import util.BinaryIntegers;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 * TODO: 乘除
 */
public class ALU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";


	/**
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	Transformer transformer = new Transformer();

	public void Reverse(char [] A) {
		for (int i = 0; i <= 15; ++i) {
			char tmp = A[i];
			A[i] = A[31 - i];
			A[31 - i] = tmp;
		}
	}

	public char [] InvertPlusOne(char [] B) {
		char [] bits = new char[32];
		for (int i = 0; i <= 31; ++i) { // 取反
			bits[i] = B[i] == '0' ? '1' : '0';
		}
		int carry = 1;  // 加一
		for (int i = 31; i >= 0 && carry == 1; --i) {
			if (bits[i] == '1') {
				carry = 1;
				bits[i] = '0';
			} else if (bits[i] == '0') {
				carry = 0;
				bits[i] = '1';
			}
		}
		return bits;
	}

	//add two integer
	void add(char [] A, char [] M) {
		// TODO
		CF = "0";
		boolean iSSameSign = A[0] == M[0] ? true : false;
		char sign = '0';
		if (iSSameSign) {
			sign = A[0];
		}
		for (int i = 31; i >= 0; --i) {
			if (CF.equals("1")) { // 进位是1
				if (A[i] == '1' && M[i] == '1') {
					A[i] = '1';
					CF = "1";
				} else if (A[i] == '0' && M[i] == '0') {
					A[i] = '1';
					CF = "0";
				} else {
					A[i] = '0';
					CF = "1";
				}
			} else {
				if (A[i] == '1' && A[i] == M[i]) {
					A[i] = '0';
					CF = "1";
				} else if (A[i] == '0' && A[i] == M[i]) {
					A[i] = '0';
					CF = "0";
				} else {
					A[i] = '1';
					CF = "0";
				}
			}
		}
		if (iSSameSign) {
			if (sign != A[0]) {
				OF = "1";
			}
		}
		System.out.println(String.valueOf(A));
	}

	//sub two integer
	// dest - src
	public void sub(char [] A, char [] M) {
		// TODO
		System.out.print("-:");
		char[] B = InvertPlusOne(M);
		add(A, B);
	}

	public void sar(char [] A, char [] Q) {
		// TODO
		for (int i = 30; i >= 0; --i) {
			Q[i + 1] = Q[i];
		}
		Q[0] = A[31];
		for (int i = 30; i >= 0; --i) {
			A[i + 1] = A[i];
		}
	}

	public String CompareInt(char[] src, char[] dest) {
		if (src[0] == '0' && dest[0] == '1') {
			return ">";
		} else if (src[0] == '1' && dest[0] == '0') {
			return "<";
		} else if (src[0] == '0') {
			for (int i = 1; i <= 31; ++i) {
				if (src[i] == '1' && dest[i] == '0') {
					return ">";
				} else if (src[i] == '0' && dest[i] == '1') {
					return "<";
				}
			}
		} else if (src[0] == '1') {
			src = InvertPlusOne(src);
			dest = InvertPlusOne(dest);
			for (int i = 1; i <= 31; ++i) {
				if (src[i] == '1' && dest[i] == '0') {
					return "<";
				} else if (src[i] == '0' && dest[i] == '1') {
					return ">";
				}
			}
		}
		return "==";
	}

	public String mul (String src, String dest){
		//TODO
		char [] Q = src.toCharArray();
		char [] M = dest.toCharArray();
		char [] A = new char[32];
		int cnt = 32;
		char Q_ = '0';
		for (int i = 0; i <= 31; ++i) {
			A[i] = '0';
		}
		/*Booth Algorithm*/
		while (cnt > 0) {
			-- cnt;
			if (Q[31] == '0' && Q_ == '1') {
				add(A, M);
			} else if (Q[31] == '1' && Q_ == '0') {
				sub(A, M);
			}
			Q_ = Q[31];
			sar(A, Q);
		}
		return String.valueOf(Q);
	}

	/**
	 * 返回两个二进制整数的除法结果 operand1 ÷ operand2
	 * @param operand1 32-bits
	 * @param operand2 32-bits
	 * @return 65-bits overflow + quotient + remainder
	 */
	public String div(String operand1, String operand2) {
		//TODO
		if (operand1.equals(BinaryIntegers.ZERO) && !operand2.equals(BinaryIntegers.ZERO)) {
			return "00000000000000000000000000000000000000000000000000000000000000000";
		} else if (!operand1.equals(BinaryIntegers.ZERO) && operand2.equals(BinaryIntegers.ZERO)) {
			throw new ArithmeticException("ArithmeticException");//
		} else if (operand1.equals(BinaryIntegers.ZERO) && operand2.equals(BinaryIntegers.ZERO)){
			return BinaryIntegers.NaN;
		} else {
			if (operand1.equals("10000000000000000000000000000000") && operand2.equals("11111111111111111111111111111111")) {
				return "11000000000000000000000000000000000000000000000000000000000000000";
			}
			char [] src = operand1.toCharArray();
			char [] dest = operand2.toCharArray();
			boolean isSameSign = src[0] == dest[0] ? true : false;
			if (src[0] == '1') {
				src = InvertPlusOne(src);
			}
			if (dest[0] == '1') {
				dest = InvertPlusOne(dest);
			}
			char [] res = new char[32];
			char [] one = new char[32];
			one[31] = '1';
			res[31] = '0';
			for (int i = 0; i <= 30; ++i) {
				one[i] = '0';
				res[i] = '0';
			}
			String ZERO = "0";
			while (true) {
				if (CompareInt(src, dest).equals("==")) {
					add(res, one);
					sub(src, dest);
					if (!isSameSign) {
						res = InvertPlusOne(res);
					}
					if (operand1.charAt(0) == '1') {
						src = InvertPlusOne(src);
					}
					return ZERO + String.valueOf(res) + String.valueOf(src);
				} else if (CompareInt(src, dest).equals(">")) {
					add(res, one);
					sub(src, dest);
				} else if (CompareInt(src, dest).equals("<")) {
					if (!isSameSign) {
						res = InvertPlusOne(res);
					}
					if (operand1.charAt(0) == '1') {
						src = InvertPlusOne(src);
					}
					return ZERO + String.valueOf(res) + String.valueOf(src);
				}
			}
		}
	}

	public static void main(String [] args) {
		ALU alu = new ALU();
		//System.out.println(alu.mul("00000000000000000000000000000010", "00000000000000000000000000000010"));
		//alu.add("11111111111111111111111111111111".toCharArray(), "00000000000000000000000000000010".toCharArray());
		System.out.println((int)(-1* Math.pow(2, 31)) / -1);
	}

}

package cpu.alu;

import transformer.Transformer;

public class NBCDU {

	// 模拟寄存器中的进位标志位
	private String CF = "0";

	// 模拟寄存器中的溢出标志位
	private String OF = "0";

	/**
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	Transformer transformer = new Transformer();
	String add(String a, String b) {
		// TODO
		int sum = Integer.parseInt(transformer.NBCDToDecimal(a)) + Integer.parseInt(transformer.NBCDToDecimal(b));
		return transformer.getBCDString(sum);
	}

	/***
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	String sub(String a, String b) {
		// TODO
		int diff = Integer.parseInt(transformer.NBCDToDecimal(b)) - Integer.parseInt(transformer.NBCDToDecimal(a));
		return transformer.getBCDString(diff);
	}

}

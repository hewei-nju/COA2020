import cpu.MMU;
import memory.Memory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author heweistart
 * @create 2020-12-03-19:28
 */
public class SampleTest {
    Memory memory = Memory.getMemory();
    MMU mmu = MMU.getMMU();
    private static SampleTest helper = new SampleTest();
    // 段⻚式
    @Test
    public void test0() {
        String eip = "00000000000000000000000000000000";
        int len = 2 * 1024;
        char[] data = helper.fillData((char) 0b00001111, len);
        memory.alloc_seg_force(0, eip, len / 2, true, "");
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }
    // 实模式
    @Test
    public void test1() {
        int len = 128;
        char[] data = fillData((char)0b00001111, 128);
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }
    // 段式
    @Test
    public void test2() {
        String eip = "00000000000000000000000000000000";
        int len = 1024 * 1024;
        char[] data = helper.fillData((char)0b00001111, len);
        memory.alloc_seg_force(0, eip, len, false, eip);
        assertArrayEquals(data,
                mmu.read("000000000000000000000000000000000000000000000000", len));
    }
    public char[] fillData(char dataUnit, int len) {
        char[] data = new char[len];
        Arrays.fill(data, dataUnit);
        return data;
    }
}

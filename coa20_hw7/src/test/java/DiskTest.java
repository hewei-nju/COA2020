import memory.Disk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import transformer.Transformer;
import util.CRC;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @CreateTime: 2020-11-25 11:29
 */
public class DiskTest {
    InputStream in = null;
    PrintStream out = null;

    InputStream inputStream = null;
    OutputStream outputStream = null;

    @Before
    public void setUp() {
        in = System.in;
        out = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }


    @After
    public void tearDown() {
        System.setIn(in);
        System.setOut(out);
    }

    /**
     * * [0M-20M): 0b00001111
     * * [20M-32M): 0b00000011
     * * [32M-64M): 0b01010101
     * * [64M-80M): 0b00110011
     * * [80M-128M): 0b00000000
     */
    @Test
    public void RandomDiskCRCTest() {
        int i = 100;
        //initDisk();
        Disk disk = Disk.getDisk();
        StringBuilder s = new StringBuilder();
        while (i-- > 0) {
            int index = new Random().nextInt((int) (64 * Math.pow(2, 20)));
            if (index % Disk.BYTE_PRE_SECTOR != 0) index = (index / Disk.BYTE_PRE_SECTOR) * Disk.BYTE_PRE_SECTOR;
            Transformer transformer = new Transformer();
            String addr = transformer.intToBinary(String.valueOf(index));
            char[] expect = new char[Disk.BYTE_PRE_SECTOR];
            if (0 <= index && index <= 1024 * 1024 * 20) {
                Arrays.fill(expect, (char) 0b00001111);
            }
            if (1024 * 1024 * 20 < index && index <= 32 * 1024 * 1024) {
                Arrays.fill(expect, (char) 0b00000011);
            }
            if (1024 * 1024 * 32 < index && index <= 64 * 1024 * 1024) {
                Arrays.fill(expect, (char) 0b01010101);
            }
            if (64 * 1024 * 1024 < index && index <= 80 * 1024 * 1024) {
                Arrays.fill(expect, (char) 0b00110011);
            }
            if (80 * 1024 * 1024 < index && index <= 128 * 1024 * 1024) {
                Arrays.fill(expect, (char) 0b00000000);
            }
            disk.write(index, Disk.BYTE_PRE_SECTOR, expect);
            char[] data = disk.readTest(addr, Disk.BYTE_PRE_SECTOR);
            s.append(new String(expect));
            assertArrayEquals(CRC.Check(disk.ToBitStream(data), Disk.POLYNOMIAL, disk.ToBitStream(disk.getCRC())), "0000000000000000".toCharArray());
        }
        String expect = s.toString();
        String actual = outputStream.toString();
        assertTrue(expect.equals(actual));
    }
}

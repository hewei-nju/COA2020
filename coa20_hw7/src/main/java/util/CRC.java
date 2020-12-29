package util;


import memory.Disk;

import java.util.Dictionary;


/**
 * @CreateTime: 2020-11-23 22:13
 */
public class CRC {


    public static void main(String [] args) {
        /*char [] data = {'1', '0', '0', '0', '1', '1'};
        String polymomial = "1001";
        char [] M = {'1', '0', '0', '1', '1', '1'};
        char [] crcCode = {'1', '1', '1'};
        char [] ans = {'1', '1', '1'};
        char [] calculate = CRC.Calculate(data, polymomial);
        char [] check = CRC.Check(data, polymomial, crcCode);
        System.out.println(String.valueOf(calculate));
        System.out.println(String.valueOf(check));*/
        //System.out.println(CRC.Calculate("1101".toCharArray(), "111100011"));
        /*Disk disk = Disk.getDisk();
        char [] test = new char [] {'a', 'b', 'c'};
        disk.write(512, 3, test);
        // 512 1000 0000 00
        System.out.println(disk.read("1000000000", 3));
        System.out.println(Disk.ToBitStream(disk.getCRC()));*/
        //System.out.println(Calculate(new char[]{'1','0','1','1','0','0','1','1'},"11001"));
        //System.out.println(Check(new char[]{'1','0','1','1','0','0','1','1'},"11001",Calculate(new char[]{'1','0','1','1','0','0','1','1'},"11001")));
        System.out.println(Calculate(Disk.ToBitStream("rtw".toCharArray()), Disk.POLYNOMIAL));
    }

    /**
     * CRC计算器
     * @param data 数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        //TODO
        char [] dataStream = new char[data.length + polynomial.length() - 1];
        char [] pol = polynomial.toCharArray();
        for (int i = 0; i <= dataStream.length - 1; ++i) {
            if (i <= data.length - 1) {
                dataStream[i] = data[i];
            } else {
                dataStream[i] = '0';
            }
        }
        int len = data.length;
        while (len > 0) {
            if (dataStream[0] == '1') {
                xor(dataStream, pol);
            }
            sal(dataStream);
            --len;
        }
        return String.valueOf(dataStream).substring(0, pol.length - 1).toCharArray();
    }

    public static void sal(char [] dataStream) {
        for (int i = 0; i <= dataStream.length - 2; ++i) {
            dataStream[i] = dataStream[i + 1];
        }
        dataStream[dataStream.length - 1] = '0';
    }

    public static void xor(char [] dataStream, char [] pol) {
        for (int i = 0; i <= pol.length - 1; ++i) {
            if (dataStream[i] == pol[i]) {
                dataStream[i] = '0';
            } else {
                dataStream[i] = '1';
            }
        }
    }

    /**
     * CRC校验器
     * @param data 接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode){
        //TODO
        char [] dataStream = new char[data.length + CheckCode.length];
        char [] pol = polynomial.toCharArray();
        for (int i = 0; i <= dataStream.length - 1; ++i) {
            if (i <= data.length - 1) {
                dataStream[i] = data[i];
            } else {
                dataStream[i] = CheckCode[i - data.length];
            }
        }
        int len = data.length;
        while (len > 0) {
            if (dataStream[0] == '1') {
                //ans += "1";
                xor(dataStream, pol);
            }
            sal(dataStream);
            --len;
        }
        return String.valueOf(dataStream).substring(0, pol.length - 1).toCharArray();
    }

    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CalculateTest(char[] data, String polynomial){
        System.out.print(Calculate(data, polynomial));
    }
    /**
     * 这个方法仅用于测试，请勿修改
     * @param data
     * @param polynomial
     */
    public static void CheckTest(char[] data, String polynomial, char[] CheckCode){
        System.out.print(Check(data, polynomial, CheckCode));
    }
}

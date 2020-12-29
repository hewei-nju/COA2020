package memory;


import transformer.Transformer;
import util.CRC;



/**
 * 磁盘抽象类，磁盘大小为64M
 */
public class Disk {

    public static int DISK_SIZE_B = 64 * 1024 * 1024;      // 磁盘大小 64 MB

    private static Disk diskInstance = new Disk();

    /**
     * 请勿修改下列属性，至少不要修改一个扇区的大小，如果要修改请保证磁盘的大小为64MB
     */
    public static final int CYLINDER_NUM = 8;           //  柱面数
    public static final int TRACK_PRE_PLATTER = 16;     //  一个盘面的轨道数
    public static final int SECTOR_PRE_TRACK = 128;     //  一个轨道上扇区数
    public static final int BYTE_PRE_SECTOR = 512;      //  一个扇区的容量大小
    public static final int PLATTER_PRE_CYLINDER = 8;   //  一个柱面上的盘面数

    public static final String POLYNOMIAL = "11000000000100001";
    public disk_head DISK_HEAD = new disk_head();

    RealDisk disk = new RealDisk();

    /**
     * 初始化
     */
    private Disk() { }

    public static Disk getDisk() {
        return diskInstance;
    }

    /**
     * 读磁盘
     * @param eip
     * @param len
     * @return
     */
    public char[] read(String eip, int len) {
        //TODO
        int addr = Integer.parseInt(new Transformer().binaryToInt("0" + eip));
        DISK_HEAD.Seek(addr);
        char [] dataRead = new char[len];
        int pos = 0;
        while (pos <= len - 1) {
            int start = DISK_HEAD.point;
            char [] dataSector = readSector(addr);  // 读取的一整个扇区的数据
            char [] crc = Disk.ToByteStream(CRC.Calculate(Disk.ToBitStream(dataSector), POLYNOMIAL));
            if (!String.valueOf(crc).equals(String.valueOf(disk.getCRC(DISK_HEAD)))) {
                throw new Error("DATA ERROR!");
            } else {
                while (start <= dataSector.length - 1 && pos <= len - 1) {
                    dataRead[pos++] = dataSector[start++];
                }
                DISK_HEAD.adjust();
            }
        }
        return dataRead;
    }

    public char [] readSector(int addr) {
        DISK_HEAD.Seek(addr);
        int point = DISK_HEAD.point;
        DISK_HEAD.point = 0;  // 定位到扇区的首部
        char [] dataSector = new char[BYTE_PRE_SECTOR];
        for (int i = 0; i <= BYTE_PRE_SECTOR - 1; ++i) {
            dataSector[i] = disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track].sectors[DISK_HEAD.sector].dataField.Data[DISK_HEAD.point];
            DISK_HEAD.point += 1;
        }
        DISK_HEAD.point = point;
        return dataSector;
    }

    /**
     * 写磁盘
     * @param eip
     * @param len
     * @param data
     */
    public void write(String eip, int len, char[] data) {
        //TODO
        int addr = Integer.parseInt(new Transformer().binaryToInt("0" + eip));
        write(addr, len, data);
    }

    /**
     * 写磁盘（地址为Integer型）
     * 测试会调用该方法
     * @param eip
     * @param len
     * @param data
     */
    public void write(int eip, int len, char[] data) {
        //TODO
        DISK_HEAD.Seek(eip);
        for (int i = 0; i <= data.length - 1; ++i) {
            disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track].sectors[DISK_HEAD.sector].dataField.Data[DISK_HEAD.point] = data[i];
            DISK_HEAD.point += 1;
            eip += 1;
            if (DISK_HEAD.point == BYTE_PRE_SECTOR) {
                char [] dataSector = readSector(eip - 1);
                char [] crc = Disk.ToByteStream(CRC.Calculate(Disk.ToBitStream(dataSector), POLYNOMIAL));
                setCRC(crc);
                DISK_HEAD.adjust();
            }
        }
        char [] dataSector = readSector(eip - 1);
        char [] crc = Disk.ToByteStream(CRC.Calculate(Disk.ToBitStream(dataSector), POLYNOMIAL));
        setCRC(crc);
        DISK_HEAD.adjust();
    }

    public void setCRC(char [] crc) {
        disk.cylinders[DISK_HEAD.cylinder].platters[DISK_HEAD.platter].tracks[DISK_HEAD.track].sectors[DISK_HEAD.sector].dataField.CRC = crc;
    }

    public static void main(String [] args) {
        char [] A = {'a', 'b'};
        char [] B = "0110000101100010".toCharArray();
        System.out.println(Disk.ToBitStream(A));
        System.out.println(Disk.ToByteStream(B));
    }

    /**
     * 该方法仅用于测试
     */
    public char[] getCRC() {
        return disk.getCRC(DISK_HEAD);
    }

    /**
     * 磁头
     */
    private class disk_head {
        int cylinder;  //  柱面
        int platter;   //  盘面
        int track;     //  轨道
        int sector;    //  扇区
        int point;     //  磁头所在的点

        /**
         * 调整磁头的位置
         */
        public void adjust() {
            if (point == BYTE_PRE_SECTOR) {         //  如果位置在扇区的末尾
                point = 0;                          //  移动到扇区的头部
                sector++;                           //  移动到下一个扇区
            }
            if (sector == SECTOR_PRE_TRACK) {       //  如果位置在一个扇区的末尾
                sector = 0;                         //  移动到第0个扇区
                track++;                            //  移动到下一个轨道
            }
            if (track == TRACK_PRE_PLATTER) {       //  如果位置在一个最后轨道
                track = 0;                          //  移动到0轨道处
                platter++;                          //  移动到下一个盘面
            }
            if (platter == PLATTER_PRE_CYLINDER) {  //  如果位置在最后一个盘面处
                platter = 0;                        //  移动到第0个盘面
                cylinder++;                         //  移动到下一个柱面
            }
            if (cylinder == CYLINDER_NUM) {         //  如果位置在最后一个柱面处
                cylinder = 0;                       //  移动到第0个柱面
            }
        }

        /**
         * 磁头回到起点
         */
        public void Init() {
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            cylinder = 0;
            track = 0;
            sector = 0;
            point = 0;
            platter = 0;
        }

        /**
         * 将磁头移动到目标位置
         * @param start
         */
        public void Seek(int start) {
//            try {
//                Thread.sleep(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            for (int i = cylinder; i < CYLINDER_NUM; i++) {
                for (int t = platter; t < PLATTER_PRE_CYLINDER; t++) {
                    for (int j = track; j < TRACK_PRE_PLATTER; j++) {
                        for (int z = sector; z < SECTOR_PRE_TRACK; z++) {
                            for (int k = point; k < BYTE_PRE_SECTOR; k++) {
                                if ((i * PLATTER_PRE_CYLINDER * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + t * TRACK_PRE_PLATTER * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + j * SECTOR_PRE_TRACK * BYTE_PRE_SECTOR + z * BYTE_PRE_SECTOR + k) == start) {
                                    cylinder = i;
                                    track = j;
                                    sector = z;
                                    point = k;
                                    platter = t;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            Init();
            Seek(start);
        }

        @Override
        public String toString() {
            return "The Head Of Disk Is In\n" +
                    "platter:\t" + cylinder + "\n" +
                    "track:\t\t" + track + "\n" +
                    "sector:\t\t" + sector + "\n" +
                    "point:\t\t" + point;
        }
    }

    /**
     * 600 Bytes/Sector
     */
    private class Sector {
        char[] gap1 = new char[17];
        IDField idField = new IDField();
        char[] gap2 = new char[41];
        DataField dataField = new DataField();
        char[] gap3 = new char[20];
    }

    /**
     * 7 Bytes/IDField
     */
    private class IDField {
        char SynchByte;
        char[] Track = new char[2];
        char Head;
        char sector;
        char[] CRC = new char[2];
    }

    /**
     * 515 Bytes/DataField
     */
    private class DataField {
        char SynchByte;
        char[] Data = new char[512];
        char[] CRC = new char[2];
    }

    /**
     * 128 sectors pre track
     */
    private class Track {
        Sector[] sectors = new Sector[SECTOR_PRE_TRACK];

        Track() {
            for (int i = 0; i < SECTOR_PRE_TRACK; i++) sectors[i] = new Sector();
        }
    }


    /**
     * 32 tracks pre platter
     */
    private class Platter {
        Track[] tracks = new Track[TRACK_PRE_PLATTER];

        Platter() {
            for (int i = 0; i < TRACK_PRE_PLATTER; i++) tracks[i] = new Track();
        }
    }

    /**
     * 8 platter pre Cylinder
     */
    private class Cylinder {
        Platter[] platters = new Platter[PLATTER_PRE_CYLINDER];

        Cylinder() {
            for (int i = 0; i < PLATTER_PRE_CYLINDER; i++) platters[i] = new Platter();
        }
    }


    private class RealDisk {
        Cylinder[] cylinders = new Cylinder[CYLINDER_NUM];

        public RealDisk() {
            for (int i = 0; i < CYLINDER_NUM; i++) cylinders[i] = new Cylinder();
        }

        public char[] getCRC(disk_head d) {
            return cylinders[d.cylinder].platters[d.platter].tracks[d.track].sectors[d.sector].dataField.CRC;
        }
    }

    /**
     * 将Byte流转换成Bit流
     * @param data
     * @return
     */
    public static char[] ToBitStream(char[] data) {
        char[] t = new char[data.length * 8];
        //TODO
        for (int i = 0; i <= data.length - 1; ++i) {
            char datum = data[i];
            char [] Byte = new char[8];
            for (int j = 0; j <= 7; ++j) {
                 Byte[j] = (char) (((datum >> (0)) & (0b00000001)) + '0');
                 datum = (char)(datum >> 1);
            }
            for (int j = i * 8; j <= i * 8 + 7; ++j) {
                t[j] = Byte[i * 8 + 7 - j];
            }
        }
        return t;
    }

    /**
     * 将Bit流转换为Byte流
     * @param data
     * @return
     */
    public static char[] ToByteStream(char[] data) {
        char[] t = new char[data.length / 8];
        //TODO
        String ch = "";
        Transformer transformer = new Transformer();
        for (int i = 0; i <= data.length - 1; ++i) {
            ch += String.valueOf(data[i]);
            if (i % 8 == 7) {
                t[i / 8] = (char)Integer.parseInt(transformer.binaryToInt("0" + ch));
                ch = "";
            }
        }
        return t;
    }


    /**
     * 这个方法仅供测试，请勿修改
     * @param eip
     * @param len
     * @return
     */
    public char[] readTest(String eip, int len){
        char[] data = read(eip, len);
        System.out.print(data);
        return data;
    }

}

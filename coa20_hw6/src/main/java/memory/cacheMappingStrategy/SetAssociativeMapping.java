package memory.cacheMappingStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheReplacementStrategy.FIFOReplacement;
import transformer.Transformer;

import java.awt.event.MouseAdapter;
import java.util.concurrent.Callable;

public class SetAssociativeMapping extends MappingStrategy{


    Transformer t = new Transformer();

    private int SETS=512;
    private int setSize=2;

    /**
     * 该方法会被用于测试，请勿修改
     * @param SETS
     */
    public void setSETS(int SETS) {
        this.SETS = SETS;
    }

    /**
     * 该方法会被用于测试，请勿修改
     * @param setSize
     */
    public void setSetSize(int setSize) {
        this.setSize = setSize;
    }


    public String transferBlockNO(int blockNO) {
        String blockStr = t.intToBinary(String.valueOf(blockNO));
        return blockStr.substring(10);
    }
    /**
     *
     * @param blockNO 内存数据块的块号
     * @return cache数据块号 22-bits  [前14位有效]
     */

    // 组关联映射
    @Override
    public char[] getTag(int blockNO) {
        //TODO FINISHED
        char [] tag = new char[22];
        char [] blockChar = transferBlockNO(blockNO).toCharArray();
        int len = 22 + (int)(Math.log(this.setSize) / Math.log(2)) - 10;
        for (int i = 0; i <= len - 1; ++i) {
            tag[i] = blockChar[i];
        }
        for (int i = len; i <= 21; ++i) {
            tag[i] = '0';
        }
        return tag;
    }

    /**
     *
     * @param blockNO 目标数据内存地址前22位int表示
     * @return -1 表示未命中
     */
    @Override
    public int map(int blockNO) {
        //TODO FINISHED  暂时认为没有错误
        int setNO = blockNO % this.SETS;
        for (int rowNO = this.setSize * setNO; rowNO <= this.setSize * (setNO + 1) - 1; ++rowNO) {
            if (Cache.getCache().isValid(rowNO) && String.valueOf(Cache.getCache().fetchCacheRowTag(rowNO)).equals(String.valueOf(getTag(blockNO)))) {
                return rowNO;
            }
        }
        return -1;
    }

    /**
    * memory: 32 * 1024块
     块号blockNO
     so, content：blockNO, blockNO+1
     0 - 1023
     1024 - 2047
     ...
     [(block-1)*1024, block * 1024 - 1]
     */

    /**
     * 未命中的情况下，将内存读取出的input数据写入cache
     * @param blockNO
     * @return 返回cache中所对应的行
     */
    @Override
    public int writeCache(int blockNO) {
        //TODO FINISHED
        if (map(blockNO) == -1) {
            String eip = t.intToBinary(String.valueOf(blockNO * 1024));
            int len = 1024;
            char [] input = Memory.getMemory().read(eip, len);
            int start = blockNO %  this.SETS * this.setSize;
            int end = start + this.setSize - 1;
            char [] tag = getTag(blockNO);
            FIFOReplacement fifoReplacement = new FIFOReplacement();
            return fifoReplacement.Replace(start, end, tag, input);
            } else {
            int rowNO = map(blockNO);
            return rowNO;
        }
    }

    /**
     * 通过映射倒推出内存地址
     * @param rowNo
     * @return 返回具体的地址
     */

    /**
     * rowNO -> tag(blockNO) -> [sAddr = (blockNO - 1) * 1024, endAddr = sAddr + 1023]
     */
    @Override
    public String getPAddr(int rowNo) {
        //TODO
        int len = 22 + (int)(Math.log(this.setSize) / Math.log(2)) - 10;
        String tag = String.valueOf(Cache.getCache().fetchCacheRowTag(rowNo)).substring(0, len);
        String str = t.intToBinary(String.valueOf(rowNo / 4)).substring(len + 10, 32);
        String blockStr = tag + str;
        int blockNO = Integer.parseInt(t.binaryToInt("0" + blockStr));
        return t.intToBinary(String.valueOf(blockNO * 1024));
    }

}











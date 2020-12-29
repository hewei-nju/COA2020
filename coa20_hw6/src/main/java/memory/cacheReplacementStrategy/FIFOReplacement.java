package memory.cacheReplacementStrategy;

import memory.Cache;
import memory.cacheWriteStrategy.WriteBackStrategy;
import memory.cacheWriteStrategy.WriteStrategy;
import transformer.Transformer;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * 先进先出算法
 */
public class FIFOReplacement extends ReplacementStrategy {

    /**
     * 在start-end范围内查找是否命中
     * @param start 起始行
     * @param end 结束行 闭区间
     */
    @Override
    public int isHit(int start, int end, char[] addrTag) {
        //TODO FINISHED
        while (start <= end) {
            if (Cache.getCache().isValid(start) && String.valueOf(addrTag).equals(String.valueOf(Cache.getCache().fetchCacheRowTag(start)))) {
                    return start;
            }
            ++start;
        }
        return -1;
    }

    /**
     * 在未命中的情况下将内存中的数写入cache
     * @param start 起始行
     * @param end 结束行 闭区间
     * @param addrTag tag
     * @param input  数据
     * @return
     */
    @Override
    public int Replace(int start, int end, char[] addrTag, char[] input) {
        //TODO FINISHED
        if (isHit(start, end, addrTag) == -1) {
            long minTime = Cache.getCache().getTimeStamp(start);
            int rowNO = start;
            while (start <= end) {
                if (!Cache.getCache().isValid(start)) {
                    Cache.getCache().toValid(start);
                    Cache.getCache().makeDirtyFalse(start);
                    Cache.getCache().fillData(start, input);
                    Cache.getCache().refreshTag(start, addrTag);
                    Cache.getCache().startTiming(start);
                    return start;
                } else if (minTime > Cache.getCache().getTimeStamp(start)) {
                    minTime = Cache.getCache().getTimeStamp(start);
                    rowNO = start;
                }
                ++start;
            }
            setWriteStrategy(Cache.getCache().getWriteStrategy());
            if (Cache.getCache().isDirty(rowNO)) {
                writeStrategy.writeBack(rowNO);
            }
            Cache.getCache().toValid(rowNO);
            Cache.getCache().fillData(rowNO, input);
            Cache.getCache().refreshTag(rowNO, addrTag);
            Cache.getCache().startTiming(rowNO);
            return rowNO;
        }
        return -1;
    }


}

package memory.cacheWriteStrategy;

import memory.Cache;
import memory.Memory;
import memory.cacheMappingStrategy.MappingStrategy;
import memory.cacheReplacementStrategy.FIFOReplacement;

import java.util.concurrent.Callable;

/**
 * @Author: A cute TA
 * @CreateTime: 2020-11-12 11:38
 */
public abstract class WriteStrategy {
    MappingStrategy mappingStrategy;
    /**
     * 将数据写入Cache，并且根据策略选择是否修改内存
     * @param rowNo 行号
     * @param input  数据
     * @return
     */

    public void write(int rowNo, char[] input) {
        //TODO FINISHED
        if (!Cache.getCache().isValid(rowNo) || Cache.getCache().isChanged(rowNo, input)) {
            Cache.getCache().makeDirtyTrue(rowNo);
            Cache.getCache().fillData(rowNo, input);
            Cache.getCache().toValid(rowNo);
        }
    }


    /**
     * 修改内存
     * @return
     */
    public void writeBack(int rowNo) {
        //TODO FINISHED
        if (!isWriteBack()) {
            char[] data = Cache.getCache().getCacheData(rowNo);
            String eip = Cache.getCache().getSAddr(rowNo);
            int len = data.length;
            Memory.getMemory().write(eip, len, data);
        }
    }

    public void setMappingStrategy(MappingStrategy mappingStrategy) {
        this.mappingStrategy = mappingStrategy;
    }

    public abstract Boolean isWriteBack();
}

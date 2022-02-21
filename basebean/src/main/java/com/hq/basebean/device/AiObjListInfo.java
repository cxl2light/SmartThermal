package com.hq.basebean.device;

import java.util.List;

/**
 * @author Administrator
 * @date 2022/2/16 0016 13:27
 */
public class AiObjListInfo {
    private int u32AiObjNum;
    private long u64Tick;
    private int u32Rev;
    private List<AiObjInfo> stAiObjElement;

    public int getU32AiObjNum() {
        return u32AiObjNum;
    }

    public void setU32AiObjNum(int u32AiObjNum) {
        this.u32AiObjNum = u32AiObjNum;
    }

    public long getU64Tick() {
        return u64Tick;
    }

    public void setU64Tick(long u64Tick) {
        this.u64Tick = u64Tick;
    }

    public int getU32Rev() {
        return u32Rev;
    }

    public void setU32Rev(int u32Rev) {
        this.u32Rev = u32Rev;
    }

    public List<AiObjInfo> getStAiObjElement() {
        return stAiObjElement;
    }

    public void setStAiObjElement(List<AiObjInfo> stAiObjElement) {
        this.stAiObjElement = stAiObjElement;
    }
}

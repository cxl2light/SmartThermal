package com.hq.basebean.device;

/**
 * @author Administrator
 * @date 2022/2/16 0016 13:29
 */
public class AiObjInfo {
    // 距离
    private int u16Dist;
    // 类型
    private int u16AIType;
    // 得分
    private double ftScore;
    // u32Rev[2]
    private long u32Rev;

    public int getU16Dist() {
        return u16Dist;
    }

    public void setU16Dist(int u16Dist) {
        this.u16Dist = u16Dist;
    }

    public int getU16AIType() {
        return u16AIType;
    }

    public void setU16AIType(int u16AIType) {
        this.u16AIType = u16AIType;
    }

    public double getFtScore() {
        return ftScore;
    }

    public void setFtScore(double ftScore) {
        this.ftScore = ftScore;
    }

    public long getU32Rev() {
        return u32Rev;
    }

    public void setU32Rev(long u32Rev) {
        this.u32Rev = u32Rev;
    }
}

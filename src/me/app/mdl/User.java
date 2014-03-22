package me.app.mdl;

import java.util.List;
import java.util.Set;

/**
 * 天猫用户类
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:31 PM
 */
public class User {
    private Long uid; // 用户ID
    private List<Behavior> behaviors; // 若干操作记录
    private Set<Long> reallyBuy; // 下个月的真实购买记录
    private Set<Long> willBuy; // 预测下个月将购买记录

    public User(Long uid) {
        this.uid = uid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public Set<Long> getReallyBuy() {
        return reallyBuy;
    }

    public void setReallyBuy(Set<Long> reallyBuy) {
        this.reallyBuy = reallyBuy;
    }

    public Set<Long> getWillBuy() {
        return willBuy;
    }

    public void setWillBuy(Set<Long> willBuy) {
        this.willBuy = willBuy;
    }
}

package me.app.mdl;

import java.util.Set;

/**
 * 天猫用户类
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:31 PM
 * Change: wuxuef
 */
public class User extends Topic {
    private Set<Long> reallyBuy; // 下个月的真实购买记录
    private Set<Long> willBuy; // 预测下个月将购买记录

    public User(Long id) {
        this.id = id;
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

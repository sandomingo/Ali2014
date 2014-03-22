package me.app.mdl;

import me.app.base.Consts;

import java.util.Date;

/**
 * 用户行为记录类
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:32 PM
 */
public class Behavior {
    private Long brandID; // 涉及的品牌ID
    private Consts.ActionType type; // 行为类型，如：点击，购买，收藏，加入购物车
    private Date visitDatetime; // 行为发生时间

    public Behavior(Long brandID, Consts.ActionType type, Date visitDatetime) {
        this.brandID = brandID;
        this.type = type;
        this.visitDatetime = visitDatetime;
    }

    public Long getBrandID() {
        return brandID;
    }

    public void setBrandID(Long brandID) {
        this.brandID = brandID;
    }

    public Consts.ActionType getType() {
        return type;
    }

    public void setType(Consts.ActionType type) {
        this.type = type;
    }

    public Date getVisitDatetime() {
        return visitDatetime;
    }

    public void setVisitDatetime(Date visitDatetime) {
        this.visitDatetime = visitDatetime;
    }
}

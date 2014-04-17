package me.app.mdl;

import me.app.base.Consts;

import java.util.Date;

/**
 * 用户行为记录类
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:32 PM
 * Change: wuxuef
 */
public class Behavior {
    private Long brandID; // 涉及的品牌ID
    private Consts.ActionType type; // 行为类型，如：点击，购买，收藏，加入购物车
    private Date visitDatetime; // 行为发生时间
    private Long uid;	// 涉及的用户ID

    public Behavior(Long brandID, Long uid, Consts.ActionType type, Date visitDatetime) {
        this.brandID = brandID;
        this.type = type;
        this.visitDatetime = visitDatetime;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return uid + "\t" + brandID + "\t" + type + "\t" + visitDatetime + "\t";
    }

    public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
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

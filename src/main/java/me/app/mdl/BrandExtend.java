package me.app.mdl;

import java.util.ArrayList;
import java.util.Date;


/**
 * 继承自brand，并对brand的behavior数据进行统计分析
 * 而将结果保存在该类中。
 *
 * @author wuxuef2
 * @date: Apr 17, 2014 11:17:47 AM
 * @version 0.1
 */
public class BrandExtend extends Brand {
	private double score;				// 商品热度评分
	
	private int buyTimes = 0;			// 商品被购买次数
	private int clickTimes = 0;			// 商品被点击次数
	private int favouriteTimes = 0;		// 商品加心愿单次数
	private int add2cartTimes = 0;		// 商品加购物车次数
	
	private int buyPersons = 0;			// 商品购买人数
	private int clickPersons = 0;		// 商品点击人数
	private int favouritePersons = 0;	// 商品加心愿单人数
	private int add2cartPersons = 0;	// 商品加购物车人数
	
	private int mostBuyTimes = 0;		// 商品被单人最多购买次数
	private Date lastBuyTimes = null;	// 商品最后一次被购买时间
	
	// 替代品（同类商品）ID存贮列表
	private ArrayList<Long> succedaneum = new ArrayList<Long>();
	// 互补品 （关联商品）ID存贮列表
	private ArrayList<Long> complements = new ArrayList<Long>();
	
	// 所属类别ID存贮列表（根据替代品差别类型判断）
	private ArrayList<Long> classId = new ArrayList<Long>();

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public int getClickTimes() {
		return clickTimes;
	}

	public void setClickTimes(int clickTimes) {
		this.clickTimes = clickTimes;
	}

	public int getFavouriteTimes() {
		return favouriteTimes;
	}

	public void setFavouriteTimes(int favouriteTimes) {
		this.favouriteTimes = favouriteTimes;
	}

	public int getAdd2cartTimes() {
		return add2cartTimes;
	}

	public void setAdd2cartTimes(int add2cartTimes) {
		this.add2cartTimes = add2cartTimes;
	}

	public int getBuyPersons() {
		return buyPersons;
	}

	public void setBuyPersons(int buyPersons) {
		this.buyPersons = buyPersons;
	}

	public int getClickPersons() {
		return clickPersons;
	}

	public void setClickPersons(int clickPersons) {
		this.clickPersons = clickPersons;
	}

	public int getFavouritePersons() {
		return favouritePersons;
	}

	public void setFavouritePersons(int favouritePersons) {
		this.favouritePersons = favouritePersons;
	}

	public int getAdd2cartPersons() {
		return add2cartPersons;
	}

	public void setAdd2cartPersons(int add2cartPersons) {
		this.add2cartPersons = add2cartPersons;
	}

	public BrandExtend(Long brandID) {
		super(brandID);
		
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Long> getSuccedaneum() {
		return succedaneum;
	}

	public void setSuccedaneum(ArrayList<Long> succedaneum) {
		this.succedaneum = succedaneum;
	}

	public ArrayList<Long> getComplements() {
		return complements;
	}

	public void setComplements(ArrayList<Long> complements) {
		this.complements = complements;
	}

	public int getMostBuyTimes() {
		return mostBuyTimes;
	}

	public void setMostBuyTimes(int mostBuyTimes) {
		this.mostBuyTimes = mostBuyTimes;
	}

	public Date getLastBuyTimes() {
		return lastBuyTimes;
	}

	public void setLastBuyTimes(Date lastBuyTimes) {
		this.lastBuyTimes = lastBuyTimes;
	}

	public ArrayList<Long> getClassId() {
		return classId;
	}

	public void setClassId(ArrayList<Long> classId) {
		this.classId = classId;
	}
}

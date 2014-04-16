package me.app.mdl;

import java.util.ArrayList;
import java.util.Date;

import me.app.base.Consts;
import me.app.base.Consts.BrandType;

public class BrandExtend extends Brand {
	private double score;
	
	private int buyTimes = 0;
	private int clickTimes = 0;
	private int favouriteTimes = 0;
	private int add2cartTimes = 0;
	
	private int buyPersons = 0;
	private int clickPersons = 0;
	private int favouritePersons = 0;
	private int add2cartPersons = 0;
	
	private int mostBuyTimes = 0;
	private Date lastBuyTimes = null;
	
	private ArrayList<Long> belongClass = new ArrayList<Long>();
	private ArrayList<Long> complements = new ArrayList<Long>();
	
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

	public ArrayList<Long> getBelongClass() {
		return belongClass;
	}

	public void setBelongClass(ArrayList<Long> belongClass) {
		this.belongClass = belongClass;
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

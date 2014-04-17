package me.app.mdl;


/**
 * 用于保存从数据文件中读到的每一行数据。
 *
 * @author wuxuef2
 * @date: Apr 17, 2014 11:19:26 AM
 * @version 
 */
public class Row {
	private String uid;	
	private String bid;
	private int type;
	private String date;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

    @Override
    public String toString() {
        return uid + "\t" + bid + "\t" + type + "\t" + date;
    }
}

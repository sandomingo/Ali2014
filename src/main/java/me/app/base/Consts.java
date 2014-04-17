package me.app.base;

import java.util.Calendar;

/**
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:34 PM
 * Change: wuxuef2
 */
public class Consts {
	// 数据文件
	public static final String INPUT_PATH = "t_alibaba_data.csv";
//	public static final String INPUT_PATH = "small.csv";
	
	// 提交文件名生成
	public static String SUBMIT_FILE_NAME = "";
	static {
		Calendar calendar = Calendar.getInstance();
		
		SUBMIT_FILE_NAME = calendar.get(Calendar.MONDAY) + "_"
				+ calendar.get(Calendar.DAY_OF_MONTH) + ".txt";
	}
	

    /**
     * 用户行为类型
     * 包括点击，购买，收藏，加入购物车
     */
    public enum ActionType{
        CLICK(0), BUY(1), FAVOURITE(2), ADD2CART(3);
        private int code;
        private ActionType(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return this.code;
        }

        public static ActionType fromCode(Integer code) {
            switch (code) {
                case 0:
                    return CLICK;
                case 1:
                    return BUY;
                case 2:
                    return FAVOURITE;
                default:
                    return ADD2CART;
            }
        }
    }
    
    /**
     * 关注项
     * 从Topic派生出USER和BRAND类
     * 用这一项标志Topic的具体类型（失败的设计，应该用instanceof）
     */
    public enum TopicType{
        USER(0), BRAND(1);
        private int code;
        private TopicType(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return this.code;
        }

        public static TopicType fromCode(Integer code) {
            switch (code) {
                case 0:
                    return USER;
                default:
                    return BRAND;
            }
        }
    }
}

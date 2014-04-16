package me.app.base;

/**
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:34 PM
 */
public class Consts {
    /**
     * 用户行为类型
     * 包括点击，购买，收藏，加入购物车
     */
	public static final String INPUT_PATH = "t_alibaba_data.csv";
//	public static final String INPUT_PATH = "small.csv";
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
    
    public enum BrandType{
    	DURABLE(-1), CONSUMERS(1), UNDETERMINED(0);
        private int code;
        private BrandType(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return this.code;
        }

        public static BrandType fromCode(Integer code) {
            switch (code) {
                case -1:
                    return DURABLE;
                case 1:
                	return CONSUMERS;
                default:
                    return UNDETERMINED;
            }
        }
    }
}

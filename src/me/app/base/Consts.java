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
}

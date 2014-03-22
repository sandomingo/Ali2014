package me.app.clf;

import me.app.mdl.User;

/**
 * 分类器接口，所有实现的业务上的分类器都需要实现它
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:41 PM
 */
public interface BaseClassifier {
    /**
     * 预测用户行为
     * @param auser
     */
    public void predict(User auser);
}

package me.app.eval;

import me.app.clf.BaseClassifier;
import me.app.clf.BuyingBehaviorClassifier;
import me.app.mdl.User;

import java.util.List;
import java.util.Set;

/**
 * 行为预测算法评估类
 * User: SanDomingo
 * Date: 3/22/14
 * Time: 9:44 PM
 */
public class Evaluator {
    private BaseClassifier clf;

    public Evaluator() {
        this.clf = new BuyingBehaviorClassifier();
    }

    /**
     * 评估预测用户行为的算法
     * @param testUsers
     */
    public void eval(List<User> testUsers) {
        for (User auser : testUsers) {
            clf.predict(auser);
        }


        long hitBrands = 0; // 用户预测的品牌列表与用户i真实购买的品牌交集的个数
        long pBrands = 0; // 对用户预测他(她)会购买的品牌列表个数
        long bBrands = 0; // 用户真实购买的品牌个数
        for (User auser : testUsers) {
            Set<Long> realBuy = auser.getReallyBuy();
            Set<Long> predictBuy = auser.getWillBuy();
            pBrands += predictBuy.size();
            bBrands += realBuy.size();
            for (Long brand : predictBuy) {
                if (realBuy.contains(brand)) { // got a hit
                    hitBrands += 1;
                }
            }
        }
        // 计算并打印评估结果
        calculateF1(hitBrands, pBrands, bBrands);
    }

    /**
     * 计算准确率和召回率
     * @param hitBrands
     * @param pBrands
     * @param bBrands
     */
    private void calculateF1(Long hitBrands, Long pBrands, Long bBrands) {
        double precision;
        double recall;
        double F1;

        precision = 1.0 * hitBrands / pBrands;
        recall = 1.0 * hitBrands / bBrands;
        // calculate F1 Score
        F1 = 2 * precision * recall / (precision + recall);
        System.out.println("F1: " + F1 + "Precision: " + precision + "\t" + "Recall: " + recall);
    }

    public static void main(String[] args) {
        Evaluator eval = new Evaluator();
        List<User> testUsers = getUserFromFile("users.txt");
        eval.eval(testUsers);
    }

    //TODO
    private static List<User> getUserFromFile(String s) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}

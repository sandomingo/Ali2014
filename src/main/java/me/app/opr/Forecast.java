package me.app.opr;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.app.base.Consts;
import me.app.eval.Evaluator;
import me.app.mdl.BrandExtend;
import me.app.mdl.Topic;
import me.app.mdl.User;

/**
 * 对规则的参数使用前几个月的预测数据进行测试
 * 而后对确定下来的参数用于真正的预测
 *
 * @author wuxuef2
 * @date: Apr 17, 2014 2:20:29 PM
 * @version 
 */
public class Forecast {
	private UserStatistics userStatistics;	
	private BrandStatistics brandStatistics;
	private Date trainStart, trainEnd, testStart, testEnd;
	private boolean isPredict;
	private int intestedDays = 14;
	private int forgetMonth = 0;
	private int forgetDays = 25;
	
	public Forecast(boolean isPredict) {
		this.isPredict = isPredict;
		
		// for trainning		
		int dayOfMonth = 15;
		int trainStartMonth = 4;
		int trainEndMonth = 7;
		int testStartMonth = 7;
		int testEndMonth = 8;

		if (isPredict) {// for predict
			dayOfMonth = 15;
			trainStartMonth = 5;
			trainEndMonth = 8;
			testStartMonth = 8;
			testEndMonth = 9;
		}

		Calendar myDate = Calendar.getInstance();
		myDate.set(Calendar.MONTH, trainStartMonth - 1);
		myDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		trainStart = myDate.getTime();

		myDate.set(Calendar.MONTH, trainEndMonth - 1);
		trainEnd = myDate.getTime();

		myDate.set(Calendar.MONTH, testStartMonth - 1);
		testStart = myDate.getTime();

		myDate.set(Calendar.MONTH, testEndMonth - 1);
		testEnd = myDate.getTime();
		brandStatistics = new BrandStatistics(trainStart, trainEnd, testStart,
				testEnd);
		userStatistics = new UserStatistics(trainStart, trainEnd, testStart,
				testEnd);
	}
	
	public void run() {
		List<User> users = userStatistics.getUsers();
		Calendar myDate = Calendar.getInstance();
		myDate.setTime(trainStart);
		
		myDate.set(Calendar.MONTH, myDate.get(Calendar.MONTH) + forgetMonth);
		myDate.set(Calendar.DAY_OF_MONTH, myDate.get(Calendar.DAY_OF_MONTH) + forgetDays);
		Date forgetTime = myDate.getTime();
		
		myDate.setTime(trainEnd);
		myDate.set(Calendar.DAY_OF_MONTH, myDate.get(Calendar.DAY_OF_MONTH) - intestedDays);
		Date interestTime = myDate.getTime();
		
		if (isPredict) {
            predict(users, forgetTime, interestTime);
        } else {
            train(users, forgetTime, interestTime);
        }
	}

	/**
	 * 生成相应用户可能喜欢的Brand集
	 * 用于后面做更严格规则的过滤
	 * 
	 * @param topic			对应的用户
	 * @param interstedTime 用于标志用户近期行为数据
	 * @return				返回来用户的interested 集 
	 */
	public ArrayList<Long> getLike(Topic topic, Date interstedTime) {		
		ArrayList<Long> like = new ArrayList<Long>();
		for (int i = 0; i < topic.getBehaviors().size(); i++) {
			if (!like.contains(topic.getBehaviors().get(i).getBrandID())) {
				if (topic.getBehaviors().get(i).getType() != Consts.ActionType.CLICK) {
					like.add(topic.getBehaviors().get(i).getBrandID());
				} else {
					int times = userStatistics.getTopicActionTimes(
							topic.getBehaviors(),
							topic.getBehaviors().get(i).getBrandID(),
							Consts.ActionType.CLICK,
							Consts.TopicType.BRAND);
					if (times >= 10 || (times >= 1 && userStatistics.getBehaviorLastHappenTime(
									topic.getBehaviors(),
									topic.getBehaviors().get(i).getBrandID(),
									Consts.ActionType.CLICK,
									Consts.TopicType.BRAND).getTime() >= interstedTime.getTime())) {
						like.add(topic.getBehaviors().get(i).getBrandID());
					}
				}
			}
		}

		return like;
	}

	/**
	 * 规则过滤代码
	 * 
	 * @param users
	 * @param forgetTime
	 * @param interstTime
	 */
	private void selectFromInterested(List<User> users, Date forgetTime, Date interstTime){
		HashMap<Long, Integer> personNumber = new HashMap<Long, Integer>();
		HashMap<Long, Double> frequence = null;
		HashMap<Long, Double> hotBrandsByScore = null;
		hotBrandsByScore = brandStatistics.getHot(0);
		HashMap<Long, Double> usersScore = userStatistics.getHot(0);
		
		frequence = brandStatistics.getActionFrequenceEveryMonthEveryPerson(
				Consts.ActionType.BUY, personNumber);
		HashMap<Long, Double>timeSpan = brandStatistics.getActionTimeSpan(Consts.ActionType.BUY);
		//对每个用户
		for (int i = 0; i < users.size(); i++) {
			// 不对不活跃用户预测
			if (!usersScore.containsKey(users.get(i).getId()) || usersScore.get(users.get(i).getId()) < 1.8) {
				users.get(i).setWillBuy(new HashSet<Long>());
				continue;
			}
			
			// 具体规则
			User user = users.get(i);
			ArrayList<Long> like = getLike(user, interstTime);
			for (int j = like.size() - 1; j >= 0; j--) {
				BrandExtend brand = brandStatistics.getBrand(like.get(j));
				int userBuyTimes = brandStatistics.getTopicActionTimes(
						user.getBehaviors(), like.get(j),
						Consts.ActionType.BUY, Consts.TopicType.BRAND);
				int userClickTimes = brandStatistics.getTopicActionTimes(
						user.getBehaviors(), like.get(j),
						Consts.ActionType.CLICK, Consts.TopicType.BRAND);
				
				Date firstVistDate = userStatistics.getBehaviorFirstHappenTime(
						user.getBehaviors(), 
						like.get(j),
						Consts.ActionType.CLICK, 
						Consts.TopicType.BRAND);
				Date tmpVistDate = userStatistics.getBehaviorFirstHappenTime(
						user.getBehaviors(), 
						like.get(j),
						Consts.ActionType.BUY, 
						Consts.TopicType.BRAND);
				if (tmpVistDate.before(firstVistDate)) firstVistDate = tmpVistDate;
				Date lastVistDate = userStatistics.getBehaviorLastHappenTime(
						user.getBehaviors(), 
						like.get(j),
						Consts.ActionType.CLICK, 
						Consts.TopicType.BRAND);
				tmpVistDate = userStatistics.getBehaviorLastHappenTime(
						user.getBehaviors(), 
						like.get(j),
						Consts.ActionType.BUY, 
						Consts.TopicType.BRAND);
				if (tmpVistDate.after(firstVistDate)) lastVistDate = tmpVistDate;
				

				// 只会被人在短时间内关注的非热门商品
//				if (lastVistDate.getTime() - firstVistDate.getTime() < 7 * 24 * 60 * 60 * 1000
//						&& lastVistDate.before(interstTime)
//						&& brand.getBuyPersons() <= 3) {
//					like.remove(j);
//					continue;
//				}
				
				
				// 在很久一次超大购买
				if ((brand.getBuyPersons() <= 3 && brand.getBuyTimes() > 1 
						&& brand.getLastBuyTimes().before(interstTime))
						|| brand.getBuyTimes() == 0) {
					like.remove(j);
					continue;
				}
				
				// 该用户在很早之前的购买行为，并且在之后未关注过该商品
				if (userBuyTimes >= 1
						&& userStatistics.getBehaviorLastHappenTime(
								user.getBehaviors(), like.get(j),
								Consts.ActionType.BUY, Consts.TopicType.BRAND)
								.getTime() <= forgetTime.getTime()) {
					like.remove(j);
					continue;
				}
				
				
				// 该商品很多人点，但很少人买
				if (brand.getClickPersons() > 30 && brand.getBuyPersons() < 3) {
					like.remove(j);
					continue;
				}
				
				
				// 冷门商品
				if (!hotBrandsByScore.containsKey(like.get(j))
						|| hotBrandsByScore.get(like.get(j)) < 1.1) {
					like.remove(j);
					continue;
				}
				
				
				// 耐用品，并且已经购买过的
				if (brand.getMostBuyTimes() == 1 && brand.getBuyPersons() > 25
						&& userBuyTimes == 1) {
					like.remove(j);
					continue;
				}
				

				
				// 一定为非周期的物品
				if ((brand.getMostBuyTimes() == 0 && lastVistDate.before(interstTime))
						|| (brand.getMostBuyTimes() > 0 && brand.getMostBuyTimes() <= 1 && brand.getLastBuyTimes().before(interstTime))) {
					like.remove(j);
					continue;
				}
				
			}
			users.get(i).setWillBuy(new HashSet<Long>(like));
		}
	}
	
	/**
	 * 训练调用函数
	 */
	public void train(List<User> users, Date forgetTime, Date interestTime) {			
		
		selectFromInterested(users, forgetTime, interestTime);
		
		Evaluator evaluator = new Evaluator();
		evaluator.eval(users);		
	}
	
	/**
	 * 预测调用函数
	 */
	public void predict(List<User> users, Date forgetTime, Date interestTime) {
		selectFromInterested(users, forgetTime, interestTime);	
		
		createSubmitFile(users, Consts.SUBMIT_FILE_NAME);
	}	
	
	/**
	 * 生成提交结果文件
	 * 
	 * @param users	预测用户
	 * @param path	文件的路径
	 */
	public void createSubmitFile(List<User> users, String path) {
		FileWriter fw = null;
		int all = 0;
		try {
			fw = new FileWriter(path);
			for (int i = 0; i < users.size(); i++) {								
				Set<Long> like = users.get(i).getWillBuy();
				if (like.size() > 0) {
					Iterator<Long> iterator = like.iterator();
					String brandsList = new String();
					brandsList += iterator.next();
					all++;
					while (iterator.hasNext()) {
						all++;
						brandsList += "," + iterator.next();
					}
					brandsList += "\n";
					fw.write(users.get(i).getId() + "\t" + brandsList);
				} 
				
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("items: " + all);
	}
}

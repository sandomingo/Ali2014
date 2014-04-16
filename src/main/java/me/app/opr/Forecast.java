package me.app.opr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.app.base.Consts;
import me.app.eval.Evaluator;
import me.app.mdl.BrandExtend;
import me.app.mdl.Row;
import me.app.mdl.Topic;
import me.app.mdl.User;

public class Forecast {
	private UserStatistics userStatistics;	
	private BrandStatistics brandStatistics;
	static public Date trainStart, trainEnd, testStart, testEnd;
     final String s = "4-16.txt";
    private static boolean isPredict = true; // 预测 9月 购买 ？
    // for trainning
    static int dayOfMonth = 15;
    static int trainStartMonth = 4;
    static int trainEndMonth = 7;
    static int testStartMonth = 7;
    static int testEndMonth = 8;
	static {
        if (isPredict) {
            dayOfMonth = 15;
            trainStartMonth = 5;
            trainEndMonth = 8;
            testStartMonth = 8;
            testEndMonth = 9;
        }
		Calendar myDate = Calendar.getInstance();
		myDate.set(Calendar.MONTH, trainStartMonth-1);
		myDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		trainStart = myDate.getTime();
		
		myDate.set(Calendar.MONTH, trainEndMonth-1);
		trainEnd = myDate.getTime();
		
		myDate.set(Calendar.MONTH, testStartMonth-1);
		testStart = myDate.getTime();
		
		myDate.set(Calendar.MONTH, testEndMonth-1);
		testEnd = myDate.getTime();
	}
	
	public Forecast() {		
		brandStatistics = new BrandStatistics(trainStart, trainEnd, testStart, testEnd);
		userStatistics = new UserStatistics(trainStart, trainEnd, testStart, testEnd);
	}

	public ArrayList<Long> getLike(Topic topic, Date deadline) {		
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
					if (times >= 4 || (times >= 1 && userStatistics.getBehaviorLastHappenTime(
									topic.getBehaviors(),
									topic.getBehaviors().get(i).getBrandID(),
									Consts.ActionType.CLICK,
									Consts.TopicType.BRAND).getTime() >= deadline.getTime())) {
						like.add(topic.getBehaviors().get(i).getBrandID());
					}
				}
			}
		}

		return like;
	}

	public void myForecast(List<User> users, Date forgetTime, Date interstTime){
		HashMap<Long, Integer> personNumber = new HashMap<Long, Integer>();
		HashMap<Long, Double> frequence = null;
		HashMap<Long, Double> hotBrandsByScore = null;
		hotBrandsByScore = brandStatistics.getHot(0);
		HashMap<Long, Double> usersScore = userStatistics.getHot(0);
		
		frequence = brandStatistics.getActionFrequenceEveryMonthEveryPerson(
				Consts.ActionType.BUY, personNumber);
		HashMap<Long, Double>timeSpan = brandStatistics.getActionTimeSpan(Consts.ActionType.BUY);
		for (int i = 0; i < users.size(); i++) {
			if (!usersScore.containsKey(users.get(i).getId()) || usersScore.get(users.get(i).getId()) < 2) {
				users.get(i).setWillBuy(new HashSet<Long>());
				continue;
			}
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
				

//				// 只会被人在短时间内关注的非热门商品
//				if (lastVistDate.getTime() - firstVistDate.getTime() < 7 * 24 * 60 * 60 * 1000
//						&& lastVistDate.before(interstTime)
//						&& brand.getBuyPersons() <= 4) {
//					like.remove(j);
//					continue;
//				}
				
				
				// 在很久一次超大购买
				if (brand.getBuyPersons() <= 2 && brand.getBuyTimes() > 3 &&brand.getLastBuyTimes().before(interstTime)) {
					like.remove(j);
					continue;
				}
				
				
				// 该用户在很早之前的购买行为，并且在之后未关注过该商品
				if (userBuyTimes >= 1
						&& userStatistics.getBehaviorLastHappenTime(
								user.getBehaviors(), like.get(j),
								Consts.ActionType.BUY, 
								Consts.TopicType.BRAND)
								.getTime() <= forgetTime.getTime()
						) {
					like.remove(j);
					continue;
				}
				
				
				// 该商品很多人点，但很少人买
				if (brand.getClickPersons() > 38 && brand.getBuyPersons() < 3) {
					like.remove(j);
					continue;
				}
				
				
				// 冷门商品
				if (!hotBrandsByScore.containsKey(like.get(j))
						|| hotBrandsByScore.get(like.get(j)) < 1.2) {
					like.remove(j);
					continue;
				}
				
				
				// 耐用品，并且已经购买过的
				if (brand.getMostBuyTimes() == 1 && brand.getBuyPersons() > 35
						&& userBuyTimes == 1) {
					like.remove(j);
					continue;
				}
				

				
				// 一定为非周期的物品
				if ((brand.getMostBuyTimes() == 0 && lastVistDate.before(interstTime))
						|| (brand.getMostBuyTimes() > 0 && brand.getMostBuyTimes() <= 2 && brand.getLastBuyTimes().before(interstTime))) {
					like.remove(j);
					continue;
				}
				/*				
				if (userBuyTimes + brandStatistics.getTopicActionTimes(
						user.getBehaviors(), like.get(j),
						Consts.ActionType.FAVOURITE, Consts.TopicType.BRAND) 
						+ brandStatistics.getTopicActionTimes(
								user.getBehaviors(), like.get(j),
								Consts.ActionType.ADD2CART, Consts.TopicType.BRAND) == 0 && lastVistDate.getTime() >= tmpDate.getTime()
						&& lastVistDate.getTime() <= strictDate.getTime()) {
					like.remove(j);
					continue;
				}*/
			}
			users.get(i).setWillBuy(new HashSet<Long>(like));
		}
	}
	
	public void curForecast() {		
		List<User> users = userStatistics.getUsers();
		Calendar myDate = Calendar.getInstance();
		myDate.setTime(trainStart);
		
		myDate.set(Calendar.MONTH, myDate.get(Calendar.MONTH) + 1);
		Date forgetTime = myDate.getTime();
		
		myDate.setTime(trainEnd);
		myDate.set(Calendar.DAY_OF_MONTH, myDate.get(Calendar.DAY_OF_MONTH) - 7);
		Date interestTime = myDate.getTime();
		

		myForecast(users, forgetTime, interestTime);
		Evaluator evaluator = new Evaluator();
		evaluator.eval(users);		
	}
	
	public void tmpForecast() {
		FileWriter fw = null;

        String path = s;
		List<User> users = userStatistics.getUsers();
		int all = 0;
		
		Calendar myDate = Calendar.getInstance();
		myDate.setTime(trainStart);
		
		myDate.set(Calendar.MONTH, myDate.get(Calendar.MONTH) + 1);
		Date forgetTime = myDate.getTime();
		
		myDate.setTime(trainEnd);
		myDate.set(Calendar.DAY_OF_MONTH, myDate.get(Calendar.DAY_OF_MONTH) - 7);
		Date interestTime = myDate.getTime();
		

		myForecast(users, forgetTime, interestTime);	
		
		
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
	
	/******************************************************************************************************/
	
	public void outScore() {
		FileWriter fw = null;
		String userScorePath = "D://userScore.txt";
		String brandScorePath = "D://brandScorePath.txt";
		

		HashMap<Long, Double> usersScore = userStatistics.getHot(0);
		HashMap<Long, Double> brandScore = brandStatistics.getHot(0);
		
		try {
			fw = new FileWriter(userScorePath);
			Set<Long> set = usersScore.keySet();
			Iterator<Long> it = set.iterator();
			while (it.hasNext()) {
				Long idLong = (Long)it.next();
				fw.write(idLong + "\t" + usersScore.get(idLong) + "\n");
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
		
		try {
			fw = new FileWriter(brandScorePath);
			Set<Long> set = brandScore.keySet();
			Iterator<Long> it = set.iterator();
			while (it.hasNext()) {
				Long idLong = (Long)it.next();
				fw.write(idLong + "\t" + brandScore.get(idLong) + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public ArrayList<Long> getIntersted(Topic topic, Date deadline) {		
		ArrayList<Long> like = new ArrayList<Long>();
		for (int i = 0; i < topic.getBehaviors().size(); i++) {
			if (!like.contains(topic.getBehaviors().get(i).getBrandID())) {
				if (topic.getBehaviors().get(i).getType() != Consts.ActionType.CLICK && topic.getBehaviors().get(i).getVisitDatetime().after(deadline)) {
					like.add(topic.getBehaviors().get(i).getBrandID());
				}
			}
		}

		return like;
	}
	
	public void select(List<User> users, Date tmpDate) {		
		HashMap<Long, Double> hotBrandsByScore = null;
		hotBrandsByScore = brandStatistics.getHot(0);
		HashMap<Long, Double> usersScore = userStatistics.getHot(0);
		Map<Set<Long>, Integer> counter = brandStatistics.getPairCounter();
		
		for (int i = 0; i < users.size(); i++) {
			if (!usersScore.containsKey(users.get(i).getId()) || usersScore.get(users.get(i).getId()) < 1) {
				users.get(i).setWillBuy(new HashSet<Long>());
				continue;
			}
			User user = users.get(i);	
			ArrayList<Long> correlation = new ArrayList<Long>();
			ArrayList<Long> like = getIntersted(user, tmpDate);
			for (int j = like.size() - 1; j >= 0; j--) {
				correlation.add(like.get(j));
				ArrayList<Long> pairs = brandStatistics.getBrand(like.get(j)).getComplements();				
				for (int k = 0; k < pairs.size(); k++) {
					Set<Long> tmp = new HashSet<Long>();
					tmp.add(like.get(j));
					tmp.add(pairs.get(k));
					if (counter.containsKey(tmp) && counter.get(tmp) > 20
							&& !correlation.contains(pairs.get(k))/* && brandStatistics.getBrand(pairs.get(k)).getScore() > 1.5*/) {
						correlation.add(pairs.get(k));
					}
				}
			}
			users.get(i).setWillBuy(new HashSet<Long>(correlation));
		}
	}
	

	public void correlationForecast() {
		FileWriter fw = null;
		String path = "D://4-14.txt";
		List<User> users = userStatistics.getUsers();
		int all = 0;
		
		Calendar myDate = Calendar.getInstance();
		myDate.set(Calendar.MONTH, 6);
		myDate.set(Calendar.DAY_OF_MONTH, 15);
		Date tmpDate = myDate.getTime();

		select(users, tmpDate);
		
		
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

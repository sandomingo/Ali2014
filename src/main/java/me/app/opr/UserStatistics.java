package me.app.opr;

import me.app.mdl.*;
import me.app.utl.FileUtil;
import me.app.base.*;

import java.awt.peer.TrayIconPeer;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计用户等各类数据 User: wuxuef Date: 3/24/14 Time: 05:00 PM
 */
public class UserStatistics extends Statistics {
	private List<User> users = new ArrayList<User>();

	public UserStatistics(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		super();
		createUsers(trainStart, trainEnd, testStart, testEnd);
	}

	public List<User> getUsers() {
		// TODO Auto-generated method stub
		return users;
	}

	public void createUsers(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		ArrayList<Row> rows = FileUtil.readFile(Consts.INPUT_PATH);
		HashMap<Long, List<Behavior>> behaviorsSets = new HashMap<Long, List<Behavior>>();

		for (int i = 0; i < rows.size(); i++) {
			Long uid = Long.parseLong(rows.get(i).getUid());
			Long brandID = Long.parseLong(rows.get(i).getBid());
			Date visitDatetime = string2Date(rows.get(i).getDate());
			Integer code = rows.get(i).getType();
			Consts.ActionType type = Consts.ActionType.fromCode(code);
			Behavior tmpBehavior = new Behavior(brandID, uid, type,
					visitDatetime);

			if (behaviorsSets.containsKey(uid)) {
				behaviorsSets.get(uid).add(tmpBehavior);
			} else {
				List<Behavior> behaviorList = new ArrayList<Behavior>();
				behaviorList.add(tmpBehavior);
				behaviorsSets.put(uid, behaviorList);
			}
		}

		Iterator<Entry<Long, List<Behavior>>> it = behaviorsSets.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<Long, List<Behavior>> entry = (Entry<Long, List<Behavior>>) it
					.next();
			User tmpUser = new User((Long) entry.getKey());
			tmpUser.setBehaviors(entry.getValue());
			users.add(tmpUser);
		}

		setForecastMode(trainStart, trainEnd, testStart, testEnd);
	}

	public void setForecastMode(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		for (User user : users) {
			user.setWillBuy(new HashSet<Long>());
			Set<Long> reallyBuy = new HashSet<Long>();
			List<Behavior> behaviors = user.getBehaviors();
			List<Behavior> trainBehaviors = new ArrayList<Behavior>();
			for (Behavior behavior : behaviors) {
				long behaviorTime = behavior.getVisitDatetime().getTime();
				if (behaviorTime <= trainEnd.getTime()
						&& behaviorTime >= trainStart.getTime()) {
					trainBehaviors.add(behavior);
				} else if (behaviorTime > testStart.getTime()
						&& behaviorTime <= testEnd.getTime()) {
                    if (behavior.getType().equals(Consts.ActionType.BUY)) {
                        reallyBuy.add(behavior.getBrandID());
                    }
				}
			}
			user.setBehaviors(trainBehaviors);
			user.setReallyBuy(reallyBuy);
		}
	}

	public void outputReallyBuy() {
		HashMap<Long, Integer> buyNuMap = new HashMap<Long, Integer>();
		for (int j = 0; j < users.size(); j++) {
			User user = users.get(j);
			buyNuMap.put(user.getId(), user.getReallyBuy().size());
		}
		FileUtil.fout2csv(buyNuMap, "D:\\see.csv");
	}

	public User getUser(long uid) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getId() == uid)
				return users.get(i);
		}

		return null;
	}

	public HashMap<Long, Double> getHot(double threshold) {
		HashMap<Long, Double> hotBrands = new HashMap<Long, Double>();
		for (int i = 0; i < users.size(); i++) {
			double score = getScore(users.get(i));
			Long uid = users.get(i).getId();
			if (score > threshold)
				hotBrands.put(uid, score);
		}

		hotBrands = (HashMap<Long, Double>) sortByValue(hotBrands);
		return hotBrands;
	}

	public HashMap<Long, Double> getActionTimeSpan(Consts.ActionType type) {
		HashMap<Long, Double> timespans = new HashMap<Long, Double>();
		for (int i = 0; i < users.size(); i++) {
			double timespan = getActionTimeSpan(users.get(i), type,
					Consts.TopicType.USER);
			Long uid = users.get(i).getId();
			timespans.put(uid, timespan);
		}
		timespans = (HashMap<Long, Double>) sortByValue(timespans);
		return timespans;
	}

	public HashMap<Long, Double> getActionFrequenceEveryMonthEveryBrand(
			Consts.ActionType type, HashMap<Long, Integer> brandNumber) {
		HashMap<Long, Double> hotBrands = new HashMap<Long, Double>();
		for (int i = 0; i < users.size(); i++) {
			AtomicInteger userNumbers = new AtomicInteger(0);
			double frequence = getTopicFrequence(users.get(i), type,
					userNumbers, Consts.TopicType.USER);
			Long uid = users.get(i).getId();

			if (frequence != 0.0) {
				hotBrands.put(uid, frequence);
				brandNumber.put(uid, userNumbers.get());
			}
		}

		hotBrands = (HashMap<Long, Double>) sortByValue(hotBrands);
		return hotBrands;
	}

	public void likeEmpty() {
		for (int i = 0; i < users.size(); i++) {
			users.get(i).setWillBuy(new HashSet<Long>());
		}
	}

}

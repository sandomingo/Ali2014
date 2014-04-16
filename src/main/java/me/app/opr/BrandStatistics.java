package me.app.opr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.print.attribute.DateTimeSyntax;

import me.app.base.Consts;
import me.app.mdl.Brand;
import me.app.mdl.Behavior;
import me.app.mdl.BrandExtend;
import me.app.mdl.Row;
import me.app.mdl.User;
import me.app.utl.FileUtil;

public class BrandStatistics extends Statistics{
	private List<BrandExtend> brands = new ArrayList<BrandExtend>();
	private Map<Long, ArrayList<Long>> classes = null;
	
	public BrandStatistics(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		super();
		createBrands(trainStart, trainEnd, testStart, testEnd);
//		classes = createClass();
//		setClassInfo();
	}
	
	private void createBrands(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		ArrayList<Row> rows = FileUtil.readFile(Consts.INPUT_PATH);
		HashMap<Long, List<Behavior>> ConsumerecordSets = new HashMap<Long, List<Behavior>>();
		
		for (int i = 0; i < rows.size(); i++) {
			Long uid = Long.parseLong(rows.get(i).getUid());
			Long brandID = Long.parseLong(rows.get(i).getBid());
			Date visitDatetime = string2Date(rows.get(i).getDate());
			Integer code = rows.get(i).getType();
			Consts.ActionType type = Consts.ActionType.fromCode(code);
			Behavior consumerecord = new Behavior(brandID, uid, type, visitDatetime);
			
			if (visitDatetime.getTime() < trainStart.getTime() || visitDatetime.getTime() > trainEnd.getTime()) {
				continue;
			}
			
			if (ConsumerecordSets.containsKey(brandID)) {
				ConsumerecordSets.get(brandID).add(consumerecord);
			} else {
				List<Behavior> behaviorList = new ArrayList<Behavior>();
				behaviorList.add(consumerecord);
				ConsumerecordSets.put(brandID, behaviorList);
			}
		}
		
		Iterator<Entry<Long, List<Behavior>>> it = ConsumerecordSets.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, List<Behavior>> entry = (Entry<Long, List<Behavior>>)it.next();
			BrandExtend tmpBrand = new BrandExtend((Long)entry.getKey());
			tmpBrand.setBehaviors(entry.getValue());
			
			tmpBrand.setBuyTimes(getActionTimes(tmpBrand, Consts.ActionType.BUY));
			tmpBrand.setClickTimes(getActionTimes(tmpBrand, Consts.ActionType.CLICK));
			tmpBrand.setFavouriteTimes(getActionTimes(tmpBrand, Consts.ActionType.FAVOURITE));
			tmpBrand.setAdd2cartTimes(getActionTimes(tmpBrand, Consts.ActionType.ADD2CART));
			
			AtomicInteger number = new AtomicInteger();
			getTopicFrequence(tmpBrand, Consts.ActionType.BUY, number, Consts.TopicType.BRAND);
			tmpBrand.setBuyPersons(number.get());
			getTopicFrequence(tmpBrand, Consts.ActionType.CLICK, number, Consts.TopicType.BRAND);
			tmpBrand.setClickPersons(number.get());
			getTopicFrequence(tmpBrand, Consts.ActionType.FAVOURITE, number, Consts.TopicType.BRAND);
			tmpBrand.setFavouritePersons(number.get());
			getTopicFrequence(tmpBrand, Consts.ActionType.ADD2CART, number, Consts.TopicType.BRAND);
			tmpBrand.setAdd2cartPersons(number.get());
			
			tmpBrand.setScore(getScore(tmpBrand));
			
			brands.add(tmpBrand);
		}
		learnFeature(trainStart, trainEnd, testStart, testEnd);
	}
	
	public void learnFeature(Date trainStart, Date trainEnd, Date testStart,
			Date testEnd) {
		UserStatistics userStatistics = new UserStatistics(trainStart, trainEnd, testStart, testEnd);

		for (int i = 0; i < userStatistics.getUsers().size(); i++) {
			User user = userStatistics.getUsers().get(i);			
			List<Behavior> behaviors = (List<Behavior>) user.getBehaviors();
						
			for (int j = 0; j < behaviors.size(); j++) {
				if (behaviors.get(j).getType() == Consts.ActionType.BUY) {
					int times = getTopicActionTimes(behaviors, 
							behaviors.get(j).getBrandID(), 
							Consts.ActionType.BUY, 
							Consts.TopicType.BRAND);
					BrandExtend brand = getBrand(behaviors.get(j).getBrandID());					
					if (brand.getMostBuyTimes() < times) {
						brand.setMostBuyTimes(times);
					}
					
					if (brand.getLastBuyTimes() == null
							|| behaviors.get(j).getVisitDatetime().after(brand.getLastBuyTimes())) {
						brand.setLastBuyTimes(behaviors.get(j).getVisitDatetime());
					} 
				}
			}
			
			HashMap<Long, ArrayList<Behavior>> holder = new HashMap<Long, ArrayList<Behavior>>();
			for (int k = 0; k < behaviors.size(); k++) {
				long days = behaviors.get(k).getVisitDatetime().getTime() / (24 * 60 * 60 * 1000);
				Set<Long> set = holder.keySet();
				Iterator<Long> it = set.iterator();
				while (it.hasNext()) {
					Long time = (Long)it.next();
					if (isRecent(time, days)) {
						holder.get(time).add(behaviors.get(k));
					}
				}
				
				if (!holder.containsKey(days)) {
					ArrayList<Behavior> tmpArrayList = new ArrayList<Behavior>();
					tmpArrayList.add(behaviors.get(k));
					holder.put(days, tmpArrayList);
				}
			}
			
			Set<Long> set = holder.keySet();
			Iterator<Long> iterator = set.iterator();
			while (iterator.hasNext()) {
				Long time = (Long)iterator.next();
				List<Behavior> tmpBehaviors = holder.get(time);
				int buyTimes = getActionTimes(tmpBehaviors, Consts.ActionType.BUY);
				
				if (buyTimes == 1) {
					for (int k = 0; k < tmpBehaviors.size(); k++) {
						BrandExtend brand = getBrand(tmpBehaviors.get(k).getBrandID());
						List<Long> belongsList = brand.getBelongClass();
						for (int j = 0; j < tmpBehaviors.size(); j++) {
							if (tmpBehaviors.get(j).getBrandID() != brand.getId() && !belongsList.contains(tmpBehaviors.get(j).getBrandID())) {
								belongsList.add(tmpBehaviors.get(j).getBrandID());
							}
						}
					}
				}
				else if (buyTimes >= 2) {
					List<Long> tmpHolder = new ArrayList<Long>();
					HashMap<Long, Integer> flag = new HashMap<Long, Integer>();
					for (int k = 0; k < tmpBehaviors.size(); k++) {
						if (tmpBehaviors.get(k).getType() == Consts.ActionType.BUY/* && !tmpHolder.contains(tmpBehaviors.get(k).getBrandID())*/) {
							tmpHolder.add(tmpBehaviors.get(k).getBrandID());
							flag.put(tmpBehaviors.get(k).getBrandID(), 0);
						}
					}
					
					
					for (int k = 0; k < tmpHolder.size(); k++) {
						BrandExtend brand = getBrand(tmpHolder.get(k));
						if (flag.get(brand.getId()) == 0) {
							flag.put(brand.getId(), 1);
						} else {
							continue;
						}
						List<Long> complementList = brand.getComplements();
						
						HashMap<Long, Integer> inputAlready = new HashMap<Long, Integer>();
						for (int j = 0; j < tmpHolder.size(); j++) {
							if (tmpHolder.get(j) != brand.getId()/* && !complementList.contains(tmpHolder.get(j))*/) {
								inputAlready.put(tmpHolder.get(j), 0);
							}
						}
						
						for (int j = 0; j < tmpHolder.size(); j++) {
							if (tmpHolder.get(j) != brand.getId()/* && !complementList.contains(tmpHolder.get(j))*/) {
								if (inputAlready.get(tmpHolder.get(j)) == 0) {
									inputAlready.put(tmpHolder.get(j), 1);
								} else {
									continue;
								}
								complementList.add(tmpHolder.get(j));
							}
						}
					}
				}
			}
		}
	}
	
	public Map<Long, ArrayList<Long>> createClass() {
		Map<Long, ArrayList<Long>> classes = new HashMap<Long, ArrayList<Long>>();
		Long id = 0L;
		
		for (int i = 0; i < brands.size(); i++) {
			ArrayList<Long> items = new ArrayList<Long>();
			BrandExtend brand = brands.get(i);
			if (brand.getBelongClass().size() != 0) {			
				for (int j = 0; j < brand.getBelongClass().size(); j++) {
					items.add(brand.getBelongClass().get(j));
					BrandExtend succedaneum = getBrand(brand.getBelongClass().get(j));
					for (int k = 0; k < succedaneum.getBelongClass().size(); k++) {
						items.add(succedaneum.getBelongClass().get(k));
					}
				}	
				classes.put(id, getTopItems(items, 2, items.size()));
				id++;
			}
			
		}
		
		return classes;
	}
	
	public void setClassInfo() {
		Set<Long> set = classes.keySet();
		Iterator<Long> iterator = set.iterator();
		while (iterator.hasNext()) {
			Long classId = iterator.next();
			ArrayList<Long> hold = classes.get(classId);
			for (int i = 0; i < hold.size(); i++) {
				BrandExtend brand = getBrand(hold.get(i));
				brand.getClassId().add(classId);
			}
		}
	}
	
	public void setForecastMode(Date deadline) {
		for (int j = 0; j < brands.size(); j++) {
			BrandExtend brand = brands.get(j);
			for (int i = brand.getBehaviors().size() - 1; i >= 0; i--) {
				if (brand.getBehaviors().get(i).getVisitDatetime().after(deadline)) {
					brand.getBehaviors().remove(i);
				}
			}
		}
	}
	
	public List<BrandExtend> getBrands() {
		return brands;
	}

	public BrandExtend getBrand(long brandID) {
		for (int i = 0; i < brands.size(); i++) {
			if (brands.get(i).getId() == brandID)
				return brands.get(i);
		}
		
		return null;
	}
		
	public HashMap<Long, Double> getHot(double threshold) {		
		HashMap<Long, Double> hotBrands = new HashMap<Long, Double>();
		for (int i = 0; i < brands.size(); i++) {
			double score = getScore(brands.get(i));
			Long brandID = brands.get(i).getId();			
			if (score > threshold)
				hotBrands.put(brandID, score);
		}
		
		hotBrands = (HashMap<Long, Double>) sortByValue(hotBrands);		
		return hotBrands;
	}
	
	public HashMap<Long, Double> getActionTimeSpan(Consts.ActionType type) {
		HashMap<Long, Double> timespans = new HashMap<Long, Double>();
		for (int i = 0; i < brands.size(); i++) {
			double timespan = getActionTimeSpan(brands.get(i), type, Consts.TopicType.BRAND);
			Long brandID = brands.get(i).getId();
			timespans.put(brandID, timespan);
		}
		timespans = (HashMap<Long, Double>) sortByValue(timespans);
		return timespans;
	}
	
	public HashMap<Long, Double> getActionFrequenceEveryMonthEveryPerson(Consts.ActionType type, HashMap<Long, Integer> personsNumber) {
		HashMap<Long, Double> hotBrands = new HashMap<Long, Double>();
		for (int i = 0; i < brands.size(); i++) {
			AtomicInteger userNumbers = new AtomicInteger(0);
			double frequence = getTopicFrequence(brands.get(i), type, userNumbers, Consts.TopicType.BRAND);	
			Long brandID = brands.get(i).getId();		
			
			if (frequence != 0.0) { 	
				hotBrands.put(brandID, frequence);
				personsNumber.put(brandID, userNumbers.get());
			}
		}
		
		hotBrands = (HashMap<Long, Double>) sortByValue(hotBrands);	
		return hotBrands;
	}
		
	public void isBuyAfterOtherAction(Consts.ActionType type) {
		int counter = 0;
		int buyCounter = 0;
		for (int i = 0; i < brands.size(); i++) {
			BrandExtend brand = brands.get(i);
			Collections.sort(brand.getBehaviors(), comparatorAsc);
			int j = 0;
			while (j < brand.getBehaviors().size()) {
				long uid = 0;
				for (; j < brand.getBehaviors().size(); j++) {
					if (brand.getBehaviors().get(j).getType() == type) {
						uid = brand.getBehaviors().get(j).getUid();
						counter++;
						break;
					}
				}
				
				if (uid != 0) {
					for (; j < brand.getBehaviors().size(); j++) {
						if (brand.getBehaviors().get(j).getType() == Consts.ActionType.BUY &&
								brand.getBehaviors().get(j).getUid() == uid) {
							System.out.println(brand.getId() + " be bought after " + type);
							buyCounter++;
							break;
						}
					}
				}
			}
		}
		System.out.println(buyCounter + " / " + counter);
	}
	
	public void correlationStatistics() {
		int[] counter = new int[10];	
		
		for (int i = 0; i < brands.size(); i++) {
			int[] hold = new int[10];
			hold[0] = getTopItems(brands.get(i).getComplements(), 1, brands.get(i).getComplements().size()).size();
			for(int j = 1; j < 10; j++) {
				hold[j] = getTopItems(brands.get(i).getComplements(), j + 1, hold[j - 1]).size();
			}
			
			for (int j = 0; j < 9; j++) {
				hold[j] = hold[j] - hold[j + 1];
				counter[j] += hold[j];
			}
			
			counter[9] += hold[9];
		}
		
		for (int i = 1; i <= 10; i++) {
			System.out.println(i + " times: " + counter[i - 1]);
		}

	}

	public Map<Set<Long>, Integer> getPairCounter() {
		Map<Set<Long>, Integer> counter = new HashMap<Set<Long>, Integer>();
		for (int i = 0; i < brands.size(); i++) {
			BrandExtend brand = brands.get(i);
			if (brand.getComplements().size() != 0) {
				for (int j = 0; j < brand.getComplements().size(); j++) {
					Set<Long> pair = new HashSet<Long>();
					pair.add(brand.getId());
					pair.add(brand.getComplements().get(j));
					
					if (counter.containsKey(pair)) {
						counter.put(pair, counter.get(pair) + 1);
					} else {
						counter.put(pair, new Integer(1));
					}
				}
			}
		}
		
		counter = sortByValue(counter);
		
		return counter;
	}
}

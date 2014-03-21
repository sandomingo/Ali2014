package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Vector;

public class Overview {
	ArrayList<Row> rows;
	HashMap<String, Integer> userMap;
	HashMap<String, Integer> brandMap;

	public Overview(ArrayList<Row> rows) {
		this.rows = rows;
		System.out.println("Rows size =" + rows.size());

		initUserAndBrand();
	}

	public void initUserAndBrand() {
		userMap = new HashMap<String, Integer>();
		brandMap = new HashMap<String, Integer>();
		for (Row row : rows) {
			userMap.put(row.getUid(), 0);
			brandMap.put(row.getBid(), 0);
		}

		System.out.println("User size =" + userMap.size());
		System.out.println("Brand size=" + brandMap.size());
	}

	public void showBrandPurchasedNumber() {
		initUserAndBrand();

		String lastUser = "";
		Integer match = 0;

		for (Row row : rows) {
			if (row.getType() == Behavior.BUY) {
				Integer tmpInt = brandMap.get(row.getBid()) + 1;
				brandMap.put(row.getBid(), tmpInt);
				lastUser = row.getUid();
				match++;
			}
		}

		System.out.println("Total buys=" + match);

		ArrayList<Entry<String, Integer>> arrayList = new ArrayList<Entry<String, Integer>>();
		Set<Entry<String, Integer>> brandSet = brandMap.entrySet();
		for (Entry<String, Integer> entry : brandSet) {
			arrayList.add(entry);
			System.out.println(entry.getKey() + "," + entry.getValue());
		}
	}

	public void showUserRelatedBrands() {
		HashMap<String, TreeSet<String>> userRelatedMap = new HashMap<String, TreeSet<String>>();

		for (Row row : rows) {
			String uid = row.getUid();
			String bid = row.getBid();

			TreeSet<String> brandSet = null;

			if (userRelatedMap.containsKey(uid)) {
				brandSet = userRelatedMap.get(uid);
				brandSet.add(bid);
			} else {
				brandSet = new TreeSet<String>();
				brandSet.add(bid);
			}

			userRelatedMap.put(uid, brandSet);
		}

		Iterator<String> iterator = userRelatedMap.keySet().iterator();
		while (iterator.hasNext()) {
			String uid = iterator.next();
			TreeSet<String> brandSet = userRelatedMap.get(uid);

			System.out.print(uid);
			System.out.print("\t");

			Iterator<String> brandIterator = brandSet.iterator();
			boolean isFirst = true;
			while (brandIterator.hasNext()) {
				String bid = brandIterator.next();
				if (isFirst) {
					System.out.print(bid);
					isFirst = false;
				} else {
					System.out.print("," + bid);
				}
			}

			System.out.print("\n");
		}
	}

	class SortByTimes implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			Entry<String, Integer> e1 = (Entry<String, Integer>) o1;
			Entry<String, Integer> e2 = (Entry<String, Integer>) o2;

			if (e1.getValue() > e2.getValue()) {
				return 1;
			}

			return 0;
		}
	}
}

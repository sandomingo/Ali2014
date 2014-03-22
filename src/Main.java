import java.util.ArrayList;
import java.util.HashMap;

import models.Behavior;
import models.Overview;
import models.Row;
import utils.FileUtil;

public class Main {
	private static final String INPUT_PATH = "C://t_alibaba_data.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Row> rows = FileUtil.readFile(INPUT_PATH);

		Overview ov = new Overview(rows);
		//ov.showBrandPurchasedNumber();
		//ov.showUserRelatedBrands();
	
		ArrayList<Integer> typeArrayList = new ArrayList<Integer>();
		typeArrayList.add(Behavior.BUY);
		typeArrayList.add(Behavior.CLICK);
		typeArrayList.add(Behavior.FAV);
		typeArrayList.add(Behavior.INCART);
		//ov.showUserBehaviorsBrands(typeArrayList);
		ov.showUserBehaviorsBrandsNumber(typeArrayList);
	}

}

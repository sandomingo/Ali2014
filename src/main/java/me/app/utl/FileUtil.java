package me.app.utl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

import me.app.mdl.Row;

public class FileUtil {
    public static ArrayList<Row> readFile(String path) {
        ArrayList<Row> rows = new ArrayList<Row>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "gb18030"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] datas = line.split(",");

                if (datas.length == 4 && Character.isDigit(datas[2].charAt(0))) {
                    Row tmpRow = new Row();
                    tmpRow.setUid(datas[0]);
                    tmpRow.setBid(datas[1]);
                    tmpRow.setType(Integer.valueOf(datas[2]));
                    tmpRow.setDate(datas[3]);

                    rows.add(tmpRow);
                    if (tmpRow.getType() == 1) {
//                        System.out.println(tmpRow);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

	public static <K, V extends Comparable<? super V>> void fout2csv(
			Map<K, V> map, String path) {
		Set<K> set = map.keySet();
		Iterator<K> iterator = set.iterator();
		FileWriter fw = null;

		try {
			fw = new FileWriter(path);
			while (iterator.hasNext()) {
				K key = iterator.next();
				fw.write(key + "," + map.get(key) + "\n");
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
	}
	
}

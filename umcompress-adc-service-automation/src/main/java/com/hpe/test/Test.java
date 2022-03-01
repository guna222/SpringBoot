package com.hpe.test;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Test {

	public static Properties properties = null;

	public static JSONObject jsonObject = null;

	public static String sss[] = new String[20];

	static int i = 0;

	static {
		properties = new Properties();
	}

	public static void main(String[] args) {

		try {

			JSONParser jsonParser = new JSONParser();

			File file = new File("src/main/resources/read.json");

			Object object = jsonParser.parse(new FileReader(file));

			jsonObject = (JSONObject) object;

			parseJson(jsonObject);

			for (String s : sss) {
				System.out.println(s);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void getArray(Object object2) throws ParseException {

		JSONArray jsonArr = (JSONArray) object2;
		 //System.out.println("inside split : "+jsonArr.toString());
		for (int k = 0; k < jsonArr.size(); k++) {
			if (jsonArr.get(k) instanceof JSONObject) {
				sss[i] = jsonArr.get(k).toString();
				i++;
				parseJson((JSONObject) jsonArr.get(k));

			} else {
				// System.out.println(jsonArr.get(k));
			}

		}

	}

	public static void parseJson(JSONObject jsonObject) throws ParseException {

		Set<Object> set = jsonObject.keySet();
		Iterator<Object> iterator = set.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (jsonObject.get(obj) instanceof JSONArray) {
				/* System.out.println(obj.toString()); */
				getArray(jsonObject.get(obj));
			} else {
				if (jsonObject.get(obj) instanceof JSONObject) {
					parseJson((JSONObject) jsonObject.get(obj));
				} else {
					//System.out.println(obj.toString() + "\t" + jsonObject.get(obj));
				}
			}
		}
	}

}

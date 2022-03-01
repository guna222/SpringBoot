//(C) Copyright 2019 Hewlett Packard Enterprise Development LP package
package com.hpe.onelytics.kafka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hpe.onelytics.kafka.constants.IKafkaConstants;
import com.hpe.onelytics.kafka.consumer.ConsumerCreator;
import com.hpe.onelytics.kafka.producer.ProducerCreator;

public class App {

	public static Properties properties = null;

	public static JSONObject jsonObject = null;

	public static String sss[] = new String[20];

	static int i = 0;

	static {
		properties = new Properties();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		runProducer();
		// runConsumer();
	}

	static void runConsumer() {
		Consumer<Long, String> consumer = ConsumerCreator.createConsumer();

		int noMessageToFetch = 0;

		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
			if (consumerRecords.count() == 0) {
				noMessageToFetch++;
				if (noMessageToFetch > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
			});
			consumer.commitAsync();
		}
		consumer.close();
	}

	static void runProducer() throws FileNotFoundException, IOException, ParseException {
		Producer<Long, String> producer = ProducerCreator.createProducer();

		JSONParser jsonParser = new JSONParser();

		File file = new File("src/main/resources/read.json");

		Object object = jsonParser.parse(new FileReader(file));

		jsonObject = (JSONObject) object;

		parseJson(jsonObject);

		for (int index = 0; index < sss.length; index++) {

			if (sss[index] != null) {
				System.out.println(sss[index]);
				final ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_NAME,
						sss[index]);
				try {

					RecordMetadata metadata = producer.send(record).get();
					System.out.println("Record sent with key " + sss[index] + " to partition " + metadata.partition()
							+ " with offset " + metadata.offset());
				} catch (ExecutionException e) {
					System.out.println("Error in sending record");
					System.out.println(e);
				} catch (InterruptedException e) {
					System.out.println("Error in sending record");
					System.out.println(e);
				}

			}
		}
	}

	public static void parseJson(JSONObject jsonObject) throws ParseException {

		Set<Object> set = jsonObject.keySet();
		Iterator<Object> iterator = set.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (jsonObject.get(obj) instanceof JSONArray) {
				getArray(jsonObject.get(obj));
			} else {
				if (jsonObject.get(obj) instanceof JSONObject) {
					parseJson((JSONObject) jsonObject.get(obj));
				}
			}
		}
	}

	public static void getArray(Object object2) throws ParseException {

		JSONArray jsonArr = (JSONArray) object2;
		for (int k = 0; k < jsonArr.size(); k++) {
			if (jsonArr.get(k) instanceof JSONObject) {
				sss[i] = jsonArr.get(k).toString();
				i++;
				parseJson((JSONObject) jsonArr.get(k));

			}
		}
	}
}
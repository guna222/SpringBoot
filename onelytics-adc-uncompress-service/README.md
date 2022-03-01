# onelytics-adc-uncompress-service


The microapp does following work
1. Decrypts the ADC data using decryptutil java library provided by Support tools team. results in a tar.gz file
2. Uncompress the above tar.gz file using apache commons compress library in java.

The microapp is built using Akka library. The application's functionality is divided as akka actors.
1. Consumer Actor used for consuming kafka messages from move service.
2. Decrypt Actor used for decrypting the kafka messages passed from consumer actor
3. Uncompress Actor used for umcompressing the tar.gz file passed from decrypt actor
4. Producer Actor used to pass the uncompressed directory location to next service.

Built using 4 actors namely Consumer Actor, Decrypt Actor, Uncompress Actor, Producer Actor.

The microapp is developed using TDD approach, Test driven development and covers most of the test scenarios.
For testing akka actors akka testkit is used.

The microapp is mainly Java based application and packaged as a container going forward.

Dependencies:
1. Need kafka server for running the Junit test cases.

TODOs:
1. Offset management. Parallel actors.
2. Adding more test cases for actors
3. Integration test end to end, embedding kafka server.
4. Tracing failures through request ID and station ID concept, how to trace the single adc from service to service in logs ?
5. Cleanup ADC data ?
6. Logging, create lobback.xml file. Common logging ?
7. Containerizing the microservice with maven command.
8. Grafana - prometheus dashboard to see the actors in action and other performance metrics
9. Documentation for microapp.
10. Performance managment in akka actors, reaching 100% CPU tested with 18 kafka messages at the same time.


Sequence diagram:
Move service -> message -> Consumer Actor -> Decrypt Actor -> Uncompress Actor -> Producer Actor -> message.



## Testing uncompress service locally in dev-sandbox

* Step1: Dowonload Zookeeper, cassandra and Kafka and extract ina noted location
Refer https://kafka.apache.org/quickstart for downloading and installing the components

* Step2:
Note the IP address of the host that can be pinged from the container

* Step3: Update the following config of Kafka
In *config/servver.properties*
```
listeners=PLAINTEXT://16.168.161.73:9092
```

* Step4: Update the following config of Cassandra
In *cassandra.yaml* set these attributes:
Under *seed_provider*:
```
- seeds: "16.168.161.73"
```
Update the listen address
```
listen_address: 16.168.161.73
```

* Step5:
Start zookeeper, Kafka and Cassandra

Assuming Kafka is located in C:\opt\kafka_2.12-2.2.0, 
```
cd C:\opt\kafka_2.12-2.2.0
bin/zookeeper-server-start.sh config/zookeeper.properties
```
Then start Kafka after zookeeper is up
Assuming Kafka is located in C:\opt\kafka_2.12-2.2.0, 
```
cd C:\opt\kafka_2.12-2.2.0
bin/kafka-server-start.sh config/server.properties
```
* Step6:
Assuming cassandra is extracted to C:\opt\apache-cassandra-3.11.4

cd C:\opt\apache-cassandra-3.11.4\bin
```
cassandra
```

* Step7:
Start Kafka publisher
```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic <topic-name - defined in UNCOMPRESS_IN_TOPIC of Step9>
```

* Step8:

*mvn install* will create a container. 
You can list the containers with following command
```
$ docker image list
REPOSITORY                                 TAG                 IMAGE ID            CREATED             SIZE
onelytix/uncompress-adc-service            1.0                 7d0f882a7e65        30 hours ago        589MB
<none>                                     <none>              a746407ef925        31 hours ago        589MB
```
Notedown the IMAGE_ID

* Step9:
Start the container and note down the docker conatiner IP address. Run the following command
```
winpty docker run \
--network=host \
--env http_proxy=http://proxy.houston.hpecorp.net:8080 \
--env https_proxy=https://proxy.houston.hpecorp.net:8088 \
--env CASSANDRA_CONTACT_POINT=16.168.161.73:4000 \
--env KAFKA_BROKER_CONTACT_POINT=16.168.161.73:9092 \
--env ADC_BASE_LOCATION='//adc' \
--env ADC_UNCOMPRESS_TO_LOCATION='//uncompressed_adc' \
--env UNCOMPRESS_IN_TOPIC='move.adc.status.change' \
--env UNCOMPRESS_OUT_TOPIC='uncompress-status-change' \
--env KAFKA_GROUPID='onelytics.uncompress.popatgroup' \
-i -t 7d0f882a7e65 sh
```
Container will start container with IMAGE ID *7d0f882a7e65* with interactive bash shell

Notedown the IP address of the container for eth0 - typically will be 192.168.65.3
if ifconfig command is not available, run the following command inside the container to install the needed packages
```
apt-get update;apt-get install vim net-tools
```

* Step10:
Open the application.conf under /app and set akka.container.address to <IP address of the container>
For ex:
```
akka.container.address = "192.168.65.3"
```
  
* Step11:
Make sure the host address where Kafka and cassandra are running by pinging them.

* Step12: Verify the environment variables passed while starting the container are set inside the container properly

* Step13: Start the microservice inside the contatiner
```
cd app
sh ./service_start.sh
```

* Step14: Send the following message in publisher started in Step7.
```
{"Device-SID": "14770ae2-aa53-43a7-b769-b1fd742265a8", 	"Device-SN": "VMware-42 3f 06 9d 0d 1d 6a ea-80 44 25 a1 ef 9e c4 f5", 	"File-Class": "data-file", 	"File-Name": "encrypted_adc.tar.gz", 	"File-Size": "830333", 	"File-URL": "/adc/encrypted_adc.tar.gz", 	"File-UUID": "37ccc038-2207-4713-8fc2-c786e96d1f8e", 	"Product-Class": "oneview", 	"Product-ID": "ai-cicvc3-126.vse.rdlabs.hpecorp.net", 	"Upload-Date": 1568073826000 } 
```
For testing purpose a sample ADC is copied inside the conatiner and is available under directory /adc. Same is defined in File-name and File-URL of above Kafka message.

* Step15: Using Kafkatool, make sure the message is updated in out topic as well.

* Step16: Check cassandra uncompress keyspace and verify the messages are persisted and task traker table has proper set of steps completed.

kill $(ps aux | grep "java -jar target/yak-0.0.1-SNAPSHOT.jar" | grep -v 'grep' | awk '{print $2}')
# cat /root/application.properties > /root/project/src/main/resources/application.properties
# cat /root/project/src/main/resources/application.properties
# java -jar target/yak-0.0.1-SNAPSHOT.jar
chmod -R 777 /root/project/showtime_API/
chmod +x mvnw && dos2unix mvnw  && ./mvnw package && ./mvnw spring-boot:run
tail -f /dev/null
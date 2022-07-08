kill $(ps aux | grep "java -jar target/yak-0.0.1-SNAPSHOT.jar" | grep -v 'grep' | awk '{print $2}')
chmod +x mvnw && dos2unix mvnw  && ./mvnw package && java -jar target/yak-0.0.1-SNAPSHOT.jar
tail -f /dev/null
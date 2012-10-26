
encrypt:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EncryptUtilMain" -Dexec.args="providers"

random:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.RandomUtilMain"

uptime:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.UptimeMain"

compression:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.CompressionUtilMain"

environment:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EnvironmentUtilMain"

window:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.util.demo.WindowMain"

window2:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.Window2Main"

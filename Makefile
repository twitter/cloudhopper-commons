
run-encrypt:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EncryptUtilMain" -Dexec.args="providers"

run-random:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.RandomUtilMain"

run-uptime:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.UptimeMain"

run-compression:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.CompressionUtilMain"

run-environment:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EnvironmentUtilMain"

run-window:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.util.demo.WindowMain"

run-window2:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.Window2Main"

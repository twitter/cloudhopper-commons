
demo-encrypt:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EncryptUtilMain" -Dexec.args="providers"

demo-random:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.RandomUtilMain"

demo-uptime:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.UptimeMain"

demo-compression:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.CompressionUtilMain"

demo-environment:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.EnvironmentUtilMain"

demo-window:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.util.demo.WindowMain"

demo-window2:
	mvn test-compile exec:java -Dexec.classpathScope="test" exec:java -Dexec.mainClass="com.cloudhopper.commons.util.demo.Window2Main"

run-charset1:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.CharsetMain"

run-charset2:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset2Main"

run-charset3:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset3Main"

run-charset4:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset4Main"

run-charset5:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset5Main"

run-charset6:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset6Main"

run-benchmark:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.BenchmarkMain"
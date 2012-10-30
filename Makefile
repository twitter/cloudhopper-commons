charset1:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.CharsetMain"

charset2:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset2Main"

charset3:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset3Main"

charset4:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset4Main"

charset5:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset5Main"

charset6:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.Charset6Main"

benchmark:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.BenchmarkMain"

utf8-benchmark:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.UTF8BenchmarkMain"

gsm-benchmark:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.GSMBenchmarkMain"

string-reflection:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.charset.demo.StringReflectionMain"
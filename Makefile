run-datacoding:
	mvn test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.gsm.demo.DataCodingMain"
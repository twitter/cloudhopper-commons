run-fileserver:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.io.demo.FileServerMain" -Dexec.args="/tmp/ 2"

run-idtest:
	mvn -e test-compile exec:java -Dexec.classpathScope="test" -Dexec.mainClass="com.cloudhopper.commons.io.demo.IdTestMain" -Dexec.args=""
package zooplus.test;

import org.junit.Test;

public class logTest extends baseTest {
	
	@Test
	public void testLogFile() {
		log.debug("Hello this is a debug message");
		log.info("Hello this is an info message");
	}

}

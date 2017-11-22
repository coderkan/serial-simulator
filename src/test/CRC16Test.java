package test;
import static org.junit.jupiter.api.Assertions.*;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import com.main.CRC16;
 

class CRC16Test extends EasyMockSupport{
	
	@Rule 
	public EasyMockRule rule = new EasyMockRule(this);
	
	@TestSubject
	private CRC16 crc = new CRC16();
	


	
	@Test
	void testGetValue() {
		assertTrue(crc.getValue() == 0xFFFF);
	}

	@Test
	void testReset() {
		crc.update(0x44);
		replayAll();
		verifyAll();
		crc.reset();
		replayAll();
		verifyAll();

		//assertTrue(crc.getValue() == 0xFFFF);
		//fail("Not yet implemented");

	}

	@Test
	void testUpdateInt() {
		//fail("Not yet implemented");
	}

	@Test
	void testUpdateByteArrayIntInt() {
		//fail("Not yet implemented");
	}

	@Test
	void testGetCrcBytes() {
		//fail("Not yet implemented");
	}

}

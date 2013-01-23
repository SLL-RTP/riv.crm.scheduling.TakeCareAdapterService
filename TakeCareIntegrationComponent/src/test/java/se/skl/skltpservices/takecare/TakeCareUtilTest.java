package se.skl.skltpservices.takecare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.skl.skltpservices.takecare.TakeCareUtil.numericToInteger;
import static se.skl.skltpservices.takecare.TakeCareUtil.shortToBoolean;

import org.junit.Test;

public class TakeCareUtilTest {

	@Test
	public void testTransformBookingAllowed() throws Exception {
		boolean bookingAllowed = shortToBoolean(Short.valueOf("1"));
		assertTrue(bookingAllowed);
	}

	@Test
	public void testTransformBookingNotAllowed() throws Exception {
		boolean bookingAllowed = shortToBoolean(Short.valueOf("0"));
		assertFalse(bookingAllowed);
	}

	@Test
	public void testTransformBookingNotAllowedWhenIllegalValue() throws Exception {
		boolean bookingAllowed = shortToBoolean(Short.valueOf("23"));
		assertFalse(bookingAllowed);
	}

	@Test
	public void testNubmericToInteger() {
		assertEquals(new Integer(1), numericToInteger("1"));
	}

	@Test
	public void testNubmericToIntegerWhenNull() {
		assertEquals(null, numericToInteger(null));
	}

	@Test
	public void testNubmericToIntegerWhenEmpty() {
		assertEquals(null, numericToInteger(""));
	}

}

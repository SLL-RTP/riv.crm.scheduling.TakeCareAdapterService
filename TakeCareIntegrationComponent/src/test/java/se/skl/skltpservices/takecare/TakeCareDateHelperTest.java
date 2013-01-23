package se.skl.skltpservices.takecare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;

public class TakeCareDateHelperTest {

	@Test
	public void testRemoveSecondsFromRivTaTime() {
		String actual = TakeCareDateHelper.toTakeCareLongTime("20120612121530");
		assertEquals("201206121215", actual);
	}

	@Test(expected = RuntimeException.class)
	public void testRemoveSecondsFromRivTaTimeThrowsErrorWhenNull() {
		TakeCareDateHelper.toTakeCareLongTime(null);
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testRemoveSecondsFromRivTaTimeThrowsErrorWhenEmpty() {
		TakeCareDateHelper.toTakeCareLongTime("");
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testRemoveSecondsFromRivTaTimeThrowsErrorWhenWrongLength() {
		TakeCareDateHelper.toTakeCareLongTime("123");
		fail("Excpected RuntimeException");
	}

	@Test
	public void testAddSecondsFromTakeCareTimeToRivTaTime() {
		String actual = TakeCareDateHelper.toRivTaLongTime(new BigInteger("201206121215"));
		assertEquals("20120612121500", actual);
	}

	@Test(expected = RuntimeException.class)
	public void testAddSecondsToTakeCareThrowsErrorWhenWrongLength() {
		TakeCareDateHelper.toRivTaLongTime(new BigInteger("123"));
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testAddSecondsToTakeCareThrowsErrorWhenEmpty() {
		TakeCareDateHelper.toRivTaLongTime(new BigInteger(""));
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testAddSecondsToTakeCareThrowsErrorWhenNull() {
		TakeCareDateHelper.toRivTaLongTime(null);
		fail("Excpected RuntimeException");
	}

	@Test
	public void testToTakeCareShortTime() {
		long actual = TakeCareDateHelper.toTakeCareShortTime("20120612121500");
		assertEquals(20120612l, actual);
	}

	@Test(expected = RuntimeException.class)
	public void testToTakeCareShortTimeRivTaToLongFormat() {
		TakeCareDateHelper.toTakeCareShortTime("201206121215000000");
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testToTakeCareShortTimeRivTaToShortFormat() {
		TakeCareDateHelper.toTakeCareShortTime("201206121");
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testToTakeCareShortTimeRivTaNotNumeric() {
		TakeCareDateHelper.toTakeCareShortTime("2012-06-12-12-");
		fail("Excpected RuntimeException");
	}

	@Test(expected = RuntimeException.class)
	public void testToTakeCareShortTimeRivTaIsNull() {
		TakeCareDateHelper.toTakeCareShortTime(null);
		fail("Excpected RuntimeException");
	}
}

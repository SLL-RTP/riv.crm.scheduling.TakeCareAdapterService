package se.skl.skltpservices.takecare.takecareintegrationcomponent.makebooking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class MakeBookingResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/MakeBooking/response-input.xml");

		String expectedResult = MiscUtil
				.readFileAsString("src/test/resources/testfiles/MakeBooking/response-output.xml");

		MakeBookingResponseTransformer transformer = new MakeBookingResponseTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		assertEquals(expectedResult, result);
	}

}
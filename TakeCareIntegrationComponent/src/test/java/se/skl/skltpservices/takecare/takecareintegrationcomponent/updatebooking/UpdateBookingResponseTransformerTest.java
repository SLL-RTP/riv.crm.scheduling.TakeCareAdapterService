package se.skl.skltpservices.takecare.takecareintegrationcomponent.updatebooking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class UpdateBookingResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/UpdateBooking/response-input.xml");

		String expectedResult = MiscUtil
				.readFileAsString("src/test/resources/testfiles/UpdateBooking/response-output.xml");

		UpdateBookingResponseTransformer transformer = new UpdateBookingResponseTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		assertEquals(expectedResult, result);
	}

}
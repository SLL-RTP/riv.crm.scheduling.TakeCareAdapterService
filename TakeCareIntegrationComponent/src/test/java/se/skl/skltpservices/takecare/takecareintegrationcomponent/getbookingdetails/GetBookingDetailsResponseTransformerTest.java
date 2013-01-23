package se.skl.skltpservices.takecare.takecareintegrationcomponent.getbookingdetails;

import static org.junit.Assert.assertEquals;
import static org.soitoolkit.commons.mule.smooks.SmooksUtil.runSmooksTransformer;

import java.io.IOException;

import org.junit.Test;
import org.mule.api.transformer.TransformerException;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class GetBookingDetailsResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		String input = MiscUtil.readFileAsString("src/test/resources/testfiles/GetBookingDetails/response-input.xml");

		String expectedResult = MiscUtil
				.readFileAsString("src/test/resources/testfiles/GetBookingDetails/response-output.xml");


		GetBookingDetailsResponseTransformer transformer = new GetBookingDetailsResponseTransformer();
		String result = (String) transformer.pojoTransform(input, "UTF-8");

		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}

}
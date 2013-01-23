package se.skl.skltpservices.takecare.takecaretestproducer;

import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

public class TakeCareUtil {

	public static final String INVOKING_SYSTEM = "InvSysMVK";
	public static final String EXTERNAL_USER = "ExtUsrMVK";
	public static final String HSAID = "hsaid";
	public static final String REQUEST = "Request";
	public static final String RESPONSE = "Response";
	public static final String WEB = "Web";

	public static final BigInteger numericToBigInteger(String value) {
		if (StringUtils.isNumeric(value)) {
			return new BigInteger(value);
		}
		return null;
	}

	public static final int numericToInt(String value) {
		if (StringUtils.isNumeric(value)) {
			return new Integer(value);
		}
		return 0;
	}

	public static final Integer numericToInteger(String value) {
		if (StringUtils.isNotEmpty(value) && StringUtils.isNumeric(value)) {
			return new Integer(value);
		}
		return null;
	}

	public static final String bigIntegerToString(BigInteger value) {
		if (value != null) {
			return value.toString();
		}
		return null;
	}
	
	public static final String integerToString(Integer value) {
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	/**
	 * @param shortValue
	 *            The short value
	 * @return true if shortValue is 1, otherwise false
	 */
	public static final Boolean shortToBoolean(Short shortValue) {
		return shortValue != null && shortValue == 1;
	}

}

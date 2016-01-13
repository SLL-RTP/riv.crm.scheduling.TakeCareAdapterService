package se.skl.skltpservices.takecare.takecaretestproducer;

import static se.skl.skltpservices.takecare.takecaretestproducer.TakeCareUtil.HSAID;
import static se.skl.skltpservices.takecare.takecaretestproducer.TakeCareUtil.RESPONSE;

import java.math.BigInteger;
import java.util.Date;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

import se.skl.skltpservices.takecare.booking.BookingSoap;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage.AvailableTimeslots;
import se.skl.skltpservices.takecare.booking.getavailabletimeslotsresponse.ProfdocHISMessage.AvailableTimeslots.Timeslot;

@WebService(targetNamespace = "http://tempuri.org/", name = "BookingSoap", portName = "BookingSoap")
public class GetAvailableTimeslotsTestProducer extends TakeCareTestProducer implements BookingSoap {

	public static final String TEST_HEALTHCAREFACILITY_OK = "HSA-VKK123";
	public static final String TEST_HEALTHCAREFACILITY_INVALID_ID = "-1";
	public static final String TEST_ID_FAULT_TIMEOUT = "0";

	private static final Logger log = LoggerFactory.getLogger(GetAvailableTimeslotsTestProducer.class);
	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("TakeCareTestProducer-config");
	private static final long SERVICE_TIMOUT_MS = Long.parseLong(rb.getString("SERVICE_TIMEOUT_MS"));

	private static final JaxbUtil jaxbUtil_outgoing = new JaxbUtil(ProfdocHISMessage.class);

	public String getAvailableTimeslots(String tcusername, String tcpassword, String externaluser,
			String careunitidtype, String careunitid, String xml) {

		log.debug("Incoming username to TakeCare {}", tcusername);
		log.debug("Incoming password to TakeCare {}", tcpassword);
		log.debug("Incoming externaluser to TakeCare {}", externaluser);
		log.debug("Incoming careunitidtype to TakeCare {}", careunitidtype);
		log.debug("Incoming careunitid to TakeCare {}", careunitid);
		log.debug("Incoming xml to TakeCare {}", xml);

		if (TEST_HEALTHCAREFACILITY_INVALID_ID.equals(careunitid)) {
			return createErrorResponse(externaluser, careunitid);
		} else if (TEST_ID_FAULT_TIMEOUT.equals(careunitid)) {
			try {
				Thread.sleep(SERVICE_TIMOUT_MS + 1000);
			} catch (InterruptedException e) {
			}
		}
		return createOkResponse(externaluser, careunitid);
	}

	private String createOkResponse(String externaluser, String careunitid) {
		ProfdocHISMessage outgoing_response = new ProfdocHISMessage();
		outgoing_response.setCareUnit(careunitid);
		outgoing_response.setCareUnitType(HSAID);
		outgoing_response.setMethod("Booking.GetAvailableTimeslots");
		outgoing_response.setMsgType(RESPONSE);
		outgoing_response.setSystem("ProfdocHIS");
		outgoing_response.setSystemInstance(0);
		outgoing_response.setTime(yyyyMMddHHmmss(new Date()));
		outgoing_response.setUser(externaluser);

		// Take Care always deliver exactly one AvailableDates element according
		// to spec
		outgoing_response.getAvailableTimeslots().add(createAvailableTimeslots(careunitid));
        jaxbUtil_outgoing.addMarshallProperty("com.sun.xml.bind.xmlDeclaration", false);
        return jaxbUtil_outgoing.marshal(outgoing_response, "", "ProfdocHISMessage");
	}

	private AvailableTimeslots createAvailableTimeslots(String careunitid) {
		AvailableTimeslots availableTimeslots = new AvailableTimeslots();
		availableTimeslots.setCareUnitId(careunitid);
		availableTimeslots.setCareUnitIdType(HSAID);
		availableTimeslots.setCareUnitName("careunit name");
		availableTimeslots.getTimeslot().add(createTimeSlot());
		availableTimeslots.getTimeslot().add(createTimeSlot());
		availableTimeslots.getTimeslot().add(createTimeSlot());
		return availableTimeslots;
	}

	private Timeslot createTimeSlot() {
		Timeslot timeslot = new Timeslot();
		timeslot.setResourceId(new BigInteger("191212121212"));
		timeslot.setResourceName("Tolvansson Tolvan (läk)");
		timeslot.setResourceType(Short.valueOf("1"));
		timeslot.setStartTime(yyyyMMddHHmm(new Date()));
		timeslot.setEndTime(yyyyMMddHHmm(new Date()));
		timeslot.setTimeTypeId(4);
		timeslot.setTimeTypeName("Nybesök Web");
		return timeslot;
	}

}

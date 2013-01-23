package se.skl.skltpservices.takecare.takecareintegrationcomponent.getsubjectofcareschedule;

import static se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareIntegrationComponentMuleServer.getAddress;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.refapps.sd.sample.wsdl.v1.Fault;
import org.w3.wsaddressing10.AttributedURIType;

import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleResponderInterface;
import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleResponderService;
import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleResponseType;
import se.riv.crm.scheduling.getsubjectofcareschedule.v1.GetSubjectOfCareScheduleType;
import se.skl.skltpservices.takecare.TakeCareTestConsumer;

public class GetSubjectOfCareScheduleTestConsumer extends TakeCareTestConsumer {

	private static final Logger log = LoggerFactory.getLogger(GetSubjectOfCareScheduleTestConsumer.class);

	private GetSubjectOfCareScheduleResponderInterface _service = null;

	public static void main(String[] args) throws Fault {
		String serviceAddress = getAddress("GETSUBJECTOFCARESCHEDULE_INBOUND_URL");
		String subjectOfCare = "191414141414";
		String healthcareFacility = "HSA-VKK123";

		GetSubjectOfCareScheduleTestConsumer consumer = new GetSubjectOfCareScheduleTestConsumer(serviceAddress);
		GetSubjectOfCareScheduleResponseType response = consumer.callService(healthcareFacility, subjectOfCare);
		log.info("Returned value = " + response.getTimeslotDetail());
	}

	public GetSubjectOfCareScheduleTestConsumer(String serviceAddress) {
		try {
			initHttpsCommunication();
			URL url = new URL(serviceAddress + "?wsdl");
			_service = new GetSubjectOfCareScheduleResponderService(url).getGetSubjectOfCareScheduleResponderPort();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Malformed URL Exception: " + e.getMessage());
		}
	}

	public GetSubjectOfCareScheduleResponseType callService(String healthcareFacility, String subjectOfCare)
			throws Fault {
		log.debug("Calling GetSubjectOfCareSchedule-service with healthcareFacility {}, subjectOfCare {}",
				healthcareFacility, subjectOfCare);

		GetSubjectOfCareScheduleType request = new GetSubjectOfCareScheduleType();
		request.setHealthcareFacility(healthcareFacility);
		request.setSubjectOfCare(subjectOfCare);
		return _service.getSubjectOfCareSchedule(new AttributedURIType(), request);
	}
}
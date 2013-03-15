package se.skl.skltpservices.takecare;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Created with IntelliJ IDEA.
 * User: khaled.daham@callistaenterprise.se
 */
public class TakeCareNamespacePrefixMapper extends XMLFilterImpl {

    private static final String PROFDOC_QNAME = "ProfdocHISMessage";
    private final String namespace;

    public TakeCareNamespacePrefixMapper(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(PROFDOC_QNAME)) {
            super.endElement(this.namespace, localName, qName);
        } else {
            super.endElement("", localName, qName);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
        if (qName.equals(PROFDOC_QNAME)) {
            super.startElement(this.namespace, localName, qName, attr);
        } else {
            super.startElement("", localName, qName, attr);
        }
    }
}
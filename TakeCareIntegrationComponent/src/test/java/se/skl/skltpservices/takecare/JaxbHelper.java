/*
 * Copyright 2013 khaleddaham.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.skl.skltpservices.takecare;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import se.skl.skltpservices.takecare.takecareintegrationcomponent.TakeCareValidationEventHandler;

/**
 *
 * @author khaleddaham
 */
public class JaxbHelper {

    public static Object transform(Object message, String namespace, String incoming_string) {
        JAXBContext jc = null;

        try {
            jc = JAXBContext.newInstance(message.getClass());
            XMLFilter filter = new TakeCareNamespacePrefixMapper(namespace);

            // Set the parent XMLReader on the XMLFilter
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            filter.setParent(xr);

            // Set UnmarshallerHandler as ContentHandler on XMLFilter
            Unmarshaller unmarshaller = null;
            unmarshaller = jc.createUnmarshaller();
            unmarshaller.setEventHandler(new TakeCareValidationEventHandler());
            UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();
            filter.setContentHandler(unmarshallerHandler);
            filter.parse(new InputSource(new StringReader(incoming_string)));
            message = unmarshallerHandler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return message;
    }
}

package com.yyzy.pz.onvif.soap;

import org.w3c.dom.Document;

import javax.xml.bind.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class SOAP {
    private boolean logging = false;
    private OnvifDevice onvifDevice;
    private static Map<String, JAXBContext> jaxbContextMap = new HashMap();

    public SOAP(OnvifDevice onvifDevice) {
        this.onvifDevice = onvifDevice;
    }

    public Object createSOAPDeviceRequest(Object soapRequestElem, Object soapResponseElem, boolean needsAuthentification) throws SOAPException, ConnectException {
        return this.createSOAPRequest(soapRequestElem, soapResponseElem, this.onvifDevice.getDeviceUri(), needsAuthentification);
    }

    public Object createSOAPPtzRequest(Object soapRequestElem, Object soapResponseElem, boolean needsAuthentification) throws SOAPException, ConnectException {
        return this.createSOAPRequest(soapRequestElem, soapResponseElem, this.onvifDevice.getPtzUri(), needsAuthentification);
    }

    public Object createSOAPMediaRequest(Object soapRequestElem, Object soapResponseElem, boolean needsAuthentification) throws SOAPException, ConnectException {
        return this.createSOAPRequest(soapRequestElem, soapResponseElem, this.onvifDevice.getMediaUri(), needsAuthentification);
    }

    public Object createSOAPImagingRequest(Object soapRequestElem, Object soapResponseElem, boolean needsAuthentification) throws SOAPException, ConnectException {
        return this.createSOAPRequest(soapRequestElem, soapResponseElem, this.onvifDevice.getImagingUri(), needsAuthentification);
    }

    public Object createSOAPEventsRequest(Object soapRequestElem, Object soapResponseElem, boolean needsAuthentification) throws SOAPException, ConnectException {
        return this.createSOAPRequest(soapRequestElem, soapResponseElem, this.onvifDevice.getEventsUri(), needsAuthentification);
    }

    public Object createSOAPRequest(Object soapRequestElem, Object soapResponseElem, String soapUri, boolean needsAuthentification) throws ConnectException, SOAPException {
        SOAPConnection soapConnection = null;
        SOAPMessage soapResponse = null;

        SOAPMessage soapMessage;
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
            soapMessage = this.createSoapMessage(soapRequestElem, needsAuthentification);
            if (this.isLogging()) {
                System.out.print("Request SOAP Message (" + soapRequestElem.getClass().getSimpleName() + "): ");
                soapMessage.writeTo(System.out);
                System.out.println();
            }

            soapResponse = soapConnection.call(soapMessage, soapUri);
            if (this.isLogging()) {
                System.out.print("Response SOAP Message (" + soapResponseElem.getClass().getSimpleName() + "): ");
                soapResponse.writeTo(System.out);
                System.out.println();
            }

            if (soapResponseElem == null) {
                throw new NullPointerException("Improper SOAP Response Element given (is null).");
            }

            String className = soapResponseElem.getClass().getName();
            JAXBContext jaxbContext = (JAXBContext)jaxbContextMap.get(className);
            if (jaxbContext == null) {
                jaxbContext = JAXBContext.newInstance(new Class[]{soapResponseElem.getClass()});
                jaxbContextMap.put(soapResponseElem.getClass().getName(), jaxbContext);
            }

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            try {
                try {
                    soapResponseElem = unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
                } catch (SOAPException var26) {
                    soapResponseElem = unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
                }
            } catch (UnmarshalException var27) {
                this.onvifDevice.getLogger().warn("Could not unmarshal, ended in SOAP fault.");
            }

            Object var12 = soapResponseElem;
            return var12;
        } catch (SocketException var28) {
            throw new ConnectException(var28.getMessage());
        } catch (SOAPException var29) {
            this.onvifDevice.getLogger().error("Unexpected response. Response should be from class " + soapResponseElem.getClass() + ", but response is: " + soapResponse);
            throw var29;
        } catch (JAXBException | IOException | ParserConfigurationException var30) {
            this.onvifDevice.getLogger().error("Unhandled exception: " + var30.getMessage());
            var30.printStackTrace();
            soapMessage = null;
        } finally {
            try {
                soapConnection.close();
            } catch (SOAPException var25) {
            }

        }

        return soapMessage;
    }

    protected SOAPMessage createSoapMessage(Object soapRequestElem, boolean needAuthentification) throws SOAPException, ParserConfigurationException, JAXBException {
        MessageFactory messageFactory = MessageFactory.newInstance("SOAP 1.2 Protocol");
        SOAPMessage soapMessage = messageFactory.createMessage();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        javax.xml.bind.Marshaller marshaller = JAXBContext.newInstance(new Class[]{soapRequestElem.getClass()}).createMarshaller();
        marshaller.marshal(soapRequestElem, document);
        soapMessage.getSOAPBody().addDocument(document);
        this.createSoapHeader(soapMessage);
        soapMessage.saveChanges();
        return soapMessage;
    }

    protected void createSoapHeader(SOAPMessage soapMessage) throws SOAPException {
        this.onvifDevice.createNonce();
        String encrypedPassword = this.onvifDevice.getEncryptedPassword();
        if (encrypedPassword != null && this.onvifDevice.getUsername() != null) {
            SOAPPart sp = soapMessage.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPHeader header = soapMessage.getSOAPHeader();
            se.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
            se.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            SOAPElement securityElem = header.addChildElement("Security", "wsse");
            SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");
            SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
            usernameElem.setTextContent(this.onvifDevice.getUsername());
            SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
            passwordElem.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
            passwordElem.setTextContent(encrypedPassword);
            SOAPElement nonceElem = usernameTokenElem.addChildElement("Nonce", "wsse");
            nonceElem.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
            nonceElem.setTextContent(this.onvifDevice.getEncryptedNonce());
            SOAPElement createdElem = usernameTokenElem.addChildElement("Created", "wsu");
            createdElem.setTextContent(this.onvifDevice.getLastUTCTime());
        }

    }

    public boolean isLogging() {
        return this.logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }
}

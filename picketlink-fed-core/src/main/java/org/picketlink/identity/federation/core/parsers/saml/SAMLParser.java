/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.identity.federation.core.parsers.saml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.picketlink.identity.federation.core.exceptions.ParsingException;
import org.picketlink.identity.federation.core.parsers.AbstractParser;
import org.picketlink.identity.federation.core.parsers.ParserNamespaceSupport;
import org.picketlink.identity.federation.core.parsers.saml.metadata.SAMLEntitiesDescriptorParser;
import org.picketlink.identity.federation.core.parsers.saml.metadata.SAMLEntityDescriptorParser;
import org.picketlink.identity.federation.core.parsers.saml.xacml.SAMLXACMLRequestParser;
import org.picketlink.identity.federation.core.parsers.util.StaxParserUtil;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLConstants;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants;

/**
 * Parse SAML payload
 * @author Anil.Saldhana@redhat.com
 * @since Oct 12, 2010
 */
public class SAMLParser extends AbstractParser
{ 
   /**
    * @see {@link ParserNamespaceSupport#parse(XMLEventReader)}
    */
   public Object parse(XMLEventReader xmlEventReader) throws ParsingException
   {
      while( xmlEventReader.hasNext() )
      {
         XMLEvent xmlEvent = StaxParserUtil.peek(xmlEventReader);

         if( xmlEvent instanceof StartElement )
         {
            StartElement startElement = (StartElement) xmlEvent;
            QName startElementName = startElement.getName();
            String nsURI = startElementName.getNamespaceURI();
            
            String localPart = startElementName.getLocalPart();

            String elementName = StaxParserUtil.getStartElementName( startElement );
            if( elementName.equalsIgnoreCase( JBossSAMLConstants.ASSERTION.get() ))
            {
               SAMLAssertionParser assertionParser = new SAMLAssertionParser();
               return assertionParser.parse( xmlEventReader ); 
            }
            else if( JBossSAMLURIConstants.PROTOCOL_NSURI.get().equals( nsURI ) &&
                  JBossSAMLConstants.AUTHN_REQUEST.get().equals( startElementName.getLocalPart() ))
            {
               SAMLAuthNRequestParser authNRequestParser = new SAMLAuthNRequestParser();
               return authNRequestParser.parse( xmlEventReader );
            }
            else if( JBossSAMLURIConstants.PROTOCOL_NSURI.get().equals( nsURI ) &&
                  JBossSAMLConstants.LOGOUT_REQUEST.get().equals( startElementName.getLocalPart() ))
            {
               SAMLSloRequestParser sloParser = new SAMLSloRequestParser();
               return sloParser.parse( xmlEventReader ); 
            }
            else if( JBossSAMLURIConstants.PROTOCOL_NSURI.get().equals( nsURI ) &&
                  JBossSAMLConstants.LOGOUT_RESPONSE.get().equals( startElementName.getLocalPart() ))
            {
               SAMLSloResponseParser sloParser = new SAMLSloResponseParser();
               return sloParser.parse( xmlEventReader ); 
            }
            else if( JBossSAMLURIConstants.PROTOCOL_NSURI.get().equals( nsURI ) &&
                  JBossSAMLConstants.RESPONSE.get().equals( startElementName.getLocalPart() ))
            {
               SAMLResponseParser responseParser = new SAMLResponseParser();
               return responseParser.parse( xmlEventReader ); 
            }

            else if( JBossSAMLURIConstants.PROTOCOL_NSURI.get().equals( nsURI ) &&
                  JBossSAMLConstants.REQUEST_ABSTRACT.get().equals( startElementName.getLocalPart() ))
            { 
               String xsiTypeValue = StaxParserUtil.getXSITypeValue(startElement); 
               if( xsiTypeValue.contains( JBossSAMLConstants.XACML_AUTHZ_DECISION_QUERY_TYPE.get() ))
               {
                  SAMLXACMLRequestParser samlXacmlParser = new SAMLXACMLRequestParser();
                  return samlXacmlParser.parse(xmlEventReader); 
               }
               throw new RuntimeException( "Unknown xsi:type=" + xsiTypeValue );
            }
            else if( JBossSAMLConstants.XACML_AUTHZ_DECISION_QUERY.get().equals( localPart ) )
            {
               SAMLXACMLRequestParser samlXacmlParser = new SAMLXACMLRequestParser();
               return samlXacmlParser.parse(xmlEventReader);
            }
            else if( JBossSAMLConstants.ENTITY_DESCRIPTOR.get().equals( localPart ))
            {
               SAMLEntityDescriptorParser entityDescriptorParser = new SAMLEntityDescriptorParser();
               return entityDescriptorParser.parse( xmlEventReader );
            }
            else if( JBossSAMLConstants.ENTITIES_DESCRIPTOR.get().equals( localPart ))
            {
               SAMLEntitiesDescriptorParser entityDescriptorParser = new SAMLEntitiesDescriptorParser();
               return entityDescriptorParser.parse( xmlEventReader );
            }
            else if( JBossSAMLURIConstants.ASSERTION_NSURI.get().equals(nsURI) )
            {
               SAMLAssertionParser assertionParser = new SAMLAssertionParser(); 
               return assertionParser.parse( xmlEventReader );
            }  
            else throw new RuntimeException( "Unknown Tag:" + elementName );
         }
         else
         {
            StaxParserUtil.getNextEvent(xmlEventReader); 
         }
      }
      throw new RuntimeException( "SAML Parsing has failed" );
   }

   /**
    * @see {@link ParserNamespaceSupport#supports(QName)}
    */
   public boolean supports(QName qname)
   {
      return JBossSAMLURIConstants.ASSERTION_NSURI.get().equals( qname.getNamespaceURI() );
   }
}
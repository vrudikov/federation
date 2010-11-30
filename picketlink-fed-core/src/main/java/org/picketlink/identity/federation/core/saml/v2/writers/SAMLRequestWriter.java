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
package org.picketlink.identity.federation.core.saml.v2.writers;

import static org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants.ASSERTION_NSURI;
import static org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants.PROTOCOL_NSURI;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLConstants;
import org.picketlink.identity.federation.core.util.StaxUtil;
import org.picketlink.identity.federation.core.util.StringUtil; 
import org.picketlink.identity.federation.newmodel.saml.v2.assertion.NameIDType;
import org.picketlink.identity.federation.newmodel.saml.v2.protocol.AuthnRequestType;
import org.picketlink.identity.federation.newmodel.saml.v2.protocol.LogoutRequestType;
import org.picketlink.identity.federation.newmodel.saml.v2.protocol.NameIDPolicyType;

/**
 * Writes a SAML2 Request Type to Stream
 * @author Anil.Saldhana@redhat.com
 * @since Nov 2, 2010
 */
public class SAMLRequestWriter extends BaseWriter
{   
   public SAMLRequestWriter(XMLStreamWriter writer) throws ProcessingException
   {
      super(writer);
   }
   
   /**
    * Write a {@code AuthnRequestType } to stream
    * @param request
    * @param out
    * @throws ProcessingException
    */
   public void write( AuthnRequestType request ) throws ProcessingException
   { 
      StaxUtil.writeStartElement( writer, PROTOCOL_PREFIX, JBossSAMLConstants.AUTHN_REQUEST.get() , PROTOCOL_NSURI.get() ); 
      StaxUtil.writeNameSpace( writer, PROTOCOL_PREFIX, PROTOCOL_NSURI.get() );   
      StaxUtil.writeDefaultNameSpace( writer, ASSERTION_NSURI.get() );
      
      //Attributes 
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.ID.get(), request.getID() );
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.VERSION.get(), request.getVersion() );
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.ISSUE_INSTANT.get(), request.getIssueInstant().toString() );
       
      URI destination = request.getDestination();
      if( destination != null )
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.DESTINATION.get(), destination.toASCIIString() ); 

      String consent = request.getConsent();
      if( StringUtil.isNotNull( consent ))
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.CONSENT.get(), consent );
      
      URI assertionURL = request.getAssertionConsumerServiceURL();
      if( assertionURL != null )
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.ASSERTION_CONSUMER_SERVICE_URL.get(), assertionURL.toASCIIString() );
      
      NameIDType issuer = request.getIssuer();
      if( issuer != null )
      {
         write( issuer, new QName( ASSERTION_NSURI.get(), JBossSAMLConstants.ISSUER.get()));
      } 
      NameIDPolicyType nameIDPolicy = request.getNameIDPolicy();
      if( nameIDPolicy != null )
         write( nameIDPolicy );
      
      StaxUtil.writeEndElement( writer); 
      StaxUtil.flush( writer );  
   }
   
   /**
    * Write a {@code LogoutRequestType} to stream
    * @param logOutRequest
    * @param out
    * @throws ProcessingException
    */
   public void write( LogoutRequestType logOutRequest ) throws ProcessingException
   {
      StaxUtil.writeStartElement( writer, PROTOCOL_PREFIX, JBossSAMLConstants.LOGOUT_REQUEST.get() , PROTOCOL_NSURI.get() ); 
      
      StaxUtil.writeNameSpace( writer, PROTOCOL_PREFIX, PROTOCOL_NSURI.get() );   
      StaxUtil.writeDefaultNameSpace( writer, ASSERTION_NSURI.get() );
      
      //Attributes 
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.ID.get(), logOutRequest.getID() );
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.VERSION.get(), logOutRequest.getVersion() );
      StaxUtil.writeAttribute( writer, JBossSAMLConstants.ISSUE_INSTANT.get(), logOutRequest.getIssueInstant().toString() );
      
      URI destination = logOutRequest.getDestination();
      if( destination != null )
      {
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.DESTINATION.get(), destination.toASCIIString() );
      }
      
      String consent = logOutRequest.getConsent();
      if( StringUtil.isNotNull( consent ))
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.CONSENT.get(), consent );
      
      NameIDType issuer = logOutRequest.getIssuer();
      write( issuer, new QName( ASSERTION_NSURI.get(), JBossSAMLConstants.ISSUER.get()));
      
      StaxUtil.writeEndElement( writer); 
      StaxUtil.flush( writer ); 
   }
   
   /**
    * Write a {@code NameIDPolicyType} to stream
    * @param nameIDPolicy
    * @param out
    * @throws ProcessingException
    */
   public void write( NameIDPolicyType nameIDPolicy ) throws ProcessingException
   {
      StaxUtil.writeStartElement( writer, PROTOCOL_PREFIX, JBossSAMLConstants.NAMEID_POLICY.get(), PROTOCOL_NSURI.get() );
      
      URI format = nameIDPolicy.getFormat();
      if( format != null )
      {
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.FORMAT.get(), format.toASCIIString() );
      }
      
      String spNameQualifier = nameIDPolicy.getSPNameQualifier();
      if( StringUtil.isNotNull( spNameQualifier ))
      {
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.SP_NAME_QUALIFIER.get(), spNameQualifier );
      }
      
      Boolean allowCreate = nameIDPolicy.isAllowCreate();
      if( allowCreate != null )
      {
         StaxUtil.writeAttribute( writer, JBossSAMLConstants.ALLOW_CREATE.get(), allowCreate.toString() ); 
      } 

      StaxUtil.writeEndElement( writer); 
      StaxUtil.flush( writer ); 
   }
}
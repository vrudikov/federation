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
package org.picketlink.identity.federation.core.saml.v2.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.picketlink.identity.federation.core.constants.AttributeConstants;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants;
import org.picketlink.identity.federation.core.saml.v2.constants.X500SAMLProfileConstants;
import org.picketlink.identity.federation.core.saml.v2.factories.JBossSAMLBaseFactory;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeType;
import org.picketlink.identity.federation.saml.v2.assertion.ObjectFactory;

/**
 * Deals with SAML2 Statements
 * @author Anil.Saldhana@redhat.com
 * @since Aug 31, 2009
 */
public class StatementUtil
{
   public static final QName X500_QNAME = new QName(JBossSAMLURIConstants.X500_NSURI.get(), "Encoding");

   private static ObjectFactory factory = new ObjectFactory();

   /**
    * Create an attribute statement with all the attributes
    * @param attributes a map with keys from {@link AttributeConstants}
    * @return
    */
   public static AttributeStatementType createAttributeStatement(Map<String, Object> attributes)
   {
      AttributeStatementType attrStatement = null;

      int i = 0;

      Set<String> keys = attributes.keySet();
      for (String key : keys)
      {
         if (i == 0)
         {
            //Deal with the X500 Profile of SAML2
            attrStatement = JBossSAMLBaseFactory.createAttributeStatement();
            i++;
         }

         // if the attribute contains roles, add each role as an attribute.
         if (AttributeConstants.ROLES.equalsIgnoreCase(key))
         {
            Object value = attributes.get(key);
            if (value instanceof Collection<?>)
            {
               Collection<?> roles = (Collection<?>) value;
               for (Object role : roles)
               {
                  AttributeType roleAttr = JBossSAMLBaseFactory.createAttributeForRole((String) role);
                  attrStatement.getAttributeOrEncryptedAttribute().add(factory.createAttribute(roleAttr));
               }
            }
         }

         else
         {
            AttributeType att = getX500Attribute();
            Object value = attributes.get(key);

            if (AttributeConstants.EMAIL_ADDRESS.equals(key))
            {
               att.setFriendlyName(X500SAMLProfileConstants.EMAIL_ADDRESS.getFriendlyName());
               att.setName(X500SAMLProfileConstants.EMAIL_ADDRESS.get());
            }
            else if (AttributeConstants.EMPLOYEE_NUMBER.equals(key))
            {
               att.setFriendlyName(X500SAMLProfileConstants.EMPLOYEE_NUMBER.getFriendlyName());
               att.setName(X500SAMLProfileConstants.EMPLOYEE_NUMBER.get());
            }
            else if (AttributeConstants.GIVEN_NAME.equals(key))
            {
               att.setFriendlyName(X500SAMLProfileConstants.GIVENNAME.getFriendlyName());
               att.setName(X500SAMLProfileConstants.GIVENNAME.get());
            }
            else if (AttributeConstants.TELEPHONE.equals(key))
            {
               att.setFriendlyName(X500SAMLProfileConstants.TELEPHONE.getFriendlyName());
               att.setName(X500SAMLProfileConstants.TELEPHONE.get());
            }
            att.getAttributeValue().add(value);
            attrStatement.getAttributeOrEncryptedAttribute().add(att);
         }
      }
      return attrStatement;
   }

   /**
    * Given a set of roles, create an attribute statement
    * @param roles
    * @return
    */
   public static AttributeStatementType createAttributeStatement(List<String> roles)
   {
      AttributeStatementType attrStatement = JBossSAMLBaseFactory.createAttributeStatement();
      for (String role : roles)
      {
         AttributeType attr = JBossSAMLBaseFactory.createAttributeForRole(role);
         attrStatement.getAttributeOrEncryptedAttribute().add(attr);
      }
      return attrStatement;
   }

   private static AttributeType getX500Attribute()
   {
      AttributeType att = factory.createAttributeType();
      att.getOtherAttributes().put(X500_QNAME, "LDAP");

      att.setNameFormat(JBossSAMLURIConstants.ATTRIBUTE_FORMAT_URI.get());
      return att;
   }
}
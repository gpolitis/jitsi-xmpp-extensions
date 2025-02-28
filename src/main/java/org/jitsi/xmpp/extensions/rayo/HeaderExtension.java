/*
 * Copyright @ 2018 - present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.xmpp.extensions.rayo;

import org.jitsi.xmpp.extensions.*;

import javax.xml.namespace.*;

/**
 * Header packet extension optionally included in {@link RayoIqProvider.RayoIq}.
 * Holds 'name' and 'value' attributes.
 *
 * @author Pawel Domas
 */
public class HeaderExtension
    extends AbstractPacketExtension
{
    /**
     * XML element name.
     */
    public static final String ELEMENT = "header";

    /**
     * Qualified name of element.
     */
    public static final QName QNAME = new QName(RayoIqProvider.NAMESPACE, ELEMENT);

    /**
     * The name of 'name' attribute.
     */
    public static final String NAME_ATTR_NAME = "name";

    /**
     * The name of 'value' attribute.
     */
    public static final String VALUE_ATTR_NAME = "value";

    /**
     * Creates new instance of <tt>HeaderPacketExtension</tt>.
     */
    public HeaderExtension()
    {
        super(RayoIqProvider.NAMESPACE, ELEMENT);
    }

    /**
     * Return the value of 'name' attribute.
     * @return the value of 'name' attribute.
     */
    public String getName()
    {
        return getAttributeAsString(NAME_ATTR_NAME);
    }

    /**
     * Sets new value for 'name' attribute of this extension.
     * @param name the new value to set for 'name' attribute.
     */
    public void setName(String name)
    {
        setAttribute(NAME_ATTR_NAME, name);
    }

    /**
     * Returns the value of 'value' attribute.
     * @return the value of 'value' attribute.
     */
    public String getValue()
    {
        return getAttributeAsString(VALUE_ATTR_NAME);
    }

    /**
     * Sets new value for the 'value' attribute.
     * @param value new value for the 'value' attribute to set.
     */
    public void setValue(String value)
    {
        setAttribute(VALUE_ATTR_NAME, value);
    }
}

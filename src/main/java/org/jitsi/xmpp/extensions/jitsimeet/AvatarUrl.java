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
package org.jitsi.xmpp.extensions.jitsimeet;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.util.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * A implementation of a {@link PacketExtension} for the jitsi-meet "avatar-url"
 * element.
 *
 * @author Boris Grozev
 */
public class AvatarUrl
    implements ExtensionElement
{
    public static final String NAMESPACE = "jabber:client";

    public static final String ELEMENT = "avatar-url";

    private String avatarUrl = null;

    /**
     * Initializes an {@link AvatarUrl} instance with a given string value.
     * @param avatarUrl the string value.
     */
    public AvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @return the value of the avatar-url element as a string.
     */
    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    /**
     * Sets the value of this avatar-url element.
     *
     * @param avatarUrl the value to set.
     */
    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Element name.
     * @return element name for this extension.
     */
    public String getElementName()
    {
        return ELEMENT;
    }

    /**
     * Returns the namespace for this extension.
     * @return the namespace for this extension.
     */
    public String getNamespace()
    {
        return NAMESPACE;
    }

    /*
     * Returns xml representation of this extension.
     * @return xml representation of this extension.
     */
    public String toXML(XmlEnvironment enclosingNamespace)
    {
        return new XmlStringBuilder()
            .element(getElementName(), getAvatarUrl())
            .toString();
    }

    /**
     * The provider.
     */
    public static class Provider
        extends ExtensionElementProvider<AvatarUrl>
    {
        @Override
        public AvatarUrl parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException, SmackParsingException
        {
            parser.next();
            final String address = parser.getText();

            // Advance to end of extension.
            while (parser.getEventType() != XmlPullParser.Event.END_ELEMENT)
            {
                parser.next();
            }

            return new AvatarUrl(address);
        }
    }
}

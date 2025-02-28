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
package org.jitsi.xmpp.extensions.jingle;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.*;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * The <tt>SctpMapExtensionProvider</tt> parses "sctpmap" elements into
 * <tt>SctpMapExtension</tt> instances.
 *
 * @author lishunyang
 * @see SctpMapExtension
 */
public class SctpMapExtensionProvider
    extends ExtensionElementProvider
{

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionElement parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        SctpMapExtension result = new SctpMapExtension();

        if (parser.getName().equals(SctpMapExtension.ELEMENT)
            && parser.getNamespace().equals(SctpMapExtension.NAMESPACE))
        {
            result.setPort(Integer.parseInt(parser.getAttributeValue(null,
                SctpMapExtension.PORT_ATTR_NAME)));
            result.setProtocol(parser.getAttributeValue(null,
                SctpMapExtension.PROTOCOL_ATTR_NAME));
            String stream_attr = parser.getAttributeValue(null, SctpMapExtension.STREAMS_ATTR_NAME);
            if ((stream_attr != null) && (!stream_attr.isEmpty()))
            {
                result.setStreams(Integer.parseInt(stream_attr));
            }
        }

        return result;
    }
}

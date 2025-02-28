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
package org.jitsi.xmpp.extensions.condesc;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.parsing.*;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.xml.*;

import java.io.*;

/**
 * Parses elements with the {@value ConferenceDescriptionExtension#NAMESPACE}
 * namespace.
 */
public class ConferenceDescriptionExtensionProvider
    extends ExtensionElementProvider<ConferenceDescriptionExtension>
{
    /**
     * Creates a <tt>ConferenceDescriptionPacketExtension</tt> by parsing
     * an XML document.
     * @param parser the parser to use.
     * @return the created <tt>ConferenceDescriptionPacketExtension</tt>.
     * @throws Exception
     */
    @Override
    public ConferenceDescriptionExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        ConferenceDescriptionExtension packetExtension
                = new ConferenceDescriptionExtension();

        //first, set all attributes
        int attrCount = parser.getAttributeCount();

        for (int i = 0; i < attrCount; i++)
        {
            packetExtension.setAttribute(
                    parser.getAttributeName(i),
                    parser.getAttributeValue(i));
        }

        //now parse the sub elements
        boolean done = false;
        String elementName;
        TransportExtension transportExt = null;

        while (!done)
        {
            switch (parser.next())
            {
                case START_ELEMENT:
                    elementName = parser.getName();
                    if (TransportExtension.ELEMENT.equals(elementName))
                    {
                        String transportNs = parser.getNamespace();
                        if (transportNs != null)
                        {
                            transportExt = new TransportExtension(transportNs);
                        }
                    }

                    break;

                case END_ELEMENT:
                    switch (parser.getName())
                    {
                        case ConferenceDescriptionExtension.ELEMENT:
                            done = true;
                            break;

                        case TransportExtension.ELEMENT:
                            if (transportExt != null)
                            {
                                packetExtension.addChildExtension(transportExt);
                            }
                            break;
                    }
            }
        }

        return packetExtension;
    }
}

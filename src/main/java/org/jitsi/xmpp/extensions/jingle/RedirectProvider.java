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
 * The <tt>RedirectProvider</tt> parses "redirect" elements into {@link
 * RedirectPacketExtension} instances.
 *
 * @author Sebastien Vincent
 */
public class RedirectProvider
    extends ExtensionElementProvider
{
    /**
     * Parses a reason extension sub-packet and creates a {@link
     * RedirectPacketExtension} instance. At the beginning of the method call,
     * the xml parser will be positioned on the opening element of the packet
     * extension. As required by the smack API, at the end of the method call,
     * the parser will be positioned on the closing element of the packet
     * extension.
     *
     * @param parser an XML parser positioned at the opening <tt>redirect</tt>
     * element.
     *
     * @return a new {@link RedirectPacketExtension} instance.
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    @Override
    public RedirectPacketExtension parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        String text = null;
        boolean done = false;
        XmlPullParser.Event eventType;

        text = parseText(parser);

        while (!done)
        {
            eventType = parser.next();

            if (eventType == XmlPullParser.Event.START_ELEMENT)
            {
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                if (parser.getName().equals(
                    RedirectPacketExtension.ELEMENT))
                {
                    done = true;
                }
            }
        }

        RedirectPacketExtension redirectExt
            = new RedirectPacketExtension();
        redirectExt.setText(text);
        redirectExt.setRedir(text);
        return redirectExt;
    }

    /**
     * Returns the content of the next {@link XmlPullParser.Event#TEXT_CHARACTERS} element that
     * we encounter in <tt>parser</tt>.
     *
     * @param parser the parse that we'll be probing for text.
     *
     * @return the content of the next {@link XmlPullParser.Event#TEXT_CHARACTERS} element we
     * come across or <tt>null</tt> if we encounter a closing tag first.
     *
     * @throws java.lang.Exception if an error occurs parsing the XML.
     */
    public String parseText(XmlPullParser parser)
        throws XmlPullParserException, IOException, SmackParsingException
    {
        boolean done = false;

        XmlPullParser.Event eventType;
        String text = null;

        while (!done)
        {
            eventType = parser.next();

            if (eventType == XmlPullParser.Event.TEXT_CHARACTERS)
            {
                text = parser.getText();
                done = true;
            }
            else if (eventType == XmlPullParser.Event.END_ELEMENT)
            {
                done = true;
            }
        }

        return text;
    }
}

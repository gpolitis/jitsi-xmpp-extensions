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

import org.jitsi.xmpp.extensions.*;

/**
 * An extension of the presence which stores an ID of the avatar. The extension
 * looks like follows:
 *
 * <pre>{@code <avatar-id>some_unique_id</avatar-id>}</pre>
 *
 * @author Nik Vaessen
 */
public class AvatarIdPacketExtension
    extends AbstractPacketExtension
{

    /**
     * The namespace (xmlns attribute) of this avatar-id presence element
     */
    public static final String NAMESPACE = "jabber:client";

    /**
     * The element name of this avatar-id presence element
     *
     */
    public static final String ELEMENT = "avatar-id";

    /**
     * Default constructor.
     *
     * {@inheritDoc}
     */
    public AvatarIdPacketExtension()
    {
        super(NAMESPACE, ELEMENT);
    }

    /**
     * Initializes an {@link AvatarIdPacketExtension} instance with a given
     * string value
     *
     * @param avatarId the string value representing the avatar id
     */
    public AvatarIdPacketExtension(String avatarId)
    {
        super(NAMESPACE, ELEMENT);

        setText(avatarId);
    }

    /**
     * Get the avatar-id value stored in this element
     *
     * @return the value of the avatar-id element as a string.
     */
    public String getAvatarId()
    {
        return getText();
    }
}

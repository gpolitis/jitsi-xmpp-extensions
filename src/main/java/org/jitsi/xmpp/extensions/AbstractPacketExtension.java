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
package org.jitsi.xmpp.extensions;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.*;

/**
 * A generic implementation of <tt>ExtensionElement</tt>. The purpose of this
 * class is quite similar to that of smack's {@link StandardExtensionElement}
 * with the main difference being that this one is meant primarily for
 * extension rather than using as a fallback for unknown elements. We let for
 * example our descendants handle child elements and we automate attribute
 * handling instead.
 *
 * @author Emil Ivov
 * @author Lyubomir Marinov
 */
public abstract class AbstractPacketExtension
    implements ExtensionElement
{
    /**
     * Clones the attributes, namespace and text of a specific
     * <tt>AbstractPacketExtension</tt> into a new
     * <tt>AbstractPacketExtension</tt> instance of the same run-time type.
     *
     * @param src the <tt>AbstractPacketExtension</tt> to be cloned
     * @return a new <tt>AbstractPacketExtension</tt> instance of the run-time
     * type of the specified <tt>src</tt> which has the same attributes,
     * namespace and text
     * @throws Exception if an error occurs during the cloning of the specified
     * <tt>src</tt>
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractPacketExtension> T clone(T src)
    {
        T dst = null;
        try
        {
            dst = (T) src.getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }

        // attributes
        for (String name : src.getAttributeNames())
        {
            dst.setAttribute(name, src.getAttribute(name));
        }
        // namespace
        dst.setNamespace(src.getNamespace());
        // text
        dst.setText(src.getText());

        return dst;
    }

    /**
     * The name space of this packet extension. Should remain <tt>null</tt> if
     * there's no namespace associated with this element.
     */
    private String namespace;

    /**
     * The name space of this packet extension. Should remain <tt>null</tt> if
     * there's no namespace associated with this element.
     */
    private final String elementName;

    /**
     * A map of all attributes that this extension is currently using.
     */
    protected final Map<String, Object> attributes
        = new LinkedHashMap<String, Object>();

    /**
     * The text content of this packet extension, if any.
     */
    private String textContent;

    /**
     * A list of extensions registered with this element.
     */
    private final List<ExtensionElement> childExtensions
                                = new ArrayList<>();

    /**
     * Creates an {@link AbstractPacketExtension} instance for the specified
     * <tt>namespace</tt> and <tt>elementName</tt>.
     *
     * @param namespace the XML namespace for this element.
     * @param elementName the name of the element
     */
    protected AbstractPacketExtension(String namespace, String elementName)
    {
        this.namespace = namespace;
        this.elementName = elementName;
    }

    /**
     * Returns the name of the <tt>encryption</tt> element.
     *
     * @return the name of the <tt>encryption</tt> element.
     */
    public String getElementName()
    {
        return elementName;
    }

    /**
     * Set the XML namespace for this element.
     *
     * @param namespace the XML namespace for this element.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * Returns the XML namespace for this element or <tt>null</tt> if the
     * element does not live in a namespace of its own.
     *
     * @return the XML namespace for this element or <tt>null</tt> if the
     * element does not live in a namespace of its own.
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * Returns an XML representation of this extension.
     *
     * @return an XML representation of this extension.
     */
    public String toXML(XmlEnvironment enclosingNamespace)
    {
        XmlStringBuilder xml = new XmlStringBuilder(this, enclosingNamespace);

        //add the rest of the attributes if any
        for (Map.Entry<String, Object> entry : attributes.entrySet())
        {
            Object value = entry.getValue();

            if (value != null)
            {
                xml.attribute(entry.getKey(), value.toString());
            }
        }

        //add child elements if any
        List<? extends ExtensionElement> childElements = getChildExtensions();
        String text = getText();
        XmlStringBuilder childBuilder = getChildElementBuilder();

        if (childElements.isEmpty() && childBuilder.length() == 0)
        {
            if (StringUtils.isEmpty(text))
            {
                return xml.closeEmptyElement().toString();
            }
            else
            {
                xml.rightAngleBracket();
            }
        }
        else
        {
            synchronized(childElements)
            {
                xml.rightAngleBracket();

                for (ExtensionElement packExt : childElements)
                {
                    xml.optAppend(packExt);
                }

                xml.append(childBuilder);
            }
        }

        //text content if any
        if (StringUtils.isNotEmpty(text))
        {
            xml.append(org.jivesoftware.smack.util.StringUtils.escapeForXmlText(text));
        }

        xml.closeElement(getElementName());

        return xml.toString();
    }

    /**
     * This method must be overwritten by subclasses to create their
     * child content.
     * @return the xml builder for the content.
     */
    public XmlStringBuilder getChildElementBuilder()
    {
        return new XmlStringBuilder();
    }

    /**
     * Returns all sub-elements for this <tt>AbstractPacketExtension</tt> or
     * <tt>null</tt> if there aren't any.
     * <p>
     * Overriding extensions may need to override this method if they would like
     * to have anything more elaborate than just a list of extensions.
     *
     * @return the {@link List} of elements that this packet extension contains.
     */
    public List<? extends ExtensionElement> getChildExtensions()
    {
        return childExtensions;
    }

    /**
     * Adds the specified <tt>childExtension</tt> to the list of extensions
     * registered with this packet.
     * <p/>
     * Overriding extensions may need to override this method if they would like
     * to have anything more elaborate than just a list of extensions (e.g.
     * casting separate instances to more specific.
     *
     * @param childExtension the extension we'd like to add here.
     */
    public void addChildExtension(ExtensionElement childExtension)
    {
        childExtensions.add(childExtension);
    }

    /**
     * Add the given extension to the list of child extensions, but, if there already exists
     * any child extensions of this type, remove them first.
     * @param childExtension the extension to add
     */
    public void setChildExtension(ExtensionElement childExtension)
    {
        getChildExtensionsOfType(childExtension.getClass())
            .forEach(this::removeChildExtension);
        addChildExtension(childExtension);
    }

    /**
     * Gets the first extension present of the given type
     * @param type the type of extension to get
     * @return the first instance of an extension of type T we find, or null if there is none
     */
    public <T extends ExtensionElement> T getChildExtension(Class<T> type)
    {
        List<T> childExts = getChildExtensionsOfType(type);
        if (!childExts.isEmpty())
        {
            return childExts.get(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes all occurrences of an extension element from the list of child
     * extensions.
     * @param childExtension the child extension to remove.
     * @return {@code true} if any extensions were removed, and {@code false}
     * otherwise.
     */
    public boolean removeChildExtension(ExtensionElement childExtension)
    {
        boolean removed = false;
        if (childExtension != null)
        {
            while (childExtensions.remove(childExtension))
            {
                removed = true;
            }
        }

        return removed;
    }

    /**
     * Sets the value of the attribute named <tt>name</tt> to <tt>value</tt>.
     *
     * @param name the name of the attribute that we are setting.
     * @param value an {@link Object} whose <tt>toString()</tt> method returns
     * the XML value of the attribute we are setting or <tt>null</tt> if we'd
     * like to remove the attribute with the specified <tt>name</tt>.
     */
    public void setAttribute(String name, Object value)
    {
        synchronized(attributes)
        {
            if (value != null)
            {
                this.attributes.put(name, value.toString());
            }
            else
            {
                this.attributes.remove(name);
            }
        }
    }

    /**
     * Removes the attribute with the specified <tt>name</tt> from the list of
     * attributes registered with this packet extension.
     *
     * @param name the name of the attribute that we are removing.
     */
    public void removeAttribute(String name)
    {
        synchronized(attributes)
        {
            attributes.remove(name);
        }
    }

    /**
     * Returns the attribute with the specified <tt>name</tt> from the list of
     * attributes registered with this packet extension.
     *
     * @param attribute the name of the attribute that we'd like to retrieve.
     *
     * @return the value of the specified <tt>attribute</tt> or <tt>null</tt>
     * if no such attribute is currently registered with this extension.
     */
    public Object getAttribute(String attribute)
    {
        synchronized(attributes)
        {
            return attributes.get(attribute);
        }
    }

    /**
     * Returns the string value of the attribute with the specified
     * <tt>name</tt>.
     *
     * @param attribute the name of the attribute that we'd like to retrieve.
     *
     * @return the String value of the specified <tt>attribute</tt> or
     * <tt>null</tt> if no such attribute is currently registered with this
     * extension.
     */
    public String getAttributeAsString(String attribute)
    {
        synchronized(attributes)
        {
            Object attributeVal = attributes.get(attribute);

            return attributeVal == null ? null : attributeVal.toString();
        }
    }

    /**
     * Returns the <tt>int</tt> value of the attribute with the specified
     * <tt>name</tt>.
     *
     * @param attribute the name of the attribute that we'd like to retrieve.
     *
     * @return the <tt>int</tt> value of the specified <tt>attribute</tt> or
     * <tt>-1</tt> if no such attribute is currently registered with this
     * extension.
     */
    public int getAttributeAsInt(String attribute)
    {
        return getAttributeAsInt(attribute, -1);
    }

    /**
     * Returns the <tt>int</tt> value of the attribute with the specified
     * <tt>name</tt>.
     *
     * @param attribute the name of the attribute that we'd like to retrieve
     * @param defaultValue the <tt>int</tt> to be returned as the value of the
     * specified attribute if no such attribute is currently registered with
     * this extension
     * @return the <tt>int</tt> value of the specified <tt>attribute</tt> or
     * <tt>defaultValue</tt> if no such attribute is currently registered with
     * this extension
     */
    public int getAttributeAsInt(String attribute, int defaultValue)
    {
        synchronized(attributes)
        {
            String value = getAttributeAsString(attribute);

            return (value == null) ? defaultValue : Integer.parseInt(value);
        }
    }

    /**
     * Tries to parse the value of the specified <tt>attribute</tt> as an
     * <tt>URI</tt> and returns it.
     *
     * @param attribute the name of the attribute that we'd like to retrieve.
     *
     * @return the <tt>URI</tt> value of the specified <tt>attribute</tt> or
     * <tt>null</tt> if no such attribute is currently registered with this
     * extension.
     * @throws IllegalArgumentException if <tt>attribute</tt> is not a valid {@link
     * URI}
     */
    public URI getAttributeAsURI(String attribute)
        throws IllegalArgumentException
    {
        synchronized(attributes)
        {
            String attributeVal = getAttributeAsString(attribute);

            if (attributeVal == null)
                return null;

            try
            {
                URI uri = new URI(attributeVal);

                return uri;
            }
            catch (URISyntaxException e)
            {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Gets the names of the attributes which currently have associated values
     * in this extension.
     *
     * @return the names of the attributes which currently have associated
     * values in this extension
     */
    public List<String> getAttributeNames()
    {
        synchronized (attributes)
        {
            return new ArrayList<String>(attributes.keySet());
        }
    }

    /**
     * Specifies the text content of this extension.
     *
     * @param text the text content of this extension.
     */
    public void setText(String text)
    {
        this.textContent = text;
    }

    /**
     * Returns the text content of this extension or <tt>null</tt> if no text
     * content has been specified so far.
     *
     * @return the text content of this extension or <tt>null</tt> if no text
     * content has been specified so far.
     */
    public String getText()
    {
        return textContent;
    }

    /**
     * Returns this packet's first direct child extension that matches the
     * specified <tt>type</tt>.
     *
     * @param <T> the specific type of <tt>ExtensionElement</tt> to be returned
     *
     * @param type the <tt>Class</tt> of the extension we are looking for.
     *
     * @return this packet's first direct child extension that matches specified
     * <tt>type</tt> or <tt>null</tt> if no such child extension was found.
     */
    public <T extends ExtensionElement> T getFirstChildOfType(Class<T> type)
    {
        List<? extends ExtensionElement> childExtensions = getChildExtensions();

        synchronized (childExtensions)
        {
            for (ExtensionElement extension : childExtensions)
            {
                if (type.isInstance(extension))
                {
                    @SuppressWarnings("unchecked")
                    T extensionAsType = (T) extension;

                    return extensionAsType;
                }
            }
        }
        return null;
    }

    /**
     * Returns this packet's direct child extensions that match the
     * specified <tt>type</tt>.
     *
     * @param <T> the specific <tt>ExtensionElement</tt> type of child extensions
     * to be returned
     *
     * @param type the <tt>Class</tt> of the extension we are looking for.
     *
     * @return a (possibly empty) list containing all of this packet's direct
     * child extensions that match the specified <tt>type</tt>
     */
    public <T extends ExtensionElement> List<T> getChildExtensionsOfType(
            Class<T> type)
    {
        List<? extends ExtensionElement> childExtensions = getChildExtensions();
        List<T> result = new ArrayList<T>();

        if (childExtensions == null)
            return result;

        synchronized (childExtensions)
        {
            for (ExtensionElement extension : childExtensions)
            {
                if (type.isInstance(extension))
                {
                    @SuppressWarnings("unchecked")
                    T extensionAsType = (T) extension;

                    result.add(extensionAsType);
                }
            }
        }

        return result;
    }
}

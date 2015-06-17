/*
 * Copyright (C) 2015 Marten Gajda <marten@dmfs.org>
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
 *
 */

package org.dmfs.android.xmlmagic.builder;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;


/**
 * A builder that builds a String from XML. It's similar to {@link AndroidCharSequenceObjectBuilder}, but it will <strong>not</strong> preserve
 * {@link android.text.Spannable} elements, whereas {@link AndroidCharSequenceObjectBuilder} does.
 * <p/>
 * This builder accepts the value in an attribute called <code>value</code> or as text value. In either case placeholder tokens will be evaluated. The attribute
 * version is more efficient when directly referring to a resource string.
 * <p/>
 * Examples:
 * 
 * <pre>
 * {@code
 * <xmlmagic:string value="@string:some_string" />  <!-- this is more efficient than the version below -->
 * <xmlmagic:string>{@literal@string:some_string}</xmlmagic:string>  <!-- like above, but less efficient
 * -->
 * <!-- without references, both versions have the same performance -->
 * <xmlmagic:string value="Some text" />
 * <xmlmagic:string>Some text</xmlmagic:string>
 * <!-- both versions support placeholders -->
 * <xmlmagic:string value="http://{@literal@cursor:hostname}/" />
 * <xmlmagic:string>http://{@literal@cursor:hostname}/</xmlmagic:string>
 * }
 * </pre>
 * 
 * Note: if both, an attribute and a text value, are present, the attribute takes precedence over the text value.
 * <p/>
 * Also note: when using the text version the text must not contain any XML or HTML elements.
 * <p/>
 * A concrete {@link ElementDescriptor} using this builder is {@link org.dmfs.android.xmlmagic.Model#STRING}.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */

public class AndroidStringObjectBuilder extends BaseAndroidObjectBuilder<String>
{
	public final static AndroidStringObjectBuilder INSTANCE = new AndroidStringObjectBuilder();


	@Override
	public String update(ElementDescriptor<String> descriptor, String object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		return getCharSequenceAttr(attribute, value, context).toString();
	}


	@Override
	public String update(ElementDescriptor<String> descriptor, String object, String text, ParserContext context) throws XmlObjectPullParserException
	{
		if (object == null)
		{
			return format(text, context).toString();
		}
		else
		{
			return object;
		}
	}
}

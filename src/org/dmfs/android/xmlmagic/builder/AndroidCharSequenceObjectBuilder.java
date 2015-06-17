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
 * A builder that builds a CharSequence from XML. It's similar to {@link AndroidStringObjectBuilder}, but it will preserve {@link android.text.Spannable}
 * elements, whereas {@link AndroidStringObjectBuilder} won't.
 * <p/>
 * This builder accepts the value in an attribute called <code>value</code> or as text value. In either case placeholder tokens will be evaluated. The attribute
 * version is more efficient when directly referring to a resource string.
 * <p/>
 * Examples:
 * 
 * <pre>
 * {@code
 * <xmlmagic:charsequence value="@string:some_string" />  <!-- this is more efficient than the version below -->
 * <xmlmagic:charsequence>{{@literal @}string:some_string}</xmlmagic:charsequence>  <!-- like above, but less efficient
 * -->
 * <!-- without references, both versions have the same performance -->
 * <xmlmagic:charsequence value="Some text" />
 * <xmlmagic:charsequence>Some text</xmlmagic:charsequence>
 * <!-- both versions support placeholders -->
 * <xmlmagic:charsequence value="http://{{@literal @}cursor:hostname}/" />
 * <xmlmagic:charsequence>http://{{@literal @}cursor:hostname}/</xmlmagic:charsequence>
 * }
 * </pre>
 * 
 * Note: if both, an attribute and a text value, are present, the attribute takes precedence over the text value.
 * <p/>
 * Also note: when using the text version the text must not contain any XML or HTML elements.
 * <p/>
 * A concrete {@link ElementDescriptor} using this builder is {@link org.dmfs.android.xmlmagic.Model#CHAR_SEQUENCE}.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AndroidCharSequenceObjectBuilder extends BaseAndroidObjectBuilder<CharSequence>
{
	public final static AndroidCharSequenceObjectBuilder INSTANCE = new AndroidCharSequenceObjectBuilder();


	@Override
	public CharSequence update(ElementDescriptor<CharSequence> descriptor, CharSequence object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		return getCharSequenceAttr(attribute, value, context);
	}


	@Override
	public CharSequence update(ElementDescriptor<CharSequence> descriptor, CharSequence object, String text, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (object == null)
		{
			return format(text, context);
		}
		else
		{
			return object;
		}
	}
}

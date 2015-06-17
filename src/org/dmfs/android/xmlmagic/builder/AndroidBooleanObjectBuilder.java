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

import org.dmfs.android.xmlmagic.AndroidParserContext;
import org.dmfs.android.xmlmagic.StringFormatter;
import org.dmfs.android.xmlmagic.tokenresolvers.ITokenResolver;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;


/**
 * An object builder to build {@link Boolean} instances from XML.
 * <p/>
 * This builder accepts the value in an attribute called <code>value</code> or as text value.
 * <p/>
 * Examples:
 * 
 * <pre>
 * {@code
 * <xmlmagic:boolean>true</xmlmagic:boolean> <!-- evaluates to true -->
 * <xmlmagic:boolean>false</xmlmagic:boolean> <!-- evaluates to false -->
 * <xmlmagic:boolean>1</xmlmagic:boolean> <!-- evaluates to true -->
 * <xmlmagic:boolean>0</xmlmagic:boolean> <!-- evaluates to false -->
 * <xmlmagic:boolean>{@literal@bool:some_boolean_resource}</xmlmagic:boolean>  <!-- evaluates to the value of that
 * resource -->
 * <xmlmagic:boolean>{@literal@string:some_string_resource}</xmlmagic:boolean>  <!-- evaluates to true if the string
 * resource equals "true" or "1" and false otherwise -->
 * <xmlmagic:boolean>some random text</xmlmagic:boolean> <!-- evaluates to false -->
 * <xmlmagic:boolean></xmlmagic:boolean> <!-- evaluates to false -->
 * <xmlmagic:boolean value="true" /> <!-- evaluates to true -->
 * <xmlmagic:boolean value="false" /> <!-- evaluates to false -->
 * <xmlmagic:boolean value="@bool:some_boolean_resource" />  <!-- evaluates to the value of that resource -->
 * }
 * </pre>
 * 
 * Note: if both, an attribute and a text value, are present, the attribute takes precedence over the text value.
 * <p/>
 * A concrete {@link ElementDescriptor} using this builder is {@link org.dmfs.android.xmlmagic.Model#BOOLEAN}.
 * <p/>
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AndroidBooleanObjectBuilder extends BaseAndroidObjectBuilder<Boolean>
{

	public final static AndroidBooleanObjectBuilder INSTANCE = new AndroidBooleanObjectBuilder();


	@Override
	public Boolean update(ElementDescriptor<Boolean> descriptor, Boolean object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		return getBooleanAttr(attribute, context);
	}


	@Override
	public Boolean update(ElementDescriptor<Boolean> descriptor, Boolean object, String text, ParserContext context) throws XmlObjectPullParserException
	{
		if (object != null)
		{
			// we already have a value
			return object;
		}

		if (text == null || text.length() == 0)
		{
			return false;
		}

		if (context instanceof AndroidParserContext)
		{
			// The context can provide a formatter.
			ITokenResolver resolver = ((AndroidParserContext) context).getResolver();
			if (resolver != null)
			{
				// We have a formatter. Format the given text.
				text = StringFormatter.format(text, resolver, 5).toString();
			}
		}

		return "true".equalsIgnoreCase(text) || "1".equals(text);
	}


	@Override
	public Boolean finish(ElementDescriptor<Boolean> descriptor, Boolean object, ParserContext context) throws XmlObjectPullParserException
	{
		if (object == null)
		{
			// no value was specified: default to false
			return false;
		}
		return object;
	}
}

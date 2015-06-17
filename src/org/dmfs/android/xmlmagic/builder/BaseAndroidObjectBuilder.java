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
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;


/**
 * The abstract base of all builders for Android. It provides methods to read the formatted value of attributes and text
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public abstract class BaseAndroidObjectBuilder<T> extends AbstractObjectBuilder<T>
{

	/**
	 * Get the resources from the given {@link ParserContext}. This returns <code>false</code> if the given context is not an {@link AndroidParserContext}.
	 *
	 * @param context
	 *            A {@link ParserContext}.
	 * @return The {@link Resources} of the current context or <code>null</code> if the context didn't provide any Resources.
	 */
	protected final Resources getResources(ParserContext context)
	{
		if (context instanceof AndroidParserContext)
		{
			return ((AndroidParserContext) context).getResources();
		}
		return null;
	}


	protected final ITokenResolver getTokenResolver(ParserContext context)
	{
		if (context instanceof AndroidParserContext)
		{
			return ((AndroidParserContext) context).getResolver();
		}
		return null;
	}


	protected final CharSequence format(CharSequence value, ParserContext context)
	{
		ITokenResolver resolver = getTokenResolver(context);
		if (resolver == null)
		{
			return value;
		}
		return StringFormatter.format(value, resolver, 5);
	}


	protected CharSequence getCharSequenceAttr(QualifiedName attribute, String value, ParserContext context)
	{
		Resources resources = getResources(context);
		AttributeSet p = Xml.asAttributeSet(context.getXmlPullParser());
		int res = p.getAttributeResourceValue(attribute.namespace, attribute.name, 0 /* the invalid resource id */);
		if (res == 0)
		{
			return format(p.getAttributeValue(attribute.namespace, attribute.name), context);
		}
		else if (resources != null)
		{
			return resources.getText(res);
		}
		return value;
	}


	/**
	 * Returns the integer value of the given attribute. This method evaluates placeholder tokes of present.
	 *
	 * @param attribute
	 *            The attribute to return.
	 * @param resolveInt
	 *            <code>true</code> to resolve the value as a resource int, <code>false</code> to return the value without trying to resolve it.<br/>
	 *            Note: when loading ids, drawable ids, layout ids or other references that are not references to integer values, this must be
	 *            <code>false</code>.
	 * @param context
	 *            A {@link ParserContext}.
	 * @return the integer value of the given attribute.
	 */
	protected Integer getIntegerAttr(QualifiedName attribute, boolean resolveInt, ParserContext context)
	{
		Resources resources = getResources(context);
		AttributeSet p = Xml.asAttributeSet(context.getXmlPullParser());
		int res = p.getAttributeResourceValue(attribute.namespace, attribute.name, 0 /* the invalid resource id */);
		if (res == 0)
		{
			try
			{
				return Integer.parseInt(format(p.getAttributeValue(attribute.namespace, attribute.name), context).toString());
			}
			catch (NumberFormatException e)
			{
				// not a valid int
				return 0;
			}
		}
		else if (resources != null && resolveInt)
		{
			try
			{
				return resources.getInteger(res);
			}
			catch (Resources.NotFoundException e)
			{
				// fall through
			}
		}
		return res;
	}


	/**
	 * Returns the boolean value of the given attribute. This method evaluates placeholder tokes of present.
	 *
	 * @param attribute
	 *            The attribute to return.
	 * @param context
	 *            A {@link ParserContext}.
	 * @return the boolean value of the given attribute.
	 */
	protected boolean getBooleanAttr(QualifiedName attribute, ParserContext context)
	{
		Resources resources = getResources(context);
		AttributeSet p = Xml.asAttributeSet(context.getXmlPullParser());
		int res = p.getAttributeResourceValue(attribute.namespace, attribute.name, 0 /* the invalid resource id */);
		if (res == 0)
		{
			String value = format(p.getAttributeValue(attribute.namespace, attribute.name), context).toString();
			return value.equalsIgnoreCase("true") || value.equals("1");
		}
		else if (resources != null)
		{
			return resources.getBoolean(res);
		}
		return false;
	}
}

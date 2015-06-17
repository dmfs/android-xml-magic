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

import java.lang.reflect.Field;
import java.net.URI;

import org.dmfs.android.xmlmagic.AndroidParserContext;
import org.dmfs.android.xmlmagic.StringFormatter;
import org.dmfs.android.xmlmagic.annotations.ResolveInt;
import org.dmfs.android.xmlmagic.annotations.ResolveTokens;
import org.dmfs.android.xmlmagic.tokenresolvers.ITokenResolver;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Xml;


/**
 * A {@link org.dmfs.xmlobjects.builder.reflection.ReflectionObjectBuilder} with special Android XML support. In particular it supports resolving Resource
 * values in element attributes. If an attribute refers to a Resource that matches the class of the annotated field the resource value is resolved and stored in
 * the field.
 * <p/>
 * Example:
 * <p/>
 * When parsing the following XML snippet:
 * 
 * <pre>
 * {@code
 * <some-element text="@string/some_text"></some-element>
 * }
 * </pre>
 * 
 * The following member field will be assigned the value of the string resource <code>some_text</code>:
 * 
 * <pre>
 * <code>{@literal @}Attribute(name="text")
 * String text;
 * </code>
 * </pre>
 * 
 * It basically works like resource references in layout files.
 * <p/>
 * If the given {@link AndroidParserContext} has been provided with an {@link ITokenResolver} this also supports named placeholders like so:
 * 
 * <pre>
 * {@code
 * <some-element text="This is {@literal@string/some_text}"></some-element>
 * }
 * </pre>
 * 
 * The result would be the value of the resource string <code>some_text</code> appended to <code>"This is "</code>.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class ReflectionObjectBuilder<T> extends org.dmfs.xmlobjects.builder.reflection.ReflectionObjectBuilder<T>
{

	public ReflectionObjectBuilder(Class<T> classParam)
	{
		super(classParam);
	}


	@Override
	public T update(ElementDescriptor<T> descriptor, T object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		Resources resources = null;
		ITokenResolver resolver = null;
		if (context instanceof AndroidParserContext)
		{
			resources = ((AndroidParserContext) context).getResources();
			resolver = ((AndroidParserContext) context).getResolver();
		}

		AttributeSet p = Xml.asAttributeSet(context.getXmlPullParser());

		Field field = mAttributeMap.get(attribute);
		if (field != null)
		{
			final String name = attribute.name;
			final String namespace = attribute.namespace;

			Object resultValue = null;
			int res = p.getAttributeResourceValue(namespace, name, 0 /* the invalid resource id */);
			if (field.getType() == String.class)
			{
				if (res == 0)
				{
					resultValue = p.getAttributeValue(namespace, name);
				}
				else if (resources != null)
				{
					resultValue = resources.getString(res);
				}

				if (resolver != null)
				{
					ResolveTokens resolve = field.getAnnotation(ResolveTokens.class);
					if (resolve != null && resolve.value())
					{
						resultValue = StringFormatter.format((String) resultValue, resolver, 5);
					}
				}
			}
			if (field.getType() == CharSequence.class)
			{
				if (res == 0)
				{
					resultValue = p.getAttributeValue(namespace, name);
				}
				else if (resources != null)
				{
					resultValue = resources.getText(res);
				}

				if (resolver != null)
				{
					ResolveTokens resolve = field.getAnnotation(ResolveTokens.class);
					if (resolve != null && resolve.value())
					{
						resultValue = StringFormatter.format((CharSequence) resultValue, resolver, 5);
					}
				}
			}
			else if (field.getType() == int.class || field.getType() == Integer.class)
			{
				ResolveInt resolveInt;
				if (res == 0)
				{
					resultValue = p.getAttributeIntValue(namespace, name, 0);
				}
				else if (resources != null && ((resolveInt = field.getAnnotation(ResolveInt.class)) != null && resolveInt.value()))
				{
					try
					{
						resultValue = resources.getInteger(res);
					}
					catch (NotFoundException e)
					{
						resultValue = res;
					}
				}
				else
				{
					// special case, return the resource id if there are no resources or we shall not resolve the integer
					resultValue = res;
				}
			}
			else if (field.getType() == float.class || field.getType() == Float.class)
			{
				resultValue = p.getAttributeFloatValue(namespace, name, 0);
			}
			else if (field.getType() == boolean.class || field.getType() == Boolean.class)
			{
				if (res == 0)
				{
					resultValue = p.getAttributeBooleanValue(namespace, name, false);
				}
				else if (resources != null)
				{
					resultValue = resources.getBoolean(res);
				}
			}
			else if (field.getType() == URI.class)
			{
				String uri = null;
				if (res == 0)
				{
					uri = p.getAttributeValue(namespace, name);
				}
				else if (resources != null)
				{
					uri = resources.getString(res);
				}

				if (uri != null)
				{
					resultValue = URI.create(uri);
				}
			}
			else if (field.getType() == Uri.class)
			{
				String uri = null;
				if (res == 0)
				{
					uri = p.getAttributeValue(namespace, name);
				}
				else if (resources != null)
				{
					uri = resources.getString(res);
				}

				if (uri != null)
				{
					resultValue = Uri.parse(uri);
				}
			}
			else if (field.getType() == Class.class)
			{
				String className = null;
				if (res == 0)
				{
					className = p.getAttributeValue(namespace, name);
				}
				else if (resources != null)
				{
					className = resources.getString(res);
				}

				if (className != null)
				{
					try
					{
						resultValue = Class.forName(className);
					}
					catch (ClassNotFoundException e)
					{
						resultValue = null;
					}
				}
			}

			if (resultValue != null)
			{
				field.setAccessible(true);
				try
				{
					field.set(object, resultValue);
				}
				catch (IllegalArgumentException e)
				{
				}
				catch (IllegalAccessException e)
				{
				}
			}
			else
			{
				return super.update(descriptor, object, attribute, value, context);
			}
		}
		return object;
	}
}

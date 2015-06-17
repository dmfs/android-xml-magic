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

import org.dmfs.android.xmlmagic.Model;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;

import android.net.Uri;


/**
 * Parses XML into a {@link Uri}.
 * <p/>
 * This builder supports 3 different ways of specifying the Uri.
 * <ol>
 * <li>By attribute for each part:
 * 
 * <pre>
 * {@code <xmlmagic:uri scheme="http"
 * authority="dmfs.org" path="/"/>}
 * </pre>
 * 
 * </li>
 * <li>By child elements for each part:
 * 
 * <pre>
 * {@code
 * <xmlmagic:uri>
 *     <xmlmagic:scheme>http</xmlmagic:scheme>
 *     <xmlmagic:authority>dmfs.org</xmlmagic:authority>
 *     <xmlmagic:path>/</xmlmagic:path>
 * </xmlmagic:uri>}
 * </pre>
 * 
 * </li>
 * <li>By text with the encoded Uri:
 * 
 * <pre>
 * {@code <xmlmagic:uri>http://dmfs.org/</xmlmagic:uri>}
 * </pre>
 * 
 * </li>
 * </ol>
 * Note that version #1 and #2 can be mixed, whereas #3 can't be mixed with #1 nor #2.
 * <p/>
 * Version #2 (by child element) also supports adding path segments and query parameters like so:
 * 
 * <pre>
 * {@code
 * <xmlmagic:uri>
 *     <xmlmagic:scheme>http</xmlmagic:scheme>
 *     <xmlmagic:authority>dmfs.org</xmlmagic:authority>
 *     <xmlmagic:path>wiki</xmlmagic:path>
 *     <xmlmagic:append-path>index.php</xmlmagic:append-path>
 *     <xmlmagic:query-parameter key="title">Main_Page</xmlmagic:query-parameter>
 * </xmlmagic:uri>}
 * </pre>
 * 
 * Which results in {@code http://dmfs.org/wiki/index.php?title=Main_Page}
 * <p/>
 * Also note that when using version #3 you have to specify a properly encoded Uri, whereas with version #1 and #2 the parts will be encoded if necessary.
 * <p/>
 * All versions support placeholders.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AndroidUriObjectBuilder extends BaseAndroidObjectBuilder<Uri>
{
	/**
	 * A static instance of an {@link AndroidUriObjectBuilder}.
	 */
	public final static AndroidUriObjectBuilder INSTANCE = new AndroidUriObjectBuilder();

	private final static QualifiedName ATTR_SCHEME = QualifiedName.get("scheme");
	private final static QualifiedName ATTR_AUTHORITY = QualifiedName.get("authority");
	private final static QualifiedName ATTR_PATH = QualifiedName.get("path");
	private final static QualifiedName ATTR_FRAGMENT = QualifiedName.get("fragment");

	private final static ElementDescriptor<String> SCHEME = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "scheme"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> AUTHORITY = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "authority"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> PATH = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "path"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> APPEND_PATH = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "append-path"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> FRAGMENT = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "fragment"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<QueryParameter> PARAMETER = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "query-parameter"),
		new BaseAndroidObjectBuilder<QueryParameter>()
		{
			@Override
			public QueryParameter get(ElementDescriptor<QueryParameter> descriptor, QueryParameter recycle, ParserContext context)
				throws XmlObjectPullParserException
			{
				return new QueryParameter();
			}


			@Override
			public QueryParameter update(ElementDescriptor<QueryParameter> descriptor, QueryParameter object, QualifiedName attribute, String value,
				ParserContext context) throws XmlObjectPullParserException
			{
				if (attribute == Model.ATTR_KEY)
				{
					object.key = value; // don't format here, we format when we add it to the builder
				}
				return object;
			}


			@Override
			public QueryParameter update(ElementDescriptor<QueryParameter> descriptor, QueryParameter object, String text, ParserContext context)
				throws XmlObjectPullParserException
			{
				object.value = text; // don't format here, we format when we add it to the builder
				return object;
			}
		});


	/**
	 * Returns the builder for the current Uri instance, creating one if necessary.
	 *
	 * @param context
	 *            A {@link ParserContext}.
	 * @return The {@link android.net.Uri.Builder} of the current instance.
	 */
	private Uri.Builder getBuilder(ParserContext context)
	{
		Uri.Builder builder = (Uri.Builder) context.getState();
		if (builder == null)
		{
			builder = new Uri.Builder();
			context.setState(builder);
		}
		return builder;
	}


	@Override
	public Uri update(ElementDescriptor<Uri> descriptor, Uri object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (attribute == ATTR_SCHEME)
		{
			getBuilder(context).scheme(getCharSequenceAttr(attribute, value, context).toString());
		}
		if (attribute == ATTR_AUTHORITY)
		{
			getBuilder(context).authority(getCharSequenceAttr(attribute, value, context).toString());
		}
		if (attribute == ATTR_PATH)
		{
			getBuilder(context).path(getCharSequenceAttr(attribute, value, context).toString());
		}
		if (attribute == ATTR_FRAGMENT)
		{
			getBuilder(context).fragment(getCharSequenceAttr(attribute, value, context).toString());
		}
		return object;
	}


	@Override
	public Uri update(ElementDescriptor<Uri> descriptor, Uri object, String text, ParserContext context) throws XmlObjectPullParserException
	{
		try
		{
			return Uri.parse(format(text.trim(), context).toString());
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}


	@Override
	public <V> Uri update(ElementDescriptor<Uri> descriptor, Uri object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (object != null)
		{
			Uri.Builder builder = object.buildUpon();
			context.setState(builder);
			object = null;
		}

		if (child instanceof String || child == null)
		{
			String text = child == null ? null : format((CharSequence) child, context).toString();
			if (childDescriptor == SCHEME)
			{
				if (child != null)
				{
					getBuilder(context).scheme(text);
				}
			}
			else if (childDescriptor == AUTHORITY)
			{
				if (child != null)
				{
					getBuilder(context).authority(text);
				}
			}
			else if (childDescriptor == PATH)
			{
				if (child == null)
				{
					getBuilder(context).path("");
				}
				else
				{
					getBuilder(context).path(text);
				}
			}
			else if (childDescriptor == FRAGMENT)
			{
				if (child != null)
				{
					getBuilder(context).fragment(text);
				}
			}
			else if (childDescriptor == APPEND_PATH)
			{
				if (child == null)
				{
					getBuilder(context).appendPath("");
				}
				else
				{
					getBuilder(context).appendPath(text);
				}
			}
		}
		else if (childDescriptor == PARAMETER)
		{
			QueryParameter param = (QueryParameter) child;
			getBuilder(context).appendQueryParameter(format(param.key, context).toString(), format(param.value, context).toString());
		}
		return object;
	}


	@Override
	public Uri finish(ElementDescriptor<Uri> descriptor, Uri object, ParserContext context) throws XmlObjectPullParserException
	{
		if (object == null)
		{
			// we're done, build the Uri
			object = getBuilder(context).build();
		}
		// don't forget to clear the state for the next object
		context.setState(null);
		return object;
	}

	/**
	 * Helper class for query parameters.
	 */
	private static class QueryParameter
	{
		/**
		 * The parameter key.
		 */
		private String key;

		/**
		 * The parameter value.
		 */
		private String value;
	}
}

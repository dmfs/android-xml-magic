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

import java.util.ArrayList;

import org.dmfs.android.xmlmagic.Model;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;

import android.os.Bundle;
import android.os.Parcelable;


/**
 * A builder that builds {@link Bundle}s from XML.
 * <p/>
 * The builder accepts <code>bundle-value</code> elements. These elements must have a <code>key</code> attribute that becomes the key of the value in the
 * bundle. The first child element of <code>value</code> becomes the value. <code>value</code> must not have more than one child element.
 * <p/>
 * Example:
 * 
 * <pre>
 * {@code
 * <xmlmagic:bundle xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
 *     <xmlmagic:bundle-value key="some_key_string">
 *         <xmlmagic:string>This string will be stored in the bundle</xmlmagic:string>
 *     </xmlmagic:bundle-value>
 *     <xmlmagic:bundle-value key="another_key_to_a_string">
 *         This is a special case: If bundle-value contains a text node it becomes the string value of this bundle-value.
 *     </xmlmagic:bundle-value>
 *     <xmlmagic:bundle-value key="key_to_a_boolean">
 *         <xmlmagic:boolean>true</xmlmagic:boolean>
 *     </xmlmagic:bundle-value>
 *     <xmlmagic:bundle-value key="key_to_another_bundle">
 *         <xmlmagic:bundle>
 *             <xmlmagic:bundle-value key="nested_key">
 *                 <xmlmagic:string>This string is in the nested bundle</xmlmagic:string>
 *             </xmlmagic:bundle-value>
 *         </xmlmagic:bundle>
 *     </xmlmagic:bundle-value>
 * </xmlmagic:bundle>
 * }
 * </pre>
 * <p/>
 * TODO: Add support for missing types
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class BundleObjectBuilder extends AbstractObjectBuilder<Bundle>
{
	public final static BundleObjectBuilder INSTANCE = new BundleObjectBuilder();

	/**
	 * Private {@link org.dmfs.xmlobjects.ElementDescriptor} for bundle values.
	 */
	public final static ElementDescriptor<ValueHolder> VALUE = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "bundle-value"),
		new BaseAndroidObjectBuilder<ValueHolder>()
		{
			@Override
			public ValueHolder get(ElementDescriptor<ValueHolder> descriptor, ValueHolder recycle, ParserContext context) throws XmlObjectPullParserException
			{
				if (recycle != null)
				{
					recycle.key = null;
					recycle.value = null;
					return recycle;
				}
				return new ValueHolder();
			}


			@Override
			public ValueHolder update(ElementDescriptor<ValueHolder> descriptor, ValueHolder object, QualifiedName attribute, String value,
				ParserContext context) throws XmlObjectPullParserException
			{
				if (attribute == Model.ATTR_KEY)
				{
					object.key = value;
				}
				return object;
			}


			@Override
			public <V> ValueHolder update(ElementDescriptor<ValueHolder> descriptor, ValueHolder object, ElementDescriptor<V> childDescriptor, V child,
				ParserContext context) throws XmlObjectPullParserException
			{
				if (object.value != null)
				{
					throw new IllegalStateException("'bundle-value' can not have more than one child element");
				}
				object.value = child;
				return object;
			}


			public ValueHolder update(ElementDescriptor<ValueHolder> descriptor, ValueHolder object, String text, ParserContext context)
				throws XmlObjectPullParserException
			{
				if (object.value != null)
				{
					throw new IllegalStateException("'bundle-value' can not have more than one child element");
				}
				object.value = format(text, context);
				return object;

			};


			@Override
			public ValueHolder finish(ElementDescriptor<ValueHolder> descriptor, ValueHolder object, ParserContext context) throws XmlObjectPullParserException
			{
				if (object.key == null)
				{
					throw new IllegalArgumentException("'bundle-value' must have a key");
				}
				return object;
			}
		});


	@Override
	public Bundle get(ElementDescriptor<Bundle> descriptor, Bundle recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (recycle != null)
		{
			recycle.clear();
			return recycle;
		}
		return new Bundle();
	}


	@Override
	public <V> Bundle update(ElementDescriptor<Bundle> descriptor, Bundle object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (child instanceof ValueHolder)
		{
			ValueHolder valueHolder = (ValueHolder) child;
			if (valueHolder.value instanceof String)
			{
				object.putString(valueHolder.key, (String) valueHolder.value);
			}
			if (valueHolder.value instanceof CharSequence)
			{
				object.putCharSequence(valueHolder.key, (CharSequence) valueHolder.value);
			}
			else if (valueHolder.value instanceof Integer)
			{
				object.putInt(valueHolder.key, (Integer) valueHolder.value);
			}
			else if (valueHolder.value instanceof Boolean)
			{
				object.putBoolean(valueHolder.key, (Boolean) valueHolder.value);
			}
			else if (valueHolder.value instanceof Bundle)
			{
				object.putBundle(valueHolder.key, (Bundle) valueHolder.value);
			}
			else if (valueHolder.value instanceof Parcelable)
			{
				object.putParcelable(valueHolder.key, (Parcelable) valueHolder.value);
			}
			else if (valueHolder.value instanceof String[])
			{
				object.putStringArray(valueHolder.key, (String[]) valueHolder.value);
			}
			if (valueHolder.value instanceof CharSequence[])
			{
				object.putCharSequenceArray(valueHolder.key, (CharSequence[]) valueHolder.value);
			}
			else if (valueHolder.value instanceof int[])
			{
				object.putIntArray(valueHolder.key, (int[]) valueHolder.value);
			}
			else if (valueHolder.value instanceof boolean[])
			{
				object.putBooleanArray(valueHolder.key, (boolean[]) valueHolder.value);
			}
			else if (valueHolder.value instanceof Parcelable[])
			{
				object.putParcelableArray(valueHolder.key, (Parcelable[]) valueHolder.value);
			}
			else if (valueHolder.value instanceof ArrayList)
			{
				// FIXME: we can't trust that the generic type is String. We have to check the type of the first element, if there is any
				// object.putStringArrayList(valueHolder.key, (ArrayList<String>) valueHolder.value);
				// object.putStringArrayList
				// object.putIntegerArrayList
				// object.putCharSequenceArrayList
				// object.putParcelableArrayList

			}

			// recycle the ValueHolder
			context.recycle(childDescriptor, child);
		}
		return object;
	}

	/**
	 * Private holder for bundle values.
	 */
	private static class ValueHolder
	{
		/**
		 * The key of the value.
		 */
		private String key;

		/**
		 * The value itself.
		 */
		private Object value;
	}
}

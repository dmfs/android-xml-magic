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
import org.dmfs.android.xmlmagic.Model;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.builder.IObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.widget.RemoteViews;


public class RemoteViewsObjectBuilder extends BaseAndroidObjectBuilder<RemoteViews>
{
	public final static RemoteViewsObjectBuilder INSTANCE = new RemoteViewsObjectBuilder();

	private final static QualifiedName ATTR_LAYOUT = QualifiedName.get("layout");
	private final static QualifiedName ATTR_ID = QualifiedName.get("id");
	private final static QualifiedName ATTR_METHOD = QualifiedName.get("method");

	private final static IObjectBuilder<RemoteBinding> BUILDER = new BaseAndroidObjectBuilder<RemoteBinding>()
	{
		@Override
		public RemoteBinding get(ElementDescriptor<RemoteBinding> descriptor, RemoteBinding recycle, ParserContext context) throws XmlObjectPullParserException
		{
			return new RemoteBinding();
		}


		@Override
		public RemoteBinding update(ElementDescriptor<RemoteBinding> descriptor, RemoteBinding object, QualifiedName attribute, String value,
			ParserContext context) throws XmlObjectPullParserException
		{
			if (attribute == ATTR_ID)
			{
				object.viewId = getIntegerAttr(attribute, false, context);
			}
			else if (attribute == ATTR_METHOD)
			{
				object.methodName = value;
			}
			return object;
		}


		@Override
		public <V> RemoteBinding update(ElementDescriptor<RemoteBinding> descriptor, RemoteBinding object, ElementDescriptor<V> childDescriptor, V child,
			ParserContext context) throws XmlObjectPullParserException
		{
			object.descriptor = childDescriptor;
			object.value = child;
			return object;
		}
	};
	private final static ElementDescriptor<RemoteBinding> REMOTE_ONCLICK = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "remote-onclick"),
		BUILDER);
	private final static ElementDescriptor<RemoteBinding> REMOTE_SET_TEXT = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "remote-set-text"),
		BUILDER);


	@Override
	public RemoteViews update(ElementDescriptor<RemoteViews> descriptor, RemoteViews object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (attribute == ATTR_LAYOUT)
		{
			return new RemoteViews(((AndroidParserContext) context).getAppContext().getPackageName(), getIntegerAttr(attribute, false, context));
		}
		else
		{
			return object;
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public <V> RemoteViews update(ElementDescriptor<RemoteViews> descriptor, RemoteViews object, ElementDescriptor<V> childDescriptor, V child,
		ParserContext context) throws XmlObjectPullParserException
	{
		if (childDescriptor.builder != BUILDER)
		{
			// not a RemoteBinding
			return object;
		}

		RemoteBinding binding = (RemoteBinding) child;

		if (childDescriptor == REMOTE_ONCLICK)
		{
			if (binding.descriptor == Model.PENDING_INTENT)
			{
				object.setOnClickPendingIntent(binding.viewId, (PendingIntent) binding.value);
			}
			else if (VERSION.SDK_INT > 11 && binding.descriptor == Model.INTENT)
			{
				object.setOnClickFillInIntent(binding.viewId, (Intent) binding.value);
			}
		}
		else if (childDescriptor == REMOTE_SET_TEXT)
		{
			if (binding.value instanceof CharSequence)
			{
				object.setTextViewText(binding.viewId, (CharSequence) binding.value);
			}
		}
		return object;
	}

	private static class RemoteBinding
	{
		ElementDescriptor<?> descriptor;
		int viewId;
		String methodName;
		Object value;
	}
}

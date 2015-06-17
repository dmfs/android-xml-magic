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
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * Created by marten on 10.05.15.
 */
public class PendingIntentObjectBuilder extends BaseAndroidObjectBuilder<PendingIntent>
{
	public final static PendingIntentObjectBuilder INSTANCE = new PendingIntentObjectBuilder();

	private final static QualifiedName ATTR_INTENT_TYPE = QualifiedName.get("intent-type");
	private final static QualifiedName ATTR_REQUEST_CODE = QualifiedName.get("request-code");


	private PendingIntentDescriptor getDescriptor(ParserContext context)
	{
		PendingIntentDescriptor descriptor = (PendingIntentDescriptor) context.getState();
		return descriptor;
	}


	@Override
	public PendingIntent get(ElementDescriptor<PendingIntent> descriptor, PendingIntent recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (!(context instanceof AndroidParserContext))
		{
			throw new IllegalArgumentException("ParserContext must be an AndroidParserContext to build a PendingIntent");
		}
		context.setState(new PendingIntentDescriptor());

		return null;
	}


	@Override
	public PendingIntent update(ElementDescriptor<PendingIntent> descriptor, PendingIntent object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (ATTR_REQUEST_CODE == attribute)
		{
			getDescriptor(context).requestCode = getIntegerAttr(attribute, true, context);
		}
		else if (ATTR_INTENT_TYPE == attribute)
		{
			getDescriptor(context).intentType = IntentType.valueOf(value);
		}
		else if (Model.NAMESPACE.equals(attribute.namespace))
		{
			Flags flag = Flags.get(attribute.name);
			if (flag != null)
			{
				if (getBooleanAttr(attribute, context))
				{
					getDescriptor(context).flags |= flag.get();
				}
				else
				{
					getDescriptor(context).flags &= ~flag.get();
				}
			}
		}
		return object;
	}


	@Override
	public <V> PendingIntent update(ElementDescriptor<PendingIntent> descriptor, PendingIntent object, ElementDescriptor<V> childDescriptor, V child,
		ParserContext context) throws XmlObjectPullParserException
	{
		if (childDescriptor == Model.INTENT)
		{
			getDescriptor(context).intent = (Intent) child;
		}
		return object;
	}


	@Override
	public PendingIntent finish(ElementDescriptor<PendingIntent> descriptor, PendingIntent object, ParserContext context) throws XmlObjectPullParserException
	{
		return getDescriptor(context).intentType.getPendingIntent(((AndroidParserContext) context).getAppContext(), getDescriptor(context));
	}

	private enum IntentType
	{
		activity {
			@Override
			public PendingIntent getPendingIntent(Context context, PendingIntentDescriptor descriptor)
			{
				return PendingIntent.getActivity(context, descriptor.requestCode, descriptor.intent, descriptor.flags);
			}
		},
		broadcast {
			@Override
			public PendingIntent getPendingIntent(Context context, PendingIntentDescriptor descriptor)
			{
				return PendingIntent.getBroadcast(context, descriptor.requestCode, descriptor.intent, descriptor.flags);
			}
		},
		service {
			@Override
			public PendingIntent getPendingIntent(Context context, PendingIntentDescriptor descriptor)
			{
				return PendingIntent.getService(context, descriptor.requestCode, descriptor.intent, descriptor.flags);
			}
		};

		public abstract PendingIntent getPendingIntent(Context context, PendingIntentDescriptor descriptor);
	}

	private enum Flags
	{
		flag_no_create {
			@Override
			public int get()
			{
				return PendingIntent.FLAG_NO_CREATE;
			}
		},
		flag_one_shot {
			@Override
			public int get()
			{
				return PendingIntent.FLAG_ONE_SHOT;
			}
		},
		flag_cancel_current {
			@Override
			public int get()
			{
				return PendingIntent.FLAG_CANCEL_CURRENT;
			}
		},
		flag_update_current {
			@Override
			public int get()
			{
				return PendingIntent.FLAG_UPDATE_CURRENT;
			}
		};

		public abstract int get();


		public static Flags get(String name)
		{
			try
			{
				return Flags.valueOf(name.replace('-', '_'));
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	private static class PendingIntentDescriptor
	{
		int requestCode = 1;
		int flags = 0;
		Intent intent;
		IntentType intentType = IntentType.activity;
	}
}
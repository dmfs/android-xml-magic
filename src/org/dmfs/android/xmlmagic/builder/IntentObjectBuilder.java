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

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;


/**
 * A builder for {@link Intent}s. It accepts a number of child elements that describe the Intent, see the example below.
 * <p/>
 * Example:
 * <p/>
 * 
 * <pre>
 * {@code
 * <xmlmagic:intent activity-clear-top="true">
 *      <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
 *      <xmlmagic:data>
 *          <xmlmagic:scheme>http</xmlmagic:scheme>
 *          <xmlmagic:authority>dmfs.org</xmlmagic:authority>
 *          <xmlmagic:path>/</xmlmagic:path>
 *      </xmlmagic:data>
 *      <xmlmagic:extras>
 *          <xmlmagic:bundle-value key="some_key">
 *              <xmlmagic:string value="@string/some_string"/>
 *          </xmlmagic:bundle-value>
 *      </xmlmagic:extras>
 * </xmlmagic:intent>
 * }
 * </pre>
 * <p/>
 * Note: this builder doesn't support recycling of instances. It will always build a new Intent instance.
 * <p/>
 * Flags are supported by adding an boolean attribute having the flag name, just without the <code>FLAG_</code>, in lower case and with <code>_</code> replaced
 * by <code>-</code>.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class IntentObjectBuilder extends BaseAndroidObjectBuilder<Intent>
{
	/**
	 * A static instance of an {@link IntentObjectBuilder}.
	 */
	public final static IntentObjectBuilder INSTANCE = new IntentObjectBuilder();

	private final static ElementDescriptor<String> ACTION = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "action"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> PACKAGE = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "package"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> CLASS = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "class"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<Uri> DATA = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "data"), AndroidUriObjectBuilder.INSTANCE);
	private final static ElementDescriptor<Bundle> EXTRAS = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "extras"),
		BundleObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> CONTENT_TYPE = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "content-type"),
		AndroidStringObjectBuilder.INSTANCE);
	private final static ElementDescriptor<String> CATEGORY = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "category"),
		AndroidStringObjectBuilder.INSTANCE);


	@Override
	public Intent get(ElementDescriptor<Intent> descriptor, Intent recycle, ParserContext context) throws XmlObjectPullParserException
	{
		return new Intent();
	}


	@Override
	public Intent update(ElementDescriptor<Intent> descriptor, Intent object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (Model.NAMESPACE.equals(attribute.namespace))
		{
			Flags flag = Flags.get(attribute.name);
			if (flag != null)
			{
				object.setFlags(getBooleanAttr(attribute, context) ? object.getFlags() | flag.get() : object.getFlags() & ~flag.get());
			}
		}
		return object;
	}


	@Override
	public <T> Intent update(ElementDescriptor<Intent> descriptor, Intent object, ElementDescriptor<T> childDescriptor, T child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (object == null)
		{
			object = new Intent();
		}

		if (childDescriptor == ACTION)
		{
			object.setAction((String) child);
		}
		else if (childDescriptor == PACKAGE)
		{
			object.setPackage((String) child);
		}
		else if (childDescriptor == CLASS)
		{
			object.setClassName(object.getPackage(), (String) child);
		}
		else if (childDescriptor == DATA)
		{
			object.setData((Uri) child);
		}
		else if (childDescriptor == EXTRAS)
		{
			object.putExtras((Bundle) child);
		}
		else if (childDescriptor == CONTENT_TYPE)
		{
			object.setType((String) child);
		}
		else if (childDescriptor == CATEGORY)
		{
			object.addCategory((String) child);
		}
		return object;
	}

	/**
	 * Flags supported by Intents. There is an enum value for each flag, having a method that returns the value of that flag. The enum value names names are
	 * chosen to match the attribute name (just with '_' instead of '-').
	 * <p/>
	 * TODO: add missing flags
	 */
	private enum Flags
	{
		activity_brought_to_front {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
			}
		},

		activity_clear_task {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public int get()
			{
				if (VERSION.SDK_INT < 11)
				{
					return 0;
				}
				return Intent.FLAG_ACTIVITY_CLEAR_TASK;
			}
		},

		activity_clear_top {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_CLEAR_TOP;
			}
		},

		activity_clear_when_task_reset {
			@SuppressWarnings("deprecation")
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
			}
		},

		grant_read_uri_permission {
			@Override
			public int get()
			{
				return Intent.FLAG_GRANT_READ_URI_PERMISSION;
			}
		},

		grant_write_uri_permission {
			@Override
			public int get()
			{
				return Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
			}
		},

		grant_persistable_uri_permission {
			@TargetApi(Build.VERSION_CODES.KITKAT)
			@Override
			public int get()
			{
				if (VERSION.SDK_INT < 19)
				{
					return 0;
				}
				return Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
			}
		},

		grant_prefix_uri_permission {
			@TargetApi(Build.VERSION_CODES.LOLLIPOP)
			@Override
			public int get()
			{
				return Intent.FLAG_GRANT_PREFIX_URI_PERMISSION;
			}
		},

		debug_log_resolution {
			@Override
			public int get()
			{
				return Intent.FLAG_DEBUG_LOG_RESOLUTION;
			}
		},

		from_background {
			@Override
			public int get()
			{
				return Intent.FLAG_FROM_BACKGROUND;
			}
		},

		activity_exclude_from_recents {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
			}
		},

		activity_forward_result {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_FORWARD_RESULT;
			}
		},

		activity_launched_from_history {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
			}
		},

		activity_multiple_task {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
			}
		},

		activity_new_document {
			@TargetApi(Build.VERSION_CODES.LOLLIPOP)
			@Override
			public int get()
			{
				if (VERSION.SDK_INT < 21)
				{
					return 0;
				}
				return Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
			}
		},

		activity_new_task {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_NEW_TASK;
			}
		},

		activity_no_animation {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_NO_ANIMATION;
			}
		},

		activity_no_history {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_NO_HISTORY;
			}
		},

		activity_no_user_action {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_NO_USER_ACTION;
			}
		},

		activity_previous_is_top {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;
			}
		},

		activity_reset_task_if_needed {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;
			}
		},

		activity_reorder_to_front {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
			}
		},

		activity_single_top {
			@Override
			public int get()
			{
				return Intent.FLAG_ACTIVITY_SINGLE_TOP;
			}
		},

		activity_task_on_home {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public int get()
			{
				if (VERSION.SDK_INT < 11)
				{
					return 0;
				}
				return Intent.FLAG_ACTIVITY_TASK_ON_HOME;
			}
		},

		receiver_registered_only {
			public int get()
			{
				return Intent.FLAG_RECEIVER_REGISTERED_ONLY;
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
}

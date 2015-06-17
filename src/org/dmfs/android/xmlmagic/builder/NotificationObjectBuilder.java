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

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


/**
 * Created by marten on 10.05.15.
 */
public class NotificationObjectBuilder extends BaseAndroidObjectBuilder<Notification>
{
	public final static NotificationObjectBuilder INSTANCE = new NotificationObjectBuilder();

	private final static QualifiedName ATTR_ICON = QualifiedName.get("icon");
	private final static QualifiedName ATTR_TICKER = QualifiedName.get("ticker");
	private final static QualifiedName ATTR_TITLE = QualifiedName.get("title");
	private final static QualifiedName ATTR_SUB_TEXT = QualifiedName.get("sub-text");
	private final static QualifiedName ATTR_ONGOING = QualifiedName.get("ongoing");
	private final static QualifiedName ATTR_AUTO_CANCEL = QualifiedName.get("auto-cancel");
	private final static QualifiedName ATTR_CONTENT = QualifiedName.get("content");
	private final static QualifiedName ATTR_NUMBER = QualifiedName.get("number");
	private final static QualifiedName ATTR_ALERT_ONLY_ONCE = QualifiedName.get("alert-only-once");
	private final static QualifiedName ATTR_SHOW_WHEN = QualifiedName.get("show-when");
	private final static QualifiedName ATTR_SORT_KEY = QualifiedName.get("sort-key");

	/**
     *
     */
	private final static QualifiedName ATTR_COLOR = QualifiedName.get("color");
	private final static QualifiedName ATTR_ON_MS = QualifiedName.get("on-ms");
	private final static QualifiedName ATTR_OFF_MS = QualifiedName.get("off-ms");

	private final static QualifiedName ATTR_SOUND = QualifiedName.get("sound");

	private final static QualifiedName ATTR_MAX = QualifiedName.get("max");
	private final static QualifiedName ATTR_PROGRESS = QualifiedName.get("progress");
	private final static QualifiedName ATTR_VISIBLE = QualifiedName.get("visible");
	private final static QualifiedName ATTR_INDETERMINANTE = QualifiedName.get("indeterminante");

	private final static QualifiedName ATTR_SMALL_ICON = QualifiedName.get("small-icon");
	private final static ElementDescriptor<NotificationCompat.Action> ADD_ACTION = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "add-action"),
		new BaseAndroidObjectBuilder<NotificationCompat.Action>()
		{
			@Override
			public NotificationCompat.Action get(ElementDescriptor<NotificationCompat.Action> descriptor, NotificationCompat.Action recycle,
				ParserContext context) throws XmlObjectPullParserException
			{
				if (!(context instanceof AndroidParserContext))
				{
					throw new IllegalArgumentException("ParserContext must be an AndroidParserContext to build a Notification");
				}
				context.setState(new Action());

				return null;
			}


			@Override
			public NotificationCompat.Action update(ElementDescriptor<NotificationCompat.Action> descriptor, NotificationCompat.Action object,
				QualifiedName attribute, String value, ParserContext context) throws XmlObjectPullParserException
			{
				if (ATTR_ICON == attribute)
				{
					((Action) context.getState()).icon = getIntegerAttr(attribute, false, context);
				}
				else if (ATTR_TITLE == attribute)
				{
					((Action) context.getState()).title = getCharSequenceAttr(attribute, value, context);
				}
				return object;
			}


			@Override
			public <V> NotificationCompat.Action update(ElementDescriptor<NotificationCompat.Action> descriptor, NotificationCompat.Action object,
				ElementDescriptor<V> childDescriptor, V child, ParserContext context) throws XmlObjectPullParserException
			{
				if (childDescriptor == Model.PENDING_INTENT)
				{
					((Action) context.getState()).intent = (PendingIntent) child;
				}
				return object;
			}


			@Override
			public NotificationCompat.Action finish(ElementDescriptor<NotificationCompat.Action> descriptor, NotificationCompat.Action object,
				ParserContext context) throws XmlObjectPullParserException
			{
				Action action = (Action) context.getState();
				if (action.intent == null)
				{
					return null;
				}

				return new NotificationCompat.Action(action.icon, action.title, action.intent);
			}
		});

	private final static ElementDescriptor<PendingIntent> CONTENT_ACTION = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "content-intent"),
		PendingIntentObjectBuilder.INSTANCE);
	private final static ElementDescriptor<PendingIntent> DELETE_ACTION = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "delete-intent"),
		PendingIntentObjectBuilder.INSTANCE);

	private final static ElementDescriptor<Lights> LIGHTS = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "lights"),
		new BaseAndroidObjectBuilder<Lights>()
		{
			@Override
			public Lights get(ElementDescriptor<Lights> descriptor, Lights recycle, ParserContext context) throws XmlObjectPullParserException
			{
				if (recycle != null)
				{
					recycle.color = Color.GREEN;
					recycle.on = recycle.off = 500;
					return recycle;
				}
				return new Lights();
			}


			@Override
			public Lights update(ElementDescriptor<Lights> descriptor, Lights object, QualifiedName attribute, String value, ParserContext context)
				throws XmlObjectPullParserException
			{
				if (attribute == ATTR_COLOR)
				{
					object.color = Color.parseColor(getCharSequenceAttr(attribute, value, context).toString());
				}
				else if (attribute == ATTR_ON_MS)
				{
					object.on = getIntegerAttr(attribute, true, context);
				}
				else if (attribute == ATTR_OFF_MS)
				{
					object.off = getIntegerAttr(attribute, true, context);
				}
				return object;
			}
		});

	private final static ElementDescriptor<Progress> PROGRESS = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "progress"),
		new BaseAndroidObjectBuilder<Progress>()
		{
			@Override
			public Progress get(ElementDescriptor<Progress> descriptor, Progress recycle, ParserContext context) throws XmlObjectPullParserException
			{
				if (recycle != null)
				{
					recycle.max = 100;
					recycle.progress = 0;
					recycle.visible = true;
					return recycle;
				}
				return new Progress();
			}


			@Override
			public Progress update(ElementDescriptor<Progress> descriptor, Progress object, QualifiedName attribute, String value, ParserContext context)
				throws XmlObjectPullParserException
			{
				if (attribute == ATTR_MAX)
				{
					object.max = getIntegerAttr(attribute, true, context);
				}
				else if (attribute == ATTR_PROGRESS)
				{
					object.progress = getIntegerAttr(attribute, true, context);
				}
				else if (attribute == ATTR_VISIBLE)
				{
					object.visible = getBooleanAttr(attribute, context);
				}
				else if (attribute == ATTR_INDETERMINANTE)
				{
					object.indeterminante = getBooleanAttr(attribute, context);
				}
				return object;
			}
		});


	private NotificationCompat.Builder getBuilder(ParserContext context)
	{
		NotificationCompat.Builder builder = (NotificationCompat.Builder) context.getState();
		if (builder == null)
		{
			builder = new NotificationCompat.Builder(((AndroidParserContext) context).getAppContext());
			context.setState(builder);
		}
		return builder;
	}


	@Override
	public Notification get(ElementDescriptor<Notification> descriptor, Notification recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (!(context instanceof AndroidParserContext))
		{
			throw new IllegalArgumentException("ParserContext must be an AndroidParserContext to build a Notification");
		}
		context.setState(new NotificationCompat.Builder(((AndroidParserContext) context).getAppContext()));

		return null;
	}


	@Override
	public Notification update(ElementDescriptor<Notification> descriptor, Notification object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (attribute == ATTR_TICKER)
		{
			getBuilder(context).setTicker(getCharSequenceAttr(attribute, value, context));
		}
		else if (attribute == ATTR_TITLE)
		{
			getBuilder(context).setContentTitle(getCharSequenceAttr(attribute, value, context));
		}
		else if (attribute == ATTR_CONTENT)
		{
			getBuilder(context).setContentText(getCharSequenceAttr(attribute, value, context));
		}
		else if (attribute == ATTR_SUB_TEXT)
		{
			getBuilder(context).setSubText(getCharSequenceAttr(attribute, value, context));
		}
		else if (attribute == ATTR_SMALL_ICON)
		{
			getBuilder(context).setSmallIcon(getIntegerAttr(attribute, false, context));
		}
		else if (attribute == ATTR_ONGOING)
		{
			getBuilder(context).setOngoing(getBooleanAttr(attribute, context));
		}
		else if (attribute == ATTR_AUTO_CANCEL)
		{
			getBuilder(context).setAutoCancel(getBooleanAttr(attribute, context));
		}
		else if (attribute == ATTR_SHOW_WHEN)
		{
			getBuilder(context).setShowWhen(getBooleanAttr(attribute, context));
		}
		else if (attribute == ATTR_SORT_KEY)
		{
			getBuilder(context).setSortKey(getCharSequenceAttr(attribute, value, context).toString());
		}
		else if (attribute == ATTR_NUMBER)
		{
			getBuilder(context).setNumber(getIntegerAttr(attribute, true, context));
		}
		else if (attribute == ATTR_ALERT_ONLY_ONCE)
		{
			getBuilder(context).setOnlyAlertOnce(getBooleanAttr(attribute, context));
		}
		else if (attribute == ATTR_SOUND)
		{
			getBuilder(context).setSound(Uri.parse(getCharSequenceAttr(attribute, value, context).toString()));
		}
		return object;
	}


	@Override
	public <V> Notification update(ElementDescriptor<Notification> descriptor, Notification object, ElementDescriptor<V> childDescriptor, V child,
		ParserContext context) throws XmlObjectPullParserException
	{
		if (child == null)
		{
			return object;
		}

		if (childDescriptor == ADD_ACTION)
		{
			getBuilder(context).addAction((NotificationCompat.Action) child);
		}
		else if (childDescriptor == CONTENT_ACTION)
		{
			getBuilder(context).setContentIntent((PendingIntent) child);
		}
		else if (childDescriptor == DELETE_ACTION)
		{
			getBuilder(context).setDeleteIntent((PendingIntent) child);
		}
		else if (childDescriptor == Model.REMOTE_VIEWS)
		{
			getBuilder(context).setContent((RemoteViews) child);
		}
		else if (childDescriptor == LIGHTS)
		{
			Lights lights = (Lights) child;
			getBuilder(context).setLights(lights.color, lights.on, lights.off);
		}
		else if (childDescriptor == PROGRESS)
		{
			Progress progress = (Progress) child;
			if (progress.visible)
			{
				getBuilder(context).setProgress(progress.max, progress.progress, progress.indeterminante);
			}
		}
		return object;
	}


	@Override
	public Notification finish(ElementDescriptor<Notification> descriptor, Notification object, ParserContext context) throws XmlObjectPullParserException
	{
		// Build and return the notification
		return getBuilder(context).build();
	}

	private static class Action
	{
		int icon;
		CharSequence title;
		PendingIntent intent;
	}

	private static class Lights
	{
		int color = Color.GREEN;
		int on = 500;
		int off = 500;
	}

	private static class Progress
	{
		int max = 100;
		int progress = 0;
		boolean visible = true;
		boolean indeterminante = false;
	}
}

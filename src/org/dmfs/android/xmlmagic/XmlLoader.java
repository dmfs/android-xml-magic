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

package org.dmfs.android.xmlmagic;

import java.io.IOException;

import org.dmfs.android.xmlmagic.builder.RecyclingReflectionObjectBuilder;
import org.dmfs.android.xmlmagic.tokenresolvers.AndroidTokenResolver;
import org.dmfs.android.xmlmagic.tokenresolvers.ITokenResolver;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.XmlContext;
import org.dmfs.xmlobjects.builder.reflection.Attribute;
import org.dmfs.xmlobjects.builder.reflection.Element;
import org.dmfs.xmlobjects.pull.XmlObjectPull;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.pull.XmlPath;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;


/**
 * Provides static methods to populate or load certain classes from XML.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class XmlLoader
{

	private final static XmlPath EMPTY_PATH = new XmlPath();


	/**
	 * Populates the given {@link Activity} from XML. This will initialize all fields annotated with {@link Element} or {@link Attribute} with the respective
	 * value. See the example in {@link #populate(Activity, int, ITokenResolver)} to see how it works.
	 * <p>
	 * When using this method, the XML source file is determined from a meta value in the AndroidManifest.xml file. See the example below:
	 * </p>
	 * 
	 * <pre>
	 * {@code
	 * <activity android:name=".MyActivity" android:label="@string/app_name">
	 *     <intent-filter>
	 *         <action android:name="android.intent.action.MAIN" />
	 *         <category android:name="android.intent.category.LAUNCHER" />
	 *     </intent-filter>
	 *     <meta-data android:name="org.dmfs.ACTIVITY_PARAMETERS"
	 *         android:value="@xml/activity_definition" />
	 * </activity>
	 * }
	 * </pre>
	 * 
	 * @param activity
	 *            The {@link Activity} to populate.
	 */
	public static void populate(Activity activity)
	{
		try
		{
			ActivityInfo app = activity.getPackageManager().getActivityInfo(activity.getComponentName(),
				PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
			int id = app.metaData.getInt("org.dmfs.ACTIVITY_PARAMETERS");

			if (id != 0)
			{
				populate(activity, id);
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			// this should be impossible
		}
	}


	/**
	 * Populates the given {@link Activity} from XML. This will initialize all fields annotated with {@link Element} or {@link Attribute} with the respective
	 * value. See the example in {@link #populate(Activity, int, ITokenResolver)} to see how it works.
	 * <p>
	 * This method can use an {@link ITokenResolver} to resolve token strings to values.
	 * </p>
	 * <p>
	 * When using this method, the XML source file is determined from a meta value in the AndroidManifest.xml file. See the example below:
	 * </p>
	 * 
	 * <pre>
	 * {@code
	 * <activity android:name=".MyActivity" android:label="@string/app_name">
	 *     <intent-filter>
	 *         <action android:name="android.intent.action.MAIN" />
	 *         <category android:name="android.intent.category.LAUNCHER" />
	 *     </intent-filter>
	 *     <meta-data android:name="org.dmfs.ACTIVITY_PARAMETERS"
	 *         android:value="@xml/myactivity" />
	 * </activity>
	 * }
	 * </pre>
	 * 
	 * @param activity
	 *            The {@link Activity} to populate.
	 * @param tokenResolver
	 *            An {@link ITokenResolver} implementation that can resolve tokens.
	 */
	public static void populate(Activity activity, ITokenResolver tokenResolver)
	{
		try
		{
			ActivityInfo app = activity.getPackageManager().getActivityInfo(activity.getComponentName(),
				PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
			int id = app.metaData.getInt("org.dmfs.ACTIVITY_PARAMETERS");

			if (id != 0)
			{
				populate(activity, id, tokenResolver);
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			// this should be impossible
		}
	}


	/**
	 * Populates the given {@link Activity} from XML. This will initialize all fields annotated with {@link Element} or {@link Attribute} with the respective
	 * value. See the example in {@link #populate(Activity, int, ITokenResolver)} to see how it works.
	 * 
	 * @param activity
	 *            The {@link Activity} to populate.
	 * @param id
	 *            The resource id of the XML activity definition file.
	 */
	public static void populate(Activity activity, int id)
	{
		populate(activity, id, null);
	}


	/**
	 * Populates the given {@link Activity} from XML. This will initialize all fields annotated with {@link Element} or {@link Attribute} with the respective
	 * value.
	 * <p>
	 * Consider the following Activity snippet
	 * </p>
	 * 
	 * <pre>
	 * <code>
	 * public class MyActivity extends Activity
	 * {
	 *     private Intent intent;
	 * 
	 *     {@literal @}attribute(name = "name")
	 *     private String name;
	 * }
	 * </code>
	 * </pre>
	 * 
	 * And the following XML file in the res/xml/myactivity.xml.
	 * 
	 * <pre>
	 * {@code
	 * <activity xmlns="http://dmfs.org/ns/android-xml-magic" name="My Activity Name">
	 *     <intent>
	 *         <action>com.android.action.VIEW</action>
	 *         <data>http://dmfs.org</data>
	 *     </intent>
	 * </activity>
	 * }
	 * </pre>
	 * 
	 * When populating the Activity with
	 * 
	 * <pre>
	 * <code>
	 * Loader.populate(this, R.xml.myactivity, null);
	 * </code>
	 * </pre>
	 * 
	 * The field <code>name</code> will have the value "My Activity Name" and <code>intent</code> will be populated with an Intent that opens "http://dmfs.org"
	 * in a browser.
	 * 
	 * 
	 * @param activity
	 *            The {@link Activity} to populate.
	 * @param id
	 *            The resource id of the XML activity definition file.
	 * @param tokenResolver
	 *            An {@link ITokenResolver} implementation that can resolve tokens.
	 */
	public static void populate(Activity activity, int id, ITokenResolver tokenResolver)
	{
		/*
		 * We need to register an ElementDescriptor for each Activity sub-class that is populated. However, that would result in a element name conflict. To
		 * avoid that we create a new XmlContext every time we populate an Activity and register the Element in that context only.
		 */
		final XmlContext mContext = new XmlContext();

		// we know for sure that activity is an Activity
		@SuppressWarnings("unchecked")
		final ElementDescriptor<Activity> ACTIVITY = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "activity"),
			new RecyclingReflectionObjectBuilder<Activity>((Class<Activity>) activity.getClass()), mContext);

		try
		{
			XmlObjectPull pullParser = getParser(activity, id, tokenResolver);
			pullParser.setContext(mContext);
			pullParser.pull(ACTIVITY, activity, EMPTY_PATH);
		}
		catch (Exception e)
		{
			throw new RuntimeException("could not populate activity", e);
		}
	}


	public static void populate(Service service)
	{
		try
		{
			ServiceInfo app = service.getPackageManager().getServiceInfo(new ComponentName(service, service.getClass()),
				PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
			int id = app.metaData.getInt("org.dmfs.SERVICE_PARAMETERS");

			if (id != 0)
			{
				populate(service, id);
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			// this should be impossible
		}
	}


	public static void populate(Service service, int id)
	{
		populate(service, id, null);
	}


	public static void populate(Service service, ITokenResolver tokenResolver)
	{
		try
		{
			ServiceInfo app = service.getPackageManager().getServiceInfo(new ComponentName(service, service.getClass()),
				PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
			int id = app.metaData.getInt("org.dmfs.SERVICE_PARAMETERS");

			if (id != 0)
			{
				populate(service, id, tokenResolver);
			}
		}
		catch (PackageManager.NameNotFoundException e)
		{
			// this should be impossible
		}
	}


	public static void populate(Service service, int id, ITokenResolver tokenResolver)
	{
		final XmlContext mContext = new XmlContext();

		// we know for sure that service is a Service
		@SuppressWarnings("unchecked")
		final ElementDescriptor<Service> SERVICE = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "service"),
			new RecyclingReflectionObjectBuilder<Service>((Class<Service>) service.getClass()), mContext);

		try
		{
			XmlObjectPull pullParser = getParser(service, id, tokenResolver);
			pullParser.setContext(mContext);
			pullParser.pull(SERVICE, service, EMPTY_PATH);
		}
		catch (Exception e)
		{
			throw new RuntimeException("could not populate service", e);
		}
	}


	public static Notification loadNotification(Context context, int id, ITokenResolver... resolvers) throws IOException, XmlPullParserException,
		XmlObjectPullParserException
	{
		return getParser(context, id, resolvers).pull(Model.NOTIFICATION, null, EMPTY_PATH);
	}


	public static Intent loadIntent(Context context, int id, ITokenResolver... resolvers) throws IOException, XmlPullParserException,
		XmlObjectPullParserException
	{
		return getParser(context, id, resolvers).pull(Model.INTENT, null, EMPTY_PATH);
	}


	public static Bundle loadBundle(Context context, int id, ITokenResolver... resolvers) throws IOException, XmlPullParserException,
		XmlObjectPullParserException
	{
		return getParser(context, id, resolvers).pull(Model.BUNDLE, null, EMPTY_PATH);
	}


	private static XmlObjectPull getParser(Context context, int id, ITokenResolver... resolvers) throws IOException, XmlPullParserException,
		XmlObjectPullParserException
	{
		Resources res = context.getResources();

		XmlResourceParser xmlParser = res.getXml(id);
		return new XmlObjectPull(xmlParser, resolvers != null && resolvers.length > 0 ? new AndroidParserContext(context,
			new AndroidTokenResolver(context).setResolvers(resolvers)) : new AndroidParserContext(context, null));
	}
}

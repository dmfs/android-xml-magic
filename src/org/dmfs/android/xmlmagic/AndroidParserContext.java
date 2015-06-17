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

import org.dmfs.android.xmlmagic.tokenresolvers.ITokenResolver;
import org.dmfs.xmlobjects.pull.ParserContext;

import android.content.Context;
import android.content.res.Resources;


/**
 * A {@link ParserContext} that provides additional information to the XML parser when running on Android.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class AndroidParserContext extends ParserContext
{
	private final Context mAppContext;
	private final Resources mResources;
	private final ITokenResolver mResolver;


	/**
	 * Create a new {@link AndroidParserContext} using the given {@link Context} and {@link ITokenResolver}.
	 *
	 * @param context
	 *            A {@link Context}.
	 * @param resolver
	 *            An {@link ITokenResolver} or <code>null</code>.
	 */
	public AndroidParserContext(Context context, ITokenResolver resolver)
	{
		mAppContext = context.getApplicationContext();
		mResources = context.getResources();
		mResolver = resolver;
	}


	/**
	 * Get a {@link Resources} instance from the current {@link Context}.
	 *
	 * @return An instance of {@link Resources}.
	 */
	public Resources getResources()
	{
		return mResources;
	}


	/**
	 * Get the application {@link Context}.
	 *
	 * @return The application {@link Context}.
	 */
	public Context getAppContext()
	{
		return mAppContext;
	}


	/**
	 * Get the {@link ITokenResolver} to use for resolving the value of placeholder tokens. May be {@code null} if none has been provided.
	 *
	 * @return An {@link ITokenResolver} or {@code null}.
	 */
	public ITokenResolver getResolver()
	{
		return mResolver;
	}
}

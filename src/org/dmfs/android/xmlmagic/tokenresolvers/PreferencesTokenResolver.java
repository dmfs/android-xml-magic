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

package org.dmfs.android.xmlmagic.tokenresolvers;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * An {@link ITokenResolver} implementation that resolves place holders to shared preferences values.
 * <p/>
 * The format of the placeholder is expected to be
 * 
 * <pre>
 * <code>
 * {@literal @}prefs:PREFERENCES_FILE_NAME/PREFERENCE_VALUE
 * </code>
 * </pre>
 * <p/>
 * Note: at this time only string preferences are supported.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PreferencesTokenResolver implements ITokenResolver
{
	private final Context mContext;


	/**
	 * Create a new {@link PreferencesTokenResolver}.
	 * 
	 * @param context
	 *            A {@link Context}.
	 */
	public PreferencesTokenResolver(Context context)
	{
		mContext = context;
	}


	@Override
	public CharSequence resolveToken(String token)
	{
		if (token == null)
		{
			return null;
		}

		if (token.startsWith("@prefs:"))
		{
			token = token.substring(7);

			int slash = token.indexOf('/');
			if (slash > 0 && slash < token.length() - 1)
			{
				SharedPreferences prefs = mContext.getSharedPreferences(token.substring(0, slash - 1), 0);
				return prefs.getString(token.substring(slash + 1), null);
			}
		}
		return null;
	}
}

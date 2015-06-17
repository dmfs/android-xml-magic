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

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;


public class AndroidTokenResolver implements ITokenResolver
{
	private final Context mContext;
	private ITokenResolver[] mResolvers;


	public AndroidTokenResolver(Context context)
	{
		mContext = context.getApplicationContext();
	}


	/**
	 * Set additional token resolvers.
	 *
	 * @param resolvers
	 * @return this instance.
	 */
	public AndroidTokenResolver setResolvers(ITokenResolver... resolvers)
	{
		mResolvers = resolvers;
		return this;
	}


	@Override
	public CharSequence resolveToken(String token)
	{
		if (token == null)
		{
			return null;
		}

		if (mResolvers != null && mResolvers.length > 0)
		{
			for (ITokenResolver resolver : mResolvers)
			{
				CharSequence result = resolver.resolveToken(token);
				if (result != null)
				{
					return result;
				}
			}
		}

		if (token.startsWith("@android."))
		{
			if (token.equals("@android.model"))
			{
				return Build.MODEL;
			}
			if (token.equals("@android.sdk"))
			{
				return Integer.toString(Build.VERSION.SDK_INT);
			}
			if (token.equals("@android.release"))
			{
				return Build.VERSION.RELEASE;
			}
			if (token.equals("@android.manufacturer"))
			{
				return Build.MANUFACTURER;
			}
			if (token.equals("@android.product"))
			{
				return Build.PRODUCT;
			}
		}
		else if (token.startsWith("@locale."))
		{
			if (token.equals("@locale.lang"))
			{
				return Locale.getDefault().getLanguage();
			}
			if (token.equals("@locale.country"))
			{
				return Locale.getDefault().getCountry();
			}
		}
		else if (token.startsWith("@app."))
		{
			PackageManager pm = mContext.getPackageManager();
			String packageName = mContext.getPackageName();
			try
			{
				PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
				if (token.equals("@app.package"))
				{
					return packageName;
				}
				if (token.equals("@app.title"))
				{
					CharSequence label = pm.getApplicationLabel(mContext.getApplicationInfo());
					return label == null ? packageName : label.toString();
				}
				if (token.equals("@app.version"))
				{
					return pInfo.versionName;
				}
				if (token.equals("@app.version_code"))
				{
					return Integer.toString(pInfo.versionCode);
				}
			}
			catch (NameNotFoundException e)
			{
			}
		}
		else if (token.startsWith("@string/"))
		{
			Resources resources = mContext.getResources();
			int id = resources.getIdentifier(token.substring(1), null, mContext.getPackageName());
			if (id != 0)
			{
				return resources.getText(id);
			}
		}
		return null;
	}
}

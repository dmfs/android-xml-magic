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

import android.content.Intent;
import android.os.Bundle;


/**
 * An {@link ITokenResolver} implementation that resolves place holders to values taken from an {@link Intent}.
 * <p/>
 * The format of the placeholder is expected to be
 * 
 * <pre>
 * <code>
 * {@literal @}intent.FIELDNAME
 * </code>
 * </pre>
 * <p/>
 * Where <code>FIELDNAME</code> is any of
 * <ul>
 * <li><code>data</code></li>
 * <li><code>data.scheme</code></li>
 * <li><code>action</code></li>
 * <li><code>type</code></li>
 * <li><code>extra:EXTRAFIELD</code></li>
 * </ul>
 * The value <code>extra:EXTRAFIELD</code> resolves the respective field value in the extras bundle. <code>EXTRAFIELD</code> may contain <code>.</code> to refer
 * to nested bundles like so:
 * 
 * <pre>
 * <code>
 * {@literal @}intent.extra:KEY1.KEY2.KEY3
 * </code>
 * </pre>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class IntentTokenResolver implements ITokenResolver
{
	private final Intent mIntent;


	/**
	 * Creates an {@link IntentTokenResolver} for the given {@link Intent}.
	 * 
	 * @param intent
	 *            The intent that provides the values.
	 */
	public IntentTokenResolver(Intent intent)
	{
		mIntent = intent;
	}


	@Override
	public CharSequence resolveToken(String token)
	{
		if (token == null)
		{
			return null;
		}
		if (token.startsWith("@intent."))
		{
			if (token.equals("@intent.data"))
			{
				return mIntent.getDataString();
			}
			else if (token.equals("@intent.data.scheme"))
			{
				return mIntent.getScheme();
			}
			else if (token.equals("@intent.action"))
			{
				return mIntent.getAction();
			}
			else if (token.equals("@intent.type"))
			{
				return mIntent.getType();
			}
			else if (token.startsWith("@intent.extra:"))
			{
				Bundle bundle = mIntent.getExtras();
				token = token.substring(14);

				if (bundle.containsKey(token))
				{
					Object value = bundle.get(token);
					if (value != null)
					{
						return value.toString();
					}
					return null;
				}
				else
				{
					int dotIndex;

					while ((dotIndex = token.indexOf('.')) > 0)
					{
						String subkey = token.substring(0, dotIndex);
						bundle = bundle.getBundle(subkey);
						if (bundle == null)
						{
							return null;
						}
						token = token.substring(dotIndex + 1);
						if (bundle.containsKey(token))
						{
							Object value = bundle.get(token);
							if (value != null)
							{
								return value.toString();
							}
							return null;
						}
					}
				}
			}
		}
		return null;
	}
}

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

import org.json.JSONException;
import org.json.JSONObject;


/**
 * An {@link ITokenResolver} implementation that resolves place holders to values taken from a JSON object.
 * <p/>
 * The format of the placeholder is expected to be
 * 
 * <pre>
 * <code>
 * {@literal @}json:FIELDNAME
 * </code>
 * </pre>
 * <p/>
 * This also supports nested JSON objects like so:
 * 
 * <pre>
 * <code>
 * {@literal @}json:FIELDNAME1.FIELDNAME2
 * </code>
 * </pre>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class JsonTokenResolver implements ITokenResolver
{
	private final JSONObject mJsonObject;


	/**
	 * Creates a {@link JsonTokenResolver} using the given {@link JSONObject}.
	 * 
	 * @param jsonObject
	 *            The {@link JSONObject} that contains the values.
	 */
	public JsonTokenResolver(JSONObject jsonObject)
	{
		mJsonObject = jsonObject;
	}


	@Override
	public CharSequence resolveToken(String token)
	{
		if (token == null)
		{
			return null;
		}
		if (token.startsWith("@json:"))
		{
			token = token.substring(6);
			if (mJsonObject.has(token))
			{
				// the key is present, return the string represenation of the value
				return getAsString(mJsonObject, token);
			}
			else
			{
				// the key is not present. Check if key contains a "." and if there is a JSONObject for that key.
				int dotIndex;
				JSONObject currentObject = mJsonObject;

				while ((dotIndex = token.indexOf('.')) > 0)
				{
					currentObject = mJsonObject.optJSONObject(token.substring(0, dotIndex));
					if (currentObject == null)
					{
						return null;
					}
					token = token.substring(dotIndex + 1);
					if (currentObject.has(token))
					{
						return getAsString(currentObject, token);
					}
				}
			}
		}
		return null;
	}


	private String getAsString(JSONObject jsonObject, String key)
	{
		try
		{
			return jsonObject.getString(key);
		}
		catch (JSONException e)
		{
			return "";
		}
	}
}

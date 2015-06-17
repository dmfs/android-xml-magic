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

import android.text.SpannableStringBuilder;
import android.text.TextUtils;


/**
 * Provides a static method {@link #format(CharSequence, ITokenResolver, int)} to format a string that contains placeholder tokens using the given
 * {@link ITokenResolver}.
 */
public class StringFormatter
{

	/**
	 * Replace all parts formed like
	 *
	 * @param text
	 *            The text to format.
	 * @param tokenResolver
	 *            The {@link ITokenResolver} to provide a value for each token.
	 * @param depth
	 *            The recursion depth. This only applies to string resources.
	 * @return The formatted {@link CharSequence}.
	 */
	public static CharSequence format(CharSequence text, ITokenResolver tokenResolver, int depth)
	{
		if (tokenResolver == null)
		{
			return text;
		}

		if (text == null || text.length() <= 2)
		{
			return text;
		}

		int contentLen = text.length();

		SpannableStringBuilder formatString = null;

		int strPos = 0;
		int tokenPos = 0;
		while ((tokenPos = TextUtils.indexOf(text, '{', strPos)) >= 0)
		{
			if (formatString == null)
			{
				formatString = new SpannableStringBuilder();
			}

			formatString.append(text.subSequence(strPos, tokenPos));
			if (tokenPos == contentLen - 1)
			{
				// this is the last character in text
				strPos = tokenPos;
				break;
			}

			if (text.charAt(tokenPos + 1) == '}')
			{
				// found a {} sequence, replace by { and continue
				formatString.append('{');
				tokenPos += 2;
			}
			else
			{
				// found a placeholder for a token
				int closingBracket = TextUtils.indexOf(text, '}', tokenPos + 1);
				if (closingBracket < 0)
				{
					throw new IllegalArgumentException("invalid token string '" + text + "'");
				}

				CharSequence token = text.subSequence(tokenPos + 1, closingBracket);
				CharSequence value = tokenResolver.resolveToken(token.toString());

				if (value != null)
				{
					formatString.append(depth == 0 || !token.toString().startsWith("@string/") ? value : format(value, tokenResolver, depth - 1));
				}
				else
				{
					formatString.append('{');
					formatString.append(token);
					formatString.append('}');
				}
				tokenPos = closingBracket + 1;
			}
			strPos = tokenPos;
		}

		if (formatString != null)
		{
			formatString.append(text.subSequence(strPos, contentLen));
			return formatString;
		}
		else
		{
			return text;
		}
	}
}

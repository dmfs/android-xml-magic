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

import android.os.Bundle;


public class BundleTokenResolver implements ITokenResolver
{
    private final Bundle mBundle;


    public BundleTokenResolver(Bundle bundle)
    {
        mBundle = bundle;
    }


    @Override
    public CharSequence resolveToken(String token)
    {
        if (token == null)
        {
            return null;
        }

        if (token.startsWith("@bundle:"))
        {
            token = token.substring(8);

            if (mBundle.containsKey(token))
            {
                Object value = mBundle.get(token);
                if (value != null)
                {
                    return value.toString();
                }
                return null;
            }
            else
            {
                int dotIndex;
                Bundle currentBundle = mBundle;

                while ((dotIndex = token.indexOf('.')) > 0)
                {
                    String subkey = token.substring(0, dotIndex);
                    currentBundle = currentBundle.getBundle(subkey);
                    if (currentBundle == null)
                    {
                        return null;
                    }
                    token = token.substring(dotIndex + 1);
                    if (currentBundle.containsKey(token))
                    {
                        Object value = mBundle.get(token);
                        if (value != null)
                        {
                            return value.toString();
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }
}

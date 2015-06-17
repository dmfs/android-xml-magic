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

import android.database.Cursor;


public class CursorTokenResolver implements ITokenResolver
{
    private final Cursor mCursor;


    public CursorTokenResolver(Cursor cursor)
    {
        mCursor = cursor;
    }


    @Override
    public CharSequence resolveToken(String token)
    {
        if (token == null)
        {
            return null;
        }
        if (token.startsWith("@cursor:"))
        {
            // remove the "@cursor." part
            token = token.substring(8);

            int columnIndex = mCursor.getColumnIndex(token);
            if (columnIndex < 0)
            {
                return null;
            }
            else
            {
                return mCursor.getString(columnIndex);
            }
        }
        return null;
    }
}

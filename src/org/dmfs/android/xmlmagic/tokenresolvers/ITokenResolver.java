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

/**
 * Interface of an object that knows how to resolve the value of certain tokens.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public interface ITokenResolver
{

    /**
     * Returns the value for the given token.
     *
     * @param token A token.
     * @return a the value belonging to the token or <code>null</code> if the token is not known.
     */
    public CharSequence resolveToken(String token);
}

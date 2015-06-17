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

package org.dmfs.android.xmlmagic.builder;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;


/**
 * A Builder that returns <code>true</code> if <em>all</em> child elements of the built element are equal (as of the {@link Object#equals(Object)} method.
 * <p/>
 * Example:
 * 
 * <pre>
 * {@code
 * <xmlmagic:equals>
 *     <xmlmagic:string value="abc" />
 *     <xmlmagic:string value="{{@literal @}cursor:name}" />
 * </xmlmagic:equals>
 * }
 * </pre>
 * 
 * The result value of the equals element above will be <code>true</code> if and only if the column <code>name</code> of the provided cursor contains the string
 * <code>"abc"</code>.
 * <p/>
 * {@link org.dmfs.android.xmlmagic.Model#EQUALS} uses this builder.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class EqualsObjectBuilder extends AbstractObjectBuilder<Boolean>
{
	@Override
	public Boolean get(ElementDescriptor<Boolean> descriptor, Boolean recycle, ParserContext context) throws XmlObjectPullParserException
	{
		// the default result is true
		return true;
	}


	@Override
	public <V> Boolean update(ElementDescriptor<Boolean> descriptor, Boolean object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (!object)
		{
			// Condition is false already, at least one element didn't equal the others
			return object;
		}

		Object state = context.getState();
		if (state == null)
		{
			context.setState(child);
			return object;
		}
		else
		{
			boolean result = state.equals(child);
			context.setState(child);
			return result;
		}
	}
}

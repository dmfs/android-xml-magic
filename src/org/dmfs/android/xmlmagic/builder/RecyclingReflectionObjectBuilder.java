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
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.Recyclable;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;


/**
 * A special {@link ReflectionObjectBuilder} that populates the provided instance of an object instead of creating a new one.
 * <p/>
 * That allows to populate objects that already have been instantiated using the annotations. In particular this is useful for populating instances of
 * {@link android.app.Activity} or {@link android.app.Service}.
 * <p/>
 * Note that if the class implements {@link Recyclable} the {@link Recyclable#recycle()} method will be called for each instance.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class RecyclingReflectionObjectBuilder<T> extends ReflectionObjectBuilder<T>
{

	/**
	 * Create a new {@link RecyclingReflectionObjectBuilder} for the provided class.
	 *
	 * @param classParam
	 */
	public RecyclingReflectionObjectBuilder(Class<T> classParam)
	{
		super(classParam);
	}


	@Override
	public T get(ElementDescriptor<T> descriptor, T recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (recycle != null)
		{
			if (recycle instanceof Recyclable)
			{
				((Recyclable) recycle).recycle();
			}
			return recycle;
		}
		return super.get(descriptor, recycle, context);
	}
}

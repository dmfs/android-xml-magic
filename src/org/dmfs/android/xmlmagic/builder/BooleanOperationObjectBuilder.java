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
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;


/**
 * A builder that calculates the result of a boolean operation over it's boolean child elements.
 * <p/>
 * The operation is specified by the parameter of {@link BooleanOperationObjectBuilder#BooleanOperationObjectBuilder(BooleanOperation)}.
 * <p/>
 * Example:
 * 
 * <pre>
 * {@code
 * <xmlmagic:or>
 *     <xmlmagic:boolean value="{@literal@cursor:modified}"/>
 *     <xmlmagic:boolean value="{@literal@cursor:deleted}"/>
 * </xmlmagic:or>
 * }
 * </pre>
 * 
 * In this case the builder results in <code>true</code> if one of the columns <code>modified</code> or <code>deleted</code> in the given cursor contains a
 * value that's evaluated to <code>true</code>.
 * <p/>
 * The value of the result can be inverted by specifying the attribute <code>invert="true"</code>. This can be used to implement the <code>not</code> operation
 * like so:
 * 
 * <pre>
 * {@code
 * <xmlmagic:or invert="true">
 *     <xmlmagic:boolean value="{@literal@cursor:deleted}"/>
 * </xmlmagic:or>
 * }
 * </pre>
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public class BooleanOperationObjectBuilder extends BaseAndroidObjectBuilder<Boolean>
{
	/**
	 * The invert parameter.
	 */
	private final static QualifiedName ATTR_INVERT = QualifiedName.get("invert");

	/**
	 * The operation to execute.
	 */
	private final BooleanOperation mOperation;


	public BooleanOperationObjectBuilder(BooleanOperation operation)
	{
		mOperation = operation;
	}


	@Override
	public Boolean get(ElementDescriptor<Boolean> descriptor, Boolean recycle, ParserContext context) throws XmlObjectPullParserException
	{
		context.setState(false);
		return null;
	}


	@Override
	public Boolean update(ElementDescriptor<Boolean> descriptor, Boolean object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{

		if (attribute == ATTR_INVERT)
		{
			context.setState(getBooleanAttr(attribute, context));
		}

		return object;
	}


	@Override
	public <V> Boolean update(ElementDescriptor<Boolean> descriptor, Boolean object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (!(child instanceof Boolean))
		{
			// ignore non-boolean child elements
			return object;
		}

		if (object == null)
		{
			return (Boolean) child;
		}
		return mOperation.calculate(object, (Boolean) child);
	}


	@Override
	public Boolean finish(ElementDescriptor<Boolean> descriptor, Boolean object, ParserContext context) throws XmlObjectPullParserException
	{
		if (object == null)
		{
			object = false;
		}

		return (Boolean) context.getState() ? !object : object;
	}

	public enum BooleanOperation
	{
		or {
			@Override
			public boolean calculate(boolean left, boolean right)
			{
				return left || right;
			}
		},
		and {
			@Override
			public boolean calculate(boolean left, boolean right)
			{
				return left && right;
			}
		},
		xor {
			@Override
			public boolean calculate(boolean left, boolean right)
			{
				return left != right;
			}
		};

		public abstract boolean calculate(boolean left, boolean right);
	}
}

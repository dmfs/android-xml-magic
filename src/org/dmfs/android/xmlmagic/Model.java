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

import org.dmfs.android.xmlmagic.builder.AndroidBooleanObjectBuilder;
import org.dmfs.android.xmlmagic.builder.AndroidCharSequenceObjectBuilder;
import org.dmfs.android.xmlmagic.builder.AndroidStringObjectBuilder;
import org.dmfs.android.xmlmagic.builder.AndroidUriObjectBuilder;
import org.dmfs.android.xmlmagic.builder.BooleanOperationObjectBuilder;
import org.dmfs.android.xmlmagic.builder.BundleObjectBuilder;
import org.dmfs.android.xmlmagic.builder.EqualsObjectBuilder;
import org.dmfs.android.xmlmagic.builder.IntentObjectBuilder;
import org.dmfs.android.xmlmagic.builder.NotificationObjectBuilder;
import org.dmfs.android.xmlmagic.builder.PendingIntentObjectBuilder;
import org.dmfs.android.xmlmagic.builder.RemoteViewsObjectBuilder;
import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


/**
 * Contains static definitions of {@link ElementDescriptor}s for all elements supported by this library.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class Model
{
	/**
	 * The namespace of all elements supported by this library: <code>{@value #NAMESPACE}</code>
	 */
	public final static String NAMESPACE = "http://dmfs.org/ns/android-xml-magic";

	/**
	 * The {@link QualifiedName} of the <code>key</code> attribute.
	 */
	public final static QualifiedName ATTR_KEY = QualifiedName.get("key");

	/**
	 * The {@link ElementDescriptor} of an {@code <and>} element. The value of this element is <code>true</code> only if the value of all child elements is
	 * <code>true</code> as well. The result can be inverted by specifying the attribute <code>invert="true"</code>. It uses the
	 * {@link BooleanOperationObjectBuilder}. This element expects boolean child elements, non-boolean children will be ignored.
	 * <p/>
	 * Example:
	 * 
	 * <pre>
	 * {@code
	 * <and xmlns="http://dmfs.org/ns/android-xml-magic">
	 *   <boolean>true</true>
	 *   <boolean>false</boolean>
	 * </and> <!-- results in "false" -->
	 * 
	 * <and invert="true" xmlns="http://dmfs.org/ns/android-xml-magic">
	 *   <boolean>true</true>
	 *   <boolean>false</boolean>
	 * </and> <!-- results in "true" -->
	 * }
	 * </pre>
	 */
	public final static ElementDescriptor<Boolean> AND = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "and"), new BooleanOperationObjectBuilder(
		BooleanOperationObjectBuilder.BooleanOperation.and));

	public final static ElementDescriptor<Boolean> BOOLEAN = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "boolean"),
		AndroidBooleanObjectBuilder.INSTANCE);

	/**
	 * The {@link ElementDescriptor} of a {@code <bundle>} element. It uses the {@link BundleObjectBuilder}. This element expects child elements of the type
	 * {@code <bundle-value>}.
	 * <p/>
	 * Example:
	 * 
	 * <pre>
	 * {@code
	 * <bundle xmlns="http://dmfs.org/ns/android-xml-magic">
	 *   <bundle-value key="some_key"><string>This is the value</string></bundle-value>
	 *   <bundle-value key="some_other_key"><boolean>true</boolean></bundle-value>
	 * </bundle>
	 * }
	 * </pre>
	 */
	public final static ElementDescriptor<android.os.Bundle> BUNDLE = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "bundle"),
		BundleObjectBuilder.INSTANCE);

	public final static ElementDescriptor<CharSequence> CHAR_SEQUENCE = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "charsequence"),
		AndroidCharSequenceObjectBuilder.INSTANCE);

	public final static ElementDescriptor<Boolean> EQUALS = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "equals"), new EqualsObjectBuilder());

	public final static ElementDescriptor<Intent> INTENT = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "intent"),
		IntentObjectBuilder.INSTANCE);

	public final static ElementDescriptor<Notification> NOTIFICATION = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "notification"),
		NotificationObjectBuilder.INSTANCE);

	public final static ElementDescriptor<Boolean> OR = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "or"), new BooleanOperationObjectBuilder(
		BooleanOperationObjectBuilder.BooleanOperation.or));

	public final static ElementDescriptor<PendingIntent> PENDING_INTENT = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "pending-intent"),
		PendingIntentObjectBuilder.INSTANCE);

	public final static ElementDescriptor<RemoteViews> REMOTE_VIEWS = ElementDescriptor.register(QualifiedName.get(Model.NAMESPACE, "remote-views"),
		RemoteViewsObjectBuilder.INSTANCE);

	public final static ElementDescriptor<String> STRING = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "string"),
		AndroidStringObjectBuilder.INSTANCE);

	public final static ElementDescriptor<Uri> URI = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "uri"), AndroidUriObjectBuilder.INSTANCE);

	public final static ElementDescriptor<Boolean> XOR = ElementDescriptor.register(QualifiedName.get(NAMESPACE, "xor"), new BooleanOperationObjectBuilder(
		BooleanOperationObjectBuilder.BooleanOperation.xor));
}

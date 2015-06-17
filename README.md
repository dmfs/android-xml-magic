# android-xml-magic

__Unleashing the power of XML on Android__

Android-xml-magic brings extended XML capabilities to Android. It allows to load instances of certain classes directly from an XML resource, using the power of Android's resource qualifiers and
a templating mechanism. 

## Key benefits

* provision Activities and Services with values defined in an XML resource file
* template Intents, Bundles, Notifications & more in XML

## Requirements

This code builds on top of xmlobjects, see http://github.org/dmfs/xmlobjects

### Types that can be loaded from XML

At present this library supports building the following types from an XML file:

* Boolean (including simple operations like "and", "or", "xor" and "equals")
* Bundle
* CharSequence
* Double (provided by xmlobjects, no support for templating yet)
* Integer (provided by xmlobjects, no support for templating yet)
* Intent
* Lists (provided by xmlobjects)
* Maps (provided by xmlobjects)
* Notification
* PendingIntent
* RemoteViews (limited support)
* Sets (provided by xmlobjects)
* String
* Uri
* URI (provided by xmlobjects, no support for templating yet)
* and custom classes

### Templating

The library comes with a simple templating mechanism called token-resolvers.
It allows you to specify place holders in your XML (and String resources) that are replaced by actual values at runtime.
At present the sources for these values can be:

* a Bundle (including nested Bundles)
* a Cursor
* an Intent
* a JSONObject (including nested JSONObjects)
* a SharedPreference
* a String resource
* or static Android fields (like device vendor name or Android version)

Here are a few examples for such strings:

* `{@bundle:some_key}` is replaced by the value of the entry with the key `some_key` of a bundle provided at runtime
* `{@cursor:some_column}` is replaced by the current value of the colum `some_colum` of a cursor provided at runtime
* `{@string/some_string}` is replaced by the value of the string resource `some_string`
* `{@android.manufacturer}` is replaced by the manufacturer name
* `{@app.title}` is replaced by the app title

So below for some examples of how this is used.

## Examples

### Basic usage

The following xml snippet defines a simple Intent that launches a web browser with a specific URL

```xml
<xmlmagic:intent activity-new-task="true" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
  <xmlmagic:data>http://dmfs.org/</xmlmagic:data>
</xmlmagic:intent>
```

Assuming the file is located in `res/xml/browser`, you can load and launch it from an Activity with

```java
Intent browserIntent = XmlLoader.loadIntent(this, R.xml.browser);
startActivity(browserIntent);
```

You can also specify extras and explicit intents like so:

```xml
<xmlmagic:intent activity-new-task="true" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:package>my.fancy.packagename</xmlmagic:package>
  <xmlmagic:class>my.fancy.activity</xmlmagic:class>
  <xmlmagic:extras>
    <xmlmagic:bundle-value key="some key">some string value</xmlmagic:bundle-value>
    <xmlmagic:bundle-value key="some other key">
      <xmlmagic:uri>
        <xmlmagic:scheme>https</xmlmagic:scheme>
        <xmlmagic:authority>google.com</xmlmagic:authority>
        <xmlmagic:path>search</xmlmagic:path>
        <xmlmagic:query-parameter key="q">{@bundle:google_query}</xmlmagic:query-parameter>
      </xmlmagic:uri>
    </xmlmagic:bundle-value>
  </xmlmagic:extras>
</xmlmagic:intent>
```

### Using resource qualifiers

This becomes even more interesting when you remember that you can use qualifiers for XML resources.
So you can easily implement different behavior depending on language, screen size and orientation, display density, UI mode or platform level just by using qualifiers.

To pick up the example from above you can define different intents depending on the platform level.

Put this into `res/xml/browser`

```xml
<xmlmagic:intent activity-new-task="true" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
  <xmlmagic:data>https://www.google.com/</xmlmagic:data>
</xmlmagic:intent>
```

And this into `res/xml-v21/browser`

```xml
<xmlmagic:intent activity-new-task="true" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
  <xmlmagic:data>http://www.android.com</xmlmagic:data>
</xmlmagic:intent>
```

When you load and launch this intent, users on pre-Android 5 devices will see https://www.google.com and users on Android 5 and higher will see http://www.android.com.

### Adding place holders

By adding place holders you can use the XML files like templates. The following examples launches an Intent to open a website that's returned in the `url` column of a Cursor:


```xml
<xmlmagic:intent activity-new-task="true" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
  <xmlmagic:data>{@cursor:url}</xmlmagic:data>
</xmlmagic:intent>
```

In your Activity call:

```java
if (cursor.moveToFirst()) {
    Intent browserIntent = XmlLoader.loadIntent(this, R.xml.browser, new CursorTokenResolver(cursor));
    startActivity(browserIntent);
}
```

### Provisioning an Activity

This library also supports to provision Activities. Among other things, that allows you to recycle the same Activity for different purposes.

The easiest way is by providing a specific meta field in the AndroidManifest that points to an XML resource:

```xml
<activity android:name=".MyActivity" android:label="@string/app_name">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
  </intent-filter>
  <meta-data android:name="org.dmfs.ACTIVITY_PARAMETERS"
    android:value="@xml/myactivity" />
</activity>
```

Given the following file in `res/xml/myactivity`:

```xml
<xmlmagic:activity layout="@layout/mylayout" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <xmlmagic:intent activity-new-task="true">
    <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
    <xmlmagic:data>https://www.google.com/search?q={@app.package}</xmlmagic:data>
   </xmlmagic:intent>
</xmlmagic:intent>
```

You can provision the Activity like so

```java
public class MyActivity extends Activity {

  @Attribute(name="layout")
  private int myLayout;

  @Element(namespace=org.dmfs.android.xmlmagic.Model.NAMESPACE, name="intent")
  private Intent myIntent;

  public void onCreate(Bundle savedState)
  {
    super(savedState);

    XmlLoader.populate(this); // this loads the layout and the intent
    ...
  }

}
```
This is very useful, when you provide an activity in an Android library. You can initialize fields without subclassing the Activity in a project that uses the library. Instead just provide an XML resource.   


Above it says that you can recycle the same activity provisioned with different XML files. You can easily achieve this by using an activity-alias. 
Just put this into your AndroidManifest underneath the previous snippet:

 ```xml
<activity-alias android:name=".MyAliasedActivity"
  android:label="@string/app_name"
  targetActivity=".MyActivity">
  <meta-data android:name="org.dmfs.ACTIVITY_PARAMETERS"
    android:value="@xml/my_other_activity" />
</activity-alias>
```

This uses the same activity class, but provides a different XML file.

### Defining own elements

In most cases it's preferable to define own Element names. Luckily this is not difficult. We change the XML file to


```xml
<xmlmagic:activity layout="@layout/mylayout" xmlns:xmlmagic="http://dmfs.org/ns/android-xml-magic">
  <browserIntent activity-new-task="true">
    <xmlmagic:action>android.intent.action.VIEW</xmlmagic:action>
    <xmlmagic:data>https://www.google.com/</xmlmagic:data>
   </browserIntent>
</xmlmagic:intent>
```

Note: the element `<xmlmagic:intent>` is now called `browserIntent`.

To load this element just define an ElementDescriptor for the new name, using an IntentObjectBuilder and update the element name like so:

```java
public class MyActivity extends Activity {

  // define the value of a browserIntent element to be an Intent
  public final static ElementDescriptor<Intent> BROWSER_INTENT =
    ElementDescriptor.register(QualifiedName.get("browserIntent"),
      IntentObjectBuilder.INSTANCE);

  @Attribute(name="layout")
  private int myLayout;

  @Element(name="browserIntent")
  private Intent myIntent;

  public void onCreate(Bundle savedState)
  {
    super(savedState);

    XmlLoader.populate(this); // this loads the layout and the intent
    ...
  }

}
```


## TODO

* finish JavaDoc
* publish some unit test

## License

Copyright (c) Marten Gajda 2015


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.content;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * An intent is an abstract description of an operation to be performed. It can
 * be used with {@link Context#startActivity(Intent) startActivity} to launch an
 * {@link android.app.Activity},
 * {@link android.content.Context#sendBroadcast(Intent) broadcastIntent} to send
 * it to any interested {@link BroadcastReceiver BroadcastReceiver} components,
 * and {@link android.content.Context#startService} or
 * {@link android.content.Context#bindService} to communicate with a background
 * {@link android.app.Service}.
 *
 * <p>
 * An Intent provides a facility for performing late runtime binding between the
 * code in different applications. Its most significant use is in the launching
 * of activities, where it can be thought of as the glue between activities. It
 * is basically a passive data structure holding an abstract description of an
 * action to be performed.
 * </p>
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>
 * For information about how to create and resolve intents, read the
 * <a href="{@docRoot}guide/topics/intents/intents-filters.html">Intents and
 * Intent Filters</a> developer guide.
 * </p>
 * </div>
 *
 * <a name="IntentStructure"></a>
 * <h3>Intent Structure</h3>
 * <p>
 * The primary pieces of information in an intent are:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>action</b> -- The general action to be performed, such as
 * {@link #ACTION_VIEW}, {@link #ACTION_EDIT}, {@link #ACTION_MAIN}, etc.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>data</b> -- The data to operate on, such as a person record in the
 * contacts database, expressed as a {@link android.net.Uri}.
 * </p>
 * </li>
 * </ul>
 *
 *
 * <p>
 * Some examples of action/data pairs are:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>{@link #ACTION_VIEW} <i>content://contacts/people/1</i></b> -- Display
 * information about the person whose identifier is "1".
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_DIAL} <i>content://contacts/people/1</i></b> -- Display the
 * phone dialer with the person filled in.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_VIEW} <i>tel:123</i></b> -- Display the phone dialer with
 * the given number filled in. Note how the VIEW action does what is considered
 * the most reasonable thing for a particular URI.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_DIAL} <i>tel:123</i></b> -- Display the phone dialer with
 * the given number filled in.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_EDIT} <i>content://contacts/people/1</i></b> -- Edit
 * information about the person whose identifier is "1".
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_VIEW} <i>content://contacts/people/</i></b> -- Display a
 * list of people, which the user can browse through. This example is a typical
 * top-level entry into the Contacts application, showing you the list of
 * people. Selecting a particular person to view would result in a new intent {
 * <b>{@link #ACTION_VIEW} <i>content://contacts/people/N</i></b> } being used
 * to start an activity to display that person.
 * </p>
 * </li>
 * </ul>
 *
 * <p>
 * In addition to these primary attributes, there are a number of secondary
 * attributes that you can also include with an intent:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>category</b> -- Gives additional information about the action to execute.
 * For example, {@link #CATEGORY_LAUNCHER} means it should appear in the
 * Launcher as a top-level application, while {@link #CATEGORY_ALTERNATIVE}
 * means it should be included in a list of alternative actions the user can
 * perform on a piece of data.
 * </p>
 * <li>
 * <p>
 * <b>type</b> -- Specifies an explicit type (a MIME type) of the intent data.
 * Normally the type is inferred from the data itself. By setting this
 * attribute, you disable that evaluation and force an explicit type.
 * </p>
 * <li>
 * <p>
 * <b>component</b> -- Specifies an explicit name of a component class to use
 * for the intent. Normally this is determined by looking at the other
 * information in the intent (the action, data/type, and categories) and
 * matching that with a component that can handle it. If this attribute is set
 * then none of the evaluation is performed, and this component is used exactly
 * as is. By specifying this attribute, all of the other Intent attributes
 * become optional.
 * </p>
 * <li>
 * <p>
 * <b>extras</b> -- This is a {@link Bundle} of any additional information. This
 * can be used to provide extended information to the component. For example, if
 * we have a action to send an e-mail message, we could also include extra
 * pieces of data here to supply a subject, body, etc.
 * </p>
 * </ul>
 *
 * <p>
 * Here are some examples of other operations you can specify as intents using
 * these additional parameters:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>{@link #ACTION_MAIN} with category {@link #CATEGORY_HOME}</b> -- Launch
 * the home screen.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_GET_CONTENT} with MIME type
 * <i>{@link android.provider.Contacts.Phones#CONTENT_URI
 * vnd.android.cursor.item/phone}</i></b> -- Display the list of people's phone
 * numbers, allowing the user to browse through them and pick one and return it
 * to the parent activity.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>{@link #ACTION_GET_CONTENT} with MIME type <i>*{@literal /}*</i> and
 * category {@link #CATEGORY_OPENABLE}</b> -- Display all pickers for data that
 * can be opened with {@link ContentResolver#openInputStream(Uri)
 * ContentResolver.openInputStream()}, allowing the user to pick one of them and
 * then some data inside of it and returning the resulting URI to the caller.
 * This can be used, for example, in an e-mail application to allow the user to
 * pick some data to include as an attachment.
 * </p>
 * </li>
 * </ul>
 *
 * <p>
 * There are a variety of standard Intent action and category constants defined
 * in the Intent class, but applications can also define their own. These
 * strings use Java-style scoping, to ensure they are unique -- for example, the
 * standard {@link #ACTION_VIEW} is called "android.intent.action.VIEW".
 * </p>
 *
 * <p>
 * Put together, the set of actions, data types, categories, and extra data
 * defines a language for the system allowing for the expression of phrases such
 * as "call john smith's cell". As applications are added to the system, they
 * can extend this language by adding new actions, types, and categories, or
 * they can modify the behavior of existing phrases by supplying their own
 * activities that handle them.
 * </p>
 *
 * <a name="IntentResolution"></a>
 * <h3>Intent Resolution</h3>
 *
 * <p>
 * There are two primary forms of intents you will use.
 *
 * <ul>
 * <li>
 * <p>
 * <b>Explicit Intents</b> have specified a component (via {@link #setComponent}
 * or {@link #setClass}), which provides the exact class to be run. Often these
 * will not include any other information, simply being a way for an application
 * to launch various internal activities it has as the user interacts with the
 * application.
 *
 * <li>
 * <p>
 * <b>Implicit Intents</b> have not specified a component; instead, they must
 * include enough information for the system to determine which of the available
 * components is best to run for that intent.
 * </ul>
 *
 * <p>
 * When using implicit intents, given such an arbitrary intent we need to know
 * what to do with it. This is handled by the process of <em>Intent
 * resolution</em>, which maps an Intent to an {@link android.app.Activity},
 * {@link BroadcastReceiver}, or {@link android.app.Service} (or sometimes two
 * or more activities/receivers) that can handle it.
 * </p>
 *
 * <p>
 * The intent resolution mechanism basically revolves around matching an Intent
 * against all of the &lt;intent-filter&gt; descriptions in the installed
 * application packages. (Plus, in the case of broadcasts, any
 * {@link BroadcastReceiver} objects explicitly registered with
 * {@link Context#registerReceiver}.) More details on this can be found in the
 * documentation on the {@link IntentFilter} class.
 * </p>
 *
 * <p>
 * There are three pieces of information in the Intent that are used for
 * resolution: the action, type, and category. Using this information, a query
 * is done on the {@link PackageManager} for a component that can handle the
 * intent. The appropriate component is determined based on the intent
 * information supplied in the <code>AndroidManifest.xml</code> file as follows:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * The <b>action</b>, if given, must be listed by the component as one it
 * handles.
 * </p>
 * <li>
 * <p>
 * The <b>type</b> is retrieved from the Intent's data, if not already supplied
 * in the Intent. Like the action, if a type is included in the intent (either
 * explicitly or implicitly in its data), then this must be listed by the
 * component as one it handles.
 * </p>
 * <li>For data that is not a <code>content:</code> URI and where no explicit
 * type is included in the Intent, instead the <b>scheme</b> of the intent data
 * (such as <code>http:</code> or <code>mailto:</code>) is considered. Again
 * like the action, if we are matching a scheme it must be listed by the
 * component as one it can handle.
 * <li>
 * <p>
 * The <b>categories</b>, if supplied, must <em>all</em> be listed by the
 * activity as categories it handles. That is, if you include the categories
 * {@link #CATEGORY_LAUNCHER} and {@link #CATEGORY_ALTERNATIVE}, then you will
 * only resolve to components with an intent that lists <em>both</em> of those
 * categories. Activities will very often need to support the
 * {@link #CATEGORY_DEFAULT} so that they can be found by
 * {@link Context#startActivity Context.startActivity()}.
 * </p>
 * </ul>
 *
 * <p>
 * For example, consider the Note Pad sample application that allows a user to
 * browse through a list of notes data and view details about individual items.
 * Text in italics indicates places where you would replace a name with one
 * specific to your own package.
 * </p>
 *
 * <pre>
 *  &lt;manifest xmlns:android="http://schemas.android.com/apk/res/android"
 *       package="<i>com.android.notepad</i>"&gt;
 *     &lt;application android:icon="@drawable/app_notes"
 *             android:label="@string/app_name"&gt;
 *
 *         &lt;provider class=".NotePadProvider"
 *                 android:authorities="<i>com.google.provider.NotePad</i>" /&gt;
 *
 *         &lt;activity class=".NotesList" android:label="@string/title_notes_list"&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.MAIN" /&gt;
 *                 &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
 *             &lt;/intent-filter&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.VIEW" /&gt;
 *                 &lt;action android:name="android.intent.action.EDIT" /&gt;
 *                 &lt;action android:name="android.intent.action.PICK" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.GET_CONTENT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *         &lt;/activity&gt;
 *
 *         &lt;activity class=".NoteEditor" android:label="@string/title_note"&gt;
 *             &lt;intent-filter android:label="@string/resolve_edit"&gt;
 *                 &lt;action android:name="android.intent.action.VIEW" /&gt;
 *                 &lt;action android:name="android.intent.action.EDIT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *
 *             &lt;intent-filter&gt;
 *                 &lt;action android:name="android.intent.action.INSERT" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *
 *         &lt;/activity&gt;
 *
 *         &lt;activity class=".TitleEditor" android:label="@string/title_edit_title"
 *                 android:theme="@android:style/Theme.Dialog"&gt;
 *             &lt;intent-filter android:label="@string/resolve_title"&gt;
 *                 &lt;action android:name="<i>com.android.notepad.action.EDIT_TITLE</i>" /&gt;
 *                 &lt;category android:name="android.intent.category.DEFAULT" /&gt;
 *                 &lt;category android:name="android.intent.category.ALTERNATIVE" /&gt;
 *                 &lt;category android:name="android.intent.category.SELECTED_ALTERNATIVE" /&gt;
 *                 &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 *             &lt;/intent-filter&gt;
 *         &lt;/activity&gt;
 *
 *     &lt;/application&gt;
 * &lt;/manifest&gt;
 * </pre>
 *
 * <p>
 * The first activity, <code>com.android.notepad.NotesList</code>, serves as our
 * main entry into the app. It can do three things as described by its three
 * intent templates:
 * <ol>
 * <li>
 * 
 * <pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_MAIN android.intent.action.MAIN}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_LAUNCHER android.intent.category.LAUNCHER}" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * This provides a top-level entry into the NotePad application: the standard
 * MAIN action is a main entry point (not requiring any other information in the
 * Intent), and the LAUNCHER category says that this entry point should be
 * listed in the application launcher.
 * </p>
 * <li>
 * 
 * <pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_VIEW android.intent.action.VIEW}" /&gt;
 *     &lt;action android:name="{@link #ACTION_EDIT android.intent.action.EDIT}" /&gt;
 *     &lt;action android:name="{@link #ACTION_PICK android.intent.action.PICK}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * This declares the things that the activity can do on a directory of notes.
 * The type being supported is given with the &lt;type&gt; tag, where
 * <code>vnd.android.cursor.dir/vnd.google.note</code> is a URI from which a
 * Cursor of zero or more items (<code>vnd.android.cursor.dir</code>) can be
 * retrieved which holds our note pad data (<code>vnd.google.note</code>). The
 * activity allows the user to view or edit the directory of data (via the VIEW
 * and EDIT actions), or to pick a particular note and return it to the caller
 * (via the PICK action). Note also the DEFAULT category supplied here: this is
 * <em>required</em> for the {@link Context#startActivity Context.startActivity}
 * method to resolve your activity when its component name is not explicitly
 * specified.
 * </p>
 * <li>
 * 
 * <pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_GET_CONTENT android.intent.action.GET_CONTENT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * This filter describes the ability to return to the caller a note selected by
 * the user without needing to know where it came from. The data type
 * <code>vnd.android.cursor.item/vnd.google.note</code> is a URI from which a
 * Cursor of exactly one (<code>vnd.android.cursor.item</code>) item can be
 * retrieved which contains our note pad data (<code>vnd.google.note</code>).
 * The GET_CONTENT action is similar to the PICK action, where the activity will
 * return to its caller a piece of data selected by the user. Here, however, the
 * caller specifies the type of data they desire instead of the type of data the
 * user will be picking from.
 * </p>
 * </ol>
 *
 * <p>
 * Given these capabilities, the following intents will resolve to the NotesList
 * activity:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>{ action=android.app.action.MAIN }</b> matches all of the activities that
 * can be used as top-level entry points into an application.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.app.action.MAIN, category=android.app.category.LAUNCHER
 * }</b> is the actual intent used by the Launcher to populate its top-level
 * list.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.intent.action.VIEW
 * data=content://com.google.provider.NotePad/notes }</b> displays a list of all
 * the notes under "content://com.google.provider.NotePad/notes", which the user
 * can browse through and see the details on.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.app.action.PICK
 * data=content://com.google.provider.NotePad/notes }</b> provides a list of the
 * notes under "content://com.google.provider.NotePad/notes", from which the
 * user can pick a note whose data URL is returned back to the caller.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.app.action.GET_CONTENT
 * type=vnd.android.cursor.item/vnd.google.note }</b> is similar to the pick
 * action, but allows the caller to specify the kind of data they want back so
 * that the system can find the appropriate activity to pick something of that
 * data type.
 * </p>
 * </ul>
 *
 * <p>
 * The second activity, <code>com.android.notepad.NoteEditor</code>, shows the
 * user a single note entry and allows them to edit it. It can do two things as
 * described by its two intent templates:
 * <ol>
 * <li>
 * 
 * <pre>
 * &lt;intent-filter android:label="@string/resolve_edit"&gt;
 *     &lt;action android:name="{@link #ACTION_VIEW android.intent.action.VIEW}" /&gt;
 *     &lt;action android:name="{@link #ACTION_EDIT android.intent.action.EDIT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * The first, primary, purpose of this activity is to let the user interact with
 * a single note, as decribed by the MIME type
 * <code>vnd.android.cursor.item/vnd.google.note</code>. The activity can either
 * VIEW a note or allow the user to EDIT it. Again we support the DEFAULT
 * category to allow the activity to be launched without explicitly specifying
 * its component.
 * </p>
 * <li>
 * 
 * <pre>
 * &lt;intent-filter&gt;
 *     &lt;action android:name="{@link #ACTION_INSERT android.intent.action.INSERT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.dir/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 * <p>
 * The secondary use of this activity is to insert a new note entry into an
 * existing directory of notes. This is used when the user creates a new note:
 * the INSERT action is executed on the directory of notes, causing this
 * activity to run and have the user create the new note data which it then adds
 * to the content provider.
 * </p>
 * </ol>
 *
 * <p>
 * Given these capabilities, the following intents will resolve to the
 * NoteEditor activity:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>{ action=android.intent.action.VIEW
 * data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b> shows
 * the user the content of note <var>{ID}</var>.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.app.action.EDIT
 * data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b> allows
 * the user to edit the content of note <var>{ID}</var>.
 * </p>
 * <li>
 * <p>
 * <b>{ action=android.app.action.INSERT
 * data=content://com.google.provider.NotePad/notes }</b> creates a new, empty
 * note in the notes list at "content://com.google.provider.NotePad/notes" and
 * allows the user to edit it. If they keep their changes, the URI of the newly
 * created note is returned to the caller.
 * </p>
 * </ul>
 *
 * <p>
 * The last activity, <code>com.android.notepad.TitleEditor</code>, allows the
 * user to edit the title of a note. This could be implemented as a class that
 * the application directly invokes (by explicitly setting its component in the
 * Intent), but here we show a way you can publish alternative operations on
 * existing data:
 * </p>
 *
 * <pre>
 * &lt;intent-filter android:label="@string/resolve_title"&gt;
 *     &lt;action android:name="<i>com.android.notepad.action.EDIT_TITLE</i>" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_DEFAULT android.intent.category.DEFAULT}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_ALTERNATIVE android.intent.category.ALTERNATIVE}" /&gt;
 *     &lt;category android:name="{@link #CATEGORY_SELECTED_ALTERNATIVE android.intent.category.SELECTED_ALTERNATIVE}" /&gt;
 *     &lt;data android:mimeType="vnd.android.cursor.item/<i>vnd.google.note</i>" /&gt;
 * &lt;/intent-filter&gt;
 * </pre>
 *
 * <p>
 * In the single intent template here, we have created our own private action
 * called <code>com.android.notepad.action.EDIT_TITLE</code> which means to edit
 * the title of a note. It must be invoked on a specific note (data type
 * <code>vnd.android.cursor.item/vnd.google.note</code>) like the previous view
 * and edit actions, but here displays and edits the title contained in the note
 * data.
 *
 * <p>
 * In addition to supporting the default category as usual, our title editor
 * also supports two other standard categories: ALTERNATIVE and
 * SELECTED_ALTERNATIVE. Implementing these categories allows others to find the
 * special action it provides without directly knowing about it, through the
 * {@link android.content.pm.PackageManager#queryIntentActivityOptions} method,
 * or more often to build dynamic menu items with
 * {@link android.view.Menu#addIntentOptions}. Note that in the intent template
 * here was also supply an explicit name for the template (via
 * <code>android:label="@string/resolve_title"</code>) to better control what
 * the user sees when presented with this activity as an alternative action to
 * the data they are viewing.
 *
 * <p>
 * Given these capabilities, the following intent will resolve to the
 * TitleEditor activity:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * <b>{ action=com.android.notepad.action.EDIT_TITLE
 * data=content://com.google.provider.NotePad/notes/<var>{ID}</var> }</b>
 * displays and allows the user to edit the title associated with note
 * <var>{ID}</var>.
 * </p>
 * </ul>
 *
 * <h3>Standard Activity Actions</h3>
 *
 * <p>
 * These are the current standard actions that Intent defines for launching
 * activities (usually through {@link Context#startActivity}. The most
 * important, and by far most frequently used, are {@link #ACTION_MAIN} and
 * {@link #ACTION_EDIT}.
 *
 * <ul>
 * <li>{@link #ACTION_MAIN}
 * <li>{@link #ACTION_VIEW}
 * <li>{@link #ACTION_ATTACH_DATA}
 * <li>{@link #ACTION_EDIT}
 * <li>{@link #ACTION_PICK}
 * <li>{@link #ACTION_CHOOSER}
 * <li>{@link #ACTION_GET_CONTENT}
 * <li>{@link #ACTION_DIAL}
 * <li>{@link #ACTION_CALL}
 * <li>{@link #ACTION_SEND}
 * <li>{@link #ACTION_SENDTO}
 * <li>{@link #ACTION_ANSWER}
 * <li>{@link #ACTION_INSERT}
 * <li>{@link #ACTION_DELETE}
 * <li>{@link #ACTION_RUN}
 * <li>{@link #ACTION_SYNC}
 * <li>{@link #ACTION_PICK_ACTIVITY}
 * <li>{@link #ACTION_SEARCH}
 * <li>{@link #ACTION_WEB_SEARCH}
 * <li>{@link #ACTION_FACTORY_TEST}
 * </ul>
 *
 * <h3>Standard Broadcast Actions</h3>
 *
 * <p>
 * These are the current standard actions that Intent defines for receiving
 * broadcasts (usually through {@link Context#registerReceiver} or a
 * &lt;receiver&gt; tag in a manifest).
 *
 * <ul>
 * <li>{@link #ACTION_TIME_TICK}
 * <li>{@link #ACTION_TIME_CHANGED}
 * <li>{@link #ACTION_TIMEZONE_CHANGED}
 * <li>{@link #ACTION_BOOT_COMPLETED}
 * <li>{@link #ACTION_PACKAGE_ADDED}
 * <li>{@link #ACTION_PACKAGE_CHANGED}
 * <li>{@link #ACTION_PACKAGE_REMOVED}
 * <li>{@link #ACTION_PACKAGE_RESTARTED}
 * <li>{@link #ACTION_PACKAGE_DATA_CLEARED}
 * <li>{@link #ACTION_PACKAGES_SUSPENDED}
 * <li>{@link #ACTION_PACKAGES_UNSUSPENDED}
 * <li>{@link #ACTION_UID_REMOVED}
 * <li>{@link #ACTION_BATTERY_CHANGED}
 * <li>{@link #ACTION_POWER_CONNECTED}
 * <li>{@link #ACTION_POWER_DISCONNECTED}
 * <li>{@link #ACTION_SHUTDOWN}
 * </ul>
 *
 * <h3>Standard Categories</h3>
 *
 * <p>
 * These are the current standard categories that can be used to further clarify
 * an Intent via {@link #addCategory}.
 *
 * <ul>
 * <li>{@link #CATEGORY_DEFAULT}
 * <li>{@link #CATEGORY_BROWSABLE}
 * <li>{@link #CATEGORY_TAB}
 * <li>{@link #CATEGORY_ALTERNATIVE}
 * <li>{@link #CATEGORY_SELECTED_ALTERNATIVE}
 * <li>{@link #CATEGORY_LAUNCHER}
 * <li>{@link #CATEGORY_INFO}
 * <li>{@link #CATEGORY_HOME}
 * <li>{@link #CATEGORY_PREFERENCE}
 * <li>{@link #CATEGORY_TEST}
 * <li>{@link #CATEGORY_CAR_DOCK}
 * <li>{@link #CATEGORY_DESK_DOCK}
 * <li>{@link #CATEGORY_LE_DESK_DOCK}
 * <li>{@link #CATEGORY_HE_DESK_DOCK}
 * <li>{@link #CATEGORY_CAR_MODE}
 * <li>{@link #CATEGORY_APP_MARKET}
 * <li>{@link #CATEGORY_VR_HOME}
 * </ul>
 *
 * <h3>Standard Extra Data</h3>
 *
 * <p>
 * These are the current standard fields that can be used as extra data via
 * {@link #putExtra}.
 *
 * <ul>
 * <li>{@link #EXTRA_ALARM_COUNT}
 * <li>{@link #EXTRA_BCC}
 * <li>{@link #EXTRA_CC}
 * <li>{@link #EXTRA_CHANGED_COMPONENT_NAME}
 * <li>{@link #EXTRA_DATA_REMOVED}
 * <li>{@link #EXTRA_DOCK_STATE}
 * <li>{@link #EXTRA_DOCK_STATE_HE_DESK}
 * <li>{@link #EXTRA_DOCK_STATE_LE_DESK}
 * <li>{@link #EXTRA_DOCK_STATE_CAR}
 * <li>{@link #EXTRA_DOCK_STATE_DESK}
 * <li>{@link #EXTRA_DOCK_STATE_UNDOCKED}
 * <li>{@link #EXTRA_DONT_KILL_APP}
 * <li>{@link #EXTRA_EMAIL}
 * <li>{@link #EXTRA_INITIAL_INTENTS}
 * <li>{@link #EXTRA_INTENT}
 * <li>{@link #EXTRA_KEY_EVENT}
 * <li>{@link #EXTRA_ORIGINATING_URI}
 * <li>{@link #EXTRA_PHONE_NUMBER}
 * <li>{@link #EXTRA_REFERRER}
 * <li>{@link #EXTRA_REMOTE_INTENT_TOKEN}
 * <li>{@link #EXTRA_REPLACING}
 * <li>{@link #EXTRA_SHORTCUT_ICON}
 * <li>{@link #EXTRA_SHORTCUT_ICON_RESOURCE}
 * <li>{@link #EXTRA_SHORTCUT_INTENT}
 * <li>{@link #EXTRA_STREAM}
 * <li>{@link #EXTRA_SHORTCUT_NAME}
 * <li>{@link #EXTRA_SUBJECT}
 * <li>{@link #EXTRA_TEMPLATE}
 * <li>{@link #EXTRA_TEXT}
 * <li>{@link #EXTRA_TITLE}
 * <li>{@link #EXTRA_UID}
 * </ul>
 *
 * <h3>Flags</h3>
 *
 * <p>
 * These are the possible flags that can be used in the Intent via
 * {@link #setFlags} and {@link #addFlags}. See {@link #setFlags} for a list of
 * all possible flags.
 */
public class Intent implements Parcelable, Cloneable {

    /**
     *  Activity Action: Start as a main entry point, does not expect to
     *  receive data.
     *  <p>Input: nothing
     *  <p>Output: nothing
     */
    public static final String ACTION_MAIN = "android.intent.action.MAIN";

    /**
     * Activity Action: Display the data to the user.  This is the most common
     * action performed on data -- it is the generic action you can use on
     * a piece of data to get the most reasonable thing to occur.  For example,
     * when used on a contacts entry it will view the entry; when used on a
     * mailto: URI it will bring up a compose window filled with the information
     * supplied by the URI; when used with a tel: URI it will invoke the
     * dialer.
     * <p>Input: {@link #getData} is URI from which to retrieve data.
     * <p>Output: nothing.
     */
    public static final String ACTION_VIEW = "android.intent.action.VIEW";

    /**
     * A synonym for {@link #ACTION_VIEW}, the "standard" action that is
     * performed on a piece of data.
     */
    public static final String ACTION_DEFAULT = ACTION_VIEW;

    /**
     * Used to indicate that some piece of data should be attached to some other
     * place.  For example, image data could be attached to a contact.  It is up
     * to the recipient to decide where the data should be attached; the intent
     * does not specify the ultimate destination.
     * <p>Input: {@link #getData} is URI of data to be attached.
     * <p>Output: nothing.
     */
    public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";

    /**
     * Activity Action: Provide explicit editable access to the given data.
     * <p>Input: {@link #getData} is URI of data to be edited.
     * <p>Output: nothing.
     */
    public static final String ACTION_EDIT = "android.intent.action.EDIT";

    /**
     * Activity Action: Pick an existing item, or insert a new item, and then edit it.
     * <p>Input: {@link #getType} is the desired MIME type of the item to create or edit.
     * The extras can contain type specific data to pass through to the editing/creating
     * activity.
     * <p>Output: The URI of the item that was picked.  This must be a content:
     * URI so that any receiver can access it.
     */
    public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";

    /**
     * Activity Action: Pick an item from the data, returning what was selected.
     * <p>Input: {@link #getData} is URI containing a directory of data
     * (vnd.android.cursor.dir/*) from which to pick an item.
     * <p>Output: The URI of the item that was picked.
     */
    public static final String ACTION_PICK = "android.intent.action.PICK";

    /**
     * Activity Action: Creates a shortcut.
     * <p>Input: Nothing.</p>
     * <p>Output: An Intent representing the shortcut. The intent must contain three
     * extras: SHORTCUT_INTENT (value: Intent), SHORTCUT_NAME (value: String),
     * and SHORTCUT_ICON (value: Bitmap) or SHORTCUT_ICON_RESOURCE
     * (value: ShortcutIconResource).</p>
     *
     * @see #EXTRA_SHORTCUT_INTENT
     * @see #EXTRA_SHORTCUT_NAME
     * @see #EXTRA_SHORTCUT_ICON
     * @see #EXTRA_SHORTCUT_ICON_RESOURCE
     * @see android.content.Intent.ShortcutIconResource
     */
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";

    /**
     * Activity Action: Display an activity chooser, allowing the user to pick
     * what they want to before proceeding.  This can be used as an alternative
     * to the standard activity picker that is displayed by the system when
     * you try to start an activity with multiple possible matches, with these
     * differences in behavior:
     * <ul>
     * <li>You can specify the title that will appear in the activity chooser.
     * <li>The user does not have the option to make one of the matching
     * activities a preferred activity, and all possible activities will
     * always be shown even if one of them is currently marked as the
     * preferred activity.
     * </ul>
     * <p>
     * This action should be used when the user will naturally expect to
     * select an activity in order to proceed.  An example if when not to use
     * it is when the user clicks on a "mailto:" link.  They would naturally
     * expect to go directly to their mail app, so startActivity() should be
     * called directly: it will
     * either launch the current preferred app, or put up a dialog allowing the
     * user to pick an app to use and optionally marking that as preferred.
     * <p>
     * In contrast, if the user is selecting a menu item to send a picture
     * they are viewing to someone else, there are many different things they
     * may want to do at this point: send it through e-mail, upload it to a
     * web service, etc.  In this case the CHOOSER action should be used, to
     * always present to the user a list of the things they can do, with a
     * nice title given by the caller such as "Send this photo with:".
     * <p>
     * As a convenience, an Intent of this form can be created with the
     * {@link #createChooser} function.
     * <p>Input: No data should be specified.  get*Extra must have
     * a {@link #EXTRA_INTENT} field containing the Intent being executed,
     * and can optionally have a {@link #EXTRA_TITLE} field containing the
     * title text to display in the chooser.
     * <p>Output: Depends on the protocol of {@link #EXTRA_INTENT}.
     */
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";

    /**
     * Convenience function for creating a {@link #ACTION_CHOOSER} Intent.
     *
     * @param target The Intent that the user will be selecting an activity
     * to perform.
     * @param title Optional title that will be displayed in the chooser.
     * @return Return a new Intent object that you can hand to
     * {@link Context#startActivity(Intent) Context.startActivity()} and
     * related methods.
     */
    public static Intent createChooser(Intent target, CharSequence title) {
        return null;
    }

    /**
     * Activity Action: Allow the user to select a particular kind of data and
     * return it.  This is different than {@link #ACTION_PICK} in that here we
     * just say what kind of data is desired, not a URI of existing data from
     * which the user can pick.  A ACTION_GET_CONTENT could allow the user to
     * create the data as it runs (for example taking a picture or recording a
     * sound), let them browse over the web and download the desired data,
     * etc.
     * <p>
     * There are two main ways to use this action: if you want a specific kind
     * of data, such as a person contact, you set the MIME type to the kind of
     * data you want and launch it with {@link Context#startActivity(Intent)}.
     * The system will then launch the best application to select that kind
     * of data for you.
     * <p>
     * You may also be interested in any of a set of types of content the user
     * can pick.  For example, an e-mail application that wants to allow the
     * user to add an attachment to an e-mail message can use this action to
     * bring up a list of all of the types of content the user can attach.
     * <p>
     * In this case, you should wrap the GET_CONTENT intent with a chooser
     * (through {@link #createChooser}), which will give the proper interface
     * for the user to pick how to send your data and allow you to specify
     * a prompt indicating what they are doing.  You will usually specify a
     * broad MIME type (such as image/* or {@literal *}/*), resulting in a
     * broad range of content types the user can select from.
     * <p>
     * When using such a broad GET_CONTENT action, it is often desirable to
     * only pick from data that can be represented as a stream.  This is
     * accomplished by requiring the {@link #CATEGORY_OPENABLE} in the Intent.
     * <p>
     * Callers can optionally specify {@link #EXTRA_LOCAL_ONLY} to request that
     * the launched content chooser only returns results representing data that
     * is locally available on the device.  For example, if this extra is set
     * to true then an image picker should not show any pictures that are available
     * from a remote server but not already on the local device (thus requiring
     * they be downloaded when opened).
     * <p>
     * Input: {@link #getType} is the desired MIME type to retrieve.  Note
     * that no URI is supplied in the intent, as there are no constraints on
     * where the returned data originally comes from.  You may also include the
     * {@link #CATEGORY_OPENABLE} if you can only accept data that can be
     * opened as a stream.  You may use {@link #EXTRA_LOCAL_ONLY} to limit content
     * selection to local data.
     * <p>
     * Output: The URI of the item that was picked.  This must be a content:
     * URI so that any receiver can access it.
     */
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";

    /**
     * Activity Action: Dial a number as specified by the data.  This shows a
     * UI with the number being dialed, allowing the user to explicitly
     * initiate the call.
     * <p>Input: If nothing, an empty dialer is started; else {@link #getData}
     * is URI of a phone number to be dialed or a tel: URI of an explicit phone
     * number.
     * <p>Output: nothing.
     */
    public static final String ACTION_DIAL = "android.intent.action.DIAL";

    /**
     * Activity Action: Perform a call to someone specified by the data.
     * <p>Input: If nothing, an empty dialer is started; else {@link #getData}
     * is URI of a phone number to be dialed or a tel: URI of an explicit phone
     * number.
     * <p>Output: nothing.
     *
     * <p>Note: there will be restrictions on which applications can initiate a
     * call; most applications should use the {@link #ACTION_DIAL}.
     * <p>Note: this Intent <strong>cannot</strong> be used to call emergency
     * numbers.  Applications can <strong>dial</strong> emergency numbers using
     * {@link #ACTION_DIAL}, however.
     */
    public static final String ACTION_CALL = "android.intent.action.CALL";

    /**
     * Activity Action: Perform a call to an emergency number specified by the
     * data.
     * <p>Input: {@link #getData} is URI of a phone number to be dialed or a
     * tel: URI of an explicit phone number.
     * <p>Output: nothing.
     * @hide
     */
    public static final String ACTION_CALL_EMERGENCY = "android.intent.action.CALL_EMERGENCY";

    /**
     * Activity action: Perform a call to any number (emergency or not)
     * specified by the data.
     * <p>Input: {@link #getData} is URI of a phone number to be dialed or a
     * tel: URI of an explicit phone number.
     * <p>Output: nothing.
     * @hide
     */
    public static final String ACTION_CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";

    /**
     * Activity Action: Send a message to someone specified by the data.
     * <p>Input: {@link #getData} is URI describing the target.
     * <p>Output: nothing.
     */
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";

    /**
     * Activity Action: Deliver some data to someone else.  Who the data is
     * being delivered to is not specified; it is up to the receiver of this
     * action to ask the user where the data should be sent.
     * <p>
     * When launching a SEND intent, you should usually wrap it in a chooser
     * (through {@link #createChooser}), which will give the proper interface
     * for the user to pick how to send your data and allow you to specify
     * a prompt indicating what they are doing.
     * <p>
     * Input: {@link #getType} is the MIME type of the data being sent.
     * get*Extra can have either a {@link #EXTRA_TEXT}
     * or {@link #EXTRA_STREAM} field, containing the data to be sent.  If
     * using EXTRA_TEXT, the MIME type should be "text/plain"; otherwise it
     * should be the MIME type of the data in EXTRA_STREAM.  Use {@literal *}/*
     * if the MIME type is unknown (this will only allow senders that can
     * handle generic data streams).
     * <p>
     * Optional standard extras, which may be interpreted by some recipients as
     * appropriate, are: {@link #EXTRA_EMAIL}, {@link #EXTRA_CC},
     * {@link #EXTRA_BCC}, {@link #EXTRA_SUBJECT}.
     * <p>
     * Output: nothing.
     */
    public static final String ACTION_SEND = "android.intent.action.SEND";

    /**
     * Activity Action: Deliver multiple data to someone else.
     * <p>
     * Like ACTION_SEND, except the data is multiple.
     * <p>
     * Input: {@link #getType} is the MIME type of the data being sent.
     * get*ArrayListExtra can have either a {@link #EXTRA_TEXT} or {@link
     * #EXTRA_STREAM} field, containing the data to be sent.
     * <p>
     * Multiple types are supported, and receivers should handle mixed types
     * whenever possible. The right way for the receiver to check them is to
     * use the content resolver on each URI. The intent sender should try to
     * put the most concrete mime type in the intent type, but it can fall
     * back to {@literal <type>/*} or {@literal *}/* as needed.
     * <p>
     * e.g. if you are sending image/jpg and image/jpg, the intent's type can
     * be image/jpg, but if you are sending image/jpg and image/png, then the
     * intent's type should be image/*.
     * <p>
     * Optional standard extras, which may be interpreted by some recipients as
     * appropriate, are: {@link #EXTRA_EMAIL}, {@link #EXTRA_CC},
     * {@link #EXTRA_BCC}, {@link #EXTRA_SUBJECT}.
     * <p>
     * Output: nothing.
     */
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";

    /**
     * Activity Action: Handle an incoming phone call.
     * <p>Input: nothing.
     * <p>Output: nothing.
     */
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";

    /**
     * Activity Action: Insert an empty item into the given container.
     * <p>Input: {@link #getData} is URI of the directory (vnd.android.cursor.dir/*)
     * in which to place the data.
     * <p>Output: URI of the new data that was created.
     */
    public static final String ACTION_INSERT = "android.intent.action.INSERT";

    /**
     * Activity Action: Create a new item in the given container, initializing it
     * from the current contents of the clipboard.
     * <p>Input: {@link #getData} is URI of the directory (vnd.android.cursor.dir/*)
     * in which to place the data.
     * <p>Output: URI of the new data that was created.
     */
    public static final String ACTION_PASTE = "android.intent.action.PASTE";

    /**
     * Activity Action: Delete the given data from its container.
     * <p>Input: {@link #getData} is URI of data to be deleted.
     * <p>Output: nothing.
     */
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    /**
     * Activity Action: Run the data, whatever that means.
     * <p>Input: ?  (Note: this is currently specific to the test harness.)
     * <p>Output: nothing.
     */
    public static final String ACTION_RUN = "android.intent.action.RUN";

    /**
     * Activity Action: Perform a data synchronization.
     * <p>Input: ?
     * <p>Output: ?
     */
    public static final String ACTION_SYNC = "android.intent.action.SYNC";

    /**
     * Activity Action: Pick an activity given an intent, returning the class
     * selected.
     * <p>Input: get*Extra field {@link #EXTRA_INTENT} is an Intent
     * used with {@link PackageManager#queryIntentActivities} to determine the
     * set of activities from which to pick.
     * <p>Output: Class name of the activity that was selected.
     */
    public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";

    /**
     * Activity Action: Perform a search.
     * <p>Input: {@link android.app.SearchManager#QUERY getStringExtra(SearchManager.QUERY)}
     * is the text to search for.  If empty, simply
     * enter your search results Activity with the search UI activated.
     * <p>Output: nothing.
     */
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";

    /**
     * Activity Action: Start the platform-defined tutorial
     * <p>Input: {@link android.app.SearchManager#QUERY getStringExtra(SearchManager.QUERY)}
     * is the text to search for.  If empty, simply
     * enter your search results Activity with the search UI activated.
     * <p>Output: nothing.
     */
    public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";

    /**
     * Activity Action: Perform a web search.
     * <p>
     * Input: {@link android.app.SearchManager#QUERY
     * getStringExtra(SearchManager.QUERY)} is the text to search for. If it is
     * a url starts with http or https, the site will be opened. If it is plain
     * text, Google search will be applied.
     * <p>
     * Output: nothing.
     */
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";

    /**
     * Activity Action: List all available applications
     * <p>Input: Nothing.
     * <p>Output: nothing.
     */
    public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";

    /**
     * Activity Action: Show settings for choosing wallpaper
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";

    /**
     * Activity Action: Show activity for reporting a bug.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
    public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";

    /**
     *  Activity Action: Main entry point for factory tests.  Only used when
     *  the device is booting in factory test node.  The implementing package
     *  must be installed in the system image.
     *  <p>Input: nothing
     *  <p>Output: nothing
     */
    public static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST";

    /**
     * Activity Action: The user pressed the "call" button to go to the dialer
     * or other appropriate UI for placing a call.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
     public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";

     /**
     * Activity Action: Start Voice Command.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";

    /**
     * Activity Action: Start action associated with long pressing on the
     * search key.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
    public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";

    /**
     * Activity Action: The user pressed the "Report" button in the crash/ANR dialog.
     * This intent is delivered to the package which installed the application, usually
     * Google Play.
     * <p>Input: No data is specified. The bug report is passed in using
     * an {@link #EXTRA_BUG_REPORT} field.
     * <p>Output: Nothing.
     *
     * @see #EXTRA_BUG_REPORT
     */
    public static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR";

    /**
     * Activity Action: Show power usage information to the user.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     */
    public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";

    /**
     * Activity Action: Setup wizard to launch after a platform update.  This
     * activity should have a string meta-data field associated with it,
     * {@link #METADATA_SETUP_VERSION}, which defines the current version of
     * the platform for setup.  The activity will be launched only if
     * {@link android.provider.Settings.Secure#LAST_SETUP_SHOWN} is not the
     * same value.
     * <p>Input: Nothing.
     * <p>Output: Nothing.
     * @hide
     */
    public static final String ACTION_UPGRADE_SETUP = "android.intent.action.UPGRADE_SETUP";

    /**
     * Activity Action: Show settings for managing network data usage of a
     * specific application. Applications should define an activity that offers
     * options to control data usage.
     */
    public static final String ACTION_MANAGE_NETWORK_USAGE =
            "android.intent.action.MANAGE_NETWORK_USAGE";

    /**
     * Activity Action: Launch application installer.
     * <p>
     * Input: The data must be a content: or file: URI at which the application
     * can be retrieved.  You can optionally supply
     * {@link #EXTRA_INSTALLER_PACKAGE_NAME}, {@link #EXTRA_NOT_UNKNOWN_SOURCE},
     * {@link #EXTRA_ALLOW_REPLACE}, and {@link #EXTRA_RETURN_RESULT}.
     * <p>
     * Output: If {@link #EXTRA_RETURN_RESULT}, returns whether the install
     * succeeded.
     *
     * @see #EXTRA_INSTALLER_PACKAGE_NAME
     * @see #EXTRA_NOT_UNKNOWN_SOURCE
     * @see #EXTRA_RETURN_RESULT
     */
    public static final String ACTION_INSTALL_PACKAGE = "android.intent.action.INSTALL_PACKAGE";

    /**
     * Used as a boolean extra field with {@link #ACTION_INSTALL_PACKAGE} to install a
     * package.  Tells the installer UI to skip the confirmation with the user
     * if the .apk is replacing an existing one.
     */
    public static final String EXTRA_ALLOW_REPLACE
            = "android.intent.extra.ALLOW_REPLACE";

    /**
     * Extra used to indicate that an intent can allow the user to select and return multiple items.
     * This is a boolean extra; the default is false. If true, an implementation is allowed to present
     * the user with a UI where they can pick multiple items that are all returned to the caller.
     * When this happens, they should be returned as the getClipData() part of the result Intent.
     */
    public static final String EXTRA_ALLOW_MULTIPLE
            = "android.intent.extra.ALLOW_MULTIPLE";

    /**
     * Used to indicate that a GET_CONTENT intent only wants URIs that can be opened with
     * ContentResolver.openInputStream. Openable URIs must support the columns in OpenableColumns
     * when queried, though it is allowable for those columns to be blank.
     */
    public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";

    /**
     * Create an empty intent.
     */
    public Intent() {
    }

    /**
     * Copy constructor.
     */
    public Intent(Intent o) {
    }

    /**
     * Create an intent with a given action. All other fields (data, type, class)
     * are null. Note that the action <em>must</em> be in a namespace because
     * Intents are used globally in the system -- for example the system VIEW action
     * is android.intent.action.VIEW; an application's custom action would be
     * something like com.google.app.myapp.CUSTOM_ACTION.
     *
     * @param action The Intent action, such as ACTION_VIEW.
     */
    public Intent(String action) {
    }

    /**
     * Create an intent with a given action and for a given data url. Note that the
     * action <em>must</em> be in a namespace because Intents are used globally in
     * the system -- for example the system VIEW action is
     * android.intent.action.VIEW; an application's custom action would be something
     * like com.google.app.myapp.CUSTOM_ACTION.
     *
     * <p>
     * <em>Note: scheme and host name matching in the Android framework is
     * case-sensitive, unlike the formal RFC. As a result, you should always ensure
     * that you write your Uri with these elements using lower case letters, and
     * normalize any Uris you receive from outside of Android to ensure the scheme
     * and host is lower case.</em>
     * </p>
     *
     * @param action The Intent action, such as ACTION_VIEW.
     * @param uri    The Intent data URI.
     */
    public Intent(String action, Uri uri) {
    }

    /**
     * Create an intent for a specific component. All other fields (action, data,
     * type, class) are null, though they can be modified later with explicit calls.
     * This provides a convenient way to create an intent that is intended to
     * execute a hard-coded class name, rather than relying on the system to find an
     * appropriate class for you; see {@link #setComponent} for more information on
     * the repercussions of this.
     *
     * @param packageContext A Context of the application package implementing this
     *                       class.
     * @param cls            The component class that is to be used for the intent.
     *
     * @see #setClass
     * @see #setComponent
     * @see #Intent(String, android.net.Uri , Context, Class)
     */
    public Intent(Context packageContext, Class<?> cls) {
    }

    /**
     * Create an intent for a specific component with a specified action and data.
     * This is equivalent to using {@link #Intent(String, android.net.Uri)} to
     * construct the Intent and then calling {@link #setClass} to set its class.
     *
     * <p>
     * <em>Note: scheme and host name matching in the Android framework is
     * case-sensitive, unlike the formal RFC. As a result, you should always ensure
     * that you write your Uri with these elements using lower case letters, and
     * normalize any Uris you receive from outside of Android to ensure the scheme
     * and host is lower case.</em>
     * </p>
     *
     * @param action         The Intent action, such as ACTION_VIEW.
     * @param uri            The Intent data URI.
     * @param packageContext A Context of the application package implementing this
     *                       class.
     * @param cls            The component class that is to be used for the intent.
     *
     * @see #Intent(String, android.net.Uri)
     * @see #Intent(Context, Class)
     * @see #setClass
     * @see #setComponent
     */
    public Intent(String action, Uri uri, Context packageContext, Class<?> cls) {
    }

    /**
     * Call {@link #parseUri} with 0 flags.
     * 
     * @deprecated Use {@link #parseUri} instead.
     */
    @Deprecated
    public static Intent getIntent(String uri) {
        return null;
    }

    /**
     * Create an intent from a URI. This URI may encode the action, category, and
     * other intent fields, if it was returned by {@link #toUri}. If the Intent was
     * not generate by toUri(), its data will be the entire URI and its action will
     * be ACTION_VIEW.
     *
     * <p>
     * The URI given here must not be relative -- that is, it must include the
     * scheme and full path.
     *
     * @param uri   The URI to turn into an Intent.
     * @param flags Additional processing flags.
     *
     * @return Intent The newly created Intent object.
     *
     * @throws URISyntaxException Throws URISyntaxError if the basic URI syntax it
     *                            bad (as parsed by the Uri class) or the Intent
     *                            data within the URI is invalid.
     *
     * @see #toUri
     */
    public static Intent parseUri(String uri, int flags) {
        return null;
    }

    /**
     * Retrieve the general action to be performed, such as {@link #ACTION_VIEW}.
     * The action describes the general way the rest of the information in the
     * intent should be interpreted -- most importantly, what to do with the data
     * returned by {@link #getData}.
     *
     * @return The action of this intent or null if none is specified.
     *
     * @see #setAction
     */
    public String getAction() {
        return null;
    }

    /**
     * Retrieve data this intent is operating on. This URI specifies the name of the
     * data; often it uses the content: scheme, specifying data in a content
     * provider. Other schemes may be handled by specific activities, such as http:
     * by the web browser.
     *
     * @return The URI of the data this intent is targeting or null.
     *
     * @see #getScheme
     * @see #setData
     */
    public Uri getData() {
        return null;
    }

    /**
     * The same as {@link #getData()}, but returns the URI as an encoded String.
     */
    public String getDataString() {
        return null;
    }

    /**
     * Return the scheme portion of the intent's data. If the data is null or does
     * not include a scheme, null is returned. Otherwise, the scheme prefix without
     * the final ':' is returned, i.e. "http".
     *
     * <p>
     * This is the same as calling getData().getScheme() (and checking for null
     * data).
     *
     * @return The scheme of this intent.
     *
     * @see #getData
     */
    public String getScheme() {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if
     *         none was found.
     *
     * @deprecated
     * @hide
     */
    @Deprecated
    public Object getExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, boolean)
     */
    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return false;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, byte)
     */
    public byte getByteExtra(String name, byte defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, short)
     */
    public short getShortExtra(String name, short defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, char)
     */
    public char getCharExtra(String name, char defaultValue) {
        return 'a';
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, int)
     */
    public int getIntExtra(String name, int defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, long)
     */
    public long getLongExtra(String name, long defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if no such item is present
     *
     * @see #putExtra(String, float)
     */
    public float getFloatExtra(String name, float defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type
     *                     is stored with the given name.
     *
     * @return the value of an item previously added with putExtra(), or the default
     *         value if none was found.
     *
     * @see #putExtra(String, double)
     */
    public double getDoubleExtra(String name, double defaultValue) {
        return -1;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         String value was found.
     *
     * @see #putExtra(String, String)
     */
    public String getStringExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         CharSequence value was found.
     *
     * @see #putExtra(String, CharSequence)
     */
    public CharSequence getCharSequenceExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         Parcelable value was found.
     *
     * @see #putExtra(String, Parcelable)
     */
    public <T extends Parcelable> T getParcelableExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         Parcelable[] value was found.
     *
     * @see #putExtra(String, Parcelable[])
     */
    public Parcelable[] getParcelableArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with
     *         putParcelableArrayListExtra(), or null if no ArrayList<Parcelable>
     *         value was found.
     *
     * @see #putParcelableArrayListExtra(String, ArrayList)
     */
    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         Serializable value was found.
     *
     * @see #putExtra(String, Serializable)
     */
    public Serializable getSerializableExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with
     *         putIntegerArrayListExtra(), or null if no ArrayList<Integer> value
     *         was found.
     *
     * @see #putIntegerArrayListExtra(String, ArrayList)
     */
    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putStringArrayListExtra(),
     *         or null if no ArrayList<String> value was found.
     *
     * @see #putStringArrayListExtra(String, ArrayList)
     */
    public ArrayList<String> getStringArrayListExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with
     *         putCharSequenceArrayListExtra, or null if no ArrayList<CharSequence>
     *         value was found.
     *
     * @see #putCharSequenceArrayListExtra(String, ArrayList)
     */
    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         boolean array value was found.
     *
     * @see #putExtra(String, boolean[])
     */
    public boolean[] getBooleanArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         byte array value was found.
     *
     * @see #putExtra(String, byte[])
     */
    public byte[] getByteArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         short array value was found.
     *
     * @see #putExtra(String, short[])
     */
    public short[] getShortArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         char array value was found.
     *
     * @see #putExtra(String, char[])
     */
    public char[] getCharArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         int array value was found.
     *
     * @see #putExtra(String, int[])
     */
    public int[] getIntArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         long array value was found.
     *
     * @see #putExtra(String, long[])
     */
    public long[] getLongArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         float array value was found.
     *
     * @see #putExtra(String, float[])
     */
    public float[] getFloatArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         double array value was found.
     *
     * @see #putExtra(String, double[])
     */
    public double[] getDoubleArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         String array value was found.
     *
     * @see #putExtra(String, String[])
     */
    public String[] getStringArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         CharSequence array value was found.
     *
     * @see #putExtra(String, CharSequence[])
     */
    public CharSequence[] getCharSequenceArrayExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     *
     * @return the value of an item previously added with putExtra(), or null if no
     *         Bundle value was found.
     *
     * @see #putExtra(String, Bundle)
     */
    public Bundle getBundleExtra(String name) {
        return null;
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue The default value to return in case no item is associated
     *                     with the key 'name'
     *
     * @return the value of an item previously added with putExtra(), or
     *         defaultValue if none was found.
     *
     * @see #putExtra
     *
     * @deprecated
     * @hide
     */
    @Deprecated
    public Object getExtra(String name, Object defaultValue) {
        return null;
    }

    /**
     * Retrieves a map of extended data from the intent.
     *
     * @return the map of all extras previously added with putExtra(), or null if
     *         none have been added.
     */
    public Bundle getExtras() {
        return null;
    }

    /**
     * Filter extras to only basic types.
     * 
     * @hide
     */
    public void removeUnsafeExtras() {
    }

    /**
     * Set the general action to be performed.
     *
     * @param action An action name, such as ACTION_VIEW. Application-specific
     *               actions should be prefixed with the vendor's package name.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #getAction
     */
    public Intent setAction(String action) {
        return null;
    }

    /**
     * Set the data this intent is operating on. This method automatically clears
     * any type that was previously set by {@link #setType} or
     * {@link #setTypeAndNormalize}.
     *
     * <p>
     * <em>Note: scheme matching in the Android framework is case-sensitive, unlike
     * the formal RFC. As a result, you should always write your Uri with a lower
     * case scheme, or use {@link Uri#normalizeScheme} or
     * {@link #setDataAndNormalize} to ensure that the scheme is converted to lower
     * case.</em>
     *
     * @param data The Uri of the data this intent is now targeting.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #getData
     * @see #setDataAndNormalize
     * @see android.net.Uri#normalizeScheme()
     */
    public Intent setData(Uri data) {
        return null;
    }

    /**
     * Set an explicit MIME data type.
     *
     * <p>This is used to create intents that only specify a type and not data,
     * for example to indicate the type of data to return.
     *
     * <p>This method automatically clears any data that was
     * previously set (for example by {@link #setData}).
     *
     * <p><em>Note: MIME type matching in the Android framework is
     * case-sensitive, unlike formal RFC MIME types.  As a result,
     * you should always write your MIME types with lower case letters,
     * or use {@link #normalizeMimeType} or {@link #setTypeAndNormalize}
     * to ensure that it is converted to lower case.</em>
     *
     * @param type The MIME type of the data being handled by this intent.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #getType
     * @see #setTypeAndNormalize
     * @see #setDataAndType
     * @see #normalizeMimeType
     */
    public Intent setType(String type) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanExtra(String, boolean)
     */
    public Intent putExtra(String name, boolean value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteExtra(String, byte)
     */
    public Intent putExtra(String name, byte value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharExtra(String, char)
     */
    public Intent putExtra(String name, char value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortExtra(String, short)
     */
    public Intent putExtra(String name, short value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The integer data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntExtra(String, int)
     */
    public Intent putExtra(String name, int value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The long data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongExtra(String, long)
     */
    public Intent putExtra(String name, long value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatExtra(String, float)
     */
    public Intent putExtra(String name, float value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleExtra(String, double)
     */
    public Intent putExtra(String name, double value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringExtra(String)
     */
    public Intent putExtra(String name, String value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceExtra(String)
     */
    public Intent putExtra(String name, CharSequence value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableExtra(String)
     */
    public Intent putExtra(String name, Parcelable value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable[] data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayExtra(String)
     */
    public Intent putExtra(String name, Parcelable[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Parcelable> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayListExtra(String)
     */
    public Intent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Integer> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntegerArrayListExtra(String)
     */
    public Intent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<String> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayListExtra(String)
     */
    public Intent putStringArrayListExtra(String name, ArrayList<String> value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<CharSequence> data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayListExtra(String)
     */
    public Intent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Serializable data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getSerializableExtra(String)
     */
    public Intent putExtra(String name, Serializable value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanArrayExtra(String)
     */
    public Intent putExtra(String name, boolean[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteArrayExtra(String)
     */
    public Intent putExtra(String name, byte[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortArrayExtra(String)
     */
    public Intent putExtra(String name, short[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharArrayExtra(String)
     */
    public Intent putExtra(String name, char[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The int array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntArrayExtra(String)
     */
    public Intent putExtra(String name, int[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongArrayExtra(String)
     */
    public Intent putExtra(String name, long[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatArrayExtra(String)
     */
    public Intent putExtra(String name, float[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleArrayExtra(String)
     */
    public Intent putExtra(String name, double[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayExtra(String)
     */
    public Intent putExtra(String name, String[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence array data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayExtra(String)
     */
    public Intent putExtra(String name, CharSequence[] value) {
        return null;
    }

    /**
     * Add extended data to the intent. The name must include a package prefix, for
     * example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Bundle data value.
     *
     * @return Returns the same Intent object, for chaining multiple calls into a
     *         single statement.
     *
     * @see #putExtras
     * @see #removeExtra
     * @see #getBundleExtra(String)
     */
    public Intent putExtra(String name, Bundle value) {
        return null;
    }

    /**
     * Copy all extras in 'src' in to this intent.
     *
     * @param src Contains the extras to copy.
     *
     * @see #putExtra
     */
    public Intent putExtras(Intent src) {
        return null;
    }

    /**
     * Add a set of extended data to the intent. The keys must include a package
     * prefix, for example the app com.android.contacts would use names like
     * "com.android.contacts.ShowAll".
     *
     * @param extras The Bundle of extras to add to this intent.
     *
     * @see #putExtra
     * @see #removeExtra
     */
    public Intent putExtras(Bundle extras) {
        return null;
    }

    /**
     * Completely replace the extras in the Intent with the extras in the given
     * Intent.
     *
     * @param src The exact extras contained in this Intent are copied into the
     *            target intent, replacing any that were previously there.
     */
    public Intent replaceExtras(Intent src) {
        return null;
    }

    /**
     * Completely replace the extras in the Intent with the given Bundle of extras.
     *
     * @param extras The new set of extras in the Intent, or null to erase all
     *               extras.
     */
    public Intent replaceExtras(Bundle extras) {
        return null;
    }

    /**
     * Remove extended data from the intent.
     *
     * @see #putExtra
     */
    public void removeExtra(String name) {
    }

    public void writeToParcel(Parcel out, int flags) {
    }

    public void readFromParcel(Parcel in) {
    }

    /**
     * Retrieve the application package name this Intent is limited to.  When
     * resolving an Intent, if non-null this limits the resolution to only
     * components in the given application package.
     *
     * @return The name of the application package for the Intent.
     *
     * @see #resolveActivity
     * @see #setPackage
     */
    public String getPackage() {
        return null;
    }

    /**
     * (Usually optional) Set an explicit application package name that limits
     * the components this Intent will resolve to.  If left to the default
     * value of null, all components in all applications will considered.
     * If non-null, the Intent can only match the components in the given
     * application package.
     *
     * @param packageName The name of the application package to handle the
     * intent, or null to allow any application package.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #getPackage
     * @see #resolveActivity
     */
    public Intent setPackage(String packageName) {
        return null;
    }

    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit class name.
     *
     * @param packageContext A Context of the application package implementing
     * this class.
     * @param className The name of a class inside of the application package
     * that will be used as the component for this Intent.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #setComponent
     * @see #setClass
     */
    public Intent setClassName(Context packageContext, String className) {
        return null;
    }

    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit application package name and class name.
     *
     * @param packageName The name of the package implementing the desired
     * component.
     * @param className The name of a class inside of the application package
     * that will be used as the component for this Intent.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #setComponent
     * @see #setClass
     */
    public Intent setClassName(String packageName, String className) {
        return null;
    }

    /**
     * Add a new category to the intent.  Categories provide additional detail
     * about the action the intent performs.  When resolving an intent, only
     * activities that provide <em>all</em> of the requested categories will be
     * used.
     *
     * @param category The desired category.  This can be either one of the
     *               predefined Intent categories, or a custom category in your own
     *               namespace.
     *
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     *
     * @see #hasCategory
     * @see #removeCategory
     */
    public Intent addCategory(String category) {
        return null;
    }
}

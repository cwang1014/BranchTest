Branch SDK Integration
==================
This is an example Android application demonstrating Branch SDK integration and some of the features that are enabled as a result.

## Documentation
Please reference the Branch developer docs for more information regarding basic configuration at https://help.branch.io/developers-hub/docs/android-basic-integration

## Steps
1) Start out by replacing the Branch and Twitter API keys and secrets in app/src/main/res/values/api_keys.xml. Branch API key and secret can be found on Account Settings page of Branch dashboard and Twitter API key and secret can be created using a developer account
```XML
...
<resources>

    <!--
    Your Branch App Key Goes Here
    If you don't have one, see the Branch Android Quick-Start for how to get one:
    https://github.com/BranchMetrics/Branch-Integration-Guides/blob/master/android-quick-start.md
    -->
    <string name="branch_key">key_live_dnW19wxhWck7gbzSnSOcuohjFEhOK69n</string>
    <string name="branch_key_test">key_test_lkhEVOq9sPU9FXgbSAoQthfhqsd5EVaV</string>


    <!--
    Your Twitter Key and Secret Goes Here
    If you don't have these, see the Twitter Kit for Android documentation:
    https://dev.twitter.com/twitter-kit/android
    -->
    <string name="twitter_key">qO0tmxg5cEaWIkoe9JZpfuh8C</string>
    <string name="twitter_secret">lnvabgSzxOnevjnyIGGqeHRmY8SpIfvTCyssQzaBBA9YiLjCd6</string>

</resources>
```
2) Update Gradle wrapper and Android API level using SDK versions in top-level and app-level build.gradle files, and set manifest package in AndroidManifest.xml to io.branch.branchster
```java
android {
    compileSdkVersion 31
    buildToolsVersion "30.0.3"
    defaultConfig {
        applicationId "io.branch.branchster"
        minSdkVersion 21
        targetSdkVersion 31
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
...
}
```
```XML
...
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="io.branch.branchster">
```
3) Generate signed bundle/APK with new keystore and key, use that generated keystore to obtain a SHA256 fingerprint from application to add to branch dashboard, more details here: https://help.branch.io/developers-hub/docs/android-app-links#setup
4) Add branchsters:// URI scheme to Branch dashboard, set custom URL to branch generated link domain, and app package name to package name from manifest
![Branch_Configuration](https://res.cloudinary.com/cwhrcloud/image/upload/v1675066156/Screenshot_2023-01-30_at_12.03.26_AM_hx0wrn.jpg)
5) Install the Branch SDK by adding the three necessary dependency implementations to the app/module level build.gradle file
```java
dependencies {
...
    /* This is the code that will install the Branch SDK */
    // required for all Android apps
    implementation 'io.branch.sdk.android:library:5.+'
    // required if your app is in the Google Play Store (tip: avoid using bundled play services libs)
    implementation 'com.google.android.gms:play-services-ads-identifier:17.1.0+'
    // alternatively, use the following lib for getting the AAID
    // implementation 'com.google.android.gms:play-services-ads:17.2.0'
    // optional
    // Chrome Tab matching (enables 100% guaranteed matching based on cookies)
    implementation 'androidx.browser:browser:1.0.0'
    // Replace above with the line below if you do not support androidx
    // implementation 'com.android.support:customtabs:28.0.0'
}
```
6) Configure the branch SDK to work within the application by adding uses-permissions, intent-filters (to splash activity set to android.intent.category.LAUNCHER), and meta data tags with Branch keys generated in the dashboard to the AndroidManifest.xml file
```XML
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="io.branch.branchster">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>

    <application
        android:name="io.branch.branchster.BranchsterAndroidApplication"
        android:largeHeap="true"
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.Default">

        <!-- Launcher Activity to handle incoming Branch intents -->
        <activity
            android:name="io.branch.branchster.SplashActivity"
            android:exported="true"
            android:screenOrientation="unspecified"
            android:theme="@style/Theme.Transparent"
            android:launchMode="singleTask" >

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
                <data android:scheme="http" android:host="curtiswang.com"/>
            </intent-filter>

            <!-- Branch URI Scheme / intent filters -->
            <intent-filter>
                <!-- If utilizing $deeplink_path please explicitly declare your hosts, or utilize a wildcard(*) -->
                <data android:scheme="branchsters" android:host="open"/>
            	<action android:name="android.intent.action.VIEW" />
            	<category android:name="android.intent.category.DEFAULT" />
            	<category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>

            <!-- Branch App Links - Live App -->
            <intent-filter android:autoVerify="true">
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="https" android:host="fcpj8.app.link" />
                <!-- example-alternate domain is required for App Links when the Journeys/Web SDK and Deepviews are used inside your website.  -->
                <data android:scheme="https" android:host="fcpj8-alternate.app.link" />
            </intent-filter>
            <!-- Branch App Links - Test App -->
            <intent-filter android:autoVerify="true">
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="https" android:host="fcpj8.test-app.link" />
                <!-- example-alternate domain is required for App Links when the Journeys/Web SDK and Deepviews are used inside your website.  -->
                <data android:scheme="https" android:host="fcpj8-alternate.test-app.link" />
            </intent-filter>
        </activity>

        <activity
            android:name="io.branch.branchster.MonsterViewerActivity"
            android:theme="@style/Theme.Default"
            android:launchMode="singleInstance"
            android:exported="true"
            android:screenOrientation="unspecified">
            <meta-data
                android:name="io.branch.sdk.auto_link_path"
                android:value="monster/viewer/"/>
            <meta-data
                android:name="io.branch.sdk.auto_link_request_code"
                android:value="@integer/AutoDeeplinkRequestCode"/>

        </activity>

        <activity
            android:name="io.branch.branchster.MonsterCreatorActivity"
            android:theme="@style/Theme.Default"
            android:launchMode="singleInstance"
            android:exported="true"
            android:screenOrientation="unspecified">
            <meta-data
                android:name="io.branch.sdk.auto_link_path"
                android:value="monster/creator/"/>
            <meta-data
                android:name="io.branch.sdk.auto_link_request_code"
                android:value="@integer/AutoDeeplinkRequestCode"/>

        </activity>

        <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="e42146267f609d143ac39e0bde3b685cd9bfe77f7bb3d5c394aaa3df3f31eda5"/>
        <!-- Branch Initialization / Make sure to replace BranchKey and BranchKey.test values with values from Branch dashboard -->
        <meta-data android:name="io.branch.sdk.BranchKey" android:value="key_live_dnW19wxhWck7gbzSnSOcuohjFEhOK69n" />
        <meta-data android:name="io.branch.sdk.BranchKey.test" android:value="key_test_fj231yqm4nk8obDTfMWGznagDzlRP18r" />
        <meta-data android:name="io.branch.sdk.TestMode" android:value="false" />     <!-- Set to true to use Branch_Test_Key (useful when simulating installs and/or switching between debug and production flavors) -->

    </application>

    <queries>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="text/plain" />
        </intent>
    </queries>

</manifest>
```
7) Load branch into the Custom Application Class that you want to link to and track data from by enabling Branch logging and initializing the Branch object with getAutoInstance method
```java
...
public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }
}
```
8) Ensure Branch is initialized in the activity you have set to android.intent.category.LAUNCHER in onStart() (onStart() is what makes the activity visible to the user, as the app prepares for the activity to enter the foreground and become interactive)
```java
...
    @Override
    protected void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
    @Override
        public void onInitFinished(JSONObject referringParams, BranchError error) {
            MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
            if (error == null) {
                String monsterName = referringParams.optString("KEY_MONSTER_NAME", "");
                    if (monsterName.equals("")) {
                        prefs.setMonsterName("");
                        startActivity(new Intent(SplashActivity.this, MonsterCreatorActivity.class));
                    } else {
                        Intent i = new Intent(SplashActivity.this, MonsterViewerActivity.class);
                        i.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                        startActivity(i);
                    }
            } else {
                Log.e("MyApp", error.getMessage());
            }}
        }).withData(this.getIntent().getData()).init();
    }
```
9) After configuration, you can test if the app properly calls the Branch API by enabling test mode and inserting the Integration Validator into the onStart() method and checking the logs after building and running the app on your device
```java
public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch.enableLogging();
        /* Be sure to enable test mode in your custom application class */
        Branch.enableTestMode();
        Branch.getAutoInstance(this);
    }
}
```
```java
@Override
    protected void onStart() {
        super.onStart();
        /* Integration Validator goes here */
        IntegrationValidator.validate(SplashActivity.this);
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener(){
        @Override
            public void onInitFinished(JSONObject referringParams,BranchError error){
            }
        }
    }
```
10) To set up routing to a specific monster based on params returned in onInitFinished the application uses in-app routing immediately on app open. Referring params will be empty if no data is found, and the user can be routed to the monster creator, otherwise if params exist then the user clicked on a Branch deep link and the application can route the user to the monster viewer page to view the shared monster.
```java
@Override
    protected void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                MonsterPreferences prefs = MonsterPreferences.getInstance(getApplicationContext());
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    // params will be empty if no data found
                    String monsterName = referringParams.optString("KEY_MONSTER_NAME", "");
                        if (monsterName.equals("")) {
                            prefs.setMonsterName("");
                            startActivity(new Intent(SplashActivity.this, MonsterCreatorActivity.class));
                        } else {
                            Intent i = new Intent(SplashActivity.this, MonsterViewerActivity.class);
                            i.putExtra(MonsterViewerActivity.MY_MONSTER_OBJ_KEY, prefs.getLatestMonsterObj());
                            startActivity(i);
                        }
                } else {
                    Log.e("MyApp", error.getMessage());
                }
            }
        }).withData(this.getIntent().getData()).init();
    }
```
11) Adding custom event tracking to the monster edit and view pages utilizes the BranchEvent and allows developers to track an event that isn't a predefined Branch event
```java
...
/* Add a new Branch Event with a custom alias and custom metadata */
new BranchEvent("monster_view")
                .addCustomDataProperty("bodyIndex", String.valueOf(myMonsterObject.getBodyIndex()))
                .addCustomDataProperty("colorIndex", String.valueOf(myMonsterObject.getColorIndex()))
                .addCustomDataProperty("faceIndex", String.valueOf(myMonsterObject.getFaceIndex()))
                .addCustomDataProperty("monsterDescription", myMonsterObject.getMonsterDescription())
                .addCustomDataProperty("monsterName", myMonsterObject.getMonsterName())
                .logEvent(MonsterViewerActivity.this);
```
12) Running the application and navigating to the different activities should post in the console logs that the Branch API made a POST call. The Liveview section of the Dashboard should also display the custom events if they are being properly tracked.
    ![Branch_Console_Logs](https://res.cloudinary.com/cwhrcloud/image/upload/v1675101238/Screenshot_2023-01-30_at_9.52.49_AM_kiuldz.jpg)
    ![Branch_Liveview](https://res.cloudinary.com/cwhrcloud/image/upload/v1675101401/Screenshot_2023-01-30_at_9.56.21_AM_vev4cs.jpg)
13) Generating short URLs asynchronously in Java can be done using new Thread(() -> {}).start() and calling the generateShortUrl method of the Branch Universal Object
```java
new Thread(() -> {
            Map prepared = myMonsterObject.prepareBranchDict();
            LinkProperties linkProperties = new LinkProperties()
                    .addControlParameter("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                    .addControlParameter("KEY_MONSTER_DESCRIPTION", (String) prepared.get("$og_description"))
                    .addControlParameter("KEY_MONSTER_IMAGE", (String) prepared.get("$og_image_url"))
                    .addControlParameter("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                    .addControlParameter("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                    .addControlParameter("KEY_COLOR_INDEX", (String) prepared.get("color_index"));

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier("monster/viewer/")
                    .setTitle((String) prepared.get("monster_name"))
                    .setContentDescription((String) prepared.get("$og_description"))
                    .setContentImageUrl((String) prepared.get("$og_image_url"))
                    .setContentMetadata(new ContentMetadata()
                            .addCustomMetadata("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                            .addCustomMetadata("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                            .addCustomMetadata("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                            .addCustomMetadata("KEY_COLOR_INDEX", (String) prepared.get("color_index")));
            branchUniversalObject.generateShortUrl(MonsterViewerActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    if (error == null) {
                        monsterUrl.setText(url);
                        progressBar.setVisibility(View.GONE);
                        Log.i("MyApp", "got my Branch link to share: " + url);
                    }
                }
            });
}).start();
```


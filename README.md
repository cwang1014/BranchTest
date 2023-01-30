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
```XML
...
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="io.branch.branchster">
```
3) Generate signed bundle/APK with new keystore and key, use that generated keystore to obtain a SHA256 fingerprint from application to add to branch dashboard, more details here: https://help.branch.io/developers-hub/docs/android-app-links#setup
![Branch_Configuration](https://res.cloudinary.com/cwhrcloud/image/upload/v1675066156/Screenshot_2023-01-30_at_12.03.26_AM_hx0wrn.jpg)


This repository does not contain API keys so you need to define your own in order for the connected APIs to function. With the exception of the *Crashlytics ApiKey* (see the note below) the keys are defined as XML string resources and referenced at build-time. If you build the project as-is, you will get something like the following error:

```
Error: .. No resource found that matches the given name (at 'value' with value '@string/..').
```

To set up your own API keys and get rid of this error:

1. Open up **api_keys.xml** which exists in the */res/values* folder.
2. Insert your Branch App Key, Facebook ID and Twitter key/secret in this file.
3. Clean/Rebuild your project.

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--
    Your Branch App Key Goes Here
    If you don't have one, see the Branch Android Quick-Start for how to get one:
    https://github.com/BranchMetrics/Branch-Integration-Guides/blob/master/android-quick-start.md
    -->
    <string name="bnc_app_key">YOUR BRANCH APP KEY</string>

    <!--
    Your Your Facebook App ID Goes Here
    If you don't have one, see the Facebook SDK for Android documentation:
    https://developers.facebook.com/docs/android
    -->
    <string name="facebook_app_id">YOUR FACEBOOK APP ID</string>

    <!--
    Your Twitter Key and Secret Goes Here
    If you don't have these, see the Twitter Kit for Android documentation:
    https://dev.twitter.com/twitter-kit/android
    -->
    <string name="twitter_key">YOUR TWITTER APP KEY</string>
    <string name="twitter_secret">YOUR TWITTER APP SECRET</string>

</resources>
```

### Fabric/Crashlytics (required for Twitter integration)

Twitter's Fabric framework doesn't currently allow the *com.crashlytics.ApiKey* meta-data to be specified as a String resource in the *ApplicationManifest.xml* file. If you try to add the key as a *@string/..* reference you will get a *Crashlytics Developer Tools error* at build time.

So to get Twitter integration working, you will need to insert your Crashyltics key directly in *AndroidManifest.xml* like so:

```XML
<manifest .. >
  <application .. />
  
    <activity .. />

    <meta-data 
      android:name="com.crashlytics.ApiKey"
      android:value="YOUR FABRIC/CRASHLYTICS ApiKey" />
      
      --
      
  </application>
</manifest>
```

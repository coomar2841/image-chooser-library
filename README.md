**Makes it easy and simple to integrate "Attach that photo/video" feature into your android apps.**

>Don't worry about various devices/OS variations.

>Don't worry about out-of-memory errors.

>Don't worry about creating thumbnails to show a preview.

>Picking up any file for your app, and it's details.

<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp&hl=en&referrer=utm_source%3Dicl_github"">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

##### Code less for capturing  images/videos
- Supports picking up images/videos from phone gallery.
- Supports capturing images/videos using the phone camera.
- Generates thumb-nails for the any chosen media.
- Works on most(99%) of the phones/os versions.
- (New) Supports picking up files in general.
- Similar code base to implement irrespective of Android version of device.

##### Maven
<pre>
<code>
&lt;dependency&gt;
    &lt;groupId&gt;com.kbeanie&lt;/groupId&gt;
    &lt;artifactId&gt;image-chooser-library&lt;/artifactId&gt;
    &lt;version&gt;1.4.5&lt;/version&gt;
&lt;/dependency&gt;
</code>
</pre>

##### Configuring on Android Studio
> compile 'com.kbeanie:image-chooser-library:1.4.4@aar'

##### Apps using Image Chooser Library

App Name            | Link to Play Store
--------------------| ------------------------
<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp&hl=en&referrer=utm_source%3Dicl_github"><img src="https://lh6.ggpht.com/5HmHU2cE12jLB1NSX9blKNVa_dj_ymh_FIzajC6joVd4jYBopGQFj5ZFHr9FboHFyQ=w300-rw" width="48" height="48"/></a>|<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp&hl=en&referrer=utm_source%3Dicl_github">Image Chooser App</a>
<a href="https://play.google.com/store/apps/details?id=com.beanie.blog&hl=en&referrer=utm_source%3Dicl_github"><img src="https://lh6.ggpht.com/PT80sExPDHg0_Y75qGYsrSqzpyEU9v9UDS-bKBKlorqjz1LQ4FAOiRL2tHX3IljbcDU=w300-rw" width="48" height="48"/></a>|<a href="https://play.google.com/store/apps/details?id=com.beanie.blog&hl=en&referrer=utm_source%3Dicl_github">Blogaway</a>

If you would like to add your app to this list, drop me an email.


To build the project:

./gradlew assembleDebug
./gradlew assembleRelease

- If you wanna publish to your own repo, lets say if you made some custom changes, set up the following Gradle properties in gradle.properties.
It can be found in one of two places:
1) vi ~/.gradle/gradle.properties, or
2) <project root>/gradle.properties

- sonatypeRepo (pointing to your Maven repo).
- sonatypeUsername (username to that repo)
- sonatypePassword (password to your repo)
- scmUrl (URL pointing to your Maven server. Example: 'scm:git@github.com:coomar2841/image-chooser-library.git')

chooserReleaseKeyAlias (the absolute path to the key store you wanna sign with). Example: /Users/donald/keystores/mykeystore.keystore.jks
chooserReleaseKeyPassword=<password>
chooserReleaseStoreFile=<store file>
chooserReleaseStorePassword=<store password>

To build and upload to your repo:
./gradlew uploadArchives


### Release Notes

##### Version "1.4.5"
1. Removed launcher icon from the library source
2.

##### Version "1.4.4"
1. Minor fix: Launcher Icon collision with default project icon

##### Version "1.4.3"
1. Minor bug fixes.


##### License
-----------------------------------------------------------------------------------

Copyright 2013 Kumar Bibek

Licensed under the Apache License, Version 2.0 (the "License");<br />
you may not use this file except in compliance with the License.<br />
You may obtain a copy of the License at
   
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
	
Unless required by applicable law or agreed to in writing, software<br />
distributed under the License is distributed on an "AS IS" BASIS,<br />
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br />
See the License for the specific language governing permissions and<br />
limitations under the License.

-----------------------------------------------------------------------------------

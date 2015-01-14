[Link to image-chooser-library v1.3.1](https://dl.dropboxusercontent.com/u/6696191/image-chooser-library/v1.3.1/image-chooser-library-1.3.1.jar)

<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

**Makes it easy and simple to integrate "Attach that photo/video" feature into your android apps.**

>Don't worry about various devices/OS variations.

>Don't worry about out-of-memory errors.

>Don't worry about creating thumbnails to show a preview.

#### Code less for capturing  images/videos
- Supports picking up images/videos from phone gallery.
- Supports capturing images/videos using the phone camera.
- Generates thumb-nails for the any chosen media.
- Works on most(99%) of the phones/os versions.
- Similar code base to implement irrespective of Android version of device.

#### Maven
<pre>
<code>
&lt;dependency&gt;
    &lt;groupId&gt;com.kbeanie&lt;/groupId&gt;
    &lt;artifactId&gt;image-chooser-library&lt;/artifactId&gt;
    &lt;version&gt;1.4.02&lt;/version&gt;
&lt;/dependency&gt;
</code>
</pre>

##### Configuring on Android Studio
> compile 'com.kbeanie:image-chooser-library:1.4.02@aar'

##### Apps using Image Chooser Library

App Name            | Link to Play Store
--------------------| ------------------------
<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp&hl=en&referrer=utm_source%3Dicl_github"><img src="https://lh6.ggpht.com/5HmHU2cE12jLB1NSX9blKNVa_dj_ymh_FIzajC6joVd4jYBopGQFj5ZFHr9FboHFyQ=w300-rw" width="48" height="48"/></a>|<a href="tps://play.google.com/store/apps/details?id=com.beanie.imagechooserapp&hl=en&referrer=utm_source%3Dicl_github">Blogaway</a>
<a href="https://play.google.com/store/apps/details?id=com.beanie.blog&hl=en&referrer=utm_source%3Dicl_github"><img src="https://lh6.ggpht.com/PT80sExPDHg0_Y75qGYsrSqzpyEU9v9UDS-bKBKlorqjz1LQ4FAOiRL2tHX3IljbcDU=w300-rw" width="48" height="48"/></a>|<a href="https://play.google.com/store/apps/details?id=com.beanie.blog&hl=en&referrer=utm_source%3Dicl_github">Blogaway</a>

If you would like to add your app to this list, drop me an email.

### Release Notes

##### Version "1.4.00"
1. Fixed correct mime type reporting based on the actual file

##### Version "1.3.56"
1. Added FileChooser for selecting any file

##### Version "1.3.1"
1. Added suport sending extras [Issue #34](https://github.com/coomar2841/image-chooser-library/issues/34)
2. Imports of com.kbeanie.imagechooser.R should be removed [Issue #38](https://github.com/coomar2841/image-chooser-library/issues/38)
3. Switching to Android Studio/Gradle.
4. Added maven support

##### Version "1.3.0"
1. Added support for Microsoft's OneDrive app (Images and Videos)

##### Version "1.2.9"
1. Added MediaChooserManager: To pick either a video or an image (Not fully Tested)
2. Fixed image pickup from the Drive app

##### Version "1.2.7"
1. Added exception when Activity not found
2. Fixed problems with fetching uncached photos/videos from Google photos app
3. Switched from ACTION_PICK to ACTION_GET_CONTENT (Gives you more options like Dropbox, File Browser etc)
4. Fix for KITKAT document picker UI

#### License
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

[Link to image-chooser-library v1.2.6](https://dl.dropboxusercontent.com/u/6696191/image-chooser-library/v1.2.6/image-chooser-library-1.2.6.jar)

<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

**Makes it easy and simple to integrate "Attach that photo/video" feature into your android apps.**

> Don't worry about various devices/OS variations.

> Don't worry about out-of-memory errors.

> Don't worry about creating thumbnails to show a preview.

## Code less for capturing  images/videos
1. Supports picking up images/videos from phone gallery.
2. Supports capturing images/videos using the phone camera.
3. Generates thumb-nails for the any chosen media.
4. Works on most(99%) of the phones/os versions.
5. Similar code base to implement irrespective of Android version of device.

## Version 1.2.7
1. Added exception when Activity not found

## Version 1.2.6
1. Fix problems with images which don't have EXIF Data. Get their actual width and height.
2. Get the video preview image (Big Thumbnail)
3. Get the actual width and height of video files
4. Changed the folder structure so that the thumbnails don't appear in your gallery
5. Added source code for the sample app
6. Added functionality to handle activity destroyals for a few devices.
6. Fixed issue: https://github.com/coomar2841/image-chooser-library/issues/7

## Version 1.2.5
1. Updated the support library
2. Some bug fixes and error handling

## Version 1.2.4
1. Bug fix: While using a configured folder, the folder wasn't being created.
2. Added support for getting image details like width, height, file extension etc.

### License
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
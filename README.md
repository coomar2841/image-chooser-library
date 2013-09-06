[Link to image-chooser-library v1.2.5](https://dl.dropboxusercontent.com/u/6696191/image-chooser-library/v1.2.5/image-chooser-library-1.2.5.jar)

<a href="https://play.google.com/store/apps/details?id=com.beanie.imagechooserapp">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

**Makes it easy and simple to integrate "Attach that photo/video" feature into your apps.**
*Forget about various devices/os variations*

## Code less for capturing  images/videos
1. Supports picking up images/videos from phone gallery.
2. Supports capturing images/videos using the phone camera.
3. Generates thumbnails for the any chosen media.
4. Works on most(99%) of the phones/os versions.
5. Similar code base to implement irrespective of Android version of device.

## Version 1.2.6
1. Fix problems with images which don't have EXIF Data. Get their actual width and height.
2. Get the video preview image (Big Thumbnail)
3. Get the actual width and height of video files
4. Changed the folder structure so that the thumbnails don't appear in your gallery
5. Added source code for the sample app
6. Added functionality to handle activity destroyals for a few devices.

## Version 1.2.5
1. Updated the support library
2. Some bug fixes and error handling

## Version 1.2.4
1. Bug fix: While using a configured folder, the folder wasn't being created.
2. Added support for getting image details like width, height, file extension etc.

### Pre-Requisites:
1. Your app should have internet permission.
2. WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions are required.

### Integration with your apps (2 options)
1. Clone this repository and add this as a dependency to your Android project. This is a library project.
2. Download the jar file, and add it to your Android project's build path

## Usage:
__For choosing an image from gallery__
```java
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();
```

__For capturing a picture using your camera__
```java
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();
```

__On Activity result, do this:__
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK && 
		(requestCode == ChooserType.REQUEST_PICK_PICTURE ||
				requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
		imageChooserManager.submit(requestCode, data);
	}
}
```

__Implement the ImageChooserListener interface__

Override these methods:
```java
@Override
public void onImageChosen(final ChosenImage image) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			if (image != null) {
				// Use the image
				// image.getFilePathOriginal();
				// image.getFileThumbnail();
				// image.getFileThumbnailSmall();
			}
		}
	});
}
```

```java
@Override
public void onError(final String reason) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// Show error message
		}
	});
}
```

__For capturing a video using your camera__
```java
videoChooserManager = new VideoChooserManager(this, ChooserType.REQUEST_CAPTURE_VIDEO);
videoChooserManager.setVideoChooserListener(this);
videoChooserManager.choose();
```

__For selecting a video from your gallery__
```java
videoChooserManager = new VideoChooserManager(this, ChooserType.REQUEST_PICK_VIDEO);
videoChooserManager.setVideoChooserListener(this);
videoChooserManager.choose();
```

__On Activity result, do this:__
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK && 
		(requestCode == ChooserType.REQUEST_PICK_VIDEO ||
				requestCode == ChooserType.REQUEST_CAPTURE_VIDEO)) {
		videoChooserManager.submit(requestCode, data);
	}
}
```

__Implement the VideoChooserListener interface__

Override these methods:
```java
@Override
public void onVideoChosen(final ChosenVideo video) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			if (video != null) {
				// Use the video
				// video.getFilePathOriginal();
				// video.getFileThumbnail();
				// video.getFileThumbnailSmall();
			}
		}
	});
}
```

```java
@Override
public void onError(final String reason) {
	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// Show error message
		}
	});
}
```

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
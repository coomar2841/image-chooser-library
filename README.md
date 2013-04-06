## Version 1.2
1. Added choosing video from gallery.
2. Added taking a video using camera.

Sample App using the library v1.2
[Link to Sample app with v1.2](https://dl.dropbox.com/u/6696191/image-chooser-library/v1.2/image-chooser-app_v1.2.apk)

## Version 1.1

1. Added optional output folder configuration.
2. Added folder cache limit and auto delete old files.
3. Added an optional flag to control thumbnail generation.

## Version 1.0
1. Supports adding images by using the device's camera.
2. Supports adding pictures from the Camera folder of your gallery.
3. Supports adding pictures from your synced Picasa folders on your phone.
4. Supports 3 types of image output sizes (Original, Thumbnail and Thumbnail smaller).

### Pre-Requisites:
1. Your app should have internet permission.
2. WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions are required.

### Future releases
1. Manage cache files from within your app.
2. Use your own directory for the cache files.

### Integration with your apps
1. Clone this repository and add this as a dependency to your Android project. This is a library project.
2. Download the jar file, and add it to your Android project's build path

[Link to image-chooser-library v1.0](https://dl.dropbox.com/u/6696191/image-chooser-library/v1.0/image-chooser-library-1.0.jar)

[Link to image-chooser-library v1.1](https://dl.dropbox.com/u/6696191/image-chooser-library/v1.1/image-chooser-library-1.1.jar)

### Usage:

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
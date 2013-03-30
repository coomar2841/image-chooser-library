## Version 1.0
1. Supports adding images by using the device's camera
2. Supports adding pictures from the Camera folder of your gallery
3. Supports adding pictures from your synced Picasa folders on your phone
4. Supports 3 types of image output sizes (Original, Thumbnail and Thumbnail smaller)

### Pre-Requisites:
1. Your app should have internet permission.
2. WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions are required

### Future releases
1. Manage cache files from within your app.
2. Use your own directory for the cache files.

### Usage:

__For choosing an image from gallery__
```java
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CHOOSE_IMAGE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();
```

__For capturing a picture using your camera__
```java
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_TAKE_PICTURE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();
```

__On Activity result, do this:__
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK && 
		(requestCode == ChooserType.REQUEST_CHOOSE_IMAGE ||
				requestCode == ChooserType.REQUEST_TAKE_PICTURE)) {
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
       

Version 1.0

Usage:

For choosing an image from gallery
```java
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CHOOSE_IMAGE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();
```

For capturing a picture using your camera
imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_TAKE_PICTURE);
imageChooserManager.setImageChooserListener(this);
imageChooserManager.choose();

On Activity result, do this:

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_CHOOSE_IMAGE || requestCode == ChooserType.REQUEST_TAKE_PICTURE)) {
        imageChooserManager.submit(requestCode, data);
    }
}

For the callbacks, you would implement it this way.

Implement the ImageChooserListener interface

Override these methods:
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

@Override
public void onError(final String reason) {
   runOnUiThread(new Runnable() {

     @Override
     public void run() {
     	// Show error message
     }
    });
}
       

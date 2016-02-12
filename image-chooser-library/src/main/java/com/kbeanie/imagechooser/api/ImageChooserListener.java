/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    
 * http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.kbeanie.imagechooser.api;

public interface ImageChooserListener {
    /**
     * When the processing is complete, you will receive this callback with
     * {@link ChosenImage}
     * 
     * @param image
     */
    void onImageChosen(ChosenImage image);

    /**
     * Handle any error conditions if at all, when you receieve this callback
     * 
     * @param reason
     */
    void onError(String reason);


    /**
     * Callback when multiple images are chosen
     * @param images
     */
    void onImagesChosen(ChosenImages images);
}

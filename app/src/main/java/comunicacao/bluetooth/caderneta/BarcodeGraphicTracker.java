/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package comunicacao.bluetooth.caderneta;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;

import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
public class BarcodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;
    private Handler bcHandler;
    private final int CB_LIDO = 7;
    private final String CB = "cb";




    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * BarcodeUpdateListener interface method onBarcodeDetected.
     */


    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> mOverlay, BarcodeGraphic mGraphic, Handler bcHandler) {
        this.mOverlay = mOverlay;
        this.mGraphic = mGraphic;
        this.bcHandler = bcHandler;
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Barcode item) {

        mGraphic.setId(id);
        android.os.Message msg = bcHandler.obtainMessage(CB_LIDO);
        Bundle bundle = new Bundle();
        bundle.putString(CB, item.displayValue);
        msg.setData(bundle);
        bcHandler.sendMessage(msg);

    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}

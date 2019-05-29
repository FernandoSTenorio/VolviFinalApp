package com.fernandotenorio.volvifinalapp.Listeners;

import com.fernandotenorio.volvifinalapp.Model.Event;

public interface OnEventSavedListener {

    void onSaved(Event event);
    void onPhotoLoadProgress(double percent);
    void onFailed(String error);
}

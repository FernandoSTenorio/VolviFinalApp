package com.fernandotenorio.volvifinalapp.Listeners;

import com.fernandotenorio.volvifinalapp.Model.Event;

public interface OnEventLoadListener {

    void onLoad(Event event);
    void onFailed(String error);
}

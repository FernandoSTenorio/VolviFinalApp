package com.fernandotenorio.volvifinalapp.Listeners;

import com.fernandotenorio.volvifinalapp.Model.Blog;

public interface OnBlogSavedListener {
    void onSaved(Blog blog);
    void onPhotoLoadProgress(double percent);
    void onFailed(String error);
}
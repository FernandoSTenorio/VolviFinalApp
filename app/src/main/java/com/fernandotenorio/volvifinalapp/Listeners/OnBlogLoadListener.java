package com.fernandotenorio.volvifinalapp.Listeners;

import com.fernandotenorio.volvifinalapp.Model.Blog;

public interface OnBlogLoadListener {
    void onLoad(Blog blog);
    void onFailed(String error);
}

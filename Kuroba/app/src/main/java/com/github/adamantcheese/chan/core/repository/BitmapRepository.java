package com.github.adamantcheese.chan.core.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.renderscript.RenderScript;
import android.util.TypedValue;

import com.github.adamantcheese.chan.R;

import static com.github.adamantcheese.chan.utils.BitmapUtils.decode;

public class BitmapRepository {
    public static RenderScript rs;

    public static Bitmap youtubeIcon;
    public static Bitmap streamableIcon;
    public static Bitmap clypIcon;
    public static Bitmap bandcampIcon;
    public static Bitmap soundcloudIcon;
    public static Bitmap vocarooIcon;
    public static Bitmap vimeoIcon;
    public static Bitmap pixivIcon;

    public static Bitmap stickyIcon;
    public static Bitmap closedIcon;
    public static Bitmap trashIcon;
    public static Bitmap archivedIcon;
    public static Bitmap error;
    public static Bitmap paddedError;
    public static Bitmap empty;
    public static Bitmap transparentCheckerboard;

    public static ResourceBitmap partyHat;
    public static ResourceBitmap xmasHat;

    public static void initialize(Context c) {
        try {
            rs = RenderScript.create(c);
        } catch (Throwable ignored) {}

        youtubeIcon = decode(c, R.drawable.youtube_icon);
        streamableIcon = decode(c, R.drawable.streamable_icon);
        clypIcon = decode(c, R.drawable.clyp_icon);
        bandcampIcon = decode(c, R.drawable.bandcamp_icon);
        soundcloudIcon = decode(c, R.drawable.soundcloud_icon);
        vocarooIcon = decode(c, R.drawable.vocaroo_icon);
        vimeoIcon = decode(c, R.drawable.vimeo_icon);
        pixivIcon = decode(c, R.drawable.pixiv_icon);

        stickyIcon = decode(c, R.drawable.sticky_icon);
        closedIcon = decode(c, R.drawable.closed_icon);
        trashIcon = decode(c, R.drawable.trash_icon);
        archivedIcon = decode(c, R.drawable.archived_icon);

        error = decode(c, R.drawable.error_icon);
        paddedError = Bitmap.createBitmap(error.getWidth() * 2, error.getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(paddedError);
        canvas.drawBitmap(error, error.getWidth() * 0.5f, error.getHeight() * 0.5f, null);

        empty = decode(c, R.drawable.empty);
        transparentCheckerboard = decode(c, R.drawable.transparent_checkerboard);

        // images are 160x160 by default, so this is the center on that original image, before scaling
        partyHat = new ResourceBitmap(c, R.drawable.partyhat, 50, 125);
        xmasHat = new ResourceBitmap(c, R.drawable.xmashat, 50, 125);
    }

    public static class ResourceBitmap {
        public TypedValue bitmapSpecs = new TypedValue();
        public Bitmap bitmap;
        // the artificial center of the bitmap
        public float centerX;
        public float centerY;

        public ResourceBitmap(Context c, int resId, int centerX, int centerY) {
            c.getResources().getValue(resId, bitmapSpecs, true);
            bitmap = decode(c, resId);
            float scaleRatio = c.getResources().getDisplayMetrics().densityDpi / (float) bitmapSpecs.density;
            this.centerX = (centerX * scaleRatio) / bitmap.getWidth();
            this.centerY = (centerY * scaleRatio) / bitmap.getHeight();
        }
    }
}

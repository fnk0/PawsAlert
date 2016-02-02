package com.gabilheri.pawsalert.helpers;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/19/16.
 */
public final class Const {

    private Const() {

    }

    /**
     * Activity Result requests
     */
    public static final int REQUEST_TAKE_PHOTO = 1000;
    public static final int REQUEST_LOAD_PHOTO = 1002;
    public static final int REQUEST_VIDEO_CAPUTRE = 1003;

    /**
     * Mime types
     */
    public static final String PIC_MIME_JPEG = "image/jpg";
    public static final String PIC_MIME_PNG = "image/png";
    public static final String VID_MIME_MP4 = "video/mp4";
    public static final String MIME_IMAGE_ALL = "image/*";

    /**
     * Directory Names
     */
    public static final String IMAGE_DIRECTORY_NAME = "Paws";
    public static final String CANOPEO_TEMP = "paws_temp";
}

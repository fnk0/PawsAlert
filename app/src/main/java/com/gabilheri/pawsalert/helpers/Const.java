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

    /**
     * Animal Class
     */
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String DOG = "Dog";
    public static final String CAT = "Cat";

    /**
     *
     */
    public static final String OBJECT_ID = "objectId";
    public static final String IMAGE_EXTRA = "petImage";
    public static final String TRANSITION_LAYOUT = "revealTransition";
    public static final int MAX_PHOTOS = 8;

    /**
     * Validation
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    public static final int MIN_PASSWORD_SIZE = 6;
    public static final int MIN_PASSWORD_NUMBERS = 1;

    /**
     * Item Callback States
     */
    public static final int ANIMAL_FAVORITE = 785;
    public static final int ANIMAL_OPEN = 786;
    public static final int ANIMAL_SHARE = 787;
}

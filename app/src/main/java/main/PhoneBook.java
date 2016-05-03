package main;

import android.graphics.Bitmap;

/**
 * Created by Mark on 2016/5/3.
 */
public class PhoneBook {
    private Bitmap mAvatar;
    private String mName;
    private String mPhone;

    public PhoneBook(Bitmap avatar, String name, String phone) {
        mAvatar = avatar;
        mName = name;
        mPhone = phone;
    }

    public void setAvatar(Bitmap avatar) {
        mAvatar = avatar;
    }
    public Bitmap getAvatar() {
        return mAvatar;
    }

    public void setName(String name) {
        mName = name;
    }
    public String getName() {
        return mName;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }
    public String getPhone() {
        return mPhone;
    }
}

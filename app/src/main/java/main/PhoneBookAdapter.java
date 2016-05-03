package main;

import android.content.Context;
import android.widget.BaseAdapter;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.www.R;

/**
 * Created by Mark on 2016/5/3.
 */
public class PhoneBookAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhoneBook> mListPhoneBook;

    public PhoneBookAdapter(Context context, List<PhoneBook> list) {
        mContext = context;
        mListPhoneBook = list;

    }

    @Override
    public int getCount() {
        return mListPhoneBook.size();
    }

    @Override
    public Object getItem(int pos) {
        return mListPhoneBook.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // get selected entry
        PhoneBook entry = mListPhoneBook.get(pos);

        // inflating list view layout if null
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_phonebook, null);
        }

        // set avatar
        ImageView ivAvatar = (ImageView)convertView.findViewById(R.id.person_image);
        ivAvatar.setImageBitmap(entry.getAvatar());

        // set name
        TextView tvName = (TextView)convertView.findViewById(R.id.person_name);
        tvName.setText(entry.getName());

        // set phone
        //TextView tvPhone = (TextView)convertView.findViewById(R.id.tvPhone);
        //tvPhone.setText(entry.getPhone());


        return convertView;
    }
}

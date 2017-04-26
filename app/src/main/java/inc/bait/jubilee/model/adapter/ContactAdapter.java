package inc.bait.jubilee.model.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import inc.bait.jubilee.R;
import inc.bait.jubilee.model.appmodel.Contact;
import inc.bait.jubilee.model.view.RoundedImageView;
import inc.bait.jubilee.ui.activity.InviteActivity;

/**
 *
 */
public class ContactAdapter extends
        RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Cursor cursor;
    private final int name_col_index,
            id_col_index, has_number;
    private Context context;
    private InviteActivity.OnContactSelectedListener listener;
    public ContactAdapter(Context context,
                          Cursor cursor,
                          InviteActivity.OnContactSelectedListener listener) {
        this.context = context;
        this.cursor = cursor;
        name_col_index =
                cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        id_col_index =
                cursor.getColumnIndex(
                        ContactsContract.Contacts._ID);
        has_number = cursor.getColumnIndex(
                ContactsContract.Contacts.HAS_PHONE_NUMBER);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int pos) {
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item,
                        parent, false);
        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,
                                 int pos) {
        cursor.moveToPosition(pos);
        String contactName =
                cursor.getString(name_col_index);
        long contactId =
                cursor.getLong(id_col_index);
        Contact contact =
                new Contact(contactName);
        contact.setPhone(getPhoneNumber(String.valueOf(contactId)));
        contact.profilePic =
                ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId);
        holder.bind(contact);
    }
    private String getPhoneNumber(String id) {
        String number = "";
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone._ID +
                        " = " +
                        id,
                null,
                null);

        assert phones != null;
        if(phones.getCount() > 0)
        {
            while(phones.moveToNext())
            {
                number = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            System.out.println(number);
        }

        phones.close();
        return number;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;
        private TextView label, number;
        private CheckBox checkBox;


        ViewHolder(final View itemView) {
            super(itemView);
            imageView = (RoundedImageView)
                    itemView.findViewById(R.id.rounded_iv_profile);
            label = (TextView) itemView.
                    findViewById(R.id.tv_label);
            number = (TextView) itemView.findViewById(R.id.number_textView);
            checkBox = (CheckBox)
                    itemView.findViewById(R.id.contact_checkBox);
        }

        void bind(final Contact contact) {
            label.setText(contact.getName());
            number.setText(contact.getPhone());
            checkBox.setOnClickListener(
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        listener.onUnselected(contact);
                    }
                    else {
                        listener.onSelected(contact);
                    }
                }
            });
            String path = contact.profilePic.getPath();

            ImageLoader loader =
                    ImageLoader.getInstance();
            ImageLoaderConfiguration configuration =
                    ImageLoaderConfiguration.createDefault(
                            itemView.getContext());
            loader.init(
                    configuration);
            path = "file://" + path;

            loader.displayImage(path,
                    imageView,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri,
                                                     View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri,
                                                    View view,
                                                    FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view,
                                                      Bitmap loadedImage) {
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri,
                                                       View view) {
                        }
                    });


        }
    }
}
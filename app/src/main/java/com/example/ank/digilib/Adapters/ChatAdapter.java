package com.example.ank.digilib.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ank.digilib.Objects.Chat;
import com.example.ank.digilib.R;

import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by adityadesai on 22/10/17.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {
    public ChatAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chat_item, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        Chat message = getItem(position);

        messageTextView.setText(message.getText());
        nameTextView.setText(message.getName());

        return convertView;
    }
}

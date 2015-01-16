package me.cutmail.disasterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.cutmail.disasterapp.model.Entry;

public class EntriesAdapter extends ArrayAdapter<Entry> {

    private final LayoutInflater inflater;

    public EntriesAdapter(Context context, List<Entry> entries) {
        super(context, android.R.layout.simple_list_item_1, entries);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Entry entry = getItem(position);

        viewHolder.nameView.setText(entry.getTitle());

        return convertView;
    }

    public static class ViewHolder {
        @InjectView(android.R.id.text1)
        TextView nameView;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }
}

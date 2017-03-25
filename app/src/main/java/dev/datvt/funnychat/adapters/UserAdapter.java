package dev.datvt.funnychat.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.models.User;

/**
 * Created by datvt on 4/6/2016.
 */
public class UserAdapter extends BaseAdapter {

    private final List<User> userName;
    private Activity context;

    public UserAdapter(Activity context, List<User> userName) {
        this.context = context;
        this.userName = userName;
    }

    @Override
    public int getCount() {
        if (userName != null) {
            return userName.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (userName != null) {
            return userName.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userName.get(position);
        holder.tvUser.setText(user.getName());
        holder.time.setText(user.getId() + " - " + user.getEmail());
        holder.ivIcon.setImageResource(R.drawable.img_conan);

        return convertView;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvUser = (TextView) v.findViewById(R.id.tvNameUser);
        holder.time = (TextView) v.findViewById(R.id.timer);
        holder.ivIcon = (ImageView) v.findViewById(R.id.ivUser);
        return holder;
    }

    private static class ViewHolder {
        public TextView tvUser;
        public TextView time;
        public ImageView ivIcon;
    }
}

package dev.datvt.funnychat.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dev.datvt.funnychat.R;

/**
 * Created by datvt on 4/24/2016.
 */
public class StatusAdapter extends BaseAdapter {

    private final List<String> status;
    private Activity context;
    private Typeface typeface;

    public StatusAdapter(Activity context, List<String> status) {
        this.context = context;
        this.status = status;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/rix_love_fool.ttf");
    }

    @Override
    public int getCount() {
        if (status != null) {
            return status.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (status != null) {
            return status.get(position);
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
            convertView = inflater.inflate(R.layout.item_status, null);
            holder = createViewHolder(convertView);
            holder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.like.setText((Integer.parseInt(holder.like.getText().toString()) + 1) + "");
                }
            });
            holder.ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.comment.setText((Integer.parseInt(holder.comment.getText().toString()) + 1) + "");
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String s = status.get(position);
        holder.tvStatus.setText(s);
        holder.tvStatus.setTypeface(typeface);
        holder.tvUser.setTypeface(typeface);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        holder.time.setText(sdf.format(new Date()));

        return convertView;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvUser = (TextView) v.findViewById(R.id.tvName);
        holder.tvStatus = (TextView) v.findViewById(R.id.status);
        holder.time = (TextView) v.findViewById(R.id.tvTimer);
        holder.comment = (TextView) v.findViewById(R.id.comment);
        holder.like = (TextView) v.findViewById(R.id.like);
        holder.ivIcon = (ImageView) v.findViewById(R.id.ivAva);
        holder.ivLike = (ImageView) v.findViewById(R.id.ivLike);
        holder.ivComment = (ImageView) v.findViewById(R.id.ivComment);
        return holder;
    }

    private static class ViewHolder {
        public TextView tvUser;
        public TextView tvStatus;
        public TextView time;
        public TextView comment;
        public TextView like;
        public ImageView ivIcon;
        public ImageView ivLike;
        public ImageView ivComment;
    }
}

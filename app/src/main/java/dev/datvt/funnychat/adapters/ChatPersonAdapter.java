package dev.datvt.funnychat.adapters;

/**
 * Created by datvt on 4/6/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dev.datvt.funnychat.R;
import dev.datvt.funnychat.controls.EmojiHandler;
import dev.datvt.funnychat.models.Message;

public class ChatPersonAdapter extends BaseAdapter {

    private final List<Message> chatMessages;
    private Activity context;
    private EmojiHandler emojiHandler = new EmojiHandler();

    public ChatPersonAdapter(Activity context, List<Message> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Message chatMessage = (Message) getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.item_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.isMe();
        setAlignment(holder, myMsg, chatMessage);
        holder.ivPhoto.setImageBitmap(chatMessage.getImage());
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emojiHandler.getSmiledText(context, s);
            }
        });
        holder.tvUser.setText(chatMessage.getName());
        holder.tvTime.setText(chatMessage.getDateTime());
        if (myMsg) {
            holder.ivAvatar.setImageResource(R.drawable.ic_shin);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_ran);
        }

        return convertView;
    }

    private void setAlignment(ViewHolder holder, boolean isMe, Message message) {
        if (isMe) {
            if (message.getImage() == null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.chat_bubble_me);
            } else {
                holder.contentWithBG.setBackgroundResource(R.drawable.border_info);
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.tvTime.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.tvTime.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.ivPhoto.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.ivPhoto.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.tvUser.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.tvUser.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.ivAvatar.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.ivAvatar.setLayoutParams(layoutParams);
        } else {
            if (message.getImage() == null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.chat_bubble_you);
            } else {
                holder.contentWithBG.setBackgroundResource(R.drawable.border_info);
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.tvTime.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.tvTime.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.ivPhoto.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.ivPhoto.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.tvUser.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.tvUser.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.ivAvatar.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.ivAvatar.setLayoutParams(layoutParams);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.tvUser = (TextView) v.findViewById(R.id.tvUser);
        holder.tvTime = (TextView) v.findViewById(R.id.tvTime);
        holder.ivAvatar = (ImageView) v.findViewById(R.id.ivAvatar);
        holder.ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);

        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView tvUser;
        public TextView tvTime;
        public ImageView ivAvatar;
        public ImageView ivPhoto;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }

}

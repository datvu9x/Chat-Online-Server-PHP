package dev.datvt.funnychat.controls;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;

import dev.datvt.funnychat.R;

/**
 * Created by datvt on 5/9/2016.
 */
public class EmojiHandler {

    public static final HashMap<String, Integer> emoticons = new HashMap<String, Integer>();

    public EmojiHandler() {
        emoticons.put(":D ", R.drawable.big_smile);
        emoticons.put(":) ", R.drawable.smile);
        emoticons.put(":( ", R.drawable.frown);
        emoticons.put(":'( ", R.drawable.cry);
        emoticons.put(":P ", R.drawable.tongue);
        emoticons.put("O:) ", R.drawable.angel);
        emoticons.put("3:) ", R.drawable.devil);
        emoticons.put("o.O ", R.drawable.confused);
        emoticons.put(";) ", R.drawable.wink);
        emoticons.put(":O ", R.drawable.surprised);
        emoticons.put("-_- ", R.drawable.squinting);
        emoticons.put(">:O ", R.drawable.angry);
        emoticons.put(":* ", R.drawable.kiss);
        emoticons.put("<3 ", R.drawable.heart);
        emoticons.put("^_^ ", R.drawable.kiki);
        emoticons.put("8-) ", R.drawable.glasses);
        emoticons.put("8| ", R.drawable.sunglasses);
        emoticons.put("(^^^) ", R.drawable.shark);
        emoticons.put(":|] ", R.drawable.robot);
        emoticons.put(">:( ", R.drawable.grumpy);
        emoticons.put(":v ", R.drawable.pacman);
        emoticons.put(":3 ", R.drawable.curly_lips);
        emoticons.put(":/ ", R.drawable.unsure);
        emoticons.put(":B ", R.drawable.blush);
        emoticons.put("!(y) ", R.drawable.dislike);
        emoticons.put("(y) ", R.drawable.like);
        emoticons.put(":poop: ", R.drawable.poop);
        emoticons.put(":putnam: ", R.drawable.putnam);
        emoticons.put("<('') ", R.drawable.penguine);
        emoticons.put(":peace: ", R.drawable.peace_fingers);
        emoticons.put(":ok: ", R.drawable.ok);
        emoticons.put(":muscle: ", R.drawable.muscle_arm);
        emoticons.put(":d ", R.drawable.triumph);
        emoticons.put("O.O ", R.drawable.terrified_with_fear);
        emoticons.put(":e ", R.drawable.satisfied_smiley_face);
        emoticons.put(":g ", R.drawable.red_angry);
        emoticons.put(":kiss: ", R.drawable.kiss_1);
        emoticons.put(":cum: ", R.drawable.medic);
        emoticons.put(":venh: ", R.drawable.smirking_smiley);
        emoticons.put(":happy: ", R.drawable.happy_smiley_blushing);
        emoticons.put(":sun: ", R.drawable.sun);
        emoticons.put(":cry_happy: ", R.drawable.crying_tears_of_joy);
        emoticons.put(":broken_heart: ", R.drawable.broken_heart);
    }

    public static Spannable getSmiledText(Context context, Editable builder) {
        int index;
        for (index = 0; index < builder.length(); index++) {
            for (Map.Entry<String, Integer> entry : emoticons.entrySet()) {
                int length = entry.getKey().length();
                if (index + length > builder.length())
                    continue;
                if (builder.subSequence(index, index + length).toString().equals(entry.getKey())) {
                    builder.setSpan(new ImageSpan(context, entry.getValue()), index, index + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += length - 1;
                    break;
                }
            }
        }
        return builder;
    }
}

package com.zuzhili.draftbox;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zuzhili.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fanruikang on 14-10-15.
 */
public class DraftBoxAdapter extends ResourceCursorAdapter {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private IContentRenderer textRenderer = new TextContentRenderer();
    private IContentRenderer imageRenderer = new ImageContentRenderer();

    public DraftBoxAdapter(Context context, Cursor c) {
        super(context, R.layout.draft_box_list_item, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView iconView = (ImageView) view.findViewById(R.id.iv_type);
        TextView timeView = (TextView) view.findViewById(R.id.tv_update_time);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);

        String type = cursor.getString(cursor.getColumnIndex(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE));
        iconView.setImageResource(type.equals(DraftContract.Draft.CONTENT_TYPE_TEXT) ? R.drawable.draft_type_text : type.equals(DraftContract.Draft.CONTENT_TYPE_PICTURE) ? R.drawable.draft_type_image : type.equals(DraftContract.Draft.CONTENT_TYPE_COMMENT) ? R.drawable.draft_type_comment : 0);

        timeView.setText(format.format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(DraftContract.Draft.COLUMN_NAME_UPDATE_TIME))))));

        String sourceContent = cursor.getString(cursor.getColumnIndex(DraftContract.Draft.COLUMN_NAME_CONTENT));
        JSONObject obj = JSON.parseObject(sourceContent);
        String text = "";
        if (type.equals(DraftContract.Draft.CONTENT_TYPE_TEXT)) {
            text = textRenderer.getContent(obj);
        }else if (type.equals(DraftContract.Draft.CONTENT_TYPE_PICTURE)) {
            text = imageRenderer.getContent(obj);
        }else if (type.equals(DraftContract.Draft.CONTENT_TYPE_COMMENT)) {
            text = textRenderer.getContent(obj);
        }
        contentView.setText(text);
    }

    interface IContentRenderer {
        String getContent(JSONObject obj);
    }

    class TextContentRenderer implements IContentRenderer {
        @Override
        public String getContent(JSONObject obj) {
            return obj.get("content").toString();
        }
    }

    class ImageContentRenderer implements IContentRenderer {
        @Override
        public String getContent(JSONObject obj) {
            JSONObject object = (JSONObject) obj.get("content");
            String value = (String) object.get("value");
            return value;
        }
    }

}

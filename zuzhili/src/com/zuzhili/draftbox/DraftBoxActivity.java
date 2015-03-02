package com.zuzhili.draftbox;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.comment.CommentEditActivity;
import com.zuzhili.ui.activity.publish.PublishImageActivity;
import com.zuzhili.ui.activity.publish.PublishWriteActivity;


public class DraftBoxActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener, BaseActivity.TimeToShowActionBarCallback, AdapterView.OnItemClickListener {

    public static final String TAG = "DraftBoxActivity";
    public static final String DRAFTBOX_CONTENT = "draft_box.params";
    private CursorAdapter cursorAdapter;
    private ListView draftList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_box);
        draftList = (ListView) findViewById(R.id.lv_draft_box);
        setCustomActionBarCallback(this);
        cursorAdapter = new DraftBoxAdapter(this, null);
        draftList.setAdapter(cursorAdapter);
        draftList.setOnItemLongClickListener(this);
        draftList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String listId = mSession.getListid();
        String ids = mSession.getIds();
        String selection = "(" + DraftContract.Draft.COLUMN_NAME_LIST_ID + " = ? AND " + DraftContract.Draft.COLUMN_NAME_IDS + " = ? )";
        String[] selectionArgs = new String[]{listId, ids};
        CursorLoader loader = new CursorLoader(this, DraftContract.Draft.CONTENT_URI, null, selection, selectionArgs, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String contentType = cursor.getString(cursor.getColumnIndex(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE));
        String content = cursor.getString(cursor.getColumnIndex(DraftContract.Draft.COLUMN_NAME_CONTENT));

        Intent intent = null;
        if (contentType.equals(DraftContract.Draft.CONTENT_TYPE_TEXT)) {
            intent = new Intent(this, PublishWriteActivity.class);
        } else if (contentType.equals(DraftContract.Draft.CONTENT_TYPE_PICTURE)) {
            intent = new Intent(this, PublishImageActivity.class);
        } else if (contentType.equals(DraftContract.Draft.CONTENT_TYPE_COMMENT)) {
            intent = new Intent(this, CommentEditActivity.class);
            intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
            String action = (String) JSON.parseObject(content).get("action");
            intent.putExtra(Constants.ACTION, action);
        }

        intent.putExtra(DRAFTBOX_CONTENT, content);
        intent.putExtra("draft_id", cursor.getString(cursor.getColumnIndex(DraftContract.Draft._ID)));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除确认").setMessage("确实要删除草稿吗？").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int _ID = cursor.getInt(cursor.getColumnIndex(DraftContract.Draft._ID));
                Uri uri = ContentUris.withAppendedId(DraftContract.Draft.CONTENT_ID_URI_BASE, _ID);
                getContentResolver().delete(uri, null, null);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Deletion was cancelled.");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.empty_draft_box, "草稿箱", false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return true;
    }

    @Override
    public boolean performClickOnRight() {
        if (cursorAdapter.getCursor().getCount() < 1) {
            Toast.makeText(this, "草稿箱已空", Toast.LENGTH_SHORT).show();
            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清空确认").setMessage("确实要清空草稿箱吗？").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(DraftContract.Draft.CONTENT_URI, null, null);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Deletion was cancelled.");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

}

package com.kos.ktodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.kos.ktodo.DBHelper.*;

public class TagsStorage {
	private static final String TAG = "TagsStorage";

	private SQLiteDatabase db;
	private DBHelper helper;

	public TagsStorage(final Context context) {
		helper = new DBHelper(context);
	}

	public void open() {
		db = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public long addTag(final String tag) {
		final ContentValues cv = new ContentValues();
		cv.put(TAG_TAG, tag);
		return db.insert(TAG_TABLE_NAME, null, cv);
	}

	public boolean renameTag(final String oldName, final String newName) {
		final ContentValues cv = new ContentValues();
		cv.put(TAG_TAG, newName);
		return db.update(TAG_TABLE_NAME, cv, TAG_TAG + "=" + oldName, null) > 0;
	}

	public boolean renameTag(final long id, final String newName) {
		final ContentValues cv = new ContentValues();
		cv.put(TAG_TAG, newName);
		return db.update(TAG_TABLE_NAME, cv, TAG_ID + "=" + id, null) > 0;
	}

//	public boolean deleteTag(final String tag) {
//		return db.delete(TABLE_NAME, TAG_NAME + "=" + tag, null) > 0;
//	}

	public boolean deleteTag(final long id) {
		return db.delete(TAG_TABLE_NAME, TAG_ID + "=" + id, null) > 0;
	}

	public Cursor getAllTagsCursor() {
		return db.query(TAG_TABLE_NAME, new String[]{TAG_ID, TAG_TAG}, null, null, null, null, TAG_ORDER + " ASC");
	}

	public Cursor getAllTagsExceptCursor(final long... except) {
		final StringBuilder where = new StringBuilder();
		for (final long e : except) {
			if (where.length() != 0)
				where.append(" AND ");
			where.append(TAG_ID).append("<>").append(e);
		}
		return db.query(TAG_TABLE_NAME, new String[]{TAG_ID, TAG_TAG},
				where.toString(), null, null, null, TAG_ORDER + " ASC");
	}

	public boolean hasTag(final String tag) {
		final Cursor cursor = db.query(TAG_TABLE_NAME, new String[]{TAG_ID}, TAG_TAG + "=\"" + tag + "\"", null, null, null, null);
		final boolean res = cursor.getCount() > 0;
		cursor.close();
		return res;
	}

	public String getTag(final long id) {
		final Cursor cursor = db.query(TAG_TABLE_NAME, new String[]{TAG_TAG}, TAG_ID + "=" + id, null, null, null, null);
		final String res = cursor.moveToFirst() ? cursor.getString(0) : null;
		cursor.close();
		return res;
	}
}

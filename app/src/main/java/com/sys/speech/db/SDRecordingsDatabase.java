package com.sys.speech.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.sys.speech.pojo.RecordingItem;

import java.util.ArrayList;
import java.util.List;

public class SDRecordingsDatabase extends SQLiteOpenHelper {
    private final static boolean USE_MEMDB = true; //if delay saved?

    private final static boolean USE_CACHE_ONLY_TEST = false;
    private static List<RecordingItem> cache = new ArrayList<>();
    private static final String TAG = "SDRDb";

	private Context mContext;

	public static final String DATABASE_NAME = "recordings.db";
	private static final int DATABASE_VERSION = 1;

	public static abstract class RecordingDatabaseItem implements BaseColumns {
		public static final String TABLE_NAME = "recordings";

		public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
		public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
		public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
		public static final String COLUMN_NAME_TIME_ADDED = "time_added";
		public static final String COLUMN_NAME_REC_TYPE = "recType";
		public static final String COLUMN_NAME_REC_CONTENT = "recContent";
		public static final String COLUMN_NAME_MEETING_ID = "meetingId";
		public static final String COLUMN_NAME_AGENDA_ID = "agendaId";
	}

	public interface OnDatabaseChangedListener {
		void onDatabaseEntryUpdated();
	}

	private OnDatabaseChangedListener mOnDatabaseChangedListener;

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + RecordingDatabaseItem.TABLE_NAME + " (" +
					RecordingDatabaseItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_REC_TYPE + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + TEXT_TYPE + COMMA_SEP +
					RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID + TEXT_TYPE +
					")";

	@SuppressWarnings("unused")
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RecordingDatabaseItem.TABLE_NAME;

	public SDRecordingsDatabase(Context context, String dirPath) {
		super(new CustomPathDatabaseContext(context, dirPath), DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
        if (USE_MEMDB) {
            loadMemDB();
        }
	}
    private List<RecordingItem> memdb = new ArrayList<>();
    private List<RecordingItem> memdbAppend = new ArrayList<>();
    @SuppressLint("Range")
    private void loadMemDB() {
        try {
            SQLiteDatabase db = getReadableDatabase();

            String[] projection = {
                    RecordingDatabaseItem._ID,
                    RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME,
                    RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH,
                    RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH,
                    RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED,
                    RecordingDatabaseItem.COLUMN_NAME_REC_TYPE,
                    RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT,
                    RecordingDatabaseItem.COLUMN_NAME_MEETING_ID,
                    RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID
            };

            String orderBy = RecordingDatabaseItem._ID + " ASC";
            //too slow
            Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection,
                    RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + " = ?",
                    new String[]{""},
                    null, null, orderBy);
            if (c.moveToFirst()) {
                do {
                    RecordingItem item = new RecordingItem();
                    item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
                    item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
                    item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
                    item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
                    item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
                    item.setRecType(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE)));
                    item.setRecContent(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT)));
                    item.setMeetingId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID)));
                    item.setAgendaId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID)));
                    memdb.add(item);
                } while (c.moveToNext());
            }
            c.close();
            db.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

	public long addRecording(String recordingName, String filePath, long length, String meetingId, String agendaId, String recType, String recContent) {
        if (USE_CACHE_ONLY_TEST) {
            RecordingItem item = new RecordingItem();
            item.setId(cache.size());
            item.setName(recordingName);
            item.setFilePath(filePath);
            item.setLength((int) length);
            item.setMeetingId(meetingId);
            item.setAgendaId(agendaId);
            item.setRecType(recType);
            item.setRecContent(recContent);
            cache.add(item);
            return cache.size() - 1;
        } else if (USE_MEMDB) {
            RecordingItem item = new RecordingItem();
            //item.setId(this.memdb.size());
            item.setName(recordingName);
            item.setFilePath(filePath);
            item.setLength((int) length);
            item.setMeetingId(meetingId);
            item.setAgendaId(agendaId);
            item.setRecType(recType);
            item.setRecContent(recContent);
            this.memdb.add(item);
            this.memdbAppend.add(item);
            return this.memdb.size() - 1;
        } else {
            try {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH, length);
                values.put(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
                values.put(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE, recType);
                values.put(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT, recContent);
                values.put(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID, meetingId);
                values.put(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID, agendaId);

                long rowId = db.insert(RecordingDatabaseItem.TABLE_NAME, null, values);

                Log.e(TAG, "==============addRecording meetingId = " + meetingId + ", agendaId = " + agendaId);
                db.close();
                if (mOnDatabaseChangedListener != null)
                    mOnDatabaseChangedListener.onDatabaseEntryUpdated();

                return rowId;
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            return -1;
        }
    }

    public void saveAll() {
        if (this.memdbAppend != null) {
            saveRecording(this.memdbAppend);
        }
    }
    public void saveRecording(List<RecordingItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        try{
            db.beginTransaction();
            //insert huge data
            //get pre-compiled SQLiteStatement object
            //SQLiteStatement statement = db.compileStatement("insert into tablename(..) value (?,?)")
            for (RecordingItem row : items) {
                ContentValues values = new ContentValues();
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, row.getName());
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, row.getFilePath());
                values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH, row.getLength());
                values.put(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
                values.put(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE, row.getRecType());
                values.put(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT, row.getRecContent());
                values.put(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID, row.getMeetingId());
                values.put(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID, row.getAgendaId());

                db.insert(RecordingDatabaseItem.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally{
            db.endTransaction();
            db.close();
        }

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryUpdated();
    }

    @SuppressLint("Range")
    public RecordingItem getItemAt(int position, String meetingId, String agendaId) {
        if (USE_CACHE_ONLY_TEST) {
            if (position >= 0 && position < cache.size()) {
                return cache.get(position);
            }
            return null;
        } else if (USE_MEMDB) {
            if (position >= 0 && position < this.memdb.size()) {
                return this.memdb.get(position);
            }
            return null;
        } else {
            try {
                SQLiteDatabase db = getReadableDatabase();

                String[] projection = {
                        RecordingDatabaseItem._ID,
                        RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME,
                        RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH,
                        RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH,
                        RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED,
                        RecordingDatabaseItem.COLUMN_NAME_REC_TYPE,
                        RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT,
                        RecordingDatabaseItem.COLUMN_NAME_MEETING_ID,
                        RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID
                };

                String orderBy = RecordingDatabaseItem._ID + " ASC";
                if (false) {
                    //too slow
                    Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection,
                            RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + " = ?",
                            new String[]{meetingId},
                            null, null, orderBy);
                    if (c.moveToPosition(position)) {
                        RecordingItem item = new RecordingItem();
                        item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
                        item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
                        item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
                        item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
                        item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
                        item.setRecType(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE)));
                        item.setRecContent(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT)));
                        item.setMeetingId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID)));
                        item.setAgendaId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID)));
                        c.close();
                        return item;
                    }
                } else {
                    Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection,
                            RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + " = ? and _id=? ",
                            new String[]{meetingId, String.valueOf(position)},
                            null, null, orderBy);
                    if (c.moveToPosition(0)) {
                        RecordingItem item = new RecordingItem();
                        item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
                        item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
                        item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
                        item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
                        item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
                        item.setRecType(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE)));
                        item.setRecContent(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT)));
                        item.setMeetingId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID)));
                        item.setAgendaId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID)));
                        c.close();
                        return item;
                    }
                }
                db.close();
            } catch (Throwable eee) {
                eee.printStackTrace();
            } finally {
            }

            return null;
        }
	}

	public void removeItemWithId(int id, String meetingId, String agendaId) {
        if (USE_CACHE_ONLY_TEST) {
            //skip
        } else if (USE_MEMDB) {
            //skip
        } else {
            try {
                SQLiteDatabase db = getWritableDatabase();
                String[] whereArgs = {String.valueOf(id), meetingId};
                db.delete(RecordingDatabaseItem.TABLE_NAME,
                        "_id=? and " + RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + " = ?",
                        whereArgs);
                db.close();
                if (mOnDatabaseChangedListener != null)
                    mOnDatabaseChangedListener.onDatabaseEntryUpdated();
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
	}

	public int getCount(String meetingId, String agendaId) {
        if (USE_CACHE_ONLY_TEST) {
            return cache.size();
        } else if (USE_MEMDB) {
            return this.memdb.size();
        } else {
            try {
                SQLiteDatabase db = getReadableDatabase();
                String[] projection = {RecordingDatabaseItem._ID};
                Cursor c = db.query(RecordingDatabaseItem.TABLE_NAME, projection,
                        RecordingDatabaseItem.COLUMN_NAME_MEETING_ID + " = ?",
                        new String[]{meetingId},
                        null, null, null);
                int count = 0;
                if (false) {
                    count = c.getCount();
                } else {
                    if (c.moveToFirst()) {
                        do {
//                            RecordingItem item = new RecordingItem();
//                            item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
//                            item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
//                            item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
//                            item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
//                            item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
//                            item.setRecType(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE)));
//                            item.setRecContent(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT)));
//                            item.setMeetingId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID)));
//                            item.setAgendaId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID)));
//                            memdb.add(item);
                            count++;
                        } while (c.moveToNext());
                    }
                }
                c.close();
                db.close();
                return count;
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            return 0;
        }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade(db, oldVersion, newVersion);
	}

	public Context getContext() {
		return mContext;
	}

//	public class RecordingComparator implements Comparator<RecordingItem> {
//		public int compare(RecordingItem item1, RecordingItem item2) {
//			Long o1 = item1.getTime();
//			Long o2 = item2.getTime();
//			return o2.compareTo(o1);
//		}
//	}

//	public void renameItem(RecordingItem item, String recordingName) {
//		SQLiteDatabase db = getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
//		db.update(RecordingDatabaseItem.TABLE_NAME, values,
//				RecordingDatabaseItem._ID + "=" + item.getId(), null);
//
//		if (mOnDatabaseChangedListener != null)
//			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
//	}

	public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
		mOnDatabaseChangedListener = listener;
	}

    public void updateItemContent(long id, String content, boolean isAppend) {
        if (USE_CACHE_ONLY_TEST) {
            RecordingItem item = null;
            if (id >= 0 && id < cache.size()) {
                item = cache.get((int)id);
            }
            if (item != null && content != null) {
                if (isAppend) {
                    item.setRecContent(item.getRecContent() != null ? (item.getRecContent() + content) : content);
                } else {
                    item.setRecContent(content);
                }
            }
        } else if (USE_MEMDB) {
            RecordingItem item = null;
            if (id >= 0 && id < this.memdb.size()) {
                item = this.memdb.get((int)id);
            }
            if (item != null && content != null) {
                if (isAppend) {
                    item.setRecContent(item.getRecContent() != null ? (item.getRecContent() + content) : content);
                } else {
                    item.setRecContent(content);
                }
            }
        } else {
            try {
                if (isAppend) {
                    RecordingItem item = getItemAt((int)id, "", "");
                    if (item != null) {
                        String oldContent = item.getRecContent();
                        if (oldContent != null) {
                            content = oldContent + content;
                        }
                    }
                }

                SQLiteDatabase db = getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT, content);
                db.update(RecordingDatabaseItem.TABLE_NAME, values,
                        RecordingDatabaseItem._ID + "=" + id, null);
                db.close();

                if (mOnDatabaseChangedListener != null)
                    mOnDatabaseChangedListener.onDatabaseEntryUpdated();
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
	}
}

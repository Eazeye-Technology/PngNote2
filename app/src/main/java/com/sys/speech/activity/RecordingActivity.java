/**
 * 
 */
package com.sys.speech.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sys.speech.db.DictationsDatabase;
import com.sys.speech.db.SDRecordingsDatabase;
import com.sys.speech.db.SDRecordingsDatabase.OnDatabaseChangedListener;
import com.sys.speech.dialog.ListenDialog;
import com.sys.speech.dialog.PlayerDialog;
import com.sys.speech.dialog.RecognizeDialog;
import com.sys.speech.pojo.RecordingItem;
import com.sys.speech.service.RecordingService;
import com.sys.speech.service.RecordingService.OnAudioLevelChangedListener;
import com.sys.speech.service.RecordingService.OnTimerChangedListener;
import com.sys.speech.util.RecordingMode;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.txkj.drawingapp.R;

/**
 *
 */
public class RecordingActivity extends Activity implements OnItemClickListener, OnAudioLevelChangedListener, OnTimerChangedListener, OnItemLongClickListener {
	private static final boolean D = true;
	private static final String TAG = "RecordingActivity";
	
	public static final String EXTRA_MEETING_ID = "EXTRA_MEETING_ID";
	public static final String EXTRA_AGENDA_ID = "EXTRA_AGENDA_ID";
	
	private String meetingId = "";
	private String agendaId = "";
	
	//{
	private static final int REQUEST_CODE_SETTINGS = 0;

	private RecordingService mRecordingService;

	private boolean mRecordingQueued = false;
	private boolean mIsBound = false;
	//}
	
	
	private ListView viewListView;
	private ReaderItemsAdapter adapter;
	private TextView timer_textview;
	private TextView state_view;
	private TextView filename_textview;
	private TextView bars;
	private Button button1; 
	private Button button2; 
	private MediaPlayer mPlayer;
	
	private final static class RetainInfo {
		public List<String> items;
		public List<String> itemInfos1;	
		public List<String> itemInfos2;	
	}
	
	private final static class ReaderItemsAdapter extends BaseAdapter implements OnDatabaseChangedListener {
		private LayoutInflater mInflater;

		private Context mContext;
		private SDRecordingsDatabase mDatabase;
		private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());		
		
		private String mMeetingId;
		private String mAgendaId;
		
		public ReaderItemsAdapter(Context context, String meetingId, String agendaId) {
			this.mInflater = LayoutInflater.from(context);
			mContext = context;
			mDatabase = new SDRecordingsDatabase(context, null);
			mDatabase.setOnDatabaseChangedListener(this);
			mMeetingId = meetingId;
			mAgendaId = agendaId;
		}
		
		@Override
		public int getCount() {
			if (mDatabase != null) {
				return mDatabase.getCount(this.mMeetingId, this.mAgendaId);
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return mDatabase.getItemAt(position, this.mMeetingId, this.mAgendaId);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder;
            if (convertView == null) {
				convertView = mInflater.inflate(R.layout.speech__list_item_recording, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.date = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
    		RecordingItem item = (RecordingItem)getItem(position);
            if (item != null) {
            	if (item.getRecType() != null && item.getRecType().equals("text")) {
            		holder.title.setText(item.getRecContent());
                } else {
            		holder.title.setText(item.getName());
            	}
            	holder.date.setText(getTime(item.getTime()));
        		//lengthView.setText(getLengthString(item.getLength()));
        	} else {
            	holder.title.setText("");
            }
            return convertView;
		}
		
		public static String getTime(long milliSeconds) {
			Date date = new Date(milliSeconds);
			return mDateAddedFormatter.format(date);
		}
		
        private static final class ViewHolder {
        	TextView title;
        	TextView date;
        }

		@Override
		public void onDatabaseEntryUpdated() {
			this.notifyDataSetChanged();
		}
		
		public void remove(RecordingItem item) {
			mDatabase.removeItemWithId(item.getId(), this.mMeetingId, this.mAgendaId);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.speech__activity_recording); //list_reader
//		this.getActionBar().setTitle("准备录音");
		
		Intent intent = this.getIntent();
		if (intent != null) {
			this.meetingId = intent.getStringExtra(EXTRA_MEETING_ID);
			this.agendaId = intent.getStringExtra(EXTRA_AGENDA_ID);
		}
		if (this.meetingId == null) {
			this.meetingId = "";
		}
		if (this.agendaId == null) {
			this.agendaId = "";
		}
		
		viewListView = (ListView) this.findViewById(R.id.viewListView);
		timer_textview = (TextView) this.findViewById(R.id.timer_textview);
		state_view = (TextView) this.findViewById(R.id.state_view);
		filename_textview = (TextView) this.findViewById(R.id.filename_textview);
		bars = (TextView) this.findViewById(R.id.bars);
		button1 = (Button) this.findViewById(R.id.button1); 
		button2 = (Button) this.findViewById(R.id.button2); 
		
		RetainInfo info = (RetainInfo) this.getLastNonConfigurationInstance();
		if (info == null) {
			
		} else {
		
		}
		
		adapter = new ReaderItemsAdapter(this, this.meetingId, this.agendaId);
		viewListView.setAdapter(adapter);
		viewListView.setFastScrollEnabled(true);
		viewListView.setOnItemClickListener(this);
		viewListView.setOnItemLongClickListener(this);
		
		onCreate2(savedInstanceState);

        getPermission();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("alreadyStarted", true);
	}
	
    @Override
	public Object onRetainNonConfigurationInstance() {
    	RetainInfo info = new RetainInfo();
    	return info;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (D) {
			Log.d(TAG, "onItemClick " + position);
		}
		RecordingItem item = (RecordingItem)adapter.getItem(position);
		if (item != null) {
        	if (item.getRecType() != null && item.getRecType().equals("text")) {
        	
        	} else {
    			PlayerDialog playerDialog = new PlayerDialog(this, item);
    			playerDialog.show();
        	}

		}
	}
	
	//-----------------------------------------


	private BroadcastReceiver mStateChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (RecordingService.INTENT_RECORDING_STARTED.equals(intent.getAction())) {
				String filename = intent.getStringExtra("filename");
				state_view.setText("Recording");mRecordingMode=RecordingMode.RECORDING;//mRecordingStatusFragment.setRecordingMode(RecordingMode.RECORDING);
				filename_textview.setText(filename.replace(".pcm", "")); //mRecordingStatusFragment.setFileName(filename.replace(".pcm", ""));
				button1.setText("Recording"); //mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.RECORDING);
				//getActionBar().setTitle(R.string.state_recording);
				if (adapter != null) adapter.notifyDataSetChanged();
			} else if (RecordingService.INTENT_RECORDING_STOPPED.equals(intent.getAction())) {
				state_view.setText("Ready for Recording");mRecordingMode=RecordingMode.IDLE;//mRecordingStatusFragment.setRecordingMode(RecordingMode.IDLE);
				button1.setText("Ready for Recording"); //mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.IDLE);
				//getActionBar().setTitle(R.string.app_name);
				if (adapter != null) adapter.notifyDataSetChanged();
			}
		}
	};

	
	private RecordingMode mRecordingMode = RecordingMode.IDLE;
	public RecordingMode getRecordingMode() {
		return this.mRecordingMode;
	}
	private int mSeconds;
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
	public void setTimeFromSeconds(int seconds) {
		mSeconds = seconds;
		if (timer_textview != null)
			timer_textview.setText(mDateFormat.format(seconds*1000));
	}
	@Override
	public void onTimerChanged(final int seconds) {
		this.runOnUiThread(new Runnable() {	
			@Override
			public void run() {
				setTimeFromSeconds(seconds);
			}
		});
	}
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mRecordingService = ((RecordingService.ServiceBinder)service).getService();
			mRecordingService.setOnTimerChangedListener(RecordingActivity.this);
			mRecordingService.setOnAudioLevelChanged(RecordingActivity.this);
			
			mRecordingService.setMeetingId(RecordingActivity.this.meetingId);
			mRecordingService.setAgendaId(RecordingActivity.this.meetingId);
			
			if (mRecordingQueued) {
				mRecordingService.startRecording();
				mRecordingQueued = false;
			}

			if (mRecordingService.isRecording()) {
				state_view.setText("正在录音");mRecordingMode=RecordingMode.RECORDING;//mRecordingStatusFragment.setRecordingMode(RecordingMode.RECORDING);
				filename_textview.setText(mRecordingService.getFilename().replace(".pcm", "")); //mRecordingStatusFragment.setFileName(mRecordingService.getFilename().replace(".pcm", ""));
				button1.setText("正在录音"); //mRecordingControlsFragment.onRecordingStateChanged(RecordingMode.RECORDING);
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mRecordingService = null;
		}
	};

	private void doBindService() {
		bindService(new Intent(RecordingActivity.this, 
				RecordingService.class), mConnection, Context.BIND_AUTO_CREATE);

		mIsBound = true;
	}

	private void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	protected void onCreate2(Bundle savedInstanceState) {
		startService(new Intent(this, RecordingService.class));
		doBindService();

		if (savedInstanceState == null) {
			button1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (adapter != null) adapter.notifyDataSetChanged();
					
					RecordingMode mode;
					if (mRecordingService == null)
						mode = getRecordingMode();
					else
						mode = mRecordingService.getRecordingMode();
					switch (mode) {
					case IDLE:
						//if (mRecordingStatusFragment != null) {
						//	mRecordingStatusFragment.setTimeFromSeconds(0);
						//	mRecordingStatusFragment.clearAudioBars();
						//}
						if (RecordingActivity.this.timer_textview != null) {
							RecordingActivity.this.setTimeFromSeconds(0);
						}
						if (RecordingActivity.this.bars != null) {
							RecordingActivity.this.bars.setText("");
						}
						MediaPlayer openPlayer = MediaPlayer.create(getApplicationContext(), R.raw.speech__open);
						if (openPlayer == null) {
							startRecording();
						} else {
							openPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mp) {
									startRecording();
								}
							});
							openPlayer.start();
						}
						break;
					case RECORDING:
					default:
						mRecordingService.stopRecording();
						MediaPlayer successPlayer = MediaPlayer.create(getApplicationContext(), R.raw.speech__success);
						if (successPlayer != null) {
							successPlayer.start();
						}
						break;
					}
				}
			});
			
			
			button2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ListenDialog dialog = new ListenDialog(RecordingActivity.this, RecordingActivity.this.meetingId, RecordingActivity.this.agendaId, adapter);
					dialog.show();
				}
			});
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void startRecording() {
		if (mRecordingService != null)
			mRecordingService.startRecording();
		else
			mRecordingQueued = true;
	}

	@Override
	protected void onPause() {
        try {
            unregisterReceiver(mStateChangedReceiver);
            doUnbindService();
            if (mRecordingService != null && mRecordingService.getRecordingMode() == RecordingMode.IDLE) {
                mRecordingService.stopSelf();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
		super.onPause();
	}

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
	protected void onResume() {
		super.onResume();
        try {
            IntentFilter iF = new IntentFilter();
            iF.addAction(RecordingService.INTENT_RECORDING_STARTED);
            iF.addAction(RecordingService.INTENT_RECORDING_STOPPED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(mStateChangedReceiver, iF, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(mStateChangedReceiver, iF); //FIXME:
            }

            if (mRecordingService == null)
                return;

            //if (mRecordingStatusFragment != null)
            //	mRecordingStatusFragment.setRecordingMode(mRecordingService.getRecordingMode());
            if (state_view != null) {
                state_view.setText(mRecordingService.getRecordingMode() == RecordingMode.RECORDING ? "正在录音" : "准备录音");mRecordingMode=mRecordingService.getRecordingMode();
            }

            //if (mRecordingControlsFragment != null)
            //	mRecordingControlsFragment.onRecordingStateChanged(mRecordingService.getRecordingMode());
            if (this.button1 != null) {
                this.button1.setText(mRecordingService.getRecordingMode() == RecordingMode.RECORDING ? "正在录音" : "准备录音");
            }

    //		if (mRecordingService.getRecordingMode() == RecordingMode.IDLE)
    //			getActionBar().setTitle("准备录音");
    //		else if (mRecordingService.getRecordingMode() == RecordingMode.RECORDING)
    //			getActionBar().setTitle("正在录音");
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
	}

	@Override
	public void onAudioLevelChanged(final int percentage) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//if (mRecordingControlsFragment != null)
				//	mRecordingControlsFragment.onAudioLevelChanged(percentage);
				
				
				//if (mRecordingStatusFragment != null)
				//	mRecordingStatusFragment.onAudioLevelChanged(percentage);
				RecordingActivity.this.bars.setText("AudioLevel:" + percentage);
			}
		});
	}

	public void setPrettyName(String string) {
		mRecordingService.setNextPrettyRecordingName(string);
		this.filename_textview.setText(string); //mRecordingStatusFragment.setFileName(string);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final RecordingItem item = (RecordingItem)adapter.getItem(position);
    	if (item.getRecType() != null 
    			&& item.getRecType().equals("text")) {
    		builder.setTitle("Sync recognition result")
    		.setItems(new String[] {
    			"Delete", //0
    		}, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if (item != null) {
    					switch (which) {
    					case 0:
    						adapter.remove(item);
    						break;						
    					}
    				}
    			}
    		});
    	} else {
    		builder.setTitle(item.getName())
    		.setItems(new String[] {
    			"History", //0
				"To Mandarin", //1
				"To Cantonese", //2
				"To English", //3
    			"", //4
    			"----", //5
    			"Clear repeat", //6
    			"Clear History", //7
    			"Delete", //8
    		}, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if (item != null) {
    					switch (which) {
    					case 0:
    						startActivity(new Intent(RecordingActivity.this, DictResultActivity.class).putExtra(DictResultActivity.EXTRA_RECORDING_ID, item.getId()));						
    						break;
    						
    					case 1: {
    							//Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
    							RecognizeDialog recogizeDialog = new RecognizeDialog(RecordingActivity.this, item, RecognizeDialog.LANG_CHINESE);
    							recogizeDialog.show();
    						}
    						break;
    					
    					case 2: {
    							//Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
    							RecognizeDialog recogizeDialog = new RecognizeDialog(RecordingActivity.this, item, RecognizeDialog.LANG_CHINESE_GD);
    							recogizeDialog.show();
    						}
    						break;
    						
    					case 3: {
    							//Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
    							RecognizeDialog recogizeDialog = new RecognizeDialog(RecordingActivity.this, item, RecognizeDialog.LANG_ENGLISH);
    							recogizeDialog.show();
    						}
    						break;

    					case 6:
    						clearHistoryRepeat(item);
    						break;
    					
    					case 7:
    						clearHistory(item);
    						break;
    						
    					case 8:
    						adapter.remove(item);
    						break;						
    					}
    				}
    			}
    		});
    	}		
		builder.show();
		
		return true;
	}
	
	public void clearHistoryRepeat(RecordingItem item) {
		DictationsDatabase mDatabase = new DictationsDatabase(this);
		mDatabase.removeItemShort(item.getId());
		mDatabase.close();
	}
	
	private void clearHistory(RecordingItem item) {
		DictationsDatabase mDatabase = new DictationsDatabase(this);
		mDatabase.removeAllItems();
		mDatabase.close();
	}


    private final static int REQUEST_CODE = 1111;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // 权限已授予
                } else {
                    // 权限未授予
                }
            }
        }
    }

    private void getPermission2(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    private void getPermission() {
        List<String> permissions = new ArrayList<>();
        permissions.add("android.permission.RECORD_AUDIO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //SDK26, Android 8
            permissions.add("android.permission.FOREGROUND_SERVICE");
        }
        XXPermissions.with(this).permission(permissions).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG,"SDK获取系统权限成功:"+all);
                for(int i=0;i<granted.size();i++){
                    Log.d(TAG,"获取到的权限有："+granted.get(i));
                }
                if(all) {
                    Toast.makeText(RecordingActivity.this, "Get recording permission successfully", Toast.LENGTH_SHORT).show();
                    getPermission2();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(RecordingActivity.this, denied);
                }else{
                    Log.e(TAG,"onDenied:权限获取失败");
                }
            }
        });
    }
}

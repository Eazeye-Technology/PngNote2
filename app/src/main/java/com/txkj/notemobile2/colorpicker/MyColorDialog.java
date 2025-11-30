//package com.txkj.notemobile2.colorpicker;
//
//import org.ebookdroid2.activity.IActivityController;
//import org.ebookdroid2.manager.BookSettings;
//import org.ebookdroid2.model.DocumentModel;
//import org.ebookdroid2.page.Page;
//import org.ebookdroid2.task.SeekBarIncrementHandler;
//import org.mupdfdemo2.view.MuPDFPageView;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.ColorFilter;
//import android.graphics.Paint;
//import android.graphics.PixelFormat;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.Shader.TileMode;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//import com.artifex.mupdfdemo.MuPDFCore;
//import com.gzsys.mtmobile_yzyjh.R;
//
///**
// * FIXME:如果想制造崩溃，可在这里引用别的layout的id
// */
//public class MyColorDialog extends Dialog {
//	//FIXME: 不需要最大化了
//    private final static boolean MAX_DIALOG = false;
//
//    private final IActivityController base;
//    private final SeekBarIncrementHandler handler;
//    private int offset;
//
//    private RelativeLayout cbutton1, cbutton2, cbutton3, cbutton4, cbutton5;
//    private RelativeLayout cbutton6, cbutton7, cbutton8, cbutton9, cbutton10;
//
//    private int m_penColor;
//    private float m_penSize;
//    private final static int MIN_PEN_SIZE = MuPDFCore.PEN_SIZE_MIN; //FIXME:
//    private final static int MAX_PEN_SIZE = MuPDFCore.PEN_SIZE_MAX; //FIXME:
//    private float m_penScale;
//
//    public MyColorDialog(final IActivityController base, int penColor, float penSize, float penScale) {
//        super(base.getContext());
//        this.setContentView(R.layout.ebookdroid_mycolor);
//        this.base = base;
//        this.m_penColor = penColor;
//        this.m_penSize = penSize;
//        this.m_penScale = penScale;
//
//        final ImageView ivPenPreview = (ImageView) findViewById(R.id.ivPenPreview);
//        //ivPenPreview.setImageDrawable(new ColorDrawable(0xff000000 | m_penColor));
//        ivPenPreview.setImageDrawable(new CircleDrawable(0xff000000 | m_penColor));
//
//        this.handler = new SeekBarIncrementHandler();
//        final BookSettings bs = base.getBookSettings();
//        this.offset = (bs != null ? bs.firstPageOffset : 1);
//        this.setTitle("设置"/*"批注颜色与笔触大小设置"*/); //FIXME:这个标题
//
//        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
//        if (this.m_penSize >= MIN_PEN_SIZE && this.m_penSize <= MAX_PEN_SIZE) {
//        	seekbar.setProgress((int)this.m_penSize - MIN_PEN_SIZE);
//        }
//        final TextView tvPenSize = (TextView) findViewById(R.id.tvPenSize);
//        tvPenSize.setText("笔触大小：" + ((seekbar.getProgress() + MIN_PEN_SIZE)));
//
//        final EditText editText = (EditText) findViewById(R.id.pageNumberTextEdit);
//        this.findViewById(R.id.goToButton).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				goToPageAndDismiss();
//			}
//		});
//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent keyEvent) {
//                if ((actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE)) {
//                    if ((keyEvent == null ||
//                    	keyEvent.getAction() == KeyEvent.ACTION_UP)) {
//                        //FIXME:这里跳转未实现
//                    	//FIXME:R.id.actions_gotoPage
//                    	//IActionController.VIEW_PROPERTY=textView
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//        this.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				onDialogCancel();
//			}
//		});
//        this.findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                onDialogDel();
//            }
//        });
//        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(final SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStartTrackingTouch(final SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
//                updateControls(progress, false);
//            }
//        });
//        handler.init(new SeekBarIncrementHandler.DialogBridge(this), seekbar, R.id.seekbar_minus, R.id.seekbar_plus);
//
//
//
//        cbutton1 = (RelativeLayout) this.findViewById(R.id.cbutton1);
//        cbutton2 = (RelativeLayout) this.findViewById(R.id.cbutton2);
//        cbutton3 = (RelativeLayout) this.findViewById(R.id.cbutton3);
//        cbutton4 = (RelativeLayout) this.findViewById(R.id.cbutton4);
//        cbutton5 = (RelativeLayout) this.findViewById(R.id.cbutton5);
//        cbutton6 = (RelativeLayout) this.findViewById(R.id.cbutton6);
//        cbutton7 = (RelativeLayout) this.findViewById(R.id.cbutton7);
//        cbutton8 = (RelativeLayout) this.findViewById(R.id.cbutton8);
//        cbutton9 = (RelativeLayout) this.findViewById(R.id.cbutton9);
//        cbutton10 = (RelativeLayout) this.findViewById(R.id.cbutton10);
//        final RelativeLayout[] buttons = new RelativeLayout[] {
//        		cbutton1, cbutton2, cbutton3, cbutton4, cbutton5,
//        		cbutton6, cbutton7, cbutton8, cbutton9, cbutton10,
//        };
//        final int[] colors = new int[] {
//        		0x2c988c, 0x3073aa, 0x8b1a63, 0x706559, 0x282425,
//        		0xe2e2e2, 0xdb5832, 0x81b845, 0xff0000, 0x00ff00
//        };
//        for (int i = 0; i < buttons.length; ++i) {
//        	if (colors[i] == this.m_penColor) {
//        		buttons[i].setBackgroundResource(R.drawable.com_facebook_button_blue);
//        	}
//        	buttons[i].setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					int selectIndex = -1;
//					if (view == cbutton1) {
//						selectIndex = 0;
//					} else if (view == cbutton2) {
//						selectIndex = 1;
//					} else if (view == cbutton3) {
//						selectIndex = 2;
//					} else if (view == cbutton4) {
//						selectIndex = 3;
//					} else if (view == cbutton5) {
//						selectIndex = 4;
//					} else if (view == cbutton6) {
//						selectIndex = 5;
//					} else if (view == cbutton7) {
//						selectIndex = 6;
//					} else if (view == cbutton8) {
//						selectIndex = 7;
//					} else if (view == cbutton9) {
//						selectIndex = 8;
//					} else if (view == cbutton10) {
//						selectIndex = 9;
//					}
//					for (int i = 0; i < buttons.length; ++i) {
//						buttons[i].setBackgroundColor(0x00000000);
//					}
//					view.setBackgroundResource(R.drawable.com_facebook_button_blue);
//					if (base != null) {
//						base.setPenColor(colors[selectIndex]);
//
//						final ImageView ivPenPreview = (ImageView) findViewById(R.id.ivPenPreview);
//						//ivPenPreview.setImageDrawable(new ColorDrawable(0xff000000 | colors[selectIndex]));
//						ivPenPreview.setImageDrawable(new CircleDrawable(0xff000000 | colors[selectIndex]));
//					}
//				}
//        	});
//        }
//    }
//
//	@Override
//    protected void onStart() {
//        super.onStart();
//        //FIXME: 不需要最大化了
//        if (MAX_DIALOG) {
//        	maximizeWindow(getWindow());
//        }
//        final DocumentModel dm = base.getDocumentModel();
//        final Page lastPage = dm != null ? dm.getLastPageObject() : null;
//        final int current = (int)((this.m_penSize >= MIN_PEN_SIZE) ? (this.m_penSize - MIN_PEN_SIZE) : 0); //dm != null ? dm.getCurrentViewPageIndex() : 0;
//        final int max = MAX_PEN_SIZE - MIN_PEN_SIZE;//lastPage != null ? lastPage.index.viewIndex : 0;
//        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
//        seekbar.setMax(max);
//        updateControls(current, true);
//    }
//
//	public static void maximizeWindow(final Window window) {
//		window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//	}
//
//    @Override
//    protected void onStop() {
//
//    }
//
//    //FIXME:跳转按钮
//    //R.id.goToButton
//    public void goToPageAndDismiss() {
//        dismiss();
//    }
//
//    private void updateControls(final int viewIndex, final boolean updateBar) {
//        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
//        final TextView tvPenSize = (TextView) findViewById(R.id.tvPenSize);
//        tvPenSize.setText("笔触大小：" + ((viewIndex + MIN_PEN_SIZE)));
//        m_penSize = viewIndex + MIN_PEN_SIZE;
//        if (base != null) {
//        	base.setPenSize(m_penSize);
//        }
//        if (updateBar) {
//            seekbar.setProgress(viewIndex);
//        }
//
//        //https://blog.csdn.net/guozhaohui628/article/details/79245679
//        final ImageView ivPenPreview = (ImageView) findViewById(R.id.ivPenPreview);
//        float scale = m_penScale > 0 ? m_penScale: 10;
//        //LayoutParams params = new LayoutParams((int)(m_penSize * scale), (int)(m_penSize * scale));
//        LinearLayout.MarginLayoutParams mlp = (LinearLayout.MarginLayoutParams) ivPenPreview.getLayoutParams();
//        mlp.width = (int)(m_penSize * scale/* * 2*/); //FIXME: why not? * 2.0
//        mlp.height = (int)(m_penSize * scale/* * 2*/); //FIXME: why not? * 2.0
//        ivPenPreview.setLayoutParams(mlp);
//        ivPenPreview.requestLayout();
//    }
//
//    //FIXME:取消对话框
//    //R.id.btnCancel
//    public void onDialogCancel() {
//    	this.dismiss();
//    }
//    public void onDialogDel() {
//        this.dismiss();
//        if (base != null) {
//            //20220621:
//            base.changeToAnnotMode();
//        }
//    }
//
//
//    // https://blog.csdn.net/lepaitianshi/article/details/50560575
//    public class CircleDrawable extends ColorDrawable {
//        private final Paint mPaint = new Paint();
//
//        public CircleDrawable(int color) {
//        	super(color);
//        }
//
//        @Override
//        public void draw(Canvas canvas) {
//            mPaint.setColor(getColor());
//            mPaint.setAntiAlias(true);
//            Rect rect = getBounds();
//            canvas.drawCircle(rect.left + rect.width() / 2, rect.top + rect.width() / 2, rect.width() / 2, mPaint);
//        }
//    }
//}

package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.txkj.contentbrowser.NoteFragment2;
import com.txkj.contentbrowser.NoteFragment3;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;
import com.txkj.notemobile2.BookListFragment;
import com.txkj.notemobile2.ui.Page;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.MainActivity;

public class BookActivity4Utils {
    public final static boolean USE_STATIC_LAYOUT = true;
    public final static int STATIC_LAYOUT_WIDTH = 3000;
    public final static boolean USE_HTML_EDIT = true;
    public final static boolean SHOW_TRANSCRIPT_FIRST = false; //should be false

    public final static boolean USE_ACTIONBAR = false;
    public final static boolean USE_FRAGMENT = true;

    //FIXME: not good
    public static String APP_OPEN;
    public static String APP_FILE;

    public static void editText(Activity context, DrawPath path) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).editText(path);
            }
        }
    }

    public static boolean isTopNoteFragment2(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof NoteFragment2) {
                return true;
            } else if (currentFragment instanceof NoteFragment3) {
                return true;
            }
        }
        return false;
    }

    public static int getCenteredTitleThemeOverlay() {
        //return com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered;
        return R.style.MyThemeOverlayAlertDialog;
    }

//    public final static int USE_NEW_UI = 3;
//        public static Class<?> getCls() {
//        Class<?> cls = null;
//        if (USE_NEW_UI == 3) {
//            cls = BookActivity4.class;
//        }
//        return cls;
//    }
public static boolean onBackPressed(Activity context) {
    if (context instanceof BookListActivity) {
        BookListActivity act = (BookListActivity) context;
        FragmentManager fragmentManager = act.getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
        if (currentFragment instanceof BookActivity4Fragment) {
            ((BookActivity4Fragment) currentFragment).onBackPressed();
            return true;
        } else {
            act.finish();
            return true;
        }
    }
    return false;
}

    public static void finish(Activity context, boolean isRefreshList) {
        if (USE_FRAGMENT) {
            if (context instanceof BookListActivity) {
                BookListActivity act = (BookListActivity) context;
                FragmentManager fragmentManager = act.getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (isRefreshList) {
//                    if (act.noteFragment2 instanceof NoteFragment2) {
//
//                    }
                    if (act.noteFragment2 != null) {
                        act.noteFragment2.refresh();
                    }
//                    if (act.noteFragment3 instanceof NoteFragment3) {
//
//                    }
                    if (act.noteFragment3 != null) {
                        act.noteFragment3.refresh();
                    }
//                    if (act.noteFragment4 instanceof NoteFragment4) {
//
//                    }
                    if (act.noteFragment4 != null) {
                        act.noteFragment4.refresh();
                    }
                }
                if (act.isIntentNew || act.isIntentOpen) {
                    act.finish();
                }
            }
        } else {
            context.finish();
        }
    }
    public static void openBookUI(Activity context, Uri data, String filePath,
                                  Integer pageIdx, String backText,
                                  boolean isClearTop, boolean isFinish) {
        if (USE_FRAGMENT) {
            if (context instanceof BookListActivity) {
                BookListActivity act = (BookListActivity) context;
                FragmentManager fragmentManager = act.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left_exit,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left_exit
                );
                Fragment fragment;
                if (true) {
                    fragment = new BookActivity4Fragment();
                } else {
                    fragment = new BookActivity4FragmentMin();
                }
                Bundle arguments = new Bundle();
                if (data != null) {
                    arguments.putString(EXTRA_DATA, data.toString());
                }
                arguments.putString(EXTRA_DIRURLPATH, filePath);
                if (pageIdx != null) {
                    arguments.putInt(PAGE_IDX, pageIdx);
                }
                if (backText != null) {
                    arguments.putString(EXTRA_BACKTEXT, backText);
                }
                fragment.setArguments(arguments);
                if (isFinish || isClearTop) {
                    fragmentTransaction.replace(R.id.content_layout, fragment);
                    fragmentTransaction.commit();
                } else {
                    String tag = "BookActivity4";
                    if (fragmentManager.findFragmentByTag(tag) == null) {
                        fragmentTransaction.add(R.id.content_layout, fragment);
                        fragmentTransaction.addToBackStack(tag);
                        fragmentTransaction.commit();
                    } else {
                        //stop starting repeat fragment
                    }
                }
            }
        } else {
            Intent it = new Intent(context, BookActivity4Fragment.class);
            if (isClearTop) {
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            if (data != null) {
                it.setData(data);
            }
            it.putExtra(BookActivity4Utils.EXTRA_DIRURLPATH, filePath);
            if (pageIdx != null) {
                it.putExtra(BookActivity4Utils.PAGE_IDX, pageIdx);
            }
            if (backText != null) {
                it.putExtra(BookActivity4Utils.EXTRA_BACKTEXT, backText);
            }
            context.startActivity(it);
            if (isFinish) {
                context.finish();
            }
        }
    }

    public final static String EXTRA_DATA = "EXTRA_DATA";
    public final static String EXTRA_DIRURLPATH = "EXTRA_DIRURLPATH";
    public final static String EXTRA_BACKTEXT = "EXTRA_BACKTEXT";
    public final static String PAGE_IDX = "PAGE_IDX";

    private static final ReentrantLock bitmapLock = new ReentrantLock();
    public static ReentrantLock getBitmapLock() {
        return bitmapLock;
    }

    public static int getPageIndex(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                return ((BookActivity4Fragment) currentFragment).getPageIdx();
            }
        }
        return 0;
    }

    public static void onVersionChanged(Activity context, boolean isUndoActive, boolean isRedoActive) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).onVersionChanged(isUndoActive, isRedoActive);
            }
        }
    }

    public static void toggleFocusMode(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).toggleFocusMode();
            }
        }
    }
    public static void nextPage(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).nextPage();
            }
        }
    }
    public static void previousPage(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).previousPage();
            }
        }
    }
    public static void flipUp(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).flipUp();
            }
        }
    }

    public static void openPage(Activity context, int pageIdx) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).openPage(pageIdx);
            }
        }
    }

    public static void deletePages(Activity context, List<Page> pages) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).deletePages(pages);
            }
        }
    }

    public static void copyPages(Activity context, List<Page> pages) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).copyPages(pages);
            }
        }
    }

    public static void reorderPages(Activity context, List<Page> pages) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).reorderPages(pages);
            }
        }
    }

    public static void editMeetingSummary(Activity context, String newName) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).editMeetingSummary(newName);
            }
        }
    }

    public static void editMeetingDuration(Activity context, String newName) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).editMeetingDuration(newName);
            }
        }
    }

    public static void editMeetingDate(Activity context, String newName, Long dateVal) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            //FIXME:use -2
            if (fragmentManager.getFragments().size() - 2 >= 0) {
                Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 2);// - 1);
                if (currentFragment instanceof BookActivity4Fragment) {
                    ((BookActivity4Fragment) currentFragment).editMeetingDate(newName, dateVal);
                }
            }
        }
    }

    public static void renameBook(Activity context, String newName) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).renameBook(newName);
            }
        }
    }

    public static void setBookBackText(Activity context, String backText_) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).setBookBackText(backText_);
            }
        }
    }

    public static void onPaintSelect(Context context, String backText) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).onPaintSelect(backText);
            }
        }
    }
    public static String getCurPattern(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                return ((BookActivity4Fragment) currentFragment).curPattern;
            }
        }
        return null;
    }

    public static int getStateStarted(Activity context) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            return act.stateStarted_;
        }
        return 0;
    }

    public static void setPenColor(Context context, int color) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).setPenColor(color);
            }
        }
    }

    public static void updateInfoBar(Context context) {
        if (context instanceof MainActivity) {
            ((MainActivity) context).updateInfoBar();
        } else if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).updateInfoBar();
            }
        }
    }

    public static boolean checkText(Context context, String textState) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookListFragment) {
                if (((BookListFragment) currentFragment).checkText(textState)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static void onNewBook(Context context, String textState, String backText) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookListFragment) {
                ((BookListFragment) currentFragment).onNewBook(textState, backText);
            }
        }
    }

    public static void btn_audio_start_setEnabled(Context context, boolean enable) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).btn_audio_start_setEnabled(enable);
            }
        }
    }

    public static void tv_result_setText(Context context, String str, String subStr, boolean isEnd, boolean isAppend) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).tv_result_setText(str, subStr, isEnd, isAppend);
            }
        }
    }

    public static void onLongClickSubmenu1_after(Context context, BookActivity4BrushEditDialog dialog, int mBrushId) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            FragmentManager fragmentManager = act.getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            if (currentFragment instanceof BookActivity4Fragment) {
                ((BookActivity4Fragment) currentFragment).onLongClickSubmenu1_after(dialog, mBrushId);
            }
        }
    }
}

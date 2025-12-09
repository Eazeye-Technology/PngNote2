package com.txkj.drawingapp.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.internal.ViewUtils;
import com.txkj.drawingapp.R;

import java.util.Collections;

import io.material.catalog.windowpreferences.WindowPreferencesManager;

public class BottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "BottomSheet";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Set up BottomSheetDialog
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(
                        getContext());//, R.style.ThemeOverlay_Catalog_BottomSheetDialog_Scrollable);
        new WindowPreferencesManager(requireContext())
                .applyEdgeToEdgePreference(bottomSheetDialog.getWindow());
        View content = LayoutInflater.from(getContext())
                .inflate(R.layout.cat_bottomsheet_scrollable_content, new FrameLayout(getContext()));
        bottomSheetDialog.setContentView(content);
        bottomSheetDialog.getBehavior().setPeekHeight(400);

        View bottomSheetContent = content.findViewById(R.id.bottom_drawer_2);
//        ViewUtils.doOnApplyWindowInsets(
//                bottomSheetContent,
//                (v, insets, initialPadding) -> {
//                    // Add the inset in the inner NestedScrollView instead to make the edge-to-edge behavior
//                    // consistent - i.e., the extra padding will only show at the bottom of all content,
//                    // i.e.,
//                    // only when you can no longer scroll down to show more content.
//                    bottomSheetContent.setPaddingRelative(
//                            initialPadding.start,
//                            initialPadding.top,
//                            initialPadding.end,
//                            initialPadding.bottom
//                                    + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
//                    return insets;
//                });
//        bottomSheetDialog.setProtections(
//                Collections.singletonList(
//                        getDefaultBottomGradientProtection(requireContext())));
        return bottomSheetDialog;
    }
}

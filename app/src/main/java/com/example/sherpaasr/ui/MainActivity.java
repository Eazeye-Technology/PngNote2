package com.example.sherpaasr.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sherpaasr.data.ModelItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.txkj.drawingapp.R;

public class MainActivity extends AppCompatActivity implements ModelsFragment.ModelSelectListener {

    private TranscribeFragment transcribeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_download);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        transcribeFragment = new TranscribeFragment();
        ModelsFragment modelsFragment = new ModelsFragment();

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? transcribeFragment : modelsFragment;
            }
            @Override public int getItemCount() { return 2; }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.title_transcribe : R.string.title_models);
        }).attach();
    }

    @Override
    public void onModelSelected(ModelItem item) {
        if (transcribeFragment != null) {
            transcribeFragment.setSelectedModel(item);
        }
    }
}

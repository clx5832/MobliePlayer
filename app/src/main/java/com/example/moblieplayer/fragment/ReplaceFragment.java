package com.example.moblieplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moblieplayer.base.BasePager;

public class ReplaceFragment extends Fragment {

    private BasePager basePager;
    public ReplaceFragment(BasePager basePager) {
            this.basePager = basePager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (basePager!=null){
            return basePager.rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

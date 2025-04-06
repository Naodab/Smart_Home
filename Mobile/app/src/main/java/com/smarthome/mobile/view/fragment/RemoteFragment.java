package com.smarthome.mobile.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smarthome.mobile.databinding.FragmentRemoteBinding;

public class RemoteFragment extends Fragment {
    private FragmentRemoteBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRemoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
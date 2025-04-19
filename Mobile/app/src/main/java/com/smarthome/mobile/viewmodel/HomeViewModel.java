package com.smarthome.mobile.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.smarthome.mobile.model.Home;
import com.smarthome.mobile.repository.HomeRepository;
import com.smarthome.mobile.util.Result;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepository homeRepository;

    public HomeViewModel(Application application) {
        super(application);
        this.homeRepository = new HomeRepository();
    }

    public MutableLiveData<Result<Home>> getHomeLiveData() {
        return this.homeRepository.getHomeLiveData();
    }

    public void fetchHome() {
        this.homeRepository.getHome();
    }
}

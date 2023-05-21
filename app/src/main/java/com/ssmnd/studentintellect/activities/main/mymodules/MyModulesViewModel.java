package com.ssmnd.studentintellect.activities.main.mymodules;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyModulesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyModulesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
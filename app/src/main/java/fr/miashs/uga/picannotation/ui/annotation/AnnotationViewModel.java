package fr.miashs.uga.picannotation.ui.annotation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnnotationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AnnotationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an ultra BG");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
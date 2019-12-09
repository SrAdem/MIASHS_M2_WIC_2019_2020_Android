package fr.miashs.uga.picannotation.ui.annotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import fr.miashs.uga.picannotation.R;

public class AnnotationFragment extends Fragment {

    private AnnotationViewModel annotationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        annotationViewModel =
                ViewModelProviders.of(this).get(AnnotationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_annotation, container, false);
        final TextView textView = root.findViewById(R.id.text_annotation);
        annotationViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
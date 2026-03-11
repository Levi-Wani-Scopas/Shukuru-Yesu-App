package com.example.shukuruyesu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NoteFragment extends Fragment {

    private CalendarView calendarView;
    private EditText previewEditText;
    private TextView selectedDateText;
    private Button editButton;

    private SharedPreferences preferences;

    private String currentDateKey;

    public NoteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        calendarView = view.findViewById(R.id.calendar_view);
        previewEditText = view.findViewById(R.id.notepad_input);
        selectedDateText = view.findViewById(R.id.selected_date_text);
        editButton = view.findViewById(R.id.editText_button);

        preferences = requireContext()
                .getSharedPreferences("choir_notes", Context.MODE_PRIVATE);

        // Make preview read-only
        previewEditText.setFocusable(false);
        previewEditText.setClickable(false);
        previewEditText.setCursorVisible(false);

        Calendar today = Calendar.getInstance();
        currentDateKey = getDateKey(today);

        updateDateLabel(today);
        loadPreview();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {

            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);

            currentDateKey = getDateKey(selected);
            updateDateLabel(selected);
            loadPreview();
        });

        requireActivity()
                .getSupportFragmentManager()
                .setFragmentResultListener(
                        "note_saved",
                        getViewLifecycleOwner(),
                        (requestKey, bundle) -> {

                            String savedKey = bundle.getString("date_key");

                            if (savedKey != null && savedKey.equals(currentDateKey)) {
                                loadPreview();
                            }
                        }
                );

        editButton.setOnClickListener(v -> openEditor());

        View root = view;

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    systemBars.bottom
            );

            return insets;
        });
    }

    private void openEditor() {

        NoteEditorFragment fragment = new NoteEditorFragment();

        Bundle bundle = new Bundle();
        bundle.putString("date_key", currentDateKey);
        fragment.setArguments(bundle);

        FragmentTransaction transaction =
                requireActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadPreview() {

        String note = preferences.getString(currentDateKey, "");
        previewEditText.setText(note);
    }

    private void updateDateLabel(Calendar calendar) {

        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

        selectedDateText.setText(sdf.format(calendar.getTime()));
    }

    private String getDateKey(Calendar calendar) {

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        return sdf.format(calendar.getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreview();   // refresh after returning from editor
    }
}
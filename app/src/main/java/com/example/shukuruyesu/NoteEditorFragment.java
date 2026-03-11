package com.example.shukuruyesu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteEditorFragment extends Fragment {

    private EditText editorInput;
    private Button saveButton, clearButton;
    private TextView dateText;

    private SharedPreferences preferences;
    private String dateKey;

    public NoteEditorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_note_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        editorInput = view.findViewById(R.id.editor_input);
        saveButton = view.findViewById(R.id.save_button);
        clearButton = view.findViewById(R.id.clear_button);
        dateText = view.findViewById(R.id.editor_date_text);

        preferences = requireContext()
                .getSharedPreferences("choir_notes", Context.MODE_PRIVATE);

        if (getArguments() != null) {
            dateKey = getArguments().getString("date_key");
        }

        // ✅ FIX: Format date according to current app language
        if (dateKey != null) {
            try {

                SimpleDateFormat originalFormat =
                        new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

                Date date = originalFormat.parse(dateKey);

                SimpleDateFormat localizedFormat =
                        new SimpleDateFormat("dd MMMM , yyyy", Locale.getDefault());

                String formattedDate = localizedFormat.format(date);

                dateText.setText(formattedDate);

            } catch (Exception e) {
                dateText.setText(dateKey);
            }
        }

        loadNote();

        // SAVE BUTTON
        saveButton.setOnClickListener(v -> {

            String text = editorInput.getText().toString().trim();

            if (TextUtils.isEmpty(text)) {

                Toast.makeText(getContext(),
                        R.string.no_notes_found_to_be_saved,
                        Toast.LENGTH_SHORT).show();

                exitEditor();

            } else {

                preferences.edit()
                        .putString(dateKey, text)
                        .apply();

                Toast.makeText(getContext(),
                        R.string.note_saved,
                        Toast.LENGTH_SHORT).show();

                exitEditor();
            }
        });

        // CLEAR BUTTON
        clearButton.setOnClickListener(v -> {

            String existingNote = preferences.getString(dateKey, "");

            if (TextUtils.isEmpty(existingNote)) {

                Toast.makeText(getContext(),
                        R.string.no_note_saved_to_be_cleared,
                        Toast.LENGTH_SHORT).show();

                exitEditor();

            } else {

                preferences.edit()
                        .remove(dateKey)
                        .apply();

                Toast.makeText(getContext(),
                        R.string.clearing_note,
                        Toast.LENGTH_SHORT).show();

                exitEditor();
            }
        });
    }

    private void loadNote() {

        String note = preferences.getString(dateKey, "");

        editorInput.setText(note);
        editorInput.setSelection(editorInput.getText().length());
    }

    private void exitEditor() {

        Bundle result = new Bundle();
        result.putString("date_key", dateKey);

        requireActivity()
                .getSupportFragmentManager()
                .setFragmentResult("note_saved", result);

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
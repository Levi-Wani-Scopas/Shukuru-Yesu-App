package com.example.shukuruyesu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PdfsFragment extends Fragment {

    private CardView cardEnglish, cardArabic;

    public PdfsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pdfs, container, false);

        cardEnglish = view.findViewById(R.id.cardEnglish);
        cardArabic  = view.findViewById(R.id.cardArabic);

        cardEnglish.setOnClickListener(v -> openPdfExternally("Shukuru Yesu (English).pdf"));
        cardArabic.setOnClickListener(v -> openPdfExternally("Shukuru Yesu (Arabic).pdf"));

        return view;
    }

    private void openPdfExternally(String assetFileName) {

        try {

            File file = new File(
                    requireContext().getCacheDir(),
                    assetFileName
            );

            // Copy only once
            if (!file.exists()) {

                InputStream inputStream =
                        requireContext().getAssets().open(assetFileName);

                FileOutputStream outputStream =
                        new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

                outputStream.flush();
                inputStream.close();
                outputStream.close();
            }

            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.example.scavenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.scavenger.databinding.FragmentPlayHuntBinding;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;


public class PlayHunt extends Fragment {

    private FragmentPlayHuntBinding binding;
    private ImageView imageView;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPlayHuntBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            binding.buttonHint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), Hint.class));
                    ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
                }
            });
        binding.buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PlayHunt.this)
                        .navigate(R.id.action_PlayHunt_to_homeFragment2);
            }
        });
        binding.buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Mapping.class));
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        });
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),100);
            }
        });
        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageResource(R.drawable.ic_menu_gallery);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.capturedImage.setImageBitmap(imageBitmap);
            Bitmap testBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.test_run);

            // Load the model
            String modelFile = "lite-model_mobilenet_v2_100_224_fp32_1.tflite";
            Interpreter interpreter = null;
            try {
                interpreter = new Interpreter(FileUtil.loadMappedFile(requireContext(), modelFile), new Interpreter.Options());
            } catch (IOException e) {
                throw new RuntimeException(e);

            }

            // Allocate memory for the input and output tensors
            Bitmap resizedBitmap1 = Bitmap.createScaledBitmap(imageBitmap, 224, 224, true);
            Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(testBitmap, 224, 224, true);

            // Preprocess the images
            TensorImage image1 = new TensorImage(DataType.FLOAT32);
            image1.load(resizedBitmap1);
            float mean = 127.5f;
            float std = 127.5f;
            TensorProcessor tensorProcessor = new TensorProcessor.Builder().add(new NormalizeOp(mean, std)).build();
            tensorProcessor.process(image1.getTensorBuffer());

            TensorImage image2 = new TensorImage(DataType.FLOAT32);
            image2.load(resizedBitmap2);
            tensorProcessor.process(image2.getTensorBuffer());

            // Input tensor shape: [1, 224, 224, 3]
            // Output tensor shape: [1, 1280]
            float[][] output = new float[1][1280];
            interpreter.run(image1.getBuffer(), output);

            // Get the embedding vector for the first image
            float[] embedding1 = output[0];

            // Repeat the process for the second image
            interpreter.run(image2.getBuffer(), output);
            float[] embedding2 = output[0];
            float distance = 0.0f;
            for (int i = 0; i < embedding1.length; i++) {
                distance += Math.pow((embedding1[i] - embedding2[i]), 2);
            }
            distance = (float) Math.sqrt(distance);

            // Output the distance
            binding.textresult.setText(String.valueOf(distance));

        }
    }

    public String convertBitmapToUrl(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String urlImage = "data:image/png;base64," + encoded;
        return urlImage;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}
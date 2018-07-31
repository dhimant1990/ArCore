package com.dhims.arcore;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private ImageView imageView;

    private int selectedColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        imageView.setOnClickListener(view -> pickColor());

        ModelRenderable.builder()
                .setSource(this, Uri.parse("childChair.sfb"))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            if (modelRenderable == null) {
                return;
            }

            ModelRenderable renderable = modelRenderable.makeCopy();
            renderable.getMaterial().setFloat4("baseColorTint", new Color(android.graphics.Color.red(selectedColor), android.graphics.Color.green(selectedColor), android.graphics.Color.blue(selectedColor), android.graphics.Color.alpha(selectedColor)));

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
            andy.setParent(anchorNode);
            andy.setRenderable(renderable);
            andy.select();

        });

    }

    private void pickColor() {

        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .lightnessSliderOnly()
                .initialColor(android.graphics.Color.BLACK)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                    //Toast.makeText(MainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    this.selectedColor = selectedColor;
                    //Toast.makeText(MainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("cancel", (dialog, which) -> {

                })
                .build()
                .show();

    }
}

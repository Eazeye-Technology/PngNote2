package com.helluva.telephone_pictionary_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.agsw.FabricView.DrawableObjects.CDrawable;
import com.agsw.FabricView.FabricView;
import com.txkj.drawingapp.R;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class SketchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //FIXME:
        //https://github.com/antwankakki/FabricView/wiki
        if (false) {
            Activity parent = this;
            FabricView myFabricView = (FabricView) parent.findViewById(R.id.fabricView); //Retrieve by ID

            //Configuration. All of which is optional. Defaults are marked with an asterisk here.
            myFabricView.setBackgroundMode(FabricView.BACKGROUND_STYLE_BLANK); //Set the background style (BACKGROUND_STYLE_BLANK*, BACKGROUND_STYLE_NOTEBOOK_PAPER, BACKGROUND_STYLE_GRAPH_PAPER)

            myFabricView.setInteractionMode(FabricView.DRAW_MODE); //Set its draw mode (DRAW_MODE*, SELECT_MODE, ROTATE_MODE, LOCKED_MODE)
            //myFabricView.setDeleteIcon(deleteIcon); //If you don't like the default delete icon
            myFabricView.setColor(0xFFCCCCCC); //Line color
            myFabricView.setSize(10); //Line width
            myFabricView.setSelectionColor(0xFFEEEEEE); //Selection box color
            //To be notified of any deletion:
            myFabricView.setDeletionListener(new FabricView.DeletionListener() {
                @Override
                public void deleted(CDrawable drawable) {
                    //doSomethingAboutThis(drawable);
                }
            });
        }

        if (true) {
            Activity parent = this;
            FabricView myFabricView = (FabricView) parent.findViewById(R.id.fabricView); //Retrieve by ID
            findViewById(R.id.draw_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.setInteractionMode(FabricView.DRAW_MODE);
                }
            });
            findViewById(R.id.select_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.setInteractionMode(FabricView.SELECT_MODE);
                }
            });
            findViewById(R.id.rotate_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.setInteractionMode(FabricView.ROTATE_MODE);
                }
            });
            findViewById(R.id.clean_page).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.cleanPage();
                }
            });
            findViewById(R.id.undo_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.undo();
                }
            });
            findViewById(R.id.redo_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myFabricView.redo();
                }
            });
            findViewById(R.id.text_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paint paint = new Paint();
                    paint.setColor(0xFFFF0000);
                    paint.setTextSize(18 * 5);
                    myFabricView.drawText("hello", 100, 100, paint);
                }
            });
            findViewById(R.id.image_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable drawable = ContextCompat.getDrawable(SketchActivity.this, R.mipmap.ic_launcher);
                    //https://blog.csdn.net/jaycee110905/article/details/38817871
                    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    drawable.draw(canvas);
                    myFabricView.drawImage(200, 200, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), bitmap);
                }
            });
        }

        Intent i = getIntent();
        String description = getIntent().getStringExtra("text_description");

        TextView textDescription = (TextView) this.findViewById(R.id.description_text_view);
        textDescription.setText(description);

        Button nextButton = (Button) this.findViewById(R.id.next_button_sketch);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FabricView canvas = (FabricView) findViewById(R.id.fabricView);
                Bitmap image = canvas.getCanvasBitmap();
                String base64String = SketchActivity.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 80);

                String imageMessage = "provideImage:" + base64String;
//                ((ApplicationState)getApplicationContext()).sendMessage(imageMessage);

//                Intent i = new Intent(SketchActivity.this, WaitActivity.class);
//                SketchActivity.this.startActivity(i);
            }
        });



//OLD BUTTON WITH RED BACKGROUND STUFF
       /* Button redButton = (Button) this.findViewById(R.id.red_button);
        redButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        //redButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        redButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FabricView canvas = (FabricView) findViewById(R.id.fabricView);
                canvas.setColor(Color.RED);
            }

        });
        */

        //COLOR SPINNER IMPLEMENTATION
        Spinner colorSpinner = (Spinner) findViewById(R.id.color_spinner);

        //for the new spinner
        ArrayAdapter<Colors> adapter = new ArrayAdapter(this,R.layout.spinner_size_item, Colors.values());
        adapter.setDropDownViewResource(R.layout.spinner_size_item);
        colorSpinner.setAdapter(adapter);

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Colors newColor = Colors.values()[position];
                FabricView canvas = (FabricView) findViewById(R.id.fabricView);
                canvas.setColor(newColor.getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //SIZES SPINNER IMPLEMENTATION
        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_spinner);

        //for the new spinner
        ArrayAdapter<Sizes> adapter2 = new ArrayAdapter(this,R.layout.spinner_size_item, Sizes.values());
        adapter.setDropDownViewResource(R.layout.spinner_size_item);
        sizeSpinner.setAdapter(adapter2);

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sizes newSize = Sizes.values()[position];
                FabricView canvas = (FabricView) findViewById(R.id.fabricView);
                canvas.setSize(newSize.getSize());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    //BITMAP HELPERS

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);

        String encoded = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
        encoded = encoded.replace("/", "~");
        return encoded;
    }

    public static Bitmap decodeBase64(String input)
    {
        input = input.replace("~", "/");
        byte[] decodedBytes = Base64.decode(input, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}

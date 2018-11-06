package com.gurtam.wiatag_kit.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gurtam.wiatagkit.Location;
import com.gurtam.wiatagkit.Message;
import com.gurtam.wiatagkit.MessageSender;
import com.gurtam.wiatagkit.MessageSenderListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    /** Callback for receiving sending status */
    MessageSenderListener messageSenderListener = new MessageSenderListener() {
        @Override
        protected void onSuccess() {
            super.onSuccess();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"Message Sent",Toast.LENGTH_LONG).show();
                }
            });
        }
        @Override
        protected void onFailure(final byte errorCode) {
            super.onFailure(errorCode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String errorMessage;
                    switch (errorCode){
                        case MessageSenderListener.FAILED_TO_CONNECT :
                            errorMessage = "Could not connect to server. Check connection and connection settings (host, port)"; break;
                        case MessageSenderListener.FAILED_TO_SEND :
                            errorMessage = "Packet parsing error"; break;
                        case MessageSenderListener.INVALID_UNIQUE_ID :
                            errorMessage = "Unit does not exist on server"; break;
                        case MessageSenderListener.INCORRECT_PASSWORD :
                            errorMessage = "Wrong password"; break;
                        default:
                            errorMessage = "Failure"; break;
                    }
                    Toast.makeText(MainActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /** Initializing MessageSender with host, port, unitId, password */
        Button initialize = findViewById(R.id.initialize);
        initialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender.initWithHost(((EditText)findViewById(R.id.server_url)).getText().toString(),
                        Integer.valueOf(((EditText)findViewById(R.id.port)).getText().toString()),
                        ((EditText)findViewById(R.id.unitId)).getText().toString(),
                        ((EditText)findViewById(R.id.password)).getText().toString());
            }
        });

        /** Sending message with sos | image | location parameters */
        Button sendMessage = findViewById(R.id.buttonSendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message().time(new Date().getTime());
                if(((CheckBox)findViewById(R.id.checkBoxSos)).isChecked()) {
                    message.Sos();
                }
                if(((CheckBox)findViewById(R.id.checkBoxImage)).isChecked()) {
                    message.image("wiatag-kit", getBytesFromDrawableBitmap(R.drawable.wiatag));
                }
                if(((CheckBox)findViewById(R.id.checkBoxLocation)).isChecked()) {
                    /**Location with latitude:53.9058289, longitude:27.4569797
                     * altitude     290 meter
                     * speed        2 m/s
                     * bearing      15
                     * satellites   8
                     */
                    message.location(new Location(53.9058289, 27.4569797, 290, 2F, (short) 15, (byte) 8));
                }
                MessageSender.sendMessage(message, messageSenderListener);
            }
        });

        // Sending array of Messages
        Button sendMessages = findViewById(R.id.buttonSendMessages);
        sendMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSender.sendMessages(generateMessages(), messageSenderListener);
            }
        });
    }

    /** helper method for Array of messages generation*/
    private List<Message> generateMessages(){
        List<Message> messages = new ArrayList<>();
        for(int i = 9; i >= 0; i--){
            messages.add(new Message()
                    .time((int)(new Date().getTime() + i*1000))
                    .batteryLevel((byte)(i *10))
                    .addParam("CustomParam",i)
                    .location(new Location(53.9058289, 27.4569797, 290, (short)0, (short)0,(byte)8)));
        }
        return messages;
    }

    /** helper method for getting byte array of Drawable resource*/
    public byte[] getBytesFromDrawableBitmap(@DrawableRes int  drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }
}

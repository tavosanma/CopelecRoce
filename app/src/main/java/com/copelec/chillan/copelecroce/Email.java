package com.copelec.chillan.copelecroce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class Email extends AppCompatActivity {

    private EditText mEditTextTo;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        final Button inicio = findViewById(R.id.inicio);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Email.this, Menu.class);
                startActivity(intent);
            }
        });mEditTextTo = findViewById(R.id.edit_text_to);
        mEditTextSubject= findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);


        final Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSend.setVisibility(View.INVISIBLE);

                String recipientList = mEditTextTo.getText().toString();
                String[] recipients = recipientList.split(",");

                String Subject = mEditTextSubject.getText().toString();
                String Message = mEditTextMessage.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,Subject);
                intent.putExtra(Intent.EXTRA_TEXT,Message);
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"choosee an email"));
                inicio.setVisibility(View.VISIBLE);
            }
        });

    }
}


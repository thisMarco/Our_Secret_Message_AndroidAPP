package com.example.oursecretmessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

import static java.lang.Character.valueOf;

public class MainActivity extends AppCompatActivity {

    int nOfCharacters = 256; //26; //62
    char[][] cipher = new char[nOfCharacters][nOfCharacters];

    String cipherCharacters = CreateCipherString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CreateCipherTable();
        ActionSend();
    }

    public String CreateCipherString(){
        String characters = "";

        for(int i = 0; i <= nOfCharacters; i++) {
            characters += Character.toString((char) i);
        }

        Log.i("String", characters);

        return characters;
    }

    public void CreateCipherTable(){
        int startIndex = 0;
        for (int i = 0; i < nOfCharacters; i++) {
            int cIndex = startIndex;
            for (int j = 0; j < nOfCharacters; j++) {
                if (cIndex >= nOfCharacters)
                    cIndex = 0;
                cipher[i][j] = cipherCharacters.charAt(cIndex);
                cIndex++;
            }
            startIndex++;
        }
    }

    public void ActionSend(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if ("text/plain".equals(type)){
            String message = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(message != null){
                EditText txtMessage = findViewById(R.id.txtMessage);
                txtMessage.setText(message);
            }
        }
    }

    public void ProcessMessage(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(ContextThemeWrapper.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        EditText sM = findViewById(R.id.txtMessage);
        EditText sW = findViewById(R.id.txtSecretWord);

        String originalMessage = sM.getText().toString();
        String secretWord = sW.getText().toString();
        String processedMessage;

        secretWord = ExpandSecretWord(secretWord, originalMessage);

        processedMessage = ProcessMessage(originalMessage, secretWord, v.getTag().toString());

        sM.setText(processedMessage);
        sW.setText("");

    }

    public String ExpandSecretWord(String sw, String message) {

        //Matching secretWord length with originalMessage length
        while (sw.length() < message.length()) {
            int d = message.length() - sw.length();

            if (d > sw.length())
                sw += sw;
            else
                sw += sw.substring(0, d);
        }

        return sw;
    }

    public String ProcessMessage(String message, String sw, String caller) {

        String processedMessage;

        if (caller.equals("Encrypt"))
            processedMessage = EncryptMessage(message, sw);
        else
            processedMessage = DecryptMessage(message, sw);

        return processedMessage;
    }

    public String EncryptMessage(String message, String sw) {
        String pm = "";
        for (int x = 0; x < message.length(); x++) {
            int i = (String.valueOf(cipher[0])).indexOf(message.charAt(x));
            int j = (String.valueOf(cipher[0])).indexOf((sw.charAt(x)));

            pm += String.valueOf(cipher[i][j]);
        }

        return pm;
    }

    public String DecryptMessage(String message, String sw) {
        String pm = "";

        for (int x = 0; x < message.length(); x++) {
            int j = (String.valueOf(cipher[0])).indexOf(sw.charAt(x));
            int i = (String.valueOf(cipher[j])).indexOf(message.charAt(x));

            pm += String.valueOf(cipher[0][i]);
        }
        return pm;
    }

    public void Share(View v){
        EditText txtMessage = findViewById(R.id.txtMessage);
        //Creating ACTION with INTENT
        Intent shareText = new Intent(Intent.ACTION_SEND);
        shareText.setType("text/plain");
        shareText.putExtra(Intent.EXTRA_TEXT, txtMessage.getText().toString());
        getBaseContext().startActivity(Intent.createChooser(shareText, "Share with"));
    }
}
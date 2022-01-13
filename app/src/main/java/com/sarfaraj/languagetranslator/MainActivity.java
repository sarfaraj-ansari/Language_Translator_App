package com.sarfaraj.languagetranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.sarfaraj.languagetranslator.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int mic_request_Code =1;
    ActivityMainBinding binding;
    private String[] fromLanguages = {"From", "English", "Hindi", "Urdu", "Bangla", "Arabic", "German", "French", "Chinese", "Russian"};
    private String[] toLanguages = {"To", "English", "Hindi", "Urdu", "Bangla", "German", "French", "Chinese", "Russian"};
    int fromLanguageCode, toLanguageCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        ArrayAdapter<String> fromadapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,fromLanguages);
        binding.spinnerFirst.setAdapter(fromadapter);
        binding.spinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getFromLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> toAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,toLanguages);
        binding.spinnerSecond.setAdapter(toAdapter);
        binding.spinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getToLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak the word");

                try{

                    startActivityForResult(intent,mic_request_Code);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.translatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.resultTv.setText("");
                if(binding.edtTextInput.getText().toString().isEmpty()){
                    binding.edtTextInput.setError("Please Enter Text");
                    binding.edtTextInput.requestFocus();
                  //  Toast.makeText(MainActivity.this, "Please Enter Text", Toast.LENGTH_LONG).show();
                }else if(fromLanguageCode==0 && toLanguageCode==0){
                    Toast.makeText(MainActivity.this, "Please select the language to make translaton", Toast.LENGTH_LONG).show();
                }else{
                    translateText(fromLanguageCode,toLanguageCode,binding.edtTextInput.getText().toString());
                }
            }
        });

    }

    private int getFromLanguageCode(String fromLanguage) {
        int fromCode = 0;
        switch (fromLanguage) {
            case "English":
                fromCode = FirebaseTranslateLanguage.EN;
                break;
            case "Hindi":
                fromCode = FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                fromCode = FirebaseTranslateLanguage.UR;
                break;
            case "Bangla":
                fromCode = FirebaseTranslateLanguage.BN;
                break;
            case "Arabic":
                fromCode = FirebaseTranslateLanguage.AR;
                break;
            case "German":
                fromCode = FirebaseTranslateLanguage.DE;
                break;
            case "French":
                fromCode = FirebaseTranslateLanguage.FR;
                break;
            case "Chinese":
                fromCode = FirebaseTranslateLanguage.ZH;
                break;
            case "Russian":
                fromCode = FirebaseTranslateLanguage.RU;
                break;
            default:
                fromCode=0;
        }
        return fromCode;
    }

    private int getToLanguageCode(String toLanguage) {
        int toCode=0;

        switch (toLanguage) {
            case "English":
                toCode = FirebaseTranslateLanguage.EN;
                break;
            case "Hindi":
                toCode = FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                toCode = FirebaseTranslateLanguage.UR;
                break;
            case "Bangla":
                toCode = FirebaseTranslateLanguage.BN;
                break;
            case "Arabic":
                toCode = FirebaseTranslateLanguage.AR;
                break;
            case "German":
                toCode = FirebaseTranslateLanguage.DE;
                break;
            case "French":
                toCode = FirebaseTranslateLanguage.FR;
                break;
            case "Chinese":
                toCode = FirebaseTranslateLanguage.ZH;
                break;
            case "Russian":
                toCode = FirebaseTranslateLanguage.RU;
                break;
            default:
                toCode=0;
        }
        return toCode;
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source_text) {
        binding.resultTv.setText("Downloading Model...");
        // Create an English-German translator:
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode)
                        .build();
        final FirebaseTranslator Translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        Translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                binding.resultTv.setText("Translating...");
                                Translator.translate(source_text).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                       binding.resultTv.setText(s);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Failure in Translating", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==mic_request_Code){
            if(resultCode==RESULT_OK && data!=null){
               ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
               binding.edtTextInput.setText(result.get(0));
            }
        }
    }
}
package com.example.krutikawadhwa.tuts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class TourActivity extends AppCompatActivity {

    public Button mFirstButton;
    public Button mSecondButton;
    public Button mBeginButton;

    //public static final String SHOWCASE_ID = "Sequence Showcase"; //Makes sure the tutorial runs only the first time


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        mBeginButton = findViewById(R.id.tutorial_begin);
        mFirstButton = findViewById(R.id.show_tutor_button);
        mSecondButton = findViewById(R.id.reset_button);


        showTutorSequence(500);

        mBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTutor(500);
            }
        });

    }

    public void showTutor(int millis){
        new MaterialShowcaseView.Builder(this) // instantiate the material showcase view using Builder
                .setTarget(mFirstButton) // set what view will be pointed or highlighted
                .setTitleText("Single") // set the title of the tutorial
                .setDismissText("GOT IT") // set the dismiss text
                .setContentText("This is the choose option button") // set the content or detail text
                .setDelay(millis) // set delay in milliseconds to show the tutor
                //.singleUse(SHOWCASE_ID) // set the single use so it is shown only once using our create SHOWCASE_ID constant
                .show();
    }

    private void showTutorSequence(int millis) {

        ShowcaseConfig config = new ShowcaseConfig(); //create the showcase config
        config.setDelay(millis); //set the delay of each sequence using millis variable

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this); // create the material showcase sequence

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        }); // set the listener of the sequence order to know we are in which position

        sequence.setConfig(config); //t the showcase config to the sequence.

        sequence.addSequenceItem(mBeginButton, "This is a button made for the first sequence", "OK"); // add view for the first sequence, in this case it is a button.

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mFirstButton)
                        .setDismissText("NEXT")
                        .setContentText("This is a textview made for the second sequence")
                        .withCircleShape()
                        .build()
        ); // add view for the second sequence, in this case it is a textview.

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mSecondButton)
                        .setDismissText("FINISH")
                        .setContentText("This is the checkbox made for the third sequence")
                        .withCircleShape()
                        .build()
        ); // add view for the third sequence, in this case it is a checkbox.

        sequence.start(); //start the sequence showcase

    }
}


package com.sammyscl.tutorial;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sammyscl.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TutActivity extends AppCompatActivity {

    private ArrayList<ImageModel> imageModelArrayList;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    int position=0;
//
    private int[] myImageList = new int[]{R.drawable.tutorial1, R.drawable.tutorial2,
            R.drawable.tutorial3};


    Button btn_Escape;
//ImageView img_anim;
//    Button  btn_next;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tut_lay);

        btn_Escape=(Button) findViewById(R.id.btn_Escape);

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();
//        btn_next=(Button) findViewById(R.id.btn_next);
//        img_anim=(ImageView) findViewById(R.id.img_anim);
//        btn_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (position>=2){
//                    finish();
//                }else{
//
//
//                    img_anim.setImageResource(myImageList[position]);
//                    position++;
//                }
//
//
//
//
//            }
//        });


        init();

    btn_Escape.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        finish();
        }
    });


    }


    private void init() {


        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(new SlidingImage_Adapter(TutActivity.this,imageModelArrayList));

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius

        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }


    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }


        return list;
    }

}

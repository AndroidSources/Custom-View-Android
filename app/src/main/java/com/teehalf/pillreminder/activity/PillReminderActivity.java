package com.teehalf.pillreminder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.teehalf.pillreminder.R;
import com.teehalf.pillreminder.util.RxBus;
import com.teehalf.pillreminder.widget.DayViewWidget;
import com.teehalf.pillreminder.widget.WeekViewWidget;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gowtham Chandrasekar on 16-09-2016.
 */
public class PillReminderActivity extends AppCompatActivity {

    private WeekViewWidget PieMenu;
    private DayViewWidget dayViewWidget;

    private RelativeLayout rl;
    ImageView mornImageView;
    ImageView aftnImageView;
    ImageView eveImageView;
    ImageView nitImageView;
    //private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //------------------------------------------------------------------------------------------
        // Generating Pie view
        //------------------------------------------------------------------------------------------
        setContentView(R.layout.activity_main);

        float screen_density = getResources().getDisplayMetrics().density;

        rl = (RelativeLayout) findViewById(R.id.mainRelativeLayoutTest);
        mornImageView = (ImageView) findViewById(R.id.morn_img_view);
        aftnImageView = (ImageView) findViewById(R.id.aftn_img_view);
        eveImageView = (ImageView) findViewById(R.id.eve_img_view);
        nitImageView = (ImageView) findViewById(R.id.nit_img_view);


        createWeekView();
        createDayView();


        initializeEventSubs();
    }

    private void createDayView() {

        dayViewWidget = new DayViewWidget(getBaseContext());
        dayViewWidget.setId(R.id.day_view);
        int xScreenSize = (getResources().getDisplayMetrics().widthPixels);
        int yScreenSize = (getResources().getDisplayMetrics().heightPixels);
        int xLayoutSize = rl.getWidth();
        int yLayoutSize = rl.getHeight();
        int xCenter = xScreenSize / 2;
        int yCenter = yScreenSize / 2;

        dayViewWidget.setSourceLocation(xCenter, yCenter);
        dayViewWidget.setShowSourceLocation(true);
        dayViewWidget.setCenterLocation(xCenter, yCenter);

        dayViewWidget.setCenterCircle(new TotalTabletsForParticularDay());
        dayViewWidget.addMenuEntry(new Morning());
        dayViewWidget.addMenuEntry(new Afternoon());
        dayViewWidget.addMenuEntry(new Evening());
        dayViewWidget.addMenuEntry(new Night());


        dayViewWidget.setVisibility(View.GONE);
        dayViewWidget.setRotation(45);
        rl.addView(dayViewWidget);


    }


    private void createWeekView() {


        PieMenu = new WeekViewWidget(getBaseContext());
        PieMenu.setId(R.id.week_view);
        int xScreenSize = (getResources().getDisplayMetrics().widthPixels);
        int yScreenSize = (getResources().getDisplayMetrics().heightPixels);
        int xLayoutSize = rl.getWidth();
        int yLayoutSize = rl.getHeight();
        int xCenter = xScreenSize / 2;
        int yCenter = yScreenSize / 2;

        PieMenu.setSourceLocation(xCenter, yCenter);
        PieMenu.setShowSourceLocation(true);
        PieMenu.setCenterLocation(xCenter, yCenter);

//				PieMenu.setHeader("X:"+xSource+" Y:"+ySource, 20);

        PieMenu.setCenterCircle(new TotalTabletsForaDayInWeek());
        PieMenu.addMenuEntry(new Monday());
        PieMenu.addMenuEntry(new Tuesday());
        PieMenu.addMenuEntry(new Wednesday());
        PieMenu.addMenuEntry(new Thursday());
        PieMenu.addMenuEntry(new Friday());
        PieMenu.addMenuEntry(new Saturday());
        PieMenu.addMenuEntry(new Sunday());


        rl.addView(PieMenu);
    }

    public static class Monday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Mon";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }


    public static class Tuesday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu2 - No Children";
        }

        public String getLabel() {
            return "Tue";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public static class Wednesday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Wed";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public static class Thursday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Thu";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public static class Friday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Fri";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }


    public static class Saturday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Sat";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }


    public static class Sunday implements WeekViewWidget.WeekViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "Sun";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public class TotalTabletsForaDayInWeek implements WeekViewWidget.WeekViewEntry {

        public String getName() {
            return "Close";
        }

        public String getLabel() {
            return "10";
        }

        public int getIcon() {
            return 0;
        }

        public void menuActiviated() {
        }
    }


    public class TotalTabletsForParticularDay implements DayViewWidget.DayViewEntry {

        public String getName() {
            return "Close";
        }

        public String getLabel() {
            return "Take";
        }

        public int getIcon() {
            return 0;
        }

        public void menuActiviated() {
        }
    }


    public static class Morning implements DayViewWidget.DayViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "";
        }

        public int getIcon() {
            return R.drawable.pill_red;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }


    public static class Afternoon implements DayViewWidget.DayViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "";
        }

        public int getIcon() {
            return R.drawable.pill_blue;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public static class Evening implements DayViewWidget.DayViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }

    public static class Night implements DayViewWidget.DayViewEntry {
        public String getName() {
            return "Menu1 - No Children";
        }

        public String getLabel() {
            return "";
        }

        public int getIcon() {
            return 0;
        }


        public void menuActiviated() {
            System.out.println("Menu #1 Activated - No Children");
        }
    }


    private void initializeEventSubs() {


        RxBus.toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, Object>() {
                    @Override
                    public Object call(Throwable error) {
                        if (error != null && error.getMessage() != null) {
                            return null;
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object s) {
                        if (s != null && s.toString().equalsIgnoreCase("show_day_view")) {
                            if (PieMenu.getVisibility() == View.VISIBLE) {
                                PieMenu.setVisibility(View.GONE);
                                dayViewWidget.setVisibility(View.VISIBLE);
                                mornImageView.setVisibility(View.VISIBLE);
                                aftnImageView.setVisibility(View.VISIBLE);
                                eveImageView.setVisibility(View.VISIBLE);
                                nitImageView.setVisibility(View.VISIBLE);
                            }
                        } else if (s != null && s.toString().equalsIgnoreCase("show_week_view")) {
                            if (dayViewWidget.getVisibility() == View.VISIBLE) {
                                PieMenu.setVisibility(View.VISIBLE);
                                dayViewWidget.setVisibility(View.GONE);
                                mornImageView.setVisibility(View.GONE);
                                aftnImageView.setVisibility(View.GONE);
                                eveImageView.setVisibility(View.GONE);
                                nitImageView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}

package com.example.week1.ui.calendar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.week1.R;
import com.example.week1.ui.calendar.decoraters.EventDecorator;
import com.example.week1.ui.calendar.decoraters.OneDayDecorator;
import com.example.week1.ui.calendar.decoraters.SaturdayDecorator;
import com.example.week1.ui.calendar.decoraters.SundayDecorator;
import com.example.week1.ui.main.PageViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class TabFragment3 extends Fragment {
    static final int REQ_ADD_CALENDAR = 1234 ;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    MaterialCalendarView materialCalendarView;
    private PageViewModel pageViewModel;

    public TabFragment3() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 3;
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_calendar, container, false);
        ImageButton button1 = (ImageButton) root.findViewById(R.id.imageButton1);
        button1.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view){

                    Intent intent = new Intent(getActivity(), AddEvent.class) ;
                    startActivity(intent);
                    //TODO
                    /*
                    startActivityForResult(intent, REQ_ADD_CALENDAR);
                    */
            }
        });

        materialCalendarView = (MaterialCalendarView) root.findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        String[] result = {"2017,03,18", "2017,04,18", "2017,05,18", "2017,06,18"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "," + Month + "," + Day;

                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();

                Toast.makeText(getActivity().getApplicationContext(), shot_Day, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
    //TODO 아래
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode){
            case REQ_ADD_CALENDAR:{
                if (resultCode == Activity.RESULT_OK){
                    CalendarDBAdapter db = new CalendarDBAdapter(getActivity());

                    String date = intent.getStringExtra("event_date");
                    String time = intent.getStringExtra("event_time");
                    String location = intent.getStringExtra("event_location");
                    String event = intent.getStringExtra("event_event");

                    EventItem eventItem = new EventItem();
                    eventItem.setEvent_Date(date);
                    eventItem.setEvent_Time(time);
                    eventItem.setEvent_Location(location);
                    eventItem.setEvent_Event(event);

                    db.insert_event(date,time,location,event);
                    //TODO 여기까지 함 앞으로 calendar DB구축 하기 위해 Ctrct 만들어야 함
                    ArrayList<EventItem> new_event_items = load_events();

                    int pos = new_event_items.indexOf(eventItem);

                    contact_items.add(pos, contactItem);

                    adapter.onActivityResult(REQ_ADD_CONTACT,1);
                    break;
                    //TODO
                }
            }

            case REQ_EDIT_CONTACT:{
                if (resultCode == Activity.RESULT_OK){

                    ContactDBAdapter db = new ContactDBAdapter(getActivity());
                    int pos = intent.getIntExtra("position",-1);

                    String new_name = intent.getStringExtra("contact_name");
                    String new_number = intent.getStringExtra("contact_number");

                    ContactItem contactItem = contact_items.get(pos);

                    String name = contactItem.getUser_Name();
                    String number = contactItem.getUser_phNumber();

                    contactItem.setUser_Name(new_name);
                    contactItem.setUser_phNumber(new_number);

                    db.update_contact(name,number,new_name,new_number);

                    //TODO : ASCENDING ORDER WHEN EDIT
                    //ArrayList<ContactItem> new_contact_items = load_contacts();
                    //int new_pos = new_contact_items.indexOf(contactItem);

                    contact_items.set(pos, contactItem);

                    adapter.onActivityResult(REQ_EDIT_CONTACT,1);
                    break;
                }
            }
        }
    }
    */
    //특정 날짜에 효과 표시 기능 AsyncTask는
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.length; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,getActivity()));
        }
    }
}
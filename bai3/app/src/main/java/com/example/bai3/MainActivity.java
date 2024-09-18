package com.example.bai3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    private TimePicker timePicker;
    private EditText etMinutes;
    private Button btnSetAlarm, btnStopAlarm;
    private TextView tvStatus;
    private Vibrator vibrator;
    private boolean isAlarmSet = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần giao diện
        timePicker = findViewById(R.id.timePicker);
        etMinutes = findViewById(R.id.etMinutes);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnStopAlarm = findViewById(R.id.btnStopAlarm);
        tvStatus = findViewById(R.id.tvStatus);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator == null) {
            Toast.makeText(this, "Thiết bị không hỗ trợ rung", Toast.LENGTH_SHORT).show();
        }

        // Thiết lập chế độ 24 giờ cho TimePicker
        timePicker.setIs24HourView(true);

        // Đặt báo thức
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minutesStr = etMinutes.getText().toString();
                int minutes;

                try {
                    minutes = Integer.parseInt(minutesStr);
                    if (minutes <= 0) {
                        Toast.makeText(MainActivity.this, "Nhập số phút hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Nhập số phút hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy thời gian từ TimePicker
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                long selectedTime = calendar.getTimeInMillis();

                // Tính thời gian báo thức
                long currentTime = System.currentTimeMillis();
                long alarmTime = selectedTime + minutes * 60 * 1000;

                // Kiểm tra nếu thời gian báo thức nằm trong quá khứ
                if (alarmTime < currentTime) {
                    Toast.makeText(MainActivity.this, "Thời gian báo thức đã qua. Vui lòng chọn thời gian trong tương lai.", Toast.LENGTH_SHORT).show();
                    return;
                }

                startAlarm(alarmTime);
            }
        });

        // Dừng báo thức
        btnStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
    }

    // Bắt đầu báo thức
    private void startAlarm(long alarmTime) {
        if (isAlarmSet) return;

        // Hiển thị nút STOP và cập nhật trạng thái
        btnStopAlarm.setVisibility(View.VISIBLE);
        tvStatus.setText("Báo thức đã đặt!");
        isAlarmSet = true;

        long delay = alarmTime - System.currentTimeMillis();
        if (delay <= 0) {
            Toast.makeText(this, "Thời gian báo thức đã qua", Toast.LENGTH_SHORT).show();
            stopAlarm();
            return;
        }

        Log.d("MainActivity", "Delay for alarm: " + delay); // Log thời gian trì hoãn

        // Dùng Handler để bắt đầu rung báo thức
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAlarmSet) {
                    vibrator.vibrate(1000); // Rung 1 giây
                    Toast.makeText(MainActivity.this, "Báo thức đang hoạt động!", Toast.LENGTH_SHORT).show(); // Thông báo khi báo thức hoạt động
                }
            }
        }, delay);

        Toast.makeText(this, "Báo thức sẽ kêu sau " + (delay / 1000) + " giây", Toast.LENGTH_SHORT).show();
    }

    // Dừng báo thức
    private void stopAlarm() {
        if (!isAlarmSet) return;

        vibrator.cancel();
        tvStatus.setText("Báo thức đã dừng!");
        btnStopAlarm.setVisibility(View.GONE);
        isAlarmSet = false;
    }
}
package yann.serversocketdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Thread thread;
    private Runnable runnable;
    private TextView tvMeaasge;
    private LineChart chart;

    private LineData lineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMeaasge = findViewById(R.id.tv_message);
        chart = findViewById(R.id.chart);

        initChart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runnable = new ServerSocketRunable();
        thread = new Thread(runnable);
        thread.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent messageEvent) {
        tvMeaasge.setText(messageEvent.getMessage());
        addEntry2Chart((byte[]) messageEvent.getObject());
    }

    private void initChart() {
        chart.setNoDataText("暂时还没数据");
        chart.setBackgroundColor(getResources().getColor(R.color.color_black));
        //如果是只显示一条线，直接传dataSet就可以了
        lineData = new LineData();
        //设置边框是否绘制，边框线的粗细
        //chart.setDrawBorders(true);
        //chart.setBorderWidth(1);

        // 数据显示的颜色
        lineData.setValueTextColor(Color.WHITE);

        // 先增加一个空的数据，随后往里面动态添加
        chart.setData(lineData);

        /*图表的缩放*/
        //图表是否允许Y轴缩放

        //chart.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        // chart.setScaleXEnabled(true);  // X轴上的缩放,默认true
        // chart.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        chart.setPinchZoom(true);  // X,Y轴同时缩放
        chart.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true


        /*轴与网格线*/
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(true);//x轴上的数值是否显示
        xAxis.setDrawAxisLine(true);//是否绘制X轴
        xAxis.setDrawGridLines(true);//是否绘制X轴的网格线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴的位置
        chart.getAxisRight().setEnabled(false);//取消y轴的右侧
        chart.getAxisLeft().setDrawGridLines(false);//是否绘制Y轴的网格线
    }

    private void addEntry2Chart(byte[] realBytes) {
        //装数据，一条线上所有的数据点的集合
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < realBytes.length; i++) {
            Entry entry = new Entry();
            entry.setX(i);
            entry.setY(TypeUtil.byte2Int(realBytes[i]));
            entries.add(entry);
        }

        //给这条线起个名字，并对文字颜色、大小做一些设置
        LineDataSet dataSet = new LineDataSet(entries, "xxxx");
        dataSet.setColor(Color.BLUE);//线的颜色

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class ServerSocketRunable implements Runnable {

        @Override
        public void run() {
            ServerSocketPoolTest socketPoolTest = new ServerSocketPoolTest();
            socketPoolTest.testCommon();
        }
    }
}

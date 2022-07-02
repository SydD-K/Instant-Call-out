package com.huawei.audiodevicekit.bluetoothsample.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.audiobluetooth.api.Cmd;
import com.huawei.audiobluetooth.api.data.Gyro;
import com.huawei.audiobluetooth.api.data.SensorData;
import com.huawei.audiobluetooth.layer.protocol.mbb.DeviceInfo;
import com.huawei.audiobluetooth.utils.DateUtils;
import com.huawei.audiobluetooth.utils.LocaleUtils;
import com.huawei.audiobluetooth.utils.LogUtils;
import com.huawei.audiodevicekit.MyActivity;
import com.huawei.audiodevicekit.R;
import com.huawei.audiodevicekit.bluetoothsample.contract.SampleBtContract;
import com.huawei.audiodevicekit.bluetoothsample.presenter.SampleBtPresenter;
import com.huawei.audiodevicekit.bluetoothsample.view.adapter.SingleChoiceAdapter;
import com.huawei.audiodevicekit.mvp.view.support.BaseAppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SampleBtActivity
    extends BaseAppCompatActivity<SampleBtContract.Presenter, SampleBtContract.View>
    implements SampleBtContract.View {
    private static final String TAG = "SampleBtActivity";

    public static  int flag=0;

    public static String strup="https://www.bilibili.com/";
    public static String strdown="https://pintia.cn/problem-sets?tab=0";
    public static String strleft="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fupload.taihainet.com%2F2021%2F0724%2F1627082814834.jpg&refer=http%3A%2F%2Fupload.taihainet.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1659256248&t=7a7bb6f63a99866334aa71a9e3a8b9b9";
    public static String strright="http://www.baidu.com/";

    public int choose=0xFFFFD480;
    public int normal=0xFFFFFFFF;

    public static  double oumega = 0.0;
    public static double updown=0.0;
    private TextView tvmyview;

    private TextView tvDevice;

    private TextView tvStatus;

    //private ListView listView;

    private TextView tvSendCmdResult;

    private Button btnSearch;

    public Button buttonup;
    public Button buttonnext;
    public Button buttonleft;
    public Button buttonright;
    public Button buttondown;

    private Button btnConnect;

    private Button btnDisconnect;

    private Spinner spinner;

    private Button btnSendCmd;

    private RecyclerView rvFoundDevice;

    private SingleChoiceAdapter mAdapter;

    private Cmd mATCmd = Cmd.VERSION;

    private String mMac;

    private List<Map<String, String>> maps;

    private SimpleAdapter simpleAdapter;

    private TextView tvDataCount;

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public SampleBtContract.Presenter createPresenter() {
        return new SampleBtPresenter();
    }

    @Override
    public SampleBtContract.View getUiImplement() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        oumega=0;
        buttonnext=(Button)findViewById(R.id.mybotton);
        buttonup = (Button)findViewById(R.id.buttonup);
        buttondown=(Button)findViewById(R.id.buttondown);
        buttonleft=(Button)findViewById(R.id.buttonleft);
        buttonright=(Button)findViewById(R.id.buttonright);
        tvDevice = findViewById(R.id.tv_device);
        tvStatus = findViewById(R.id.tv_status);
        //tvDataCount = findViewById(R.id.tv_data_count);
        //listView = findViewById(R.id.listview);
        tvSendCmdResult = findViewById(R.id.tv_send_cmd_result);
        btnSearch = findViewById(R.id.btn_search);
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        spinner = findViewById(R.id.spinner);
        btnSendCmd = findViewById(R.id.btn_send_cmd);
        rvFoundDevice = findViewById(R.id.found_device);
        initSpinner();
        initRecyclerView();
        maps = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, maps, android.R.layout.simple_list_item_1,
            new String[] {"data"}, new int[] {android.R.id.text1});
        buttonup.setBackgroundResource(R.drawable.button_circle_shape);
        buttondown.setBackgroundResource(R.drawable.button_circle_shape);
        buttonright.setBackgroundResource(R.drawable.button_circle_shape);
        buttonleft.setBackgroundResource(R.drawable.button_circle_shape);
        //listView.setAdapter(simpleAdapter);
    }

    private void initSpinner() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Cmd cmd : Cmd.values()) {
            if (cmd.isEnable()) {
                HashMap<String, String> map = new HashMap<>();
                Boolean isChinese = LocaleUtils.isChinese(this);
                String name = isChinese ? cmd.getNameCN() : cmd.getName();
                map.put("title", cmd.getType() + "-" + name);
                data.add(map);
            }
        }
        spinner.setAdapter(
            new SimpleAdapter(this, data, R.layout.item_spinner, new String[] {"title"},
                new int[] {R.id.tv_name}));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i(TAG, "onItemSelected position = " + position);
                String title = data.get(position).get("title");
                String type = Objects.requireNonNull(title).split("-")[0];
                try {
                    int typeValue = Integer.parseInt(type);
                    mATCmd = Cmd.getATCmdByType(typeValue);
                } catch (NumberFormatException e) {
                    LogUtils.e(TAG, "parseInt fail e = " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.i(TAG, "onNothingSelected parent = " + parent);
            }
        });
    }

    private void initRecyclerView() {
        SingleChoiceAdapter.SaveOptionListener mOptionListener = new SingleChoiceAdapter.SaveOptionListener() {
            @Override
            public void saveOption(String optionText, int pos) {
                LogUtils.i(TAG, "saveOption optionText = " + optionText + ",pos = " + pos);
                mMac = optionText.substring(1, 18);
                boolean connected = getPresenter().isConnected(mMac);
                if (connected) {
                    getPresenter().disConnect(mMac);
                } else {
                    getPresenter().connect(mMac);
                }
            }

            @Override
            public void longClickOption(String optionText, int pos) {
                LogUtils.i(TAG, "longClickOption optionText = " + optionText + ",pos = " + pos);
            }
        };
        mAdapter = new SingleChoiceAdapter(this, new ArrayList<>());
        mAdapter.setSaveOptionListener(mOptionListener);
        rvFoundDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvFoundDevice.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        getPresenter().initBluetooth(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String ssup=data.getStringExtra("data_return");
                    btnConnect.setText(ssup);
                }
                break;
            default:
        }
    }

    @Override
    protected void setOnclick() {
        super.setOnclick();
        btnConnect.setOnClickListener(v -> getPresenter().connect(mMac));
        btnDisconnect.setOnClickListener(v -> getPresenter().disConnect(mMac));
        btnSendCmd.setOnClickListener(v -> getPresenter().sendCmd(mMac, mATCmd.getType()));
        btnSearch.setOnClickListener(v -> getPresenter().checkLocationPermission(this));
        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SampleBtActivity.this,MyActivity.class);
                startActivityForResult(intent,1);
            }
        });
        buttonup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SampleBtActivity.this,"You clicked Button up",Toast.LENGTH_SHORT).show();
            }
        });
        buttondown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SampleBtActivity.this,"You clicked Button down",Toast.LENGTH_SHORT).show();
            }
        });
        buttonleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SampleBtActivity.this,"You clicked Button left",Toast.LENGTH_SHORT).show();
            }
        });
        buttonright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SampleBtActivity.this,"You clicked Button right",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getPresenter().processLocationPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onDeviceFound(DeviceInfo info) {
        if (mAdapter == null) {
            return;
        }
        runOnUiThread(() -> mAdapter
            .pushData(String.format("[%s] %s", info.getDeviceBtMac(), "HUAWEI Eyewear")));
    }

    @Override
    public void onStartSearch() {
        if (mAdapter != null) {
            runOnUiThread(() -> mAdapter.clearData());
        }
    }

    @Override
    public void onDeviceChanged(BluetoothDevice device) {
        if (tvDevice != null) {
            runOnUiThread(() -> tvDevice
                .setText(String.format("[%s] %s", device.getAddress(), "HUAWEI Eyewear")));
        }
    }

    @Override
    public void onConnectStateChanged(String stateInfo) {
        if (tvStatus != null) {
            runOnUiThread(() -> tvStatus.setText(stateInfo));
        }
    }

    @Override
    public void onSensorDataChanged(SensorData sensorData) {
        runOnUiThread(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("data", sensorData.toString());
            maps.add(0, map);
            simpleAdapter.notifyDataSetChanged();
        });


        int n=sensorData.getGyroDataLen() ;
        int my_pitch=0;
        for(int i=0;i<n;i++){
            Gyro g=sensorData.getGyroData()[i];
                my_pitch += g.pitch;
        }
        double d;
        if(n==0){
            d=0;
        }else {
            d=my_pitch*1.0/n;
        }
        if(flag<4&&sensorData.knockDetect!=0){
            if(flag==0){
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(strleft));
                startActivity(intent);
            }
            else if(flag==1){
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(strup));
                startActivity(intent);
            }
            else if(flag==2){
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(strdown));
                startActivity(intent);
            }
            else if(flag==3){
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(strright));
                startActivity(intent);
            }
            flag=5;
            //finish();
        }
        if(flag==5){
            flag++;
        }
        if(flag>1000){
            flag=0;
        }
        if(sensorData.getGyroData()[1].yaw<-50000){
            flag=1;
            buttonup.setBackgroundResource(R.drawable.button_circle_shape1);
            buttondown.setBackgroundResource(R.drawable.button_circle_shape);
            buttonright.setBackgroundResource(R.drawable.button_circle_shape);
            buttonleft.setBackgroundResource(R.drawable.button_circle_shape);
        }
        else if(sensorData.getGyroData()[1].yaw>50000){
            flag=2;
            buttonup.setBackgroundResource(R.drawable.button_circle_shape);
            buttondown.setBackgroundResource(R.drawable.button_circle_shape1);
            buttonright.setBackgroundResource(R.drawable.button_circle_shape);
            buttonleft.setBackgroundResource(R.drawable.button_circle_shape);
        }
        else if(sensorData.getGyroData()[1].roll>50000){
            flag=3;
            buttonup.setBackgroundResource(R.drawable.button_circle_shape);
            buttondown.setBackgroundResource(R.drawable.button_circle_shape);
            buttonright.setBackgroundResource(R.drawable.button_circle_shape1);
            buttonleft.setBackgroundResource(R.drawable.button_circle_shape);
        }
        else if(sensorData.getGyroData()[1].roll<-50000){
            flag=0;
            buttonup.setBackgroundResource(R.drawable.button_circle_shape);
            buttondown.setBackgroundResource(R.drawable.button_circle_shape);
            buttonright.setBackgroundResource(R.drawable.button_circle_shape);
            buttonleft.setBackgroundResource(R.drawable.button_circle_shape1);
        }
    }

    @Override
    public void onSendCmdSuccess(Object result) {
        runOnUiThread(() -> {
            String info = DateUtils.getCurrentDate() + "\n" + result.toString();
            tvSendCmdResult.setText(info);
        });
    }

    @Override
    public void onError(String errorMsg) {
        runOnUiThread(
            () -> Toast.makeText(SampleBtActivity.this, errorMsg, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().deInit();
    }
}

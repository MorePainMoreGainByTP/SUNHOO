package com.sunhoo.swjtu.sunhoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by tangpeng on 2017/7/19.
 */

public class AddressActivity extends AppCompatActivity {

    Spinner city;
    Spinner province;
    String tProvince;
    String tCity;
    EditText detailAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selector);
        setToolBar();
        detailAddress = (EditText) findViewById(R.id.detailAddress);
        province = (Spinner) findViewById(R.id.provinceSpinner);
        city = (Spinner) findViewById(R.id.citySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.province,
                R.layout.spinner_checked_text);
        province.setAdapter(adapter);
        province.setOnItemSelectedListener(new SpinnerItemSelected());
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tCity = city.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("地址");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void confirm(View v) {
        String address = detailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "输入详细地址", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(tProvince) || tProvince.equals("-省份-")) {
            Toast.makeText(this, "选择省份", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(tCity) || tCity.equals("-城市-")) {
            Toast.makeText(this, "选择城市", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = getIntent().putExtra("province", tProvince).putExtra("city", tCity).putExtra("address", address);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // 二级联动adapter
    class SpinnerItemSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            String pro = (String) spinner.getItemAtPosition(position);
            tProvince = province.getSelectedItem().toString();
            // 处理省的市的显示
            ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.def, R.layout.spinner_checked_text);
            if (pro.equals("北京")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.北京,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("天津")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.天津,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("河北")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.河北,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("山西")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.山西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("内蒙古")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.内蒙古,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("辽宁")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.辽宁,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("吉林")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.吉林,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("黑龙江")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.黑龙江,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("上海")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.上海,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("江苏")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.江苏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("浙江")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.浙江,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("安徽")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.安徽,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("福建")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.福建,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("江西")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.江西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("山东")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.山东,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("河南")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.河南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("湖北")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.湖北,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("湖南")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.湖南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("广东")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.广东,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("广西")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.广西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("海南")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.海南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("重庆")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.重庆,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("四川")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.四川,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("贵州")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.贵州,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("云南")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.云南,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("西藏")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.西藏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("陕西")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.陕西,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("甘肃")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.甘肃,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("青海")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.青海,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("宁夏")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.宁夏,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("新疆")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.新疆,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("台湾")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.台湾,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("香港")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.香港,
                        R.layout.spinner_checked_text);
            } else if (pro.equals("澳门")) {
                cityAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.澳门,
                        R.layout.spinner_checked_text);
            }
            city.setAdapter(cityAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

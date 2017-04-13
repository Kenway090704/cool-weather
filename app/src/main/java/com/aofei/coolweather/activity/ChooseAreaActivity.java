package com.aofei.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aofei.coolweather.R;
import com.aofei.coolweather.db.CoolWeatherDB;
import com.aofei.coolweather.model.City;
import com.aofei.coolweather.model.County;
import com.aofei.coolweather.model.Province;
import com.aofei.coolweather.util.HttpCallbackListener;
import com.aofei.coolweather.util.HttpUtil;
import com.aofei.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenway on 17/4/12 10:04
 * Email : xiaokai090704@126.com
 */

public class ChooseAreaActivity extends AppCompatActivity {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    //ui
    private ListView listView;
    private TextView title_tv;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;

    //data
    private CoolWeatherDB coolWeatherDB;
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    //当前选中的级别
    private int currentLevel;
    //选中的省
    private Province selectProvince;
    //选中的市
    private City selectCity;

    /**
     * 判断是否从WeatherActivity中跳转过来的
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }


        //使用该代码无法去除标题栏`
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_View);
        title_tv = (TextView) findViewById(R.id.title_text);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCities();
                }
                if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }


        });
        queryProvinces();
    }

    /**
     * 查询省列表
     */
    private void queryProvinces() {

        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_tv.setText("Chinese");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }

    }

    /**
     * 从服务器获取相关省
     *
     * @param code
     * @param type
     */
    private void queryFromServer(String code, final String type) {
        String adress;

        if (!TextUtils.isEmpty(code)) {
            adress = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            adress = "http://www.weather.com.cn/data/list3/city.xml";
        }
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void finish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    //获取成功,后将数据封装到数据库中。
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.hanleCityResponse(coolWeatherDB, response, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(coolWeatherDB, response, selectCity.getId());
                }
                if (result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void error(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        HttpUtil.sendHttpRequest(adress, listener);
    }


    /**
     * 查询市列表
     */
    private void queryCities() {
        cityList = coolWeatherDB.loadCitys(selectProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_tv.setText("City");
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询县列表
     */
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounty(selectCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County cou : countyList) {
                dataList.add(cou.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_tv.setText("County");
            currentLevel = LEVEL_COUNTY;

        } else {
            queryFromServer(selectCity.getCityCode(), "county");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY) {
            queryCounties();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}

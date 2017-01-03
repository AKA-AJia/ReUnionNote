package com.jia.fantatic4.reunionnote.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.fantatic4.reunionnote.R;
import com.jia.fantatic4.reunionnote.constant.Constant;
import com.jia.fantatic4.reunionnote.db.CityDB;
import com.jia.fantatic4.reunionnote.db.CountyDB;
import com.jia.fantatic4.reunionnote.db.ProvinceDB;
import com.jia.fantatic4.reunionnote.utils.HttpUtil;
import com.jia.fantatic4.reunionnote.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jia on 2017/1/3.
 */

public class ChooesAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ArrayList<String> dataList = new ArrayList<>();

    private List<ProvinceDB> provinceList;
    private List<CityDB> cityList;
    private List<CountyDB> countyList;
    private ProgressDialog progressDialog;


    private ProvinceDB selectedProvince;
    private CityDB selectedCity;

    private int currentLevel;

    private ArrayAdapter<String> adapter;
    private TextView tv_title;
    private ListView lv_area;
    private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area, container, false);
        tv_title = (TextView) view.findViewById(R.id.tv_area_title);
        lv_area = (ListView) view.findViewById(R.id.lv_area);
        btn = (Button) view.findViewById(R.id.btn_weather_back);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        lv_area.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lv_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCity();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounty();
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        queryProvince();
    }

    /**
     * 查询省份，优先从数据库中获取。
     */
    private void queryProvince() {
        tv_title.setText("中国");
        btn.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(ProvinceDB.class);

        if (provinceList.size()>0){
            dataList.clear();
            for (ProvinceDB provinceDB : provinceList) {
                dataList.add(provinceDB.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_area.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address= Constant.BASE_URL;
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询城市
     */
    private void queryCity() {
        tv_title.setText(selectedProvince.getProvinceName());
        btn.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(CityDB.class);
        if (cityList.size()>0){
            dataList.clear();
            for (CityDB cityDB : cityList) {
                dataList.add(cityDB.getCityName());
            }

            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_CITY;
            lv_area.setSelection(0);
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            String address=Constant.BASE_URL+provinceCode;
            queryFromServer(address,"city");
        }
    }


    /**
     * 查询县
     */
    private void queryCounty(){
        tv_title.setText(selectedCity.getCityName());
        btn.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(CountyDB.class);
        if (countyList.size()>0){
            dataList.clear();
            for (CountyDB countyDB : countyList) {
                dataList.add(countyDB.getCountyName());
            }

            adapter.notifyDataSetChanged();
            currentLevel=LEVEL_COUNTY;
            lv_area.setSelection(0);
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address=Constant.BASE_URL+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 从网络上获取数据
     * @param address 地址
     * @param type 类型
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"加载失败...",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultText=response.body().string();
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(resultText);
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(resultText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(resultText,selectedCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}

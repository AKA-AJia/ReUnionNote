package com.jia.fantatic4.reunionnote.db;

import org.litepal.crud.DataSupport;

/**
 * Created by jia on 2016/12/31.
 */
/**
 * 数据库省份表
 */
public class ProvinceDB extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

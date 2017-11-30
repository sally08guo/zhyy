package com.tphy.zhyycs.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017\11\15 0015.
 */

public class ProductExpand implements Serializable {

    private String PCode;

    private String PName;

    private List<Product> listProduct;

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPCode() {
        return PCode;
    }

    public void setPCode(String PCode) {
        this.PCode = PCode;
    }

    public List<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<Product> listProduct) {
        this.listProduct = listProduct;
    }
}

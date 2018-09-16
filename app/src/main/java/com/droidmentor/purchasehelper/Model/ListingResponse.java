package com.droidmentor.purchasehelper.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jaison.
 */
public class ListingResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("city_listing")
        private ArrayList<City_listing> city_listing;

        public ArrayList<City_listing> getCity_listing() {
            return city_listing;
        }

        public void setCity_listing(ArrayList<City_listing> city_listing) {
            this.city_listing = city_listing;
        }
    }

    public static class City_listing {
        @SerializedName("is_purchase")
        private boolean is_purchase;
        @SerializedName("list_count")
        private int list_count;
        @SerializedName("is_free")
        private boolean is_free;
        @SerializedName("thumb_id")
        private int thumb_id;
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private String id;
        @SerializedName("product_id")
        private String product_id;

        public boolean getIs_purchase() {
            return is_purchase;
        }

        public void setIs_purchase(boolean is_purchase) {
            this.is_purchase = is_purchase;
        }

        public int getList_count() {
            return list_count;
        }

        public void setList_count(int list_count) {
            this.list_count = list_count;
        }

        public boolean getIs_free() {
            return is_free;
        }

        public void setIs_free(boolean is_free) {
            this.is_free = is_free;
        }

        public int getThumb_id() {
            return thumb_id;
        }

        public void setThumb_id(int thumb_id) {
            this.thumb_id = thumb_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }
    }
}

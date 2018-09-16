package com.droidmentor.purchasehelper.Util;

import android.text.TextUtils;
import android.util.Log;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.droidmentor.purchasehelper.Model.ListingResponse;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jaison.
 */
public class SearchHelper {


    // To get the index of the item which has the specified value in the given list

    public static int getSkuDetailsPosition(String productID, List<SkuDetails> skuListings) {
        Collection<SkuDetails> skuDetailsCollection = Collections2.filter(skuListings, skuListingItem -> {
            assert skuListingItem != null;
            return skuListingItem.getSku().equals(productID);
        });

        ArrayList<SkuDetails> skuDetailsArrayList = new ArrayList<>(skuDetailsCollection);

        if (skuDetailsArrayList.size() > 0)
            return skuListings.indexOf(skuDetailsArrayList.get(0));
        else
            return -1;
    }

    // To get the index of the item which has the specified value in the given list

    public static int getPurchaseDetailsPosition(String productID, List<Purchase> purchaseListing) {
        Collection<Purchase> purchaseItemCollection = Collections2.filter(purchaseListing, purchaseListingItem -> {
            assert purchaseListingItem != null;
            return purchaseListingItem.getSku().equals(productID);
        });

        ArrayList<Purchase> purchaseList = new ArrayList<>(purchaseItemCollection);

        if (purchaseList.size() > 0)
            return purchaseListing.indexOf(purchaseList.get(0));
        else
            return -1;
    }

    // To get the index of the item which has the specified value in the given list

    public static int getCityItemPosition(String productID, ArrayList<ListingResponse.City_listing> cityListing) {
        Collection<ListingResponse.City_listing> cityCollection = Collections2.filter(cityListing, cityListingItem -> {
            assert cityListingItem != null;
            return cityListingItem.getProduct_id().equals(productID);
        });

        ArrayList<ListingResponse.City_listing> skuDetailsArrayList = new ArrayList<>(cityCollection);

        if (skuDetailsArrayList.size() > 0)
            return cityListing.indexOf(skuDetailsArrayList.get(0));
        else
            return -1;
    }



    // To get the Playstore product Ids from the city array list

    public static List<String> getPremiumCityProductIdListing(ArrayList<ListingResponse.City_listing> premiumListing) {

        List<String> skuList = Stream.of(premiumListing).filter(isPaidCollection()).map(ListingResponse.City_listing::getProduct_id).collect(Collectors.toList());
        Log.d("Predicate", "getPremiumCity predicate:" + skuList.size());
        System.out.println(skuList);

        return skuList;
    }

    // To filter the premium items from the given collection

    private static Predicate<ListingResponse.City_listing> isPaidCollection() {
        return premiumCityItem -> !premiumCityItem.getIs_free() && !TextUtils.isEmpty(premiumCityItem.getProduct_id());
    }


    // To get the Playstore product Ids from the purchase array list

    public static List<String> getPurchasedProductIdListing(List<Purchase> purchaseList) {

        List<String> purchasedKkuList = Stream.of(purchaseList).map(Purchase::getSku).collect(Collectors.toList());
        Log.d("Predicate", "getPurchasedProductIdListing predicate:" + purchasedKkuList.size());
        System.out.println(purchasedKkuList);

        return purchasedKkuList;
    }

    // To filter the already purchased items from the list using the playstore purchase Id's list

    public static ArrayList<ListingResponse.City_listing> getAlreadyPurchasedCities(ArrayList<ListingResponse.City_listing> premiumListing, List<String> purchasedSkuList) {
        Collection<ListingResponse.City_listing> premiumCityCollection = Collections2.filter(premiumListing, premiumListingItem -> {
            assert premiumListingItem != null;
            return purchasedSkuList.contains(premiumListingItem.getProduct_id());
        });

        return new ArrayList<>(premiumCityCollection);
    }

}

package com.droidmentor.purchasehelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.droidmentor.purchasehelper.ClickListener.CityListingClickListener;
import com.droidmentor.purchasehelper.Model.ListingResponse;
import com.droidmentor.purchasehelper.Util.JSONHelper;
import com.droidmentor.purchasehelper.Util.PurchaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.droidmentor.purchasehelper.Util.SearchHelper.getAlreadyPurchasedCities;
import static com.droidmentor.purchasehelper.Util.SearchHelper.getCityItemPosition;
import static com.droidmentor.purchasehelper.Util.SearchHelper.getPremiumCityProductIdListing;
import static com.droidmentor.purchasehelper.Util.SearchHelper.getPurchasedProductIdListing;

public class PurchaseActivity extends AppCompatActivity {

    String TAG = "PurchaseActivity";

    @BindView(R.id.rvListing)
    RecyclerView rvListing;
    CityListingAdapter cityListingAdapter;
    ArrayList<ListingResponse.City_listing> placeData = new ArrayList<>();

    PurchaseHelper purchaseHelper;

    // To store the purchased items
    List<Purchase> purchaseHistory;

    boolean isPurchaseQueryPending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        ButterKnife.bind(this);

        // To instantiate the object
        purchaseHelper = new PurchaseHelper(this, getPurchaseHelperListener());
        setupList();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (purchaseHelper != null)
            purchaseHelper.endConnection();
    }

    /**
     * To setup the recycler view
     */
    private void setupList() {
        cityListingAdapter = new CityListingAdapter(this, placeData, getItemClickListener());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        rvListing.setLayoutManager(gridLayoutManager);
        rvListing.setAdapter(cityListingAdapter);


    }

    /**
     * 1. To load the data from the assets file
     * 2. Get purchases details for all the items bought within your app.
     */
    private void loadData() {
        placeData = JSONHelper.getCityListings(this, "city_list");
        cityListingAdapter.placeData = placeData;
        cityListingAdapter.notifyDataSetChanged();

        if (purchaseHelper != null && purchaseHelper.isServiceConnected())
            purchaseHelper.getPurchasedItems(BillingClient.SkuType.INAPP);
        else
            isPurchaseQueryPending = true;
    }

    //

    /**
     * To update the purchase status based on the play-billing library results
     *
     * @param alreadyPurchasedItems List of purchased items, which is filter from
     *                              all items based on the purchase status which is returned from the library
     */
    public void updatePurchaseStatus(ArrayList<ListingResponse.City_listing> alreadyPurchasedItems) {
        for (ListingResponse.City_listing purchasedItem : alreadyPurchasedItems) {
            int itemIndex = placeData.indexOf(purchasedItem);

            if (itemIndex >= 0)
                placeData.get(itemIndex).setIs_purchase(true);
        }

        cityListingAdapter.notifyDataSetChanged();
    }

    /**
     * Your listener to handle the click actions of the listing cards
     */
    public CityListingClickListener getItemClickListener() {

        return new CityListingClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onItemPurchaseClick(View v, int position) {
                purchaseHelper.launchBillingFLow(BillingClient.SkuType.INAPP, placeData.get(position).getProduct_id());
            }
        };
    }

    /**
     * Your listener to handle the various responses of the Purchase helper util
     */
    public PurchaseHelper.PurchaseHelperListener getPurchaseHelperListener() {
        return new PurchaseHelper.PurchaseHelperListener() {
            @Override
            public void onServiceConnected(int resultCode) {
                Log.d(TAG, "onServiceConnected: " + resultCode);

                if (isPurchaseQueryPending) {
                    purchaseHelper.getPurchasedItems(BillingClient.SkuType.INAPP);
                    isPurchaseQueryPending = false;
                }

            }

            @Override
            public void onSkuQueryResponse(List<SkuDetails> skuDetails) {
                cityListingAdapter.setSkuList(skuDetails);
            }

            @Override
            public void onPurchasehistoryResponse(List<Purchase> purchasedItems) {
                purchaseHistory = purchasedItems;

                if (purchaseHistory != null) {

                    // SkuList which is filtered from the city listing

                    List<String> skuList = getPremiumCityProductIdListing(placeData);

                    List<String> tempSkuList = new ArrayList<>(skuList);

                    // SkuList which is filtered from the purchase history

                    List<String> purchasedSkuList = getPurchasedProductIdListing(purchaseHistory);

                    tempSkuList.retainAll(purchasedSkuList);

                    System.out.println("Already Purchased " + tempSkuList);

                    ArrayList<ListingResponse.City_listing> alreadyPurchasedPlaces = getAlreadyPurchasedCities(placeData, tempSkuList);

                    updatePurchaseStatus(alreadyPurchasedPlaces);

                    skuList.removeAll(purchasedSkuList);

                    System.out.println("Yet to purchase " + skuList);

                    // To make the request to get the pending SkuDetails

                    if (skuList.size() > 0)
                        purchaseHelper.getSkuDetails(skuList, BillingClient.SkuType.INAPP);

                }
            }

            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

                Log.d(TAG, "onPurchasesUpdated: " + responseCode);
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null)
                {

                    int itemIndex = getCityItemPosition(purchases.get(0).getSku(),placeData);

                    if (itemIndex >= 0)
                    {
                        placeData.get(itemIndex).setIs_purchase(true);
                        cityListingAdapter.placeData = placeData;
                        cityListingAdapter.notifyDataSetChanged();
                    }

                }


            }
        };
    }
}

package com.droidmentor.purchasehelper;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.SkuDetails;
import com.droidmentor.purchasehelper.ClickListener.CityListingClickListener;
import com.droidmentor.purchasehelper.Model.ListingResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.droidmentor.purchasehelper.CityListingAdapter.PriceStatus.AVAILABLE;
import static com.droidmentor.purchasehelper.CityListingAdapter.PriceStatus.FREE;
import static com.droidmentor.purchasehelper.CityListingAdapter.PriceStatus.PURCHASE;
import static com.droidmentor.purchasehelper.Util.SearchHelper.getSkuDetailsPosition;


/**
 * Created by Jaison.
 */
public class CityListingAdapter extends RecyclerView.Adapter {


    private String TAG = "CityListingAdapter";

    private Context context;
    ArrayList<ListingResponse.City_listing> placeData;
    private CityListingClickListener cityListingClickListener;

    private List<SkuDetails> listSkuDetails;



    public CityListingAdapter(Activity activity, ArrayList<ListingResponse.City_listing> placeData,
                              CityListingClickListener cityListingClickListener) {
        this.context = activity;
        this.placeData = placeData;
        this.cityListingClickListener = cityListingClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_city, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder type_item = (ViewHolder) holder;
        final ListingResponse.City_listing cityItem = placeData.get(position);

        type_item.tvCollectionName.setText(cityItem.getName());


        setListingCount(type_item.tvListingCount, cityItem.getList_count());
        setPriceStatus(type_item.tvPricingStatus, getPriceStatus(cityItem.getIs_free(), cityItem.getIs_purchase()), cityItem.getProduct_id());
        setBannerImage(type_item.ivBanner, cityItem.getThumb_id());

        type_item.cvCollectionItem.setOnClickListener(view -> {
            if (cityListingClickListener != null)
                cityListingClickListener.onItemClick(view, type_item.getAdapterPosition());
        });

        type_item.llPurchase.setOnClickListener(view -> {

            if (cityListingClickListener != null)
                if (cityItem.getIs_free())
                    cityListingClickListener.onItemClick(view, type_item.getAdapterPosition());
                else
                    cityListingClickListener.onItemPurchaseClick(view, type_item.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return placeData.size();
    }

    // To get the price category
    private PriceStatus getPriceStatus(boolean isFree, boolean isPurchase) {
        if (isFree)
            return FREE;
        else if (isPurchase)
            return AVAILABLE;
        else
            return PURCHASE;
    }

    // To set the price field text based on the category of the item
    private void setPriceStatus(TextView tvPricingStatus, PriceStatus priceStatus, String productID) {
        switch (priceStatus) {
            case FREE:
                tvPricingStatus.setText(context.getString(R.string.li_pricing_state_free));
                tvPricingStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.cl_free_text_color, null));
                break;
            case AVAILABLE:
                tvPricingStatus.setText(context.getString(R.string.li_pricing_state_available));
                tvPricingStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.cl_explore_text_color, null));
                break;
            case PURCHASE:

                tvPricingStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.cl_price_text_color, null));

                if (listSkuDetails != null)
                {
                    int skuIndex = getSkuDetailsPosition(productID, listSkuDetails);

                    if (skuIndex >= 0)
                    {
                        tvPricingStatus.setText(listSkuDetails.get(skuIndex).getPrice());
                        return;
                    }

                }
                tvPricingStatus.setText(context.getString(R.string.li_pricing_state_purchase));
                break;
        }
    }


    // To set the listing count field value
    private void setListingCount(TextView tvListingCount, int count) {

        String listingCount = "";

        if (count > 1)
            listingCount = count + " Listings";
        else if (count == 1)
            listingCount = "1 Listing";

        tvListingCount.setText(listingCount);

    }

    // To set the banner image
    private void setBannerImage(ImageView ivBanner, int thumbID) {
        int drawableID[] = {0, R.drawable.city_1, R.drawable.city_2, R.drawable.city_3, R.drawable.city_4, R.drawable.city_5, R.drawable.city_6};
        if (thumbID < 7)
            ivBanner.setImageResource(drawableID[thumbID]);
        else
            ivBanner.setImageResource(drawableID[1]);

    }

    // To set the SkuDetails list, which will initiate from the controller
    public void setSkuList(List<SkuDetails> skuDetailsList) {
        this.listSkuDetails = skuDetailsList;
        Log.d(TAG, "setSkuList: "+skuDetailsList.size());
        this.notifyDataSetChanged();
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivBanner)
        ImageView ivBanner;
        @BindView(R.id.tvCollectionName)
        TextView tvCollectionName;
        @BindView(R.id.tvListingCount)
        TextView tvListingCount;
        @BindView(R.id.tvPricingStatus)
        TextView tvPricingStatus;
        @BindView(R.id.llPurchase)
        LinearLayout llPurchase;
        @BindView(R.id.cvCollectionItem)
        CardView cvCollectionItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public enum PriceStatus {
        FREE, PURCHASE, AVAILABLE;
    }
}
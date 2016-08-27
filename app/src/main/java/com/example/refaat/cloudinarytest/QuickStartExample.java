package com.example.refaat.cloudinarytest;

/**
 * Created by refaat on 8/26/16.
 */
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;
import com.facebook.ads.sdk.APIException;

public class QuickStartExample {

    public static final String ACCESS_TOKEN = "[Your access token]";
    public static final Long ACCOUNT_ID = 1416468899999999l;
    public static final String APP_SECRET = "[Your app secret]";

    public static final APIContext context = new APIContext(ACCESS_TOKEN, APP_SECRET);
    public static void main(String[] args) {
        try {
            AdAccount account = new AdAccount(ACCOUNT_ID, context);
            Campaign campaign = account.createCampaign()
                    .setName("Java SDK Test Campaign")
                    .setObjective(Campaign.EnumObjective.VALUE_LINK_CLICKS)
                    .setSpendCap(10000L)
                    .setStatus(Campaign.EnumStatus.VALUE_PAUSED)
                    .execute();
            System.out.println(campaign.fetch());
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
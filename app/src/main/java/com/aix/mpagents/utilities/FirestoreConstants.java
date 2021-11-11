package com.aix.mpagents.utilities;

public class FirestoreConstants {


    private static String db_ = "prod_";
    //Merchant
    public static String MPARTNER_MERCHANTS = db_+"merchants";
    public static String MPARTNER_ADDRESSES = "addressses";
    public static String PAYMENT_ACCCOUNTS = "payment_accounts";
    public static String MPARTNER_PUSH_NOTIF = db_+"push_notifications_mp";

    //Products
    public static String MPARTNER_PRODUCTS = db_+"products";
    public static String MPARTNER_MEDIA = "media";

    //Category
    public static String MPARTNER_CATEGORY = db_+"categories";

    //Orders
    public static String MPARTNER_ORDER = db_+"orders";
    //App config
    public static String MPARTNER_APP_CONFIG = "app_config_mpagents";
    public static String MPARTNER_VERSION_CONTROL = "version_control";


}

package com.aix.mpagents.utilities;

public class FirestoreConstants {


    private static String db_ = "test_";
    //Merchant
    public static String MPARTNER_AGENTS = db_+"agents";
    public static String MPARTNER_ADDRESSES = "addressses";
    public static String PAYMENT_ACCCOUNTS = "payment_accounts";
    public static String MPARTNER_PUSH_NOTIF = db_+"push_notifications_mp";

    //Products
    public static String MPARTNER_PRODUCTS = db_+"products";
    public static String MPARTNER_MEDIA = "media";

    //Category
    public static String MPARTNER_PRODUCT_CATEGORY = db_+"product_categories";
    public static String MPARTNER_SERVICE_CATEGORY = db_+"service_categories";


    //Product Type
    public static String MPARTNER_PRODUCT_TYPE = db_+"product_types";


    //Orders
    public static String MPARTNER_ORDER = db_+"orders";
    //App config
    public static String MPARTNER_APP_CONFIG = "app_config_mpartners";
    public static String MPARTNER_VERSION_CONTROL = "version_control";


}

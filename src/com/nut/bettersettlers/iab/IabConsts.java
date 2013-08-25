/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nut.bettersettlers.iab;

/**
 * This class holds global constants that are used throughout the application
 * to support in-app billing.
 */
public class IabConsts {
	public static final String BUY_ALL = "seafarers.all";
	public static final String FAKE_PRODUCT_ID = "android.test.purchased";
	
    // Billing response codes
    public static final long BILLING_RESPONSE_RESULT_OK = 0;
    public static final long BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final long BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final long BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final long BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final long BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final long BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final long BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    // Keys for the responses from InAppBillingService
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";

    // Item type: in-app item
	public static final String ITEM_TYPE_INAPP = "inapp";

    // The possible states of an in-app purchase, as defined by Android Market.
    public enum PurchaseState {
        // Responses to requestPurchase or restoreTransactions.
        PURCHASED,   // User was charged for the order.
        CANCELED,    // The charge failed on the server.
        REFUNDED;    // User received a refund for the order.

        // Converts from an ordinal value to the PurchaseState
        public static PurchaseState valueOf(int index) {
            PurchaseState[] values = PurchaseState.values();
            if (index < 0 || index >= values.length) {
                return CANCELED;
            }
            return values[index];
        }
    }
}

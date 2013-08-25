/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nut.bettersettlers.iab;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public final class Purchase {
    public final String orderId;
    public final String packageName;
    public final String sku;
    public final long purchaseTime;
    public final int purchaseState;
    public final String developerPayload;
    public final String token;
    public final String originalJson;
    public final String signature;

    public Purchase(String jsonPurchaseInfo, String signature) throws JSONException {
        originalJson = jsonPurchaseInfo;
        
        JSONObject o = new JSONObject(originalJson);
        orderId = o.optString("orderId");
        packageName = o.optString("packageName");
        sku = o.optString("productId");
        purchaseTime = o.optLong("purchaseTime");
        purchaseState = o.optInt("purchaseState");
        developerPayload = o.optString("developerPayload");
        token = o.optString("token", o.optString("purchaseToken"));
        
        this.signature = signature;
    }

    @Override
    public String toString() {
    	return String.format("PurchaseInfo: %s", originalJson);
    }
}

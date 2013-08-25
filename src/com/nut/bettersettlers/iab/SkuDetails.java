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
 * Represents an in-app product's listing details.
 */
public final class SkuDetails {
    public final String sku;
    public final String type;
    public final String price;
    public final String title;
    public final String description;
    public final String json;

    public SkuDetails(String jsonSkuDetails) throws JSONException {
        json = jsonSkuDetails;
        JSONObject o = new JSONObject(json);
        sku = o.optString("productId");
        type = o.optString("type");
        price = o.optString("price");
        title = o.optString("title");
        description = o.optString("description");
    }

    @Override
    public String toString() {
        return "SkuDetails:" + json;
    }
}

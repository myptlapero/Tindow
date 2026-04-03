# Free Public API Research for "What to Eat / Where to Go" Tinder-Swiping App
**Date:** 2026-04-03  
**Purpose:** Identify free APIs suitable for Android hackathon project with restaurant/place suggestions and images

---

## Executive Summary

**Recommended Stack for Hackathon Demo:**
- **Food:** TheMealDB (easiest, no auth) + optional Spoonacular (more variety)
- **Places:** Geoapify Places API (best free tier) or OpenTripMap (global coverage)
- **Images:** Unsplash or Pexels API (reliable, free, high quality)
- **AI Enhancement:** Gemini API (free tier with limits, no credit card needed)

---

## 1. Food/Recipe APIs

### **TheMealDB** (RECOMMENDED - Easiest for Hackathon)
- **URL:** https://www.themealdb.com/api.php
- **Free Tier:** ✅ Unlimited requests, no authentication required
- **Data Available:**
  - Meal names, images, full recipes, ingredients, measurements, categories
  - Search by name, ingredient, category, area/cuisine
  - Random meal suggestions (perfect for swipe-based randomization)
- **Integration Difficulty:** Very Easy (simple REST API, JSON responses)
- **Android/Kotlin:** Excellent - straightforward JSON parsing, no special libraries needed
- **API Key Required:** No (default key "1" in URL)
- **Best For:** Core meal/food suggestions, recipe details, ingredients

### **Spoonacular**
- **URL:** https://spoonacular.com/food-api
- **Free Tier:** Limited requests per day (exact limit varies, typically 50-150/day free tier exists)
- **Data Available:**
  - 365,000+ recipes, 86,000+ food products
  - Chain restaurant menu items
  - Nutritional information, ingredient substitutions
  - Natural language recipe search
  - Recipe cost estimation
- **Integration Difficulty:** Easy (REST API, well-documented)
- **Android/Kotlin:** Good - standard REST integration
- **API Key Required:** Yes (free registration needed)
- **Best For:** Diverse recipe pool, restaurant chain menus, nutrition data

### **Open Food Facts**
- **URL:** https://world.openfoodfacts.org/data (data downloads available)
- **Free Tier:** ✅ Completely free, open-source database
- **Data Available:** 3M+ packaged food products, ingredients, nutrition labels
- **Limitation:** Better for packaged foods than restaurants
- **Best For:** Complementary food product data

---

## 2. Places/Attractions/Activities APIs

### **Geoapify Places API** (RECOMMENDED - Best Free Tier)
- **URL:** https://www.geoapify.com/places-api/
- **Free Tier Limits:**
  - 3,000 credits/day (1 credit per 20 places returned, effectively 60K places/day)
  - 5 requests per second (RPS) rate limit
  - Free tier includes category filtering for attractions/restaurants
- **Data Available:**
  - 400+ place categories (restaurants, attractions, parks, entertainment, etc.)
  - Coordinates, opening hours, phone, website
  - Ability to filter by entry-free attractions
  - OpenStreetMap-based data (global coverage)
- **Integration Difficulty:** Easy (REST API with geographic queries)
- **Android/Kotlin:** Excellent - location services integrate naturally
- **API Key Required:** Yes (free registration, included in URL)
- **Best For:** Nearby places/attractions, restaurant discovery by category

### **OpenTripMap API**
- **URL:** https://dev.opentripmap.org/product
- **Free Tier:**
  - Free API key for non-commercial use
  - 10M+ tourist attractions globally documented
  - No mention of hard rate limits in free tier
- **Data Available:**
  - Tourist attractions, facilities, sightseeing points
  - Images and descriptions
  - Coordinates, ratings, contact info
  - Filter by category (museums, restaurants, parks, etc.)
- **Integration Difficulty:** Easy-Moderate (REST API with geographic bounding boxes)
- **Android/Kotlin:** Good - geographic queries map to location screens
- **API Key Required:** Yes (free, lightweight)
- **Best For:** Weekend activities, tourist attractions, diverse place discovery

### **OpenStreetMap/Overpass API**
- **URL:** https://overpass-api.de/
- **Free Tier:** ✅ Completely free, no authentication
- **Data Available:**
  - Any OpenStreetMap POI (restaurants, cafes, parks, museums, etc.)
  - Raw geographic data in XML/JSON
  - Highly customizable queries by tags
- **Integration Difficulty:** Moderate (Requires understanding OSM tagging system)
- **Android/Kotlin:** Good - but requires parsing complex geographic data
- **API Key Required:** No
- **Best For:** Advanced geographic filtering, maximum customization

---

## 3. Image APIs (Food & Places)

### **Unsplash API** (HIGHEST QUALITY)
- **URL:** https://unsplash.com/developers
- **Free Tier:**
  - 50 requests/hour (1,200/day limit)
  - High-resolution images, creative commons license
- **Data Quality:** Professional, artistic food photography
- **Integration Difficulty:** Easy (REST API with search)
- **Android/Kotlin:** Excellent - simple image URL retrieval
- **API Key Required:** Yes (free registration)
- **Limitation:** Cannot hotlink indefinitely; requires proper attribution
- **Best For:** High-quality food images, aesthetic appeal

### **Pexels API** (EASIEST, RECOMMENDED)
- **URL:** https://www.pexels.com/api/
- **Free Tier:** ✅ Unlimited API calls, no rate limits
- **Data Available:** Millions of high-quality free photos (food, places, landscapes)
- **Integration Difficulty:** Very Easy
- **Android/Kotlin:** Excellent - straightforward REST API
- **API Key Required:** Yes (free, no credit card)
- **Advantage:** No rate limiting, easiest integration
- **Best For:** App backend images without worrying about quotas

### **Pixabay API**
- **URL:** https://pixabay.com/api/
- **Free Tier:**
  - 5,000 requests/hour (~120K/day)
  - 2M+ free assets (photos and vectors)
  - Commercial license for all images
- **Integration Difficulty:** Easy
- **Android/Kotlin:** Good
- **API Key Required:** Yes (free)
- **Best For:** Commercial licensing, bulk downloads, caching strategy

---

## 4. AI/LLM APIs for Smart Suggestions

### **Google Gemini API** (RECOMMENDED - No Credit Card)
- **URL:** https://ai.google.dev/gemini-api/
- **Free Tier Limits (March 2026):**
  - **Gemini 2.5 Flash:** 10 requests/minute, 250 daily requests
  - **Gemini 2.5 Flash-Lite:** 15 requests/minute, 1,000 daily requests
  - 1M token context window
  - No credit card required
  - Rate limits reset at midnight PT
- **Use Case:** Generate contextual food/place suggestions based on user preferences
- **Integration Difficulty:** Easy (REST API + JSON)
- **Android/Kotlin:** Excellent - official SDKs available
- **Example Prompt:** "Based on these meal preferences [list], suggest 5 interesting restaurants near [location]"
- **Caveat:** Real-world throughput may be lower during high traffic periods
- **Best For:** Personalized recommendations, multi-query suggestion generation

### **Claude API (Anthropic)**
- **Free Tier:** ❌ No free tier (paid only, $0.25-$5.00 per 1M tokens)
- **Not Recommended:** For hackathon project with limited budget

### **OpenAI ChatGPT API**
- **Free Tier:** ❌ No free tier (paid only, ~$0.50-$15.00 per 1M tokens)
- **Not Recommended:** For hackathon

---

## 5. Comparison Table: Best Hackathon Stack

| Category | Best Choice | Reason |
|----------|------------|--------|
| **Food Suggestions** | TheMealDB | No auth, unlimited requests, perfect for random swipes |
| **Place Discovery** | Geoapify Places | 3K credits/day = huge volume, geographic queries, free tier generous |
| **Images** | Pexels | Unlimited API calls, no rate limits, easiest integration |
| **AI Enhancement (Optional)** | Gemini API | 250-1K daily requests free, no credit card, context-aware suggestions |

---

## 6. Integration Recommendations

### **Minimum Viable Stack (MVP)**
1. **TheMealDB** for food (or random meal generator endpoint)
2. **Pexels API** for food images
3. ✅ **Ready to ship:** ~30 min setup, zero quota concerns

### **Enhanced MVP (Add Places)**
1. TheMealDB (meals)
2. Geoapify Places API (nearby attractions/restaurants)
3. Pexels API (images)
4. ✅ **Setup:** ~1-2 hours, need API keys for Geoapify/Pexels

### **Premium Demo (Add AI)**
1. TheMealDB (meals)
2. Geoapify Places (places)
3. Pexels API (images)
4. **Gemini API** (personalized suggestions based on user swipe history)
5. ✅ **Setup:** ~2-3 hours, showcase smart recommendation engine

---

## 7. Android/Kotlin Integration Notes

### **HTTP Client Recommendation**
- **Retrofit 2** with **Moshi** for JSON parsing (standard for Android)
- **OkHttp** for HTTP client (minimal dependencies)
- **Coroutines** for async API calls

### **Key Considerations**
- All recommended APIs are HTTPS REST endpoints (no special auth beyond API keys)
- Response sizes are small enough for mobile networks
- No WebSocket/gRPC requirements
- Image loading: Use **Glide** or **Coil** library for caching/optimization

### **Rate Limiting Strategy**
- TheMealDB: No limits, poll aggressively for swipe randomization
- Geoapify: Stay under 5 RPS (easy to manage in swipe UI)
- Pexels: No limits, safe for image preloading
- Gemini: Queue requests if hitting 10/min, defer non-critical calls

---

## 8. Implementation Cost/Effort Summary

| Phase | Effort | APIs Needed |
|-------|--------|------------|
| Basic app (5 swipeable meals/places) | 2-3 hours | TheMealDB + Pexels |
| Location-aware suggestions | +1-2 hours | Add Geoapify Places |
| Smart recommendation engine | +2-3 hours | Add Gemini API |
| Full app (all features) | ~6-8 hours | All 4 APIs |

---

## 9. Known Limitations & Workarounds

| API | Limitation | Workaround |
|-----|-----------|-----------|
| TheMealDB | Limited to meals, no restaurants | Combine with Geoapify for restaurant/place variety |
| Geoapify | 3K credits/day | ~60K places/day before needing paid tier; more than enough for demo |
| Pexels | May need to cache images locally | Download images during dev, cache endpoint responses |
| Gemini API | 250 daily requests for Flash | Use Flash-Lite (1K/day) or queue non-critical calls; schedule generation at off-peak times |

---

## 10. Unresolved Questions & Next Steps

1. **User Preferences Persistence:** Will you use local SQLite or need backend sync? (Affects data architecture)
2. **Geolocation:** Will app require precise location data or approximate city selection?
3. **Caching Strategy:** Pre-cache 50 items on startup or fetch on-demand?
4. **Image Fallbacks:** What placeholder for meals/places without images?
5. **Offline Mode:** Should app work without internet (requires pre-caching)?

---

## 11. Recommended Action Items

- [ ] Start with **TheMealDB + Pexels** (simplest, 0 quota risk)
- [ ] Test **Geoapify Places API** free tier with target city (confirm quota sufficiency)
- [ ] Set up **Gemini API** for optional AI enhancements (account creation takes 5 min)
- [ ] Design swipe-to-skip + like/dislike logic before API integration
- [ ] Plan caching layer (Retrofit + OkHttp interceptors) for image URLs

---

## Sources

- [TheMealDB API Documentation](https://www.themealdb.com/api.php)
- [Spoonacular Recipe API](https://spoonacular.com/food-api)
- [Geoapify Places API Documentation](https://apidocs.geoapify.com/docs/places/)
- [OpenTripMap API](https://dev.opentripmap.org/product)
- [Pexels Free Image API](https://www.pexels.com/api/)
- [Unsplash API](https://unsplash.com/developers)
- [Google Gemini API Documentation](https://ai.google.dev/gemini-api/)
- [Gemini API Free Tier Guide (2026)](https://blog.laozhang.ai/en/posts/gemini-api-free-tier)
- [Big List of Free APIs (No Auth Needed)](https://mixedanalytics.com/blog/list-actually-free-open-no-auth-needed-apis/)

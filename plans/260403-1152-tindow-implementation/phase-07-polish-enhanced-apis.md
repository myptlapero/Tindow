# Phase 7: Polish & Enhanced APIs

## Context
- [plan.md](plan.md) | Depends on: Phase 6
- [API Research](../reports/researcher-260403-1152-free-api-research.md)

## Overview
- **Priority:** P2 (do if time allows)
- **Status:** pending
- **Effort:** 1-2 hr

## Requirements
- Visual polish: animations, transitions, app icon
- Pexels API for images on manual text options
- Geoapify for place suggestions (optional)
- Gemini AI for contextual suggestions (optional)

## Implementation Steps

### 1. Visual Polish (30 min)
- Screen transition animations (slide in/out)
- Celebration animation on winner (simple scale + confetti)
- Loading shimmer while fetching API data
- App icon & splash screen (basic)
- Custom theme colors (move away from default purple)

### 2. Pexels Image API (30 min)
- When user adds text-only option, auto-fetch relevant image from Pexels
- API: `GET https://api.pexels.com/v1/search?query={title}&per_page=1`
- Header: `Authorization: {API_KEY}`
- Free tier: unlimited requests
- Create: `PexelsApiService.kt`, add to `RetrofitClient`

### 3. Geoapify Places (30 min) — Optional
- Add "Nearby Places" button in InputScreen
- Requires location permission
- API: `GET https://api.geoapify.com/v2/places?categories=catering&lat={}&lon={}`
- Free: 3K credits/day
- Create: `PlaceApiService.kt`

### 4. Gemini AI Suggestions (30 min) — Optional
- Add "AI Suggest" button
- Send prompt: "Suggest 5 {food/places} options for {context}"
- Parse response into Option objects
- Free: 250-1K requests/day
- Use google-generativeai SDK or REST API

## Todo
- [ ] Add screen transition animations
- [ ] Add winner celebration animation
- [ ] Integrate Pexels for auto-images
- [ ] (Optional) Add Geoapify for places
- [ ] (Optional) Add Gemini for AI suggestions
- [ ] Custom theme colors
- [ ] App icon

## Success Criteria
- App feels polished for demo
- Images auto-load for text-only options
- No crashes on API failures (graceful fallbacks)

package com.example.fproject1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartAiHelper {

    private SmartAiHelper() {}

    public static List<City> rankCitiesByNaturalLanguage(String query, List<City> cities) {
        if (cities == null) return new ArrayList<>();
        if (query == null || query.trim().isEmpty())
            return new ArrayList<>(cities);

        String q = query.toLowerCase(Locale.ROOT);
        Map<String, List<Place>> allData = DataProvider.getAllData();
        List<RankedCity> ranked = new ArrayList<>();

        for (City city : cities) {
            int score = 0;
            String cityName = city.getName().toLowerCase(Locale.ROOT);
            if (q.contains(cityName))
                score += 6;
            if (cityName.contains(q))
                score += 4;

            List<Place> places = allData.get(city.getName());
            if (places != null) {
                for (Place place : places) {
                    score += scoreForPlace(place, q);
                }
            }
            ranked.add(new RankedCity(city, score));
        }
        Collections.sort(ranked, (a, b) -> Integer.compare(b.score, a.score));
        List<City> result = new ArrayList<>();
        for (RankedCity rc : ranked) {
            if (rc.score > 0) result.add(rc.city);
        }
        return result.isEmpty() ? new ArrayList<>(cities) : result;
    }

    public static List<Place> rankPlacesInCityByNaturalLanguage(String query, List<Place> cityPlaces) {
        if (cityPlaces == null)
            return new ArrayList<>();

        List<Place> result = new ArrayList<>(cityPlaces);
        if (query == null || query.trim().isEmpty()) {
            return result;
        }

        String q = query.toLowerCase(Locale.ROOT);
        List<RankedPlace> ranked = new ArrayList<>();
        for (Place place : cityPlaces) {
            ranked.add(new RankedPlace(place, scoreForPlace(place, q)));
        }
        Collections.sort(ranked, (a, b) -> Integer.compare(b.score, a.score));

        result.clear();
        for (RankedPlace rp : ranked) {
            if (rp.score > 0) {
                result.add(rp.place);
            }
        }

        return result.isEmpty() ? new ArrayList<>(cityPlaces) : result;
    }

    public static String generateItinerary(List<Place> places, String prompt) {
        if (places == null || places.isEmpty()) {
            return "No places were found for this city yet.";
        }
        String q = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        boolean lowBudget = q.contains("cheap") || q.contains("budget") || q.contains("free");
        boolean art = q.contains("museum") || q.contains("culture") || q.contains("history");
        boolean nature = q.contains("nature") || q.contains("beach") || q.contains("view");
        boolean night = q.contains("night") || q.contains("evening") || q.contains("late");

        List<Place> sorted = new ArrayList<>(places);
        Collections.sort(sorted, (a, b) -> {
            int aScore = scoreForItinerary(a, lowBudget, art, nature, night);
            int bScore = scoreForItinerary(b, lowBudget, art, nature, night);
            return Integer.compare(bScore, aScore);
        });
        Place morning = sorted.get(0);
        Place afternoon = sorted.size() > 1 ? sorted.get(1) : sorted.get(0);
        Place evening = sorted.size() > 2 ? sorted.get(2) : sorted.get(sorted.size() - 1);
        StringBuilder sb = new StringBuilder();
        sb.append("AI Trip Plan\n");
        sb.append("Morning: ").append(morning.getName()).append(" (").append(morning.getHours()).append(")\n");
        sb.append("Afternoon: ").append(afternoon.getName()).append(" (").append(afternoon.getHours()).append(")\n");
        sb.append("Evening: ").append(evening.getName()).append(" (").append(evening.getHours()).append(")");
        return sb.toString();
    }

    public static String translateForTourist(String text, String language) {
        if (text == null || text.trim().isEmpty()) return "";
        if (language == null || language.equalsIgnoreCase("English")) return text;
        String normalized = language.trim().toLowerCase(Locale.ROOT);
        String knownPhraseTranslation = translateKnownPhrase(text, normalized);
        if (knownPhraseTranslation != null) return knownPhraseTranslation;
        if (text.trim().equalsIgnoreCase("Discover the story, highlights, and useful tips for this destination.")) {
            Map<String, String> directTranslations = new HashMap<>();
            directTranslations.put("Spanish", "Descubre la historia, los aspectos destacados y consejos útiles de este destino.");
            directTranslations.put("French", "Découvrez l'histoire, les points forts et des conseils utiles pour cette destination.");
            directTranslations.put("Arabic", "اكتشف قصة هذه الوجهة وأبرز معالمها ونصائح مفيدة لزيارتها.");
            directTranslations.put("Hebrew", "גלו את הסיפור, נקודות השיא והטיפים השימושיים של היעד הזה.");
            String translated = directTranslations.get(language);
            if (translated != null) return translated;
        }
        if (normalized.equals("spanish")) {
            return translateWithDictionary(text, spanishDictionary());
        }
        if (normalized.equals("french")) {
            return translateWithDictionary(text, frenchDictionary());
        }
        if (normalized.equals("arabic")) {
            return translateWithDictionary(text, arabicDictionary());
        }
        if (normalized.equals("hebrew")) {
            return translateWithDictionary(text, hebrewDictionary());
        }
        return text;
    }

    private static String translateKnownPhrase(String text, String normalizedLanguage) {
        String normalizedText = text.trim().toLowerCase(Locale.ROOT);
        Map<String, String> phraseMap = new HashMap<>();
        if (normalizedLanguage.equals("arabic")) {
            phraseMap.put("historic port with sea view and restaurants", "ميناء تاريخي بإطلالة على البحر مع مطاعم.");
            phraseMap.put("famous open market with food and shopping", "سوق مفتوح مشهور للطعام والتسوق.");
            phraseMap.put("modern street with cafes and architecture", "شارع حديث يضم مقاهي وعمارة مميزة.");
            phraseMap.put("large art museum with international exhibitions", "متحف فني كبير يضم معارض دولية.");
            phraseMap.put("historic religious site with holy places", "موقع ديني تاريخي يضم أماكن مقدسة.");
            phraseMap.put("sacred prayer site", "موقع مقدس للصلاة.");
            phraseMap.put("national museum with archaeology and art", "متحف وطني للآثار والفنون.");
            phraseMap.put("busy food market with local food", "سوق طعام نابض بالحياة يقدم مأكولات محلية.");
            phraseMap.put("beautiful terraced gardens with sea view", "حدائق مدرّجة جميلة بإطلالة على البحر.");
            phraseMap.put("historic street with restaurants and cafes", "شارع تاريخي يضم مطاعم ومقاهي.");
            phraseMap.put("amazing panoramic view of the city", "إطلالة بانورامية مذهلة على المدينة.");
            phraseMap.put("archaeology and history museum", "متحف للآثار والتاريخ.");
            phraseMap.put("snorkeling and coral reef beach", "شاطئ للشعاب المرجانية والغطس السطحي.");
            phraseMap.put("marine life aquarium and observation tower", "حوض أحياء بحرية وبرج للمشاهدة.");
            phraseMap.put("swimming with dolphins experience", "تجربة السباحة مع الدلافين.");
            phraseMap.put("boats, shops, and nightlife", "قوارب ومتاجر وحياة ليلية.");
            phraseMap.put("historic crusader city by the sea", "مدينة صليبية تاريخية على شاطئ البحر.");
            phraseMap.put("underground crusader halls", "قاعات صليبية تحت الأرض.");
            phraseMap.put("fishing port and boat tours", "ميناء صيد وجولات بالقوارب.");
            phraseMap.put("historic underground tunnels", "أنفاق تاريخية تحت الأرض.");
            phraseMap.put("famous christian holy site", "موقع مسيحي مقدس ومشهور.");
            phraseMap.put("traditional arab market", "سوق عربي تقليدي.");
            phraseMap.put("historic water source", "مصدر مياه تاريخي.");
            phraseMap.put("panoramic view of valley", "إطلالة بانورامية على الوادي.");
            phraseMap.put("beautiful lake and beaches", "بحيرة جميلة وشواطئ رائعة.");
            phraseMap.put("ancient hot springs", "ينابيع حارة قديمة.");
            phraseMap.put("lakeside walking area", "ممشى بجانب البحيرة.");
            phraseMap.put("historic church by lake", "كنيسة تاريخية قرب البحيرة.");
            phraseMap.put("hours:", "ساعات العمل:");
            phraseMap.put("fee:", "الرسوم:");
            phraseMap.put("address:", "العنوان:");
        } else if (normalizedLanguage.equals("hebrew")) {
            phraseMap.put("historic port with sea view and restaurants", "נמל היסטורי עם נוף לים ומסעדות.");
            phraseMap.put("famous open market with food and shopping", "שוק פתוח מפורסם עם אוכל וקניות.");
            phraseMap.put("modern street with cafes and architecture", "רחוב מודרני עם בתי קפה ואדריכלות.");
            phraseMap.put("large art museum with international exhibitions", "מוזיאון אמנות גדול עם תערוכות בינלאומיות.");
            phraseMap.put("historic religious site with holy places", "אתר דתי היסטורי עם מקומות קדושים.");
            phraseMap.put("sacred prayer site", "אתר תפילה קדוש.");
            phraseMap.put("national museum with archaeology and art", "מוזיאון לאומי לארכאולוגיה ולאמנות.");
            phraseMap.put("busy food market with local food", "שוק אוכל תוסס עם מאכלים מקומיים.");
            phraseMap.put("beautiful terraced gardens with sea view", "גנים מדורגים יפים עם נוף לים.");
            phraseMap.put("historic street with restaurants and cafes", "רחוב היסטורי עם מסעדות ובתי קפה.");
            phraseMap.put("amazing panoramic view of the city", "תצפית פנורמית מדהימה על העיר.");
            phraseMap.put("archaeology and history museum", "מוזיאון לארכאולוגיה והיסטוריה.");
            phraseMap.put("snorkeling and coral reef beach", "חוף לשנורקלינג ושונית אלמוגים.");
            phraseMap.put("marine life aquarium and observation tower", "אקווריום ימי ומגדל תצפית.");
            phraseMap.put("swimming with dolphins experience", "חוויה של שחייה עם דולפינים.");
            phraseMap.put("boats, shops, and nightlife", "סירות, חנויות וחיי לילה.");
            phraseMap.put("historic crusader city by the sea", "עיר צלבנית היסטורית ליד הים.");
            phraseMap.put("underground crusader halls", "אולמות צלבניים תת-קרקעיים.");
            phraseMap.put("fishing port and boat tours", "נמל דיג וסיורי סירות.");
            phraseMap.put("historic underground tunnels", "מנהרות תת-קרקעיות היסטוריות.");
            phraseMap.put("famous christian holy site", "אתר קדוש נוצרי מפורסם.");
            phraseMap.put("traditional arab market", "שוק ערבי מסורתי.");
            phraseMap.put("historic water source", "מקור מים היסטורי.");
            phraseMap.put("panoramic view of valley", "נוף פנורמי של העמק.");
            phraseMap.put("beautiful lake and beaches", "אגם יפה וחופים.");
            phraseMap.put("ancient hot springs", "מעיינות חמים עתיקים.");
            phraseMap.put("lakeside walking area", "אזור הליכה לצד האגם.");
            phraseMap.put("historic church by lake", "כנסייה היסטורית ליד האגם.");
            phraseMap.put("hours:", "שעות פתיחה:");
            phraseMap.put("fee:", "מחיר:");
            phraseMap.put("address:", "כתובת:");
        } else {
            return null;
        }

        return phraseMap.get(normalizedText);
    }

    private static String translateWithDictionary(String text, Map<String, String> dictionary) {
        String translated = text;
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            String key = entry.getKey();
            if (!key.contains(" ")) continue;
            String pattern = "(?i)(?<!\\p{L})" + Pattern.quote(key) + "(?!\\p{L})";
            translated = translated.replaceAll(pattern, Matcher.quoteReplacement(entry.getValue()));
        }
        Matcher wordMatcher = Pattern.compile("\\p{L}+").matcher(translated);
        StringBuffer sb = new StringBuffer();
        while (wordMatcher.find()) {
            String originalWord = wordMatcher.group();
            String replacement = dictionary.get(originalWord.toLowerCase(Locale.ROOT));
            if (replacement == null) replacement = originalWord;
            wordMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        wordMatcher.appendTail(sb);
        return sb.toString();
    }

    private static Map<String, String> spanishDictionary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Discover the story, highlights, and useful tips for this destination.", "Descubre la historia, los aspectos destacados y consejos útiles de este destino.");
        map.put("Hours:", "Horario:");
        map.put("Fee:", "Precio:");
        map.put("Address:", "Dirección:");
        map.put("historical", "histórico");
        map.put("historic", "histórico");
        map.put("beautiful", "hermoso");
        map.put("large", "gran");
        map.put("famous", "famoso");
        map.put("traditional", "tradicional");
        map.put("port", "puerto");
        map.put("market", "mercado");
        map.put("museum", "museo");
        map.put("street", "calle");
        map.put("gardens", "jardines");
        map.put("view", "vista");
        map.put("restaurants", "restaurantes");
        map.put("cafes", "cafés");
        map.put("food", "comida");
        map.put("with", "con");
        map.put("and", "y");
        map.put("Open", "Abierto");
        map.put("Closed", "Cerrado");
        map.put("Free", "Gratis");
        map.put("Entrance", "Entrada");
        map.put("open", "abierto");
        map.put("closed", "cerrado");
        map.put("free", "gratis");
        map.put("entrance", "entrada");
        map.put("site", "sitio");
        map.put("religious", "religioso");
        map.put("holy", "sagrado");
        map.put("places", "lugares");
        map.put("national", "nacional");
        map.put("archaeology", "arqueología");
        map.put("art", "arte");
        map.put("busy", "animado");
        map.put("local", "local");
        map.put("old", "antiguo");
        map.put("city", "ciudad");
        map.put("sea", "mar");
        map.put("panoramic", "panorámica");
        map.put("history", "historia");
        map.put("beach", "playa");
        map.put("snorkeling", "esnórquel");
        map.put("tours", "tours");
        map.put("by", "junto a");
        map.put("the", "el");
        return map;
    }

    private static Map<String, String> frenchDictionary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Discover the story, highlights, and useful tips for this destination.", "Découvrez l'histoire, les points forts et des conseils utiles pour cette destination.");
        map.put("Hours:", "Horaires :");
        map.put("Fee:", "Tarif :");
        map.put("Address:", "Adresse :");
        map.put("historical", "historique");
        map.put("historic", "historique");
        map.put("beautiful", "beau");
        map.put("large", "grand");
        map.put("famous", "célèbre");
        map.put("traditional", "traditionnel");
        map.put("port", "port");
        map.put("market", "marché");
        map.put("museum", "musée");
        map.put("street", "rue");
        map.put("gardens", "jardins");
        map.put("view", "vue");
        map.put("restaurants", "restaurants");
        map.put("cafes", "cafés");
        map.put("food", "nourriture");
        map.put("with", "avec");
        map.put("and", "et");
        map.put("Open", "Ouvert");
        map.put("Closed", "Fermé");
        map.put("Free", "Gratuit");
        map.put("Entrance", "Entrée");
        map.put("open", "ouvert");
        map.put("closed", "fermé");
        map.put("free", "gratuit");
        map.put("entrance", "entrée");
        map.put("site", "site");
        map.put("religious", "religieux");
        map.put("holy", "sacré");
        map.put("places", "lieux");
        map.put("national", "national");
        map.put("archaeology", "archéologie");
        map.put("art", "art");
        map.put("busy", "animé");
        map.put("local", "local");
        map.put("old", "ancien");
        map.put("city", "ville");
        map.put("sea", "mer");
        map.put("panoramic", "panoramique");
        map.put("history", "histoire");
        map.put("beach", "plage");
        map.put("snorkeling", "plongée");
        map.put("tours", "visites");
        map.put("by", "près de");
        map.put("the", "le");
        return map;
    }

    private static Map<String, String> arabicDictionary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Discover the story, highlights, and useful tips for this destination.", "اكتشف قصة هذه الوجهة وأبرز معالمها ونصائح مفيدة لزيارتها.");
        map.put("Hours:", "ساعات العمل:");
        map.put("Fee:", "الرسوم:");
        map.put("Address:", "العنوان:");
        map.put("historical", "تاريخي");
        map.put("historic", "تاريخي");
        map.put("beautiful", "جميل");
        map.put("large", "كبير");
        map.put("famous", "مشهور");
        map.put("traditional", "تقليدي");
        map.put("port", "ميناء");
        map.put("market", "سوق");
        map.put("museum", "متحف");
        map.put("street", "شارع");
        map.put("gardens", "حدائق");
        map.put("view", "إطلالة");
        map.put("restaurants", "مطاعم");
        map.put("cafes", "مقاهٍ");
        map.put("food", "طعام");
        map.put("with", "مع");
        map.put("and", "و");
        map.put("Open", "مفتوح");
        map.put("Closed", "مغلق");
        map.put("Free", "مجاني");
        map.put("Entrance", "الدخول");
        map.put("open", "مفتوح");
        map.put("closed", "مغلق");
        map.put("free", "مجاني");
        map.put("entrance", "الدخول");
        map.put("site", "موقع");
        map.put("religious", "ديني");
        map.put("holy", "مقدس");
        map.put("places", "أماكن");
        map.put("national", "وطني");
        map.put("archaeology", "آثار");
        map.put("art", "فن");
        map.put("busy", "مزدحم");
        map.put("local", "محلي");
        map.put("old", "قديم");
        map.put("city", "مدينة");
        map.put("sea", "بحر");
        map.put("panoramic", "بانورامي");
        map.put("history", "تاريخ");
        map.put("beach", "شاطئ");
        map.put("snorkeling", "غوص سطحي");
        map.put("tours", "جولات");
        map.put("by", "بجانب");
        map.put("the", "ال");
        return map;
    }

    private static Map<String, String> hebrewDictionary() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Discover the story, highlights, and useful tips for this destination.", "גלו את הסיפור, נקודות השיא והטיפים השימושיים של היעד הזה.");
        map.put("Hours:", "שעות פתיחה:");
        map.put("Fee:", "מחיר:");
        map.put("Address:", "כתובת:");
        map.put("historical", "היסטורי");
        map.put("historic", "היסטורי");
        map.put("beautiful", "יפה");
        map.put("large", "גדול");
        map.put("famous", "מפורסם");
        map.put("traditional", "מסורתי");
        map.put("port", "נמל");
        map.put("market", "שוק");
        map.put("museum", "מוזיאון");
        map.put("street", "רחוב");
        map.put("gardens", "גנים");
        map.put("view", "נוף");
        map.put("restaurants", "מסעדות");
        map.put("cafes", "בתי קפה");
        map.put("food", "אוכל");
        map.put("with", "עם");
        map.put("and", "ו");
        map.put("Open", "פתוח");
        map.put("Closed", "סגור");
        map.put("Free", "חינם");
        map.put("Entrance", "כניסה");
        map.put("open", "פתוח");
        map.put("closed", "סגור");
        map.put("free", "חינם");
        map.put("entrance", "כניסה");
        map.put("site", "אתר");
        map.put("religious", "דתי");
        map.put("holy", "קדוש");
        map.put("places", "מקומות");
        map.put("national", "לאומי");
        map.put("archaeology", "ארכאולוגיה");
        map.put("art", "אמנות");
        map.put("busy", "הומה");
        map.put("local", "מקומי");
        map.put("old", "עתיק");
        map.put("city", "עיר");
        map.put("sea", "ים");
        map.put("panoramic", "פנורמי");
        map.put("history", "היסטוריה");
        map.put("beach", "חוף");
        map.put("snorkeling", "שנורקלינג");
        map.put("tours", "סיורים");
        map.put("by", "ליד");
        map.put("the", "ה");
        return map;
    }

    private static int scoreForItinerary(Place place, boolean lowBudget, boolean art, boolean nature, boolean night) {
        int score = 0;
        String fee = safe(place.getFee());
        String desc = safe(place.getDescription());
        String hours = safe(place.getHours());
        if (lowBudget && isLowBudgetFee(fee)) score += 4;
        if (art && (desc.contains("museum") || desc.contains("history") || desc.contains("art"))) score += 4;
        if (nature && (desc.contains("beach") || desc.contains("view") || desc.contains("sea") || desc.contains("lake"))) score += 4;
        if (night && (hours.contains("24/7") || hours.contains("22:00") || hours.contains("19:00"))) score += 3;
        if (score == 0) score = 1;
        return score;
    }

    private static int scoreForPlace(Place place, String query) {
        int score = 0;
        String name = safe(place.getName());
        String city = safe(place.getCity());
        String desc = safe(place.getDescription());
        String fee = safe(place.getFee());
        String hours = safe(place.getHours());
        String address = safe(place.getAddress());
        if (query.contains(name)) score += 8;
        if (query.contains(city)) score += 6;
        if (name.contains(query)) score += 5;
        for (String token : query.split("\\s+")) {
            if (token.length() < 2) continue;
            if (name.contains(token)) score += 4;
            if (city.contains(token)) score += 4;
            if (desc.contains(token)) score += 3;
            if (address.contains(token)) score += 2;
            if ((token.equals("cheap") || token.equals("budget") || token.equals("free") || token.equals("low-cost"))
                    && isLowBudgetFee(fee)) score += 4;
            if ((token.equals("museum") || token.equals("history") || token.equals("culture")) &&
                    (desc.contains("museum") || desc.contains("history") || desc.contains("archaeology"))) score += 4;
            if ((token.equals("beach") || token.equals("sea") || token.equals("lake") || token.equals("nature")) &&
                    (desc.contains("beach") || desc.contains("sea") || desc.contains("lake") || desc.contains("view"))) score += 4;
            if ((token.equals("night") || token.equals("late") || token.equals("evening")) &&
                    (hours.contains("24/7") || hours.contains("22:00") || hours.contains("19:00"))) score += 3;
        }
        return score;
    }

    private static boolean isLowBudgetFee(String normalizedFee) {
        if (normalizedFee == null || normalizedFee.trim().isEmpty()) return false;
        String fee = normalizedFee.toLowerCase(Locale.ROOT);
        if (fee.contains("free")) return true;
        if (fee.contains("low cost") || fee.contains("low-cost") || fee.contains("budget")) return true;
        Matcher matcher = Pattern.compile("(\\d{1,3})").matcher(fee);
        while (matcher.find()) {
            try {
                int amount = Integer.parseInt(matcher.group(1));
                if (amount <= 30) return true;
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    private static String safe(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private static class RankedCity {
        City city;
        int score;

        RankedCity(City city, int score) {
            this.city = city;
            this.score = score;
        }
    }

    private static class RankedPlace {
        Place place;
        int score;

        RankedPlace(Place place, int score) {
            this.place = place;
            this.score = score;
        }
    }
}
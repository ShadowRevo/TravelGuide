package com.example.fproject1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProvider {

    private static Map<String, List<Place>> data = new HashMap<>();
    static {
        // ================= TEL AVIV =================
        List<Place> telAviv = new ArrayList<>();

        telAviv.add(new Place(
                "Jaffa Port",
                "Tel Aviv",
                "Historic port with sea view and restaurants",
                "09:00 - 22:00",
                "Free",
                "Jaffa, Tel Aviv",
                R.drawable.jaffa_port
        ));

        telAviv.add(new Place(
                "Carmel Market",
                "Tel Aviv",
                "Famous open market with food and shopping",
                "08:00 - 20:00",
                "Free",
                "Allenby St, Tel Aviv",
                R.drawable.carmel_market
        ));

        telAviv.add(new Place(
                "Rothschild Boulevard",
                "Tel Aviv",
                "Modern street with cafes and architecture",
                "24/7",
                "Free",
                "Rothschild Blvd, Tel Aviv",
                R.drawable.rothschild
        ));

        telAviv.add(new Place(
                "Tel Aviv Museum of Art",
                "Tel Aviv",
                "Large art museum with international exhibitions",
                "10:00 - 18:00",
                "~50 ILS",
                "Sderot Sha'ul HaMelech 27",
                R.drawable.museum_of_art
        ));
        data.put("Tel Aviv", telAviv);

        // ================= JERUSALEM =================
        List<Place> jerusalem = new ArrayList<>();

        jerusalem.add(new Place(
                "Old City",
                "Jerusalem",
                "Historic religious site with holy places",
                "24/7",
                "Free",
                "Old City, Jerusalem",
                R.drawable.jerusalem_old_city
        ));

        jerusalem.add(new Place(
                "Western Wall",
                "Jerusalem",
                "Sacred prayer site",
                "24/7",
                "Free",
                "Old City, Jerusalem",
                R.drawable.western_wall
        ));

        jerusalem.add(new Place(
                "Israel Museum",
                "Jerusalem",
                "National museum with archaeology and art",
                "10:00 - 17:00",
                "~54 ILS",
                "Derech Ruppin 11",
                R.drawable.israel_museum
        ));

        jerusalem.add(new Place(
                "Mahane Yehuda Market",
                "Jerusalem",
                "Busy food market with local food",
                "08:00 - 19:00",
                "Free",
                "Agripas St, Jerusalem",
                R.drawable.mahane_yehuda_market
        ));
        data.put("Jerusalem", jerusalem);

        // ================= HAIFA =================
        List<Place> haifa = new ArrayList<>();

        haifa.add(new Place(
                "Baha'i Gardens",
                "Haifa",
                "Beautiful terraced gardens with sea view",
                "09:00 - 17:00",
                "Free",
                "Yefe Nof St, Haifa",
                R.drawable.bahai
        ));

        haifa.add(new Place(
                "German Colony",
                "Haifa",
                "Historic street with restaurants and cafes",
                "24/7",
                "Free",
                "Haifa Port Area",
                R.drawable.german_colony
        ));

        haifa.add(new Place(
                "Louis Promenade",
                "Haifa",
                "Amazing panoramic view of the city",
                "24/7",
                "Free",
                "Haifa",
                R.drawable.promenade
        ));

        haifa.add(new Place(
                "Hecht Museum",
                "Haifa",
                "Archaeology and history museum",
                "10:00 - 16:00",
                "Free",
                "University of Haifa",
                R.drawable.hecht_museum
        ));
        data.put("Haifa", haifa);

        // ================= EILAT =================
        List<Place> eilat = new ArrayList<>();

        eilat.add(new Place(
                "Coral Beach",
                "Eilat",
                "Snorkeling and coral reef beach",
                "08:00 - 18:00",
                "~35 ILS",
                "Southern Eilat",
                R.drawable.coral_beach
        ));

        eilat.add(new Place(
                "Underwater Observatory",
                "Eilat",
                "Marine life aquarium and observation tower",
                "09:00 - 17:00",
                "~99 ILS",
                "Coral Beach, Eilat",
                R.drawable.underwater_observatory
        ));

        eilat.add(new Place(
                "Dolphin Reef",
                "Eilat",
                "Swimming with dolphins experience",
                "09:00 - 16:00",
                "~70 ILS",
                "Eilat coast",
                R.drawable.dolphin_reef
        ));

        eilat.add(new Place(
                "Eilat Marina",
                "Eilat",
                "Boats, shops, and nightlife",
                "24/7",
                "Free",
                "Eilat center",
                R.drawable.marina
        ));
        data.put("Eilat", eilat);

        // ================= AKKO =================
        List<Place> akko = new ArrayList<>();

        akko.add(new Place(
                "Old City Akko",
                "Akko",
                "Historic Crusader city by the sea",
                "09:00 - 18:00",
                "Free",
                "Akko Old City",
                R.drawable.akko_old_city
        ));

        akko.add(new Place("Knights’ Halls",
                "Akko",
                "Underground crusader halls",
                "09:00 - 17:00",
                "~25 ILS",
                "Akko Center",
                R.drawable.knights_halls
        ));

        akko.add(new Place("Akko Port",
                "Akko",
                "Fishing port and boat tours",
                "24/7",
                "Free",
                "Akko Coast",
                R.drawable.akko_port
        ));

        akko.add(new Place("Tunnels of Akko",
                "Akko",
                "Historic underground tunnels",
                "09:00 - 18:00",
                "~20 ILS",
                "Old City",
                R.drawable.tunnels_of_akko
        ));
        data.put("Akko", akko);

        // ================= NAZARETH =================
        List<Place> nazareth = new ArrayList<>();

        nazareth.add(new Place("Basilica of Annunciation",
                "Nazareth",
                "Famous Christian holy site",
                "08:00 - 17:00",
                "Free",
                "Nazareth Center",
                R.drawable.basilica
        ));

        nazareth.add(new Place("Old Market",
                "Nazareth",
                "Traditional Arab market",
                "09:00 - 19:00",
                "Free",
                "City Center",
                R.drawable.nazareth_old_market
        ));

        nazareth.add(new Place("Mary’s Well",
                "Nazareth",
                "Historic water source",
                "24/7",
                "Free",
                "Old City",
                R.drawable.marys_well
        ));

        nazareth.add(new Place("Mount Precipice",
                "Nazareth",
                "Panoramic view of valley",
                "24/7",
                "Free",
                "South Nazareth",
                R.drawable.mount_precipice
        ));
        data.put("Nazareth", nazareth);

        // ================= TIBERIAS =================
        List<Place> tiberias = new ArrayList<>();

        tiberias.add(new Place("Sea of Galilee",
                "Tiberias",
                "Beautiful lake and beaches",
                "24/7",
                "Free",
                "Tiberias Waterfront",
                R.drawable.sea_of_galilee
        ));

        tiberias.add(new Place("Hamat Tiberias",
                "Tiberias",
                "Ancient hot springs",
                "08:00 - 17:00",
                "~40 ILS",
                "South Tiberias",
                R.drawable.hamat
        ));

        tiberias.add(new Place("Tiberias Promenade",
                "Tiberias",
                "Lakeside walking area",
                "24/7",
                "Free",
                "City Center",
                R.drawable.tiberias_promenade
        ));

        tiberias.add(new Place("St. Peter Church",
                "Tiberias",
                "Historic church by lake",
                "08:00 - 18:00",
                "Free",
                "Old City",
                R.drawable.st_peter_church
        ));
        data.put("Tiberias", tiberias);
    }

    public static Map<String, List<Place>> getAllData() {
        return data;
    }

    public static List<Place> getPlacesByCity(String city) {
        if (city == null) return new ArrayList<>();
        List<Place> list = data.get(city);
        if (list == null) return new ArrayList<>();
        return list;
    }
}
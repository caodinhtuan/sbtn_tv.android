package com.sbtn.androidtv.request.datacallback;

import java.util.ArrayList;

/**
 * Created by hoanguyen on 6/14/16.
 */
public class MainMenuDataCallback {
    private ArrayList<Provider> provider;
    private ArrayList<Category> category;

    public ArrayList<Category> getCategory() {
        return category;
    }

    public ArrayList<Provider> getProvider() {
        return provider;
    }

    public static class Provider {
        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public static class Category {
        private String name;
        private int id;
        private int mode;
        private int karaoke;
        private int sort_order;

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public int getMode() {
            return mode;
        }

        public int getKaraoke() {
            return karaoke;
        }

        public int getSort_order() {
            return sort_order;
        }
    }
}

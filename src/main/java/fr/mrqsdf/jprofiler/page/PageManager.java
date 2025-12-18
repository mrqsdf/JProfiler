package fr.mrqsdf.jprofiler.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class PageManager {

    private static PageManager instance;
    private final Map<String, Page> pages;
    private String currentPageId;

    private PageManager() {
        this.pages = new HashMap<>();
        this.currentPageId = null;
    }

    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }

    public static Page createPage(String id, String title) {
        Page page = new Page(id, title);
        getInstance().pages.put(id, page);
        if (getInstance().currentPageId == null) {
            getInstance().currentPageId = id;
        }
        return page;
    }

    public static Page getPage(String id) {
        return getInstance().pages.get(id);
    }

    public static Page getCurrentPage() {
        if (getInstance().currentPageId == null) {
            return null;
        }
        return getInstance().pages.get(getInstance().currentPageId);
    }

    public static void setCurrentPage(String id) {
        if (!getInstance().pages.containsKey(id)) {
            throw new IllegalArgumentException("Page with id '" + id + "' does not exist");
        }
        getInstance().currentPageId = id;
    }

    public static String getCurrentPageId() {
        return getInstance().currentPageId;
    }

    public static boolean pageExists(String id) {
        return getInstance().pages.containsKey(id);
    }

    public static List<Page> getAllPages() {
        return new ArrayList<>(getInstance().pages.values());
    }

    public static void removePage(String id) {
        getInstance().pages.remove(id);
        if (getInstance().currentPageId != null && getInstance().currentPageId.equals(id)) {
            if (!getInstance().pages.isEmpty()) {
                getInstance().currentPageId = getInstance().pages.keySet().iterator().next();
            } else {
                getInstance().currentPageId = null;
            }
        }
    }

    public static void clearAll() {
        getInstance().pages.clear();
        getInstance().currentPageId = null;
    }

    public static void reset() {
        instance = null;
    }
}

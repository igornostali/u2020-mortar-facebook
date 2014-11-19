package co.lemonlabs.mortar.example.data.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.lemonlabs.mortar.example.data.api.model.Image;

final class SortUtil {
    private static final Comparator<Image> TIME  = new Comparator<Image>() {
        @Override
        public int compare(Image lhs, Image rhs) {
            long left = lhs.datetime;
            long right = rhs.datetime;
            return left < right ? 1 : (left == right ? 0 : -1);
        }
    };
    private static final Comparator<Image> VIRAL = new Comparator<Image>() {
        @Override
        public int compare(Image lhs, Image rhs) {
            // Just use views for mock data.
            int left = lhs.views;
            int right = rhs.views;
            return left < right ? 1 : (left == right ? 0 : -1);
        }
    };

    private SortUtil() {
    }

    static void sort(List<Image> images, Sort sort) {
        switch (sort) {
            case TIME:
                Collections.sort(images, TIME);
                break;

            case VIRAL:
                Collections.sort(images, VIRAL);
                break;

            default:
                throw new IllegalArgumentException("Unknown sort: " + sort);
        }
    }
}

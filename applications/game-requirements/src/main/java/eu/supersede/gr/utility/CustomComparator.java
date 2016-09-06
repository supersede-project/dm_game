package eu.supersede.gr.utility;

import java.util.Comparator;

import eu.supersede.gr.model.GamePlayerPoint;

public class CustomComparator implements Comparator<GamePlayerPoint> {
    @Override
    public int compare(GamePlayerPoint o1, GamePlayerPoint o2) {
        return o2.getPoints().compareTo(o1.getPoints());
    }
}
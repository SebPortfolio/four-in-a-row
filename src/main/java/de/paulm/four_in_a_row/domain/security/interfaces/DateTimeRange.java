package de.paulm.four_in_a_row.domain.security.interfaces;

// T muss eine Zeit-Klasse sein, kein beliebiges Comparable!
public interface DateTimeRange<T extends Comparable<? super T>> {
    T getStartAt();

    T getEndAt();
}
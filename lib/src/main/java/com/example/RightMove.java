package com.example;

import java.util.Arrays;

public class RightMove {
    public static void main(String args[]) {
        int[] a = {1, 2, 3, 4, 5};
        rightMove(a, 9);
        System.out.print(Arrays.toString(a));
    }

    private static void rightMove(int[] a, int i) {
        for (int j = 0; j < i; j++) {
            int temp = a[a.length - 1];
            for (int n = a.length - 2; n >= 0; n--) {
                a[n + 1] = a[n];
            }
            a[0] = temp;
        }
    }
}

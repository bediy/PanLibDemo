package com.example.panlibrary;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ye on 2017/5/23/0023.
 */
public class PanViewTest {

    private Context context;
    private PanView panView;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        panView = new PanView(context);
        panView.lastMoveDegrees = 0.2;
    }

    @Test
    public void isClockWise() throws Exception {
        /*assertEquals(false, panView.isClockWise(0.111));
        assertEquals(false, panView.isClockWise(-0.11));
        assertEquals(false, panView.isClockWise(-2.3413));
        assertEquals(true, panView.isClockWise(0.21));
        assertEquals(true, panView.isClockWise(1.1));
        assertEquals(true, panView.isClockWise(0.771));*/
    }

}
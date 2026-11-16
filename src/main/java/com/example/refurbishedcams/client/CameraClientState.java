package com.example.refurbishedcams.client;

import com.example.refurbishedcams.data.CameraEntry;
import com.example.refurbishedcams.net.Net;
import com.example.refurbishedcams.net.ScanRequestPacket;

import java.util.List;

/** Last scan result received from the server (client side only). */
public class CameraClientState {
    public static volatile List<CameraEntry> cams = List.of();
    public static volatile boolean scanned = false;
    public static volatile int radius = 0;

    public static void request() {
        scanned = false;
        Net.CH.sendToServer(new ScanRequestPacket());
    }

    public static void receive(int r, List<CameraEntry> list) {
        radius = r;
        cams = list;
        scanned = true;
    }
}

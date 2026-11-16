package com.example.refurbishedcams.client;

import com.example.refurbishedcams.data.CameraEntry;

import java.util.List;

/** Only touched from client-side code paths (via DistExecutor). */
public class ClientHooks {
    public static void onScanResult(int radius, List<CameraEntry> list) {
        CameraClientState.receive(radius, list);
    }
}

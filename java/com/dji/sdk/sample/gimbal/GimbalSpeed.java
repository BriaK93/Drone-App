package com.dji.sdk.sample.gimbal;

import java.util.Timer;

import dji.common.gimbal.DJIGimbalSpeedRotation;

/**
 * Created by Bria Kai on 3/13/2017.
 */

public class GimbalSpeed {
    private Timer mTimer;
    private MoveGimbalWithSpeedView.GimbalRotateTimerTask mGimbalRotationTimerTask;

    private DJIGimbalSpeedRotation mPitchSpeedRotation;
}

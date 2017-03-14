package com.dji.sdk.sample.gimbal;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.dji.sdk.sample.R;
import com.dji.sdk.sample.common.DJISampleApplication;
import com.dji.sdk.sample.common.Utils;
import com.dji.sdk.sample.utils.DJIModuleVerificationUtil;
import com.dji.sdk.sample.utils.OnScreenJoystick;
import com.dji.sdk.sample.utils.OnScreenJoystickListener;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerDataType;
import dji.common.flightcontroller.DJISimulatorInitializationData;
import dji.common.flightcontroller.DJISimulatorStateData;
import dji.common.flightcontroller.DJIVirtualStickFlightControlData;
import dji.common.flightcontroller.DJIVirtualStickFlightCoordinateSystem;
import dji.common.flightcontroller.DJIVirtualStickRollPitchControlMode;
import dji.common.flightcontroller.DJIVirtualStickVerticalControlMode;
import dji.common.flightcontroller.DJIVirtualStickYawControlMode;
import dji.common.util.DJICommonCallbacks.DJICompletionCallback;
import dji.sdk.flightcontroller.DJISimulator;
import dji.sdk.mobilerc.DJIMobileRemoteController;
import dji.sdk.products.DJIAircraft;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for mobile remote controller for gimbal.
 */
public class GimbalRemote extends RelativeLayout implements View.OnClickListener {

    private TextView mTextView;

    private OnScreenJoystick mScreenJoystickRight;

    private DJIMobileRemoteController mobileRemoteController;

    private float mPitch;
    private float mYaw;

    public GimbalRemote(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initUI(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View content = layoutInflater.inflate(R.layout.view_mobile_rc, null, false);
        addView(content, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        mScreenJoystickRight = (OnScreenJoystick)findViewById(R.id.directionJoystickRight);



        DJISampleApplication.getAircraftInstance().getFlightController().getSimulator()
                .setUpdatedSimulatorStateDataCallback(new DJISimulator.UpdatedSimulatorStateDataCallback() {
                    @Override
                    public void onSimulatorDataUpdated(final DJISimulatorStateData djiSimulatorStateData) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                mTextView.setText("Yaw : " + djiSimulatorStateData.getYaw() + "," + "X : " + djiSimulatorStateData.getPositionX() + "\n" +
                                        "Y : " + djiSimulatorStateData.getPositionY() + "," +
                                        "Z : " + djiSimulatorStateData.getPositionZ());
                            }
                        });
                    }
                });
        try {
            mobileRemoteController = ((DJIAircraft) DJISampleApplication.getAircraftInstance()).getMobileRemoteController();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (mobileRemoteController != null) {
            mTextView.setText(mTextView.getText()+ "\n" + "Mobile Connected");
        } else {
            mTextView.setText(mTextView.getText()+ "\n" + "Mobile Disconnected");
        }

        mScreenJoystickRight.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
                //float PitchJoyControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMaxVelocity;
                //float RollJoyControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMaxVelocity;
                //
                //pitch = (float) (PitchJoyControlMaxSpeed * pY);
                //roll = (float) (RollJoyControlMaxSpeed * pX);
                if (mobileRemoteController != null) {
                    mobileRemoteController.setRightStickHorizontal(pX);
                    mobileRemoteController.setRightStickVertical(pY);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!DJIModuleVerificationUtil.isFlightControllerAvailable()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_take_off:

                DJISampleApplication.getAircraftInstance().getFlightController().takeOff(
                        new DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                Utils.showDialogBasedOnError(getContext(), djiError);
                            }
                        }
                );
                break;
            case R.id.btn_force_land:
                DJISampleApplication.getAircraftInstance().getFlightController().confirmLanding(new DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        Utils.showDialogBasedOnError(getContext(), djiError);
                    }
                });
                break;
            case R.id.btn_auto_land:
                DJISampleApplication.getAircraftInstance().getFlightController().autoLanding(new DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        Utils.showDialogBasedOnError(getContext(), djiError);
                    }
                });
                break;
            default:
                break;
        }
    }
}

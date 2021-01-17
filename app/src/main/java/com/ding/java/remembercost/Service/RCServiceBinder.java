package com.ding.java.remembercost.Service;

import android.os.Binder;

/**
 * Created by xiaobei on 2017/2/22.
 */

public class RCServiceBinder extends Binder {

    int getProcess() {
        return 99;
    }

}

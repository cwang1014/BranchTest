package io.branch.branchster;

import androidx.multidex.MultiDexApplication;

import io.branch.referral.Branch;

/**
 * Created by sahilverma on 3/7/17.
 */

public class BranchsterAndroidApplication extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
        Branch.enableLogging();
        Branch.getAutoInstance(this);
//        Branch.enableTestMode();
//        Branch branch = Branch.getAutoInstance(this);
//        Branch.enableLogging();
//        Branch.setPlayStoreReferrerCheckTimeout(3000);
//        branch.setIdentity(UUID.randomUUID().toString());
    }
}
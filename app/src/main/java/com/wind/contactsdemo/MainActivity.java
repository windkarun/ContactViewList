package com.wind.contactsdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.wind.contactsdemo.ContactList.ListFragment.ContactListFragment;
import com.wind.contactsdemo.Utilities.C;
import jagerfield.utilities.lib.AppUtilities;
import jagerfield.utilities.lib.PermissionsUtil.GuiDialog.PermissionsManager;
import jagerfield.utilities.lib.PermissionsUtil.PermissionsUtil;
import jagerfield.utilities.lib.PermissionsUtil.Results.IGetPermissionResult;

public class MainActivity extends AppCompatActivity {
    private static final int READ_CONTACT_PERMISSION_REQUEST_CODE = 76;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void launchFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_container, new ContactListFragment())
                .commit();
    }

    private void checkPermission()
    {
        PermissionsUtil permissionsUtil = AppUtilities.getPermissionUtil(MainActivity.this);
        IGetPermissionResult result = permissionsUtil.getPermissionResults(C.REQUIRED_PERMISSION);

        if (result.isGranted())
        {
            launchFragment();
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !result.isGranted())
        {
            /**
             * For SDK >= M, make a permission request
             */
            permissionsUtil.requestPermissions(C.REQUIRED_PERMISSION);
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M && !result.isGranted())
        {
            /**
             * For SDK < M, there are permissions missing in the manifest
             */
            String missingPermissions = TextUtils.join(", ", result.getMissingInManifest_ForSdkBelowM()).trim();
            Toast.makeText(this, "Following permissions are missing : " + missingPermissions, Toast.LENGTH_LONG).show();
            Log.e(C.TAG_LIB, "Following permissions are missing : " + missingPermissions);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        PermissionsUtil permissionsUtil = AppUtilities.getPermissionUtil(this);

        if (requestCode == permissionsUtil.getPermissionsReqCodeId())
        {
            IGetPermissionResult result = null;
            result = permissionsUtil.getPermissionResults(permissions);

            if (result.isGranted())
            {
                launchFragment();
                return;
            }

            PermissionsManager.getNewInstance(this, result, permissions, new PermissionsManager.PermissionsManagerCallback()
            {
                @Override
                public void onPermissionsGranted(IGetPermissionResult result) {

                    /**
                     * User accepted all requested permissions
                     */
                    launchFragment();
                }

                @Override
                public void onPermissionsMissing(IGetPermissionResult result)
                {
                    Toast.makeText(MainActivity.this, "User didn't accept all permissions", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

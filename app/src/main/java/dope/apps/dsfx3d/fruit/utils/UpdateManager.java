package dope.apps.dsfx3d.fruit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.Streams;

import dope.apps.dsfx3d.fruit.MainActivity;

/**
 */

public class UpdateManager {

    public static void check4Update(final Activity activity) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Keys.APP_UPDATE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("__check4Update", "latest ver: "+dataSnapshot.child(Keys.APP_VER).getValue(int.class));
                String downloadUrl = dataSnapshot.child(Keys.APK_URL).getValue(String.class);
                int latestVer = dataSnapshot.child(Keys.APP_VER).getValue(int.class);
                String updateMessage = dataSnapshot.child(Keys.UPDATE_MSG).getValue(String.class);

                PackageInfo info = null;
                try {
                    info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int currentVer = info.versionCode;

                if(currentVer!=latestVer){
                    ((MainActivity)activity).showDownloadUpdateDialog(updateMessage, downloadUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

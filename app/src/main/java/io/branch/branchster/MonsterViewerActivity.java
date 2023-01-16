package io.branch.branchster;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.branch.branchster.fragment.InfoFragment;
import io.branch.branchster.util.MonsterImageView;
import io.branch.branchster.util.MonsterObject;
import io.branch.branchster.util.MonsterPreferences;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class MonsterViewerActivity extends FragmentActivity implements InfoFragment.OnFragmentInteractionListener {
    static final int SEND_SMS = 12345;

    private static String TAG = MonsterViewerActivity.class.getSimpleName();
    public static final String MY_MONSTER_OBJ_KEY = "my_monster_obj_key";

    TextView monsterUrl;
    View progressBar;

    MonsterImageView monsterImageView_;
    MonsterObject myMonsterObject;
    MonsterPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_viewer);

        monsterImageView_ = (MonsterImageView) findViewById(R.id.monster_img_view);
        monsterUrl = (TextView) findViewById(R.id.shareUrl);
        progressBar = findViewById(R.id.progress_bar);
        prefs = MonsterPreferences.getInstance(getApplicationContext());

        // Change monster
        findViewById(R.id.cmdChange).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MonsterCreatorActivity.class);
                startActivity(i);
                finish();
            }
        });

        // More info
        findViewById(R.id.infoButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                InfoFragment infoFragment = InfoFragment.newInstance();
                ft.replace(R.id.container, infoFragment).addToBackStack("info_container").commit();
            }
        });

        //Share monster
        findViewById(R.id.share_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareMyMonster();
            }
        });

        initUI();
    }

    private void initUI() {
        myMonsterObject = getIntent().getParcelableExtra(MY_MONSTER_OBJ_KEY);

        new BranchEvent("monster_view")
                .addCustomDataProperty("bodyIndex", String.valueOf(myMonsterObject.getBodyIndex()))
                .addCustomDataProperty("colorIndex", String.valueOf(myMonsterObject.getColorIndex()))
                .addCustomDataProperty("faceIndex", String.valueOf(myMonsterObject.getFaceIndex()))
                .addCustomDataProperty("monsterDescription", myMonsterObject.getMonsterDescription())
                .addCustomDataProperty("monsterName", myMonsterObject.getMonsterName())
                .logEvent(MonsterViewerActivity.this);

        new Thread(() -> {
            Map prepared = myMonsterObject.prepareBranchDict();
            LinkProperties linkProperties = new LinkProperties()
                    .addControlParameter("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                    .addControlParameter("KEY_MONSTER_DESCRIPTION", (String) prepared.get("$og_description"))
                    .addControlParameter("KEY_MONSTER_IMAGE", (String) prepared.get("$og_image_url"))
                    .addControlParameter("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                    .addControlParameter("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                    .addControlParameter("KEY_COLOR_INDEX", (String) prepared.get("color_index"));

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier("monster/viewer/")
                    .setTitle("My Monster")
                    .setContentDescription((String) prepared.get("$og_description"))
                    .setContentImageUrl((String) prepared.get("$og_image_url"))
                    .setContentMetadata(new ContentMetadata()
                            .addCustomMetadata("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                            .addCustomMetadata("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                            .addCustomMetadata("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                            .addCustomMetadata("KEY_COLOR_INDEX", (String) prepared.get("color_index")));
            branchUniversalObject.generateShortUrl(MonsterViewerActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    if (error == null) {
                        monsterUrl.setText(url);
                        progressBar.setVisibility(View.GONE);
                        Log.i("MyApp", "got my Branch link to share: " + url);
                    }
                }
            });
        }).start();

        if (myMonsterObject != null) {
            String monsterName = getString(R.string.monster_name);

            if (!TextUtils.isEmpty(myMonsterObject.getMonsterName())) {
                monsterName = myMonsterObject.getMonsterName();
            }

            ((TextView) findViewById(R.id.txtName)).setText(monsterName);
            String description = MonsterPreferences.getInstance(this).getMonsterDescription();

            if (!TextUtils.isEmpty(myMonsterObject.getMonsterDescription())) {
                description = myMonsterObject.getMonsterDescription();
            }

            ((TextView) findViewById(R.id.txtDescription)).setText(description);

            // set my monster image
            monsterImageView_.setMonster(myMonsterObject);

            progressBar.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "Monster is null. Unable to view monster");
        }
    }

    /**
     * Method to share my custom monster with sharing with Branch Share sheet
     */
    private void shareMyMonster() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            Map prepared = myMonsterObject.prepareBranchDict();
            LinkProperties linkProperties = new LinkProperties()
                    .setChannel("sms")
                    .setFeature("sharing")
                    .addControlParameter("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                    .addControlParameter("KEY_MONSTER_DESCRIPTION", (String) prepared.get("$og_description"))
                    .addControlParameter("KEY_MONSTER_IMAGE", (String) prepared.get("$og_image_url"))
                    .addControlParameter("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                    .addControlParameter("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                    .addControlParameter("KEY_COLOR_INDEX", (String) prepared.get("color_index"))
                    .addControlParameter("$android_deeplink_path", "monster/viewer/");

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                    .setCanonicalIdentifier("monster/viewer/")
                    .setTitle((String) prepared.get("monster_name"))
                    .setContentDescription((String) prepared.get("$og_description"))
                    .setContentImageUrl((String) prepared.get("$og_image_url"))
                    .setContentMetadata(new ContentMetadata()
                            .addCustomMetadata("KEY_MONSTER_NAME", (String) prepared.get("monster_name"))
                            .addCustomMetadata("KEY_FACE_INDEX", (String) prepared.get("face_index"))
                            .addCustomMetadata("KEY_BODY_INDEX", (String) prepared.get("body_index"))
                            .addCustomMetadata("KEY_COLOR_INDEX", (String) prepared.get("color_index")));
            branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    if (error == null) {
                        progressBar.setVisibility(View.GONE);
                        Log.i("MyApp", "got my Branch link to share: " + url);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, String.format("Check out my Branchster named %s at %s", myMonsterObject.getMonsterName(), url));
                        startActivityForResult(i, SEND_SMS);
                    }
                }
            });
        }).start();

//        String shareUrl = "fcpj8.app.link"; // TODO: Replace with Branch-generated shortUrl

//        progressBar.setVisibility(View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SEND_SMS == requestCode) {
            if (MonsterViewerActivity.RESULT_OK == resultCode) {
                // TODO: Track successful share via Branch.
                new BranchEvent("monster_share")
                        .addCustomDataProperty("bodyIndex", String.valueOf(myMonsterObject.getBodyIndex()))
                        .addCustomDataProperty("colorIndex", String.valueOf(myMonsterObject.getColorIndex()))
                        .addCustomDataProperty("faceIndex", String.valueOf(myMonsterObject.getFaceIndex()))
                        .addCustomDataProperty("monsterDescription", myMonsterObject.getMonsterDescription())
                        .addCustomDataProperty("monsterName", myMonsterObject.getMonsterName())
                        .logEvent(MonsterViewerActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }
    }


    @Override
    public void onFragmentInteraction() {
        //no-op
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initUI();
    }
}

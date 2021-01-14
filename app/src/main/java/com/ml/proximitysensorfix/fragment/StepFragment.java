package com.ml.proximitysensorfix.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.judemanutd.autostarter.AutoStartPermissionHelper;
import com.ml.proximitysensorfix.R;
import com.ml.proximitysensorfix.utils.RomUtils;
import com.ml.proximitysensorfix.activity.MainActivity;
import com.ml.proximitysensorfix.activity.PermissionsActivity;
import com.ml.proximitysensorfix.receiver.AdminReceiver;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

public class StepFragment extends Fragment implements Step {



    public StepFragment() {
        // Required empty public constructor
    }

    TextView stepText;
    Button stepButton;
    static boolean verified = false;
    SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity()!=null)
        prefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

    }
    protected void didVisibilityChange() {
        Activity activity = getActivity();
        if (isResumed()) {
            int position= getArguments().getInt("position",0);
            if(position>0 && getActivity()!=null)
                getActivity().setTitle("Step "+(position+1));
            switch(position) {
                case 0: {
                    final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(
                            Context.DEVICE_POLICY_SERVICE);
                    final AccessibilityManager accessibilityService = (AccessibilityManager) getActivity().getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && accessibilityService.isEnabled()) ||
                            (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && devicePolicyManager.isAdminActive(new ComponentName(getActivity(), AdminReceiver.class)))) {
                        verified=true;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        didVisibilityChange();
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        didVisibilityChange();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_step, container, false);
        stepText=view.findViewById(R.id.textview_first);
        stepButton=view.findViewById(R.id.button_first);
        if (getArguments() != null) {
            int position= getArguments().getInt("position",0);

            switch(position){
                case 0 : {
                    stepButton.setText(R.string.concedi_permesso);
                    if((prefs!=null && prefs.getBoolean("adminEnabled", false)) ||Build.VERSION.SDK_INT < Build.VERSION_CODES.P ){
                        stepText.setText(R.string.admin_description);
                        stepButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(getActivity()!=null) {
                                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(getActivity(), AdminReceiver.class));
                                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.admin_description));
                                    startActivityForResult(intent, 0);
                                }
                            }
                        });
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        stepText.setText(R.string.accesibilty_description);
                        stepButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent,0);

                            }
                        });
                    }
                    break;
                }
                case 2: {
                    stepText.setText(R.string.enable_avvio_automatico_description);
                    stepButton.setText(R.string.concedi_permesso);
                    stepButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(getActivity())){
                                    AutoStartPermissionHelper.getInstance().getAutoStartPermission(getActivity());
                                } else if(Build.BRAND.equalsIgnoreCase("xiaomi") || MainActivity.isMIUI(getActivity())){
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                    startActivityForResult(intent, 2);
                                }else if(Build.BRAND.equalsIgnoreCase("Letv")){
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                                    startActivityForResult(intent,2);

                                }
                                else if(Build.BRAND.equalsIgnoreCase("Honor")){
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                                    startActivityForResult(intent,2);
                                } else {
                                    if(getContext()!=null) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                    verified=true;
                                    PermissionsActivity.goNext();
                                }


                            }catch (Exception e){
                                if(getContext()!=null) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                                verified=true;
                                PermissionsActivity.goNext();
                            }
                        }
                    });
                    break;
                }
                case 1: {
                    stepText.setText(R.string.disable_battery_optimization);
                    stepButton.setText(R.string.concedi_permesso);
                    stepButton.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                      try {
                          if(getContext()!=null) {
                              Intent intent = new Intent();
                              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                              Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                              intent.setData(uri);
                              startActivity(intent);
                          }
                          verified=true;
                          PermissionsActivity.goNext();

                      } catch (Exception e) {
                          e.printStackTrace();
                          verified=true;
                          PermissionsActivity.goNext();
                      }

                                                      }
                                                  }
                    );

                    break;
                }
                case 3: {
                    stepText.setText(R.string.state_telephone);
                    stepButton.setText(R.string.concedi_permesso);
                    stepButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getActivity()!=null && getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission has not been granted, therefore prompt the user to grant permission
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.READ_PHONE_STATE},
                                            3);
                                    verified=true;
                                    PermissionsActivity.goNext();
                                } else {
                                    verified=true;
                                    PermissionsActivity.goNext();

                                }
                            }
                        }
                    });
                    break;
                }
            }
        }
        return view;
    }

    public void applyMiuiPermission(Context context) {
        if(RomUtils.checkIsMiuiRom()) {
            int versionCode = getMiuiVersion();
            if (versionCode == 5) {
                goToMiuiPermissionActivity_V5(context);
            } else if (versionCode == 6) {
                goToMiuiPermissionActivity_V6(context);
            } else if (versionCode == 7) {
                goToMiuiPermissionActivity_V7(context);
            } else if (versionCode >= 8) {
                goToMiuiPermissionActivity_V8(context);
            } else {
                Log.e("ERR", "this is a special MIUI rom version, its version code " + versionCode);
            }
        }
        StepFragment.verified=true;
        PermissionsActivity.goNext();
    }


    public static int getMiuiVersion() {
        String version = RomUtils.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e("ERR", "get miui version code error, version : " + version);
            }
        }
        return -1;
    }

    public static void goToMiuiPermissionActivity_V5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package" , packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e("ERR", "intent is not available!");
        }
    }

    private static  boolean isIntentAvailable(Intent intent,Context context) {
        if(context!=null)
            return  context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
        return false;
    }

    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e("ERR", "Intent is not available!");
        }
    }

    public static void goToMiuiPermissionActivity_V8(Context context){
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e("ERR", "Intent is not available!");
        }
    }
    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e("ERR", "Intent is not available!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verified=true;
        PermissionsActivity.goNext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0){
            if(resultCode== Activity.RESULT_OK) {
                verified = true;
                PermissionsActivity.goNext();
            }
        } else if(requestCode==2){
            if(resultCode== Activity.RESULT_OK) {
                verified = true;
                PermissionsActivity.goNext();
            }
        }
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        if(verified)
            return null;
        else return new  VerificationError(getString(R.string.all_will_not_work));
    }

    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }
}
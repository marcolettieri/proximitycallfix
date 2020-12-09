package com.ml.proximitysensorfix;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.w3c.dom.Text;

public class StepFragment extends Fragment implements Step {



    public StepFragment() {
        // Required empty public constructor
    }

    TextView stepText;
    Button stepButton;
    static boolean verified = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            if(position>0 && getActivity()!=null)
                getActivity().setTitle("Step "+position);
            switch(position){
                case 0 : {
                    stepText.setText(R.string.admin_description);
                    stepButton.setText(R.string.concedi_permesso);

                    stepButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(getActivity(), AdminReceiver.class));
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.admin_description));
                            startActivityForResult(intent, 0);
                        }
                    });
                    break;
                }
                case 2: {
                    stepText.setText(R.string.enable_avvio_automatico_description);
                    stepButton.setText(R.string.concedi_permesso);
                    stepButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                if(Build.BRAND.equalsIgnoreCase("xiaomi") || MainActivity.isMIUI(getActivity())){
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
                                }
                                    verified=true;
                                    PermissionsActivity.goNext();


                            }catch (Exception e){
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
                                                              Intent intent = new Intent();
                                                              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                              Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                                              intent.setData(uri);
                                                              startActivity(intent);
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
                                if (getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
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

    public static void applyMiuiPermission(Context context) {
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
        Intent intent = null;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
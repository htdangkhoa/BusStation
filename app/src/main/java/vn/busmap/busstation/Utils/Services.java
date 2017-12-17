package vn.busmap.busstation.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vn.busmap.busstation.MainActivity;
import vn.busmap.busstation.R;

/**
 * Created by dangkhoa on 9/25/17.
 */

public class Services {
    public static final int NO_ANIMATION = -1;
    public static final int FROM_RIGHT_TO_LEFT = 0;
    public static final int FROM_LEFT_TO_RIGHT = 1;
    public static final int FROM_BOTTOM_TO_TOP = 2;
    public static final int FROM_TOP_TO_BOTTOM = 3;

    private static android.support.v7.app.AlertDialog dialog;

    public static void Navigate(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable String tagFragmentBefore, @NonNull boolean isBack, Bundle bundle, int direction) {
        HideSoftKeyboard((Activity) context);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment oldFragment = fragmentManager.findFragmentByTag(fragmentManager.getClass().getName());

        if (!isBack) {
            MainActivity.btnBack.setVisibility(View.GONE);
        }else {
            MainActivity.btnBack.setVisibility(View.VISIBLE);
        }

        if (oldFragment != null) {
            transaction.remove(oldFragment).commit();
        }

        if (bundle != null) {
            fragment.setArguments(bundle);
        }


        switch (direction) {
            case FROM_RIGHT_TO_LEFT: {
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            }
            case FROM_LEFT_TO_RIGHT: {
                transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            }
            case FROM_BOTTOM_TO_TOP: {
                transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);
                break;
            }
            case FROM_TOP_TO_BOTTOM: {
                transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);
                break;
            }
        }
        transaction.addToBackStack(tagFragmentBefore).replace(R.id.content_frame, fragment, tagFragmentBefore).commit();
    }

    public static void HideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void ShowDialog(final Context context, String title, String message, String negativeButton, DialogInterface.OnClickListener negativeButtonOnClickListener, String positiveButton, DialogInterface.OnClickListener positiveButtonOnClickListener ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton, negativeButtonOnClickListener);
        }

        if (positiveButton != null) {
            builder.setPositiveButton(positiveButton, positiveButtonOnClickListener);
        }

        final AlertDialog alert = builder.create();
//        alert.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorError));
//                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorOrange));
//            }
//        });
        alert.show();
    }

    public static void FuckingDialog(@NonNull Context context, @Nullable String title, @Nullable String message, @Nullable final View.OnClickListener onLeftButtonClickListener, @Nullable final View.OnClickListener onRightButtonClickListener) {
        if (dialog != null) {
            if (dialog.isShowing()) DismissFuckingDialog();

            dialog = null;
        }

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_dialog, null);

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(view);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText(title);

        TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        Button btnDialogNo = (Button) view.findViewById(R.id.btnDialogNo);
        if (onLeftButtonClickListener == null) {
            btnDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DismissFuckingDialog();
                }
            });
        } else {
            btnDialogNo.setOnClickListener(onLeftButtonClickListener);
        }

        Button btnDialogYes = (Button) view.findViewById(R.id.btnDialogYes);
        if (onRightButtonClickListener == null) {
            btnDialogYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DismissFuckingDialog();
                }
            });
        } else {
            btnDialogYes.setOnClickListener(onRightButtonClickListener);
        }

        dialog.show();
    }

    public static void DismissFuckingDialog() {
        dialog.dismiss();
    }
}

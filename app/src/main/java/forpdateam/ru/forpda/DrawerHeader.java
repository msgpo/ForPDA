package forpdateam.ru.forpda;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import forpdateam.ru.forpda.api.Api;
import forpdateam.ru.forpda.api.profile.models.ProfileModel;
import forpdateam.ru.forpda.apirx.RxApi;
import forpdateam.ru.forpda.fragments.TabFragment;
import forpdateam.ru.forpda.fragments.profile.ProfileFragment;
import forpdateam.ru.forpda.utils.IntentHandler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by radiationx on 16.03.17.
 */

public class DrawerHeader {
    private ImageView avatar;
    private TextView nick;
    private View headerLayout;
    private ImageButton openLinkButton;
    private MainActivity activity;
    private View.OnClickListener headerClickListener = v -> {
        TabFragment tabFragment = null;
        for (TabFragment fragment : TabManager.getInstance().getFragments()) {
            if (fragment.getClass().getSimpleName().equals(ProfileFragment.class.getSimpleName()) && fragment.getConfiguration().isMenu()) {
                tabFragment = fragment;
                break;
            }
        }
        if (tabFragment == null) {
            tabFragment = new TabFragment.Builder<>(ProfileFragment.class).build();
            tabFragment.getConfiguration().setMenu(true);
            TabManager.getInstance().add(tabFragment);
        } else {
            TabManager.getInstance().select(tabFragment);;
        }
        /*TabFragment fragment = TabManager.getInstance().get(TabManager.getInstance().getTagContainClass(ProfileFragment.class));
        if (fragment == null | (fragment != null && fragment.getConfiguration().isMenu())) {
            TabManager.getInstance().add(new TabFragment.Builder<>(ProfileFragment.class).setIsMenu().build());
        } else {
            TabManager.getInstance().select(fragment);
        }*/
        activity.getMenuDrawer().close();
    };

    public DrawerHeader(MainActivity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        headerLayout = drawerLayout.findViewById(R.id.drawer_header_container);
        avatar = (ImageView) headerLayout.findViewById(R.id.drawer_header_avatar);
        nick = (TextView) headerLayout.findViewById(R.id.drawer_header_nick);
        openLinkButton = (ImageButton) headerLayout.findViewById(R.id.drawer_header_open_link);
        openLinkButton.setOnClickListener(v -> {
            activity.getMenuDrawer().close();
            String url;
            url = readFromClipboard(activity);
            if (url == null) url = "";
            final FrameLayout frameLayout = new FrameLayout(activity);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            frameLayout.setPadding(App.px24, 0, App.px24, 0);
            final EditText linkField = new EditText(activity);
            frameLayout.addView(linkField);
            linkField.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linkField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            linkField.setText(url);
            new AlertDialog.Builder(activity)
                    .setTitle("Перейти по ссылке")
                    .setView(frameLayout)
                    .setPositiveButton("Перейти", (dialog, which) -> IntentHandler.handle(linkField.getText().toString()))
                    .setNegativeButton("Отмена", null)
                    .show();
        });
        Api.Auth().addLoginObserver((observable, o) -> {
            state((boolean) o);
        });
        state(Api.Auth().getState());
    }

    public String readFromClipboard(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    private void state(boolean b) {
        if (b) {
            headerLayout.setOnClickListener(headerClickListener);
            nick.setText("");
            load();
        } else {
            headerLayout.setOnClickListener(null);
            ImageLoader.getInstance().displayImage("assets://av.png", avatar);
            nick.setText("Гость");
        }
    }

    private void load() {
        RxApi.Profile().getProfile("http://4pda.ru/forum/index.php?showuser=".concat(Api.Auth().getUserId() == null ? "2556269" : Api.Auth().getUserId())).onErrorReturn(throwable -> new ProfileModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoad);
    }

    private void onLoad(ProfileModel profileModel) {
        Log.d("FORPDA_LOG", "ONLOAD PROFILE " + profileModel.getNick() + " : " + profileModel.getAvatar());
        ImageLoader.getInstance().displayImage(profileModel.getAvatar(), avatar);
        nick.setText(profileModel.getNick());
    }

    public void setStatusBarHeight(int height) {
        headerLayout.setPadding(0, height, 0, 0);

    }
}

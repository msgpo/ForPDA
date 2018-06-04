package forpdateam.ru.forpda.ui.fragments.profile;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.function.Consumer;

import forpdateam.ru.forpda.App;
import forpdateam.ru.forpda.R;
import forpdateam.ru.forpda.common.BitmapUtils;
import forpdateam.ru.forpda.common.LinkMovementMethod;
import forpdateam.ru.forpda.entity.remote.profile.ProfileModel;
import forpdateam.ru.forpda.model.AuthHolder;
import forpdateam.ru.forpda.presentation.profile.ProfilePresenter;
import forpdateam.ru.forpda.presentation.profile.ProfileView;
import forpdateam.ru.forpda.ui.fragments.TabFragment;
import forpdateam.ru.forpda.ui.fragments.profile.adapters.ProfileAdapter;
import forpdateam.ru.forpda.ui.views.ScrimHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by radiationx on 03.08.16.
 */
public class ProfileFragment extends TabFragment implements ProfileAdapter.ClickListener, ProfileView {

    private RecyclerView recyclerView;
    private TextView nick, group, sign;
    private ImageView avatar;
    private CircularProgressView progressView;

    private MenuItem copyLinkMenuItem;
    private MenuItem writeMenuItem;

    private ProfileAdapter adapter;

    private AuthHolder authHolder = App.get().Di().getAuthHolder();

    @InjectPresenter
    ProfilePresenter presenter;

    @ProvidePresenter
    ProfilePresenter providePresenter() {
        return new ProfilePresenter(
                App.get().Di().getProfileRepository(),
                App.get().Di().getRouter(),
                App.get().Di().getLinkHandler()
        );
    }

    public ProfileFragment() {
        configuration.setFitSystemWindow(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String profileUrl = null;
        if (getArguments() != null) {
            profileUrl = getArguments().getString(ARG_TAB);
        }
        if (profileUrl == null || profileUrl.isEmpty()) {
            profileUrl = "https://4pda.ru/forum/index.php?showuser=" + authHolder.get().getUserId();
        }
        presenter.setProfileUrl(profileUrl);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        baseInflateFragment(inflater, R.layout.fragment_profile);
        ViewStub viewStub = (ViewStub) findViewById(R.id.toolbar_content);
        viewStub.setLayoutResource(R.layout.toolbar_profile);
        viewStub.inflate();
        nick = (TextView) findViewById(R.id.profile_nick);
        group = (TextView) findViewById(R.id.profile_group);
        sign = (TextView) findViewById(R.id.profile_sign);
        avatar = (ImageView) findViewById(R.id.profile_avatar);
        recyclerView = (RecyclerView) findViewById(R.id.profile_list);
        progressView = (CircularProgressView) findViewById(R.id.profile_progress);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbarLayout.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
        toolbarLayout.setLayoutParams(params);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new ProfileAdapter();
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        toolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        toolbarLayout.setTitleEnabled(true);
        toolbarTitleView.setVisibility(View.GONE);

        ScrimHelper scrimHelper = new ScrimHelper(appBarLayout, toolbarLayout);
        scrimHelper.setScrimListener((boolean scrim1) -> {
            if (scrim1) {
                toolbar.getNavigationIcon().clearColorFilter();
                toolbar.getOverflowIcon().clearColorFilter();
            } else {
                toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        });

        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void addBaseToolbarMenu(Menu menu) {
        super.addBaseToolbarMenu(menu);
        copyLinkMenuItem = menu.add(R.string.copy_link)
                .setOnMenuItemClickListener(menuItem -> {
                    presenter.copyUrl();
                    return false;
                });
        writeMenuItem = menu.add(R.string.write)
                .setIcon(App.getVecDrawable(getContext(), R.drawable.ic_profile_toolbar_create))
                .setOnMenuItemClickListener(item -> {
                    presenter.navigateToQms();
                    return false;
                })
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refreshToolbarMenuItems(false);
    }

    @Override
    protected void refreshToolbarMenuItems(boolean enable) {
        super.refreshToolbarMenuItems(enable);
        if (enable) {
            copyLinkMenuItem.setEnabled(true);
        } else {
            copyLinkMenuItem.setEnabled(false);
            writeMenuItem.setVisible(false);
        }
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        super.setRefreshing(isRefreshing);
        if (isRefreshing) {
            refreshToolbarMenuItems(false);
        }
    }

    @Override
    public void onSaveClick(String text) {
        presenter.saveNote(text);
    }

    @Override
    public void onSaveNote(boolean success) {
        Toast.makeText(getContext(), getString(success ? R.string.profile_note_saved : R.string.error_occurred), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProfile(ProfileModel data) {
        refreshToolbarMenuItems(true);
        adapter.setProfile(data);
        adapter.notifyDataSetChanged();
        ImageLoader.getInstance().loadImage(data.getAvatar(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                //Нужен handler, иначе при повторном создании фрагмента неверно вычисляется высота вьюхи
                Handler handler = new Handler();
                handler.post(() -> {
                    if (!isAdded())
                        return;
                    blur(loadedImage);
                    Bitmap overlay = Bitmap.createBitmap(loadedImage.getWidth(), loadedImage.getHeight(), Bitmap.Config.RGB_565);
                    overlay.eraseColor(Color.WHITE);
                    Canvas canvas = new Canvas(overlay);
                    canvas.drawBitmap(loadedImage, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    avatar.setImageBitmap(overlay);
                    avatar.startAnimation(animation);

                    AlphaAnimation animation1 = new AlphaAnimation(1, 0);
                    animation1.setDuration(500);
                    animation1.setFillAfter(true);
                    progressView.startAnimation(animation1);
                    handler.postDelayed(() -> {
                        progressView.stopAnimation();
                        progressView.setVisibility(View.GONE);
                    }, 500);
                });
            }
        });

        setTabTitle(String.format(getString(R.string.profile_with_Nick), data.getNick()));
        setTitle(data.getNick());
        nick.setText(data.getNick());
        group.setText(data.getGroup());
        if (data.getSign() != null) {
            sign.setText(data.getSign());
            sign.setVisibility(View.VISIBLE);
            sign.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (!data.getContacts().isEmpty()) {
            boolean isMe = data.getId() == authHolder.get().getUserId();
            writeMenuItem.setVisible(!isMe);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg) {
        float scaleFactor = 3;
        int radius = 4;
        Disposable disposable = Observable
                .fromCallable(() -> {
                    Bitmap overlay = BitmapUtils.centerCrop(bkg, toolbarBackground.getWidth(), toolbarBackground.getHeight(), scaleFactor);
                    BitmapUtils.fastBlur(overlay, radius, true);
                    return overlay;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    AlphaAnimation animation1 = new AlphaAnimation(0, 1);
                    animation1.setDuration(500);
                    animation1.setFillAfter(true);
                    toolbarBackground.setBackground(new BitmapDrawable(getResources(), bitmap));
                    toolbarBackground.startAnimation(animation1);
                }, throwable -> {
                    Toast.makeText(App.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
        addToDisposable(disposable);
    }
}

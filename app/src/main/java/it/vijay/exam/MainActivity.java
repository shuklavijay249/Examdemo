package it.vijay.exam;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.FloatingActionButtonBehavior;
import it.vijay.exam.Test.Questions;

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;

@TargetApi (Build.VERSION_CODES.KITKAT_WATCH)
public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {

    static final String TAG = MainActivity.class.getSimpleName();
    public final static String Message = "it.vijay.exam.MESSAGE";

    public static final int MENU_TYPE_5_ITEMS_NO_BACKGROUND = 5;
    private ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigation.DEBUG = BuildConfig.DEBUG;

        setContentView(getActivityLayoutResId());
        final ViewGroup root = (ViewGroup) findViewById(R.id.CoordinatorLayout01);
        final CoordinatorLayout coordinatorLayout;
        if (root instanceof CoordinatorLayout) {
            coordinatorLayout = (CoordinatorLayout) root;
        } else {
            coordinatorLayout = null;
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();
        final boolean translucentNavigation = hasTranslucentNavigation();

        log(TAG, VERBOSE, "translucentStatus: %b", translucentStatus);

        if (translucentStatus) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }

        if (translucentNavigation) {
            final ViewPager viewPager = getViewPager();
            if (null != viewPager) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
                params.bottomMargin = -getNavigationBarHeight();
            }
        }

        initializeBottomNavigation(savedInstanceState);
        initializeUI(savedInstanceState);
    }

    protected int getActivityLayoutResId() {return R.layout.activity_main;}

    protected void initializeBottomNavigation(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            getBottomNavigation().setDefaultSelectedIndex(0);
            final BadgeProvider provider = getBottomNavigation().getBadgeProvider();
            provider.show(R.id.bbn_item3);
            provider.show(R.id.bbn_item4);
        }
    }

    protected void initializeUI(final Bundle savedInstanceState) {
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (null != floatingActionButton) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View root = findViewById(R.id.CoordinatorLayout01);
                    Snackbar snackbar = Snackbar.make(root, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction(
                            "Action",
                            null
                        );
                    snackbar.show();
                }
            });

            if (hasTranslucentNavigation()) {
                final ViewGroup.LayoutParams params = floatingActionButton.getLayoutParams();
                if (CoordinatorLayout.LayoutParams.class.isInstance(params)) {
                    CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) params;
                    if (FloatingActionButtonBehavior.class.isInstance(params1.getBehavior())) {
                        ((FloatingActionButtonBehavior) params1.getBehavior()).setNavigationBarHeight(getNavigationBarHeight());
                    }
                }
            }
        }

        final ViewPager viewPager = getViewPager();
        if (null != viewPager) {

            getBottomNavigation().setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
                @Override
                public void onMenuChanged(final BottomNavigation parent) {

                    viewPager.setAdapter(new ViewPagerAdapter(MainActivity.this, parent.getMenuItemCount()));
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                            final int position, final float positionOffset, final int positionOffsetPixels) { }

                        @Override
                        public void onPageSelected(final int position) {
                            if (getBottomNavigation().getSelectedIndex() != position) {
                                getBottomNavigation().setSelectedIndex(position, false);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(final int state) { }
                    });
                }
            });

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.item6:
                return setMenuType(MENU_TYPE_5_ITEMS_NO_BACKGROUND);

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean setMenuType(final int type) {
        BottomNavigation navigation = getBottomNavigation();
        if (null == navigation) {
            return false;
        }

        switch (type) {

            case MENU_TYPE_5_ITEMS_NO_BACKGROUND:
                navigation.inflateMenu(R.menu.bottombar_menu_5items_no_background);
                break;
        }

        return true;
    }

    @Override
    public void onMenuItemSelect(final int itemId, final int position, final boolean fromUser) {
        log(TAG, INFO, "onMenuItemSelect(" + itemId + ", " + position + ", " + fromUser + ")");
        if (fromUser)
        {
            {

                //To show button click
                new Handler().postDelayed(new Runnable() {@Override public void run(){}}, 400);


                progressBar = new ProgressDialog(MainActivity.this);//Create new object of progress bar type
                progressBar.setCancelable(false);//Progress bar cannot be cancelled by pressing any wher on screen
                progressBar.setMessage("Getting Questions Ready ...");//Title shown in the progress bar
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//Style of the progress bar
                progressBar.setProgress(0);//attributes
                progressBar.setMax(100);//attributes
                progressBar.show();//show the progress bar
                //This handler will add a delay of 3 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Intent start to open the navigation drawer activity
                        progressBar.cancel();//Progress bar will be cancelled (hide from screen) when this run function will execute after 3.5seconds
                        Intent intent = new Intent(MainActivity.this, Questions.class);
                        intent.putExtra(Message, "c1");//by this statement we are sending the name of the button that invoked the new Questions.java activity "Message" is defined by the name of the package + MESSAGE
                        startActivity(intent);
                    }
                }, 2000);
            }
        }
//        {
////            getBottomNavigation().getBadgeProvider().remove(itemId);
////            if (null != getViewPager()) {
////                getViewPager().setCurrentItem(position);
////            }
//
//            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
//            startActivity(intent);
////            finish();
//        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {
        log(TAG, INFO, "onMenuItemReselect(" + itemId + ", " + position + ", " + fromUser + ")");

        if (fromUser) {
            final FragmentManager manager = getSupportFragmentManager();
            MainActivityFragment1 fragment = (MainActivityFragment1) manager.findFragmentById(R.id.fragment);
            if (null != fragment) {
                fragment.scrollToTop();
            }
        }

    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int mCount;

        public ViewPagerAdapter(final AppCompatActivity activity, int count) {
            super(activity.getSupportFragmentManager());
            this.mCount = count;
        }

        @Override
        public Fragment getItem(final int position) {
            return new MainActivityFragment1();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

}

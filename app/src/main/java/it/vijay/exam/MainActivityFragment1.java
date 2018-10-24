package it.vijay.exam;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.MiscUtils;


public class MainActivityFragment1 extends Fragment {
    private static final String TAG = MainActivityFragment1.class.getSimpleName();
    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;
    ViewGroup mRoot;
    private SystemBarConfig config;
    private ToolbarScrollHelper scrollHelper;

    public MainActivityFragment1() { }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView01);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final BaseActivity activity = (BaseActivity) getActivity();
        config = activity.getSystemBarTint().getConfig();
        mRoot = (ViewGroup) activity.findViewById(R.id.CoordinatorLayout01);
        if (mRoot instanceof CoordinatorLayout) {
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }

        final int navigationHeight;
        final int actionbarHeight;

        if (activity.hasTranslucentNavigation()) {
            navigationHeight = config.getNavigationBarHeight();
        } else {
            navigationHeight = 0;
        }

        if (activity.hasTranslucentStatusBar()) {
            actionbarHeight = config.getActionBarHeight();
        } else {
            actionbarHeight = 0;
        }

        MiscUtils.log(TAG, Log.VERBOSE, "navigationHeight: " + navigationHeight);
        MiscUtils.log(TAG, Log.VERBOSE, "actionbarHeight: " + actionbarHeight);

        final BottomNavigation navigation = activity.getBottomNavigation();
        if (null != navigation) {
            navigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final ViewGroup.LayoutParams params = navigation.getLayoutParams();
                    final CoordinatorLayout.Behavior behavior;

                    if (params instanceof CoordinatorLayout.LayoutParams) {
                        final CoordinatorLayout.LayoutParams coordinatorLayoutParams = (CoordinatorLayout.LayoutParams) params;
                        behavior = coordinatorLayoutParams.getBehavior();
                    } else {
                        behavior = null;
                    }

                    if (behavior instanceof BottomBehavior) {
                        final boolean scrollable = ((BottomBehavior) behavior).isScrollable();
                        int systemBottomNavigation = activity.hasTranslucentNavigation() ? activity.getNavigationBarHeight() : 0;

                        MiscUtils.log(TAG, Log.VERBOSE, "scrollable: " + scrollable);

                        int totalHeight;

                        if (scrollable) {
                            if (systemBottomNavigation > 0) {
                                totalHeight = systemBottomNavigation;
                            } else {
                                totalHeight = navigationHeight;
                            }
                        } else {
                            totalHeight = navigation.getNavigationHeight();
                        }

                        createAdater(totalHeight, activity.hasManagedToolbarScroll());
                    } else {
                        createAdater(navigationHeight, activity.hasAppBarLayout());
                    }
                }
            });
        } else {
            createAdater(navigationHeight, activity.hasAppBarLayout());
        }

        if (!activity.hasManagedToolbarScroll()) {
            scrollHelper = new ToolbarScrollHelper(activity, activity.getToolbar());
            scrollHelper.initialize(mRecyclerView);
        }
    }

    private void createAdater(int height, final boolean hasAppBarLayout) {
        MiscUtils.log(getClass().getSimpleName(), Log.INFO, "createAdapter(" + height + ")");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(getContext(), height, hasAppBarLayout, createData()));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final Button button1;
        final Button button2;
        final int marginBottom;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            marginBottom = ((MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
            button1 = (Button) itemView.findViewById(android.R.id.button1);
            button2 = (Button) itemView.findViewById(android.R.id.button2);
        }
    }

    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {
        private final int navigationHeight;
        private final Book[] data;
        private final boolean hasAppBarLayout;

        public Adapter(final Context context, final int navigationHeight, final boolean hasAppBarLayout, final Book[] data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
            this.hasAppBarLayout = hasAppBarLayout;
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            final TwoLinesViewHolder holder = new TwoLinesViewHolder(view);

            holder.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Drawable dr = getResources().getDrawable(R.drawable.button_pressed);
                    dr.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_ATOP);

                    holder.button1.setBackground(dr);

//                    fragintro fragintro = new fragintro();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_main, fragintro);
//                    fragmentTransaction.commit();

                    Snackbar snackbar =
                        Snackbar.make(mRoot, "Button 1 of item " + holder.getAdapterPosition(), Snackbar.LENGTH_LONG)
                            .setAction(
                                "Action",
                                null
                            );
                    snackbar.show();
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, final int position) {
            ((MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = 0;
            if (position == getItemCount() - 1) {
                ((MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom + navigationHeight;
            } else if (position == 0 && !hasAppBarLayout) {
                ((MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = scrollHelper.getToolbarHeight();
            } else {
                ((MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom;
            }

            final Book item = data[position];
            holder.button1.setText(item.title);

        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    private Book[] createData() {
        return new Book[]{
            new Book("Introduction", "Scott Masterson" ),
            new Book("The Service Lifecycle", "Ali Conners"),
            new Book("Service Strategy", "Sandra Adams"),
            new Book("Service Design", "Janet Perkins"),
            new Book("Service Transition", "Peter Carlsson"),
            new Book("Service Operation", "Trevor Hansen"),
            new Book("Continual Service Imperovement", "Britta Holt"),
        };
    }

    static class Book {
        final String title;
        final String author;

        Book(final String title, final String author) {
            this.title = title;
            this.author = author;
        }
    }
}

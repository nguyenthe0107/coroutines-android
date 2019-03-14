package android.support.core.base;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

public abstract class BasePager extends PagerAdapter {
    private SparseArray<PagerHolder> mCache = new SparseArray<>();

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return o == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PagerHolder viewHolder = onCreateViewHolder(container, getViewType(position));
        View view = viewHolder.itemView;
        mCache.put(position, viewHolder);
        container.addView(view);
        viewHolder.mPosition = position;
        notifyItemChanged(position);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        PagerHolder pagerHolder = mCache.get(position);
        mCache.remove(position);
        pagerHolder.onRecycled();
        container.removeView((View) object);
    }

    public void notifyItemChanged(int position) {
        notifyItemChanged(position, null);
    }

    public void notifyItemChanged(int position, Object payload) {
        Object item = getItem(position);
        PagerHolder pagerHolder = mCache.get(position);
        if (item != null && pagerHolder != null) {
            if (payload != null) {
                pagerHolder.bind(item, payload);
            } else {
                pagerHolder.bind(item);
            }
        }
    }

    protected int getViewType(int position) {
        return position;
    }

    protected abstract PagerHolder onCreateViewHolder(ViewGroup container, int viewType);

    protected abstract Object getItem(int position);

    public <T> PagerHolder<T> getViewHolder(int index) {
        return mCache.get(index);
    }

    public static class PagerHolder<T> {

        public final View itemView;
        public int mPosition;
        protected T item;

        public PagerHolder(View itemView) {
            this.itemView = itemView;
        }

        public PagerHolder(ViewGroup parent, @LayoutRes int layoutId) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(layoutId, parent, false);
        }

        public int getPosition() {
            return mPosition;
        }

        public void bind(T item) {
            this.item = item;
        }

        public void bind(T item, @NotNull Object payload) {
            this.item = item;
        }

        public <V extends View> V findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }

        public void onRecycled() {

        }
    }

}

package com.objectlife.statelayout;

/*
 * Copyright (C) 2016 objectlife
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.AbsSavedState;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author objectlife (wangyuyanmail[at]gmail[dot]com)
 */
public class StateLayout extends FrameLayout {

    public static final int STATE_NONE = 0b00000000;
    public static final int STATE_DEFAULT = 0b00000001;
    public static final int STATE_CONTENT = 0b00000010;
    public static final int STATE_EMPTY = 0b00000100;
    public static final int STATE_ERROR = 0b00001000;
    public static final int STATE_LOADING = 0b00010000;

    public static int custom(int i) {
        return STATE_LOADING << (i+1);
    }

    private int currentState = STATE_DEFAULT;

    public StateLayout(Context context) {
        this(context, null);
    }

    public StateLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StateLayout);

            currentState = a.getInteger(R.styleable.StateLayout_initWith, currentState);

            a.recycle();
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showStateView(currentState);
    }

    private StateLayout setView(int state, View stateView, boolean add) {
        if (state == STATE_NONE) {
            return this;
        }
        ((LayoutParams) stateView.getLayoutParams()).state = state;
        if (add) {
            addView(stateView);
        }
        return this;
    }

    /**
     * <p>Set state view.</p>
     *
     * @param state     The state of the view to add
     * @param stateView The state view to add
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setView(int state, View stateView) {
        return setView(state, stateView, false);
    }


    public StateLayout addView(int state, View stateView) {
        setView(state, stateView, true);
        return this;
    }

    /**
     * <p>Specify content view with the given id</p>
     *
     * @param state     The state of the view to add
     * @param viewResId The id to specify
     * @return This StateLayout object to allow for chaining of calls to set methods
     */
    public StateLayout setViewResId(int state, @IdRes int viewResId) {
        setView(state, findViewById(viewResId));
        return this;
    }

    public void initWith(int state) {
        currentState = state;

        if (ViewCompat.isAttachedToWindow(this)) {
            showStateView(state);
        }
    }


    public void setState(int state) {
        if (currentState == state) {
            return;
        }
        currentState = state;
        showStateView(state);
    }

    public int getState() {
        return currentState;
    }

    public boolean hasState(int state) {
        return hasFlag(currentState, state);
    }

    private void showStateView(int state) {
        Log.d("StateLayout", "show state = " + currentState);
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final int state1 = ((LayoutParams) child.getLayoutParams()).state;
            child.setVisibility(hasFlag(state1, state) ? VISIBLE : GONE);
            Log.d("StateLayout", "child state = " + state1);
        }
    }

    private boolean hasFlag(int state, int flag) {
        return (state & flag) != 0;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.currentLayoutState = currentState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    public static class SavedState extends AbsSavedState implements Parcelable {

        int currentLayoutState;

        protected SavedState(@NonNull Parcelable superState) {
            super(superState);
        }

        protected SavedState(Parcel in, ClassLoader loader) {
            super(in);
            this.currentLayoutState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentLayoutState);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private int state = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.StateLayout_Layout);

            state = a.getInteger(R.styleable.StateLayout_Layout_layout_state, -1);

            a.recycle();
        }
    }
}


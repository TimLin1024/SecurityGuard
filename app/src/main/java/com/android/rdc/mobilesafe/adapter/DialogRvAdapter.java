package com.android.rdc.mobilesafe.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;

public class DialogRvAdapter extends BaseSimpleRvAdapter {
    private int mPosition = 0;//默认选中项

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.item_text;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new TextViewHolder(view);
    }

    class TextViewHolder extends BaseRvHolder {
        TextView mTvText;

        public TextViewHolder(View itemView) {
            super(itemView);
            mTvText = itemView.findViewById(R.id.tv_text);
        }

        @Override
        protected void bindView(Object o) {
            if (getAdapterPosition() == mPosition) {
                mTvText.setText(String.format("> %s", o.toString()));
                mTvText.setTextColor(Color.parseColor("#58C18D"));
//                mTvText.setPressed(true);
            } else {
                mTvText.setText(String.format("  %s", o.toString()));
                mTvText.setTextColor(Color.BLACK);
            }
        }
    }
}

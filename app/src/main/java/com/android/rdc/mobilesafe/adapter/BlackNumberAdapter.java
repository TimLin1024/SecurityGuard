package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.callback.OnCheckedCountChangeListener;
import com.android.rdc.mobilesafe.entity.BlackContactInfo;

import butterknife.BindView;

public class BlackNumberAdapter extends BaseSimpleRvAdapter<BlackContactInfo> {
    private OnIvClickListener mOnIvClickListener;
    private boolean mShowCheckBox;
    private OnCheckedCountChangeListener mListener;

    @Override
    protected int setLayoutId() {
        return R.layout.item_black_number;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new BlackNumberVH(view);
    }

    public boolean isShowCheckBox() {
        return mShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
    }

    public void setOnCheckedCountChangeListener(OnCheckedCountChangeListener listener) {
        mListener = listener;
    }

    public void setOnIvClickListener(OnIvClickListener onIvClickListener) {
        mOnIvClickListener = onIvClickListener;
    }

    public interface OnIvClickListener {
        void onIvClick(int position);
    }

    class BlackNumberVH extends BaseRvHolder implements CompoundButton.OnCheckedChangeListener {

        @BindView(R.id.tv_black_name)
        TextView mTvBlackName;
        @BindView(R.id.tv_phone_num)
        TextView mTvPhoneNum;
        @BindView(R.id.tv_black_mode)
        TextView mTvBlackMode;
        @BindView(R.id.cb)
        CheckBox mCb;

        private BlackContactInfo mBlackContactInfo;

        BlackNumberVH(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(final BlackContactInfo contactInfo) {
            mBlackContactInfo = contactInfo;
            mTvBlackName.setText(contactInfo.getContractName());
            mTvPhoneNum.setText(contactInfo.getPhoneNumber());
            mTvBlackMode.setText(contactInfo.getStringMode());

            mCb.setChecked(contactInfo.isSelected());
            mCb.setOnCheckedChangeListener(this);
            if (mShowCheckBox) {
                mTvBlackMode.setVisibility(View.GONE);
                mCb.setVisibility(View.VISIBLE);
//                mCb.setChecked(contactInfo.isSelected());
            } else {
                mTvBlackMode.setVisibility(View.VISIBLE);
                mCb.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return super.onLongClick(v);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mBlackContactInfo.setSelected(isChecked);

            if (mListener != null) {
                int count = 0;
                for (BlackContactInfo blackContactInfo : mDataList) {
                    if (blackContactInfo.isSelected()) {
                        count++;
                    }
                }
                mListener.onCheckedCountChanged(count);
            }
        }
    }
}

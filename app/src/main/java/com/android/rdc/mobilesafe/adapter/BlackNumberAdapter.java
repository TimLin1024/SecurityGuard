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
    private int mCheckCount;

    @Override
    protected int setLayoutId() {
        return R.layout.item_black_number;
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

    class BlackNumberVH extends BaseRvHolder {

        @BindView(R.id.tv_black_name)
        TextView mTvBlackName;
        @BindView(R.id.tv_phone_num)
        TextView mTvPhoneNum;
        @BindView(R.id.tv_black_mode)
        TextView mTvBlackMode;
        @BindView(R.id.cb)
        CheckBox mCb;

        BlackNumberVH(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(final BlackContactInfo contactInfo) {
            mTvBlackName.setText(contactInfo.getContractName());
            mTvPhoneNum.setText(contactInfo.getPhoneNumber());
            mTvBlackMode.setText(contactInfo.getStringMode());
            if (mShowCheckBox) {
                mTvBlackMode.setVisibility(View.GONE);
                mCb.setVisibility(View.VISIBLE);
                mCb.setChecked(contactInfo.isSelected());
                mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        contactInfo.setSelected(isChecked);
                        if (mListener != null) {
                            if (isChecked) {
                                mListener.onCheckedCountChanged(++mCheckCount);
                            } else {
                                mListener.onCheckedCountChanged(--mCheckCount);
                            }
                        }
                    }
                });
            } else {
                mTvBlackMode.setVisibility(View.VISIBLE);
                mCb.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return super.onLongClick(v);
        }
    }
}

package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.entity.BlackContactInfo;

import butterknife.BindView;

public class BlackNumberAdapter extends BaseSimpleRvAdapter<BlackContactInfo> {
    private OnIvClickListener mOnIvClickListener;

    @Override
    protected int setLayoutId() {
        return R.layout.item_black_number;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new BlackNumberVH(view);
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
        protected void bindView(BlackContactInfo contactInfo) {
            mTvBlackName.setText(contactInfo.getContractName());
            mTvPhoneNum.setText(contactInfo.getPhoneNumber());
            mTvBlackMode.setText(contactInfo.getStringMode());
        }

        @Override
        public boolean onLongClick(View v) {
            return super.onLongClick(v);
        }
    }
}

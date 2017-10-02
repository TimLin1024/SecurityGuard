package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.ImageView;
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
        @BindView(R.id.tv_black_mode)
        TextView mTvBlackMode;
        @BindView(R.id.iv_black_delete)
        ImageView mIvBlackDelete;

        BlackNumberVH(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(BlackContactInfo contactInfo) {
            mTvBlackName.setText(contactInfo.getContractName());
            mTvBlackMode.setText(String.valueOf(contactInfo.getMode()));
            mIvBlackDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnIvClickListener != null) {
                        mOnIvClickListener.onIvClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}

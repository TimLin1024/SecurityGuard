package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.callback.OnCheckedCountChangeListener;
import com.android.rdc.mobilesafe.entity.ContactInfo;

import butterknife.BindView;

public class ContactAdapter extends BaseSimpleRvAdapter<ContactInfo> {

    private OnCheckedCountChangeListener mListener;

    @Override
    protected int setLayoutId() {
        return R.layout.item_contact;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new ContactVH(view);
    }

    public void setCheckedCountChangeListener(OnCheckedCountChangeListener listener) {
        mListener = listener;
    }

    class ContactVH extends BaseRvHolder implements CompoundButton.OnCheckedChangeListener {

        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_phone_num)
        TextView mTvPhoneNum;
        @BindView(R.id.cb_selected)
        CheckBox mCbSelected;

        ContactInfo mContactInfo;

        public ContactVH(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(int position, ContactInfo contactInfo) {

        }

        @Override
        protected void bindView(ContactInfo contactInfo) {
            mContactInfo = contactInfo;
            mTvName.setText(contactInfo.getName());
            mTvPhoneNum.setText(contactInfo.getPhoneNum());
            mCbSelected.setChecked(contactInfo.isChecked());
            mCbSelected.setOnCheckedChangeListener(this);
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mContactInfo.setChecked(isChecked);

            if (mListener != null) {
                int count = 0;
                for (ContactInfo contactInfo : mDataList) {
                    if (contactInfo.isChecked()) {
                        count++;
                    }
                }
                mListener.onCheckedCountChanged(count);
            }

        }
    }
}

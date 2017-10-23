package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.entity.ContactInfo;

import butterknife.BindView;

public class ContactAdapter extends BaseSimpleRvAdapter<ContactInfo> {

    @Override
    protected int setLayoutId() {
        return R.layout.item_contact;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new ContactVH(view);
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
        public void onClick(View v) {
//            super.onClick(v);
            mCbSelected.setChecked(!mContactInfo.isChecked());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mContactInfo.setChecked(isChecked);
        }
    }

//    /**
//     * 获取catalog首次出现位置
//     */
//    public int getPositionForSection(String catalog) {
//        for (int i = 0; i < getItemCount(); i++) {
//            String sortStr = mDataList.get(i).getFirstLetter();
//            if (catalog.equalsIgnoreCase(sortStr)) {
//                return i;
//            }
//        }
//        return -1;
//    }


}

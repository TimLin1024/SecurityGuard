package com.android.rdc.mobilesafe.adapter;

import android.view.View;
import android.widget.CheckBox;
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

    class ContactVH extends BaseRvHolder {

//        @BindView(R.id.tv_alphabet)
//        TextView mTvAlphabet;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_phone_num)
        TextView mTvPhoneNum;
        @BindView(R.id.cb_selected)
        CheckBox mCbSelected;

        public ContactVH(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(int position, ContactInfo contactInfo) {
            mTvName.setText(contactInfo.getName());
            mTvPhoneNum.setText(contactInfo.getPhoneNum());

            String firstLetter = contactInfo.getFirstLetter();
            if (firstLetter == null) {
                firstLetter = "#";
            }
//            if (position == 0) {
//                mTvAlphabet.setVisibility(View.VISIBLE);
//                mTvAlphabet.setText(String.valueOf(firstLetter.charAt(0)));
//                return;
//            }

//            if (mDataList.get(position - 1).getFirstLetter() != null &&
//                    firstLetter.charAt(0) != mDataList.get(position - 1).getFirstLetter().charAt(0)) {
//                mTvAlphabet.setVisibility(View.VISIBLE);
//                mTvAlphabet.setText(String.valueOf(firstLetter.charAt(0)));
//            } else {
//                mTvAlphabet.setVisibility(View.GONE);
//            }
        }

        @Override
        protected void bindView(ContactInfo contactInfo) {

        }
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mDataList.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }


}

package com.sososhopping.customer.purchase.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.sososhopping.customer.R;
import com.sososhopping.customer.purchase.viewmodel.PurchaseViewModel;
import com.sososhopping.customer.common.types.enumType.PaymentType;
import com.sososhopping.customer.databinding.PurchaseMainBinding;

public class PurchaseFragment_Payments {

    PurchaseMainBinding binding;
    PurchaseViewModel purchaseViewModel;
    Context context;

    public PurchaseFragment_Payments(PurchaseMainBinding binding, PurchaseViewModel purchaseViewModel, Context context) {
        this.binding = binding;
        this.purchaseViewModel = purchaseViewModel;
        this.context = context;
    }

    protected void setPaymentsLayout(Resources rs){
        binding.includeLayoutPurchase.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.radio_cash:{
                        binding.includeLayoutPurchase.textViewCashNotice.setVisibility(View.VISIBLE);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setEnabled(false);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setHint(rs.getString(R.string.payments_info));

                        //결제수단 에러 제거
                        binding.includeLayoutPurchase.menuPaymentsExtra.setError(null);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setErrorEnabled(false);
                        //결제수단 등록
                        purchaseViewModel.setPaymentType(PaymentType.CASH);

                        //배송 안되게
                        binding.includeLayoutVisit.toggleButton.check(R.id.button_visit);
                        binding.includeLayoutVisit.buttonDelivery.setEnabled(false);

                        break;
                    }

                    case R.id.radio_localPay:{

                        binding.includeLayoutPurchase.textViewCashNotice.setVisibility(View.GONE);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setEnabled(false);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setHint(rs.getString(R.string.payments_info));

                        //결제수단 에러 제거
                        binding.includeLayoutPurchase.menuPaymentsExtra.setError(null);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setErrorEnabled(false);

                        //결제수단 등록
                        purchaseViewModel.setPaymentType(PaymentType.LOCAL);

                        //배송 가능
                        binding.includeLayoutVisit.buttonDelivery.setEnabled(true);
                        break;
                    }

                    case R.id.radio_extra:{
                        binding.includeLayoutPurchase.textViewCashNotice.setVisibility(View.GONE);
                        binding.includeLayoutPurchase.menuPaymentsExtra.setEnabled(true);
                        //배송 가능
                        binding.includeLayoutVisit.buttonDelivery.setEnabled(true);
                        break;
                    }
                }
            }
        });

        String[] paymentsType = rs.getStringArray(R.array.payments_types);
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, paymentsType);
        binding.includeLayoutPurchase.autoCompleteSelectedMenu.setAdapter(adapterType);

        binding.includeLayoutPurchase.autoCompleteSelectedMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.includeLayoutPurchase.menuPaymentsExtra.setError(null);
                binding.includeLayoutPurchase.menuPaymentsExtra.setErrorEnabled(false);

                switch (i){
                    case 0:
                        purchaseViewModel.setPaymentType(PaymentType.CARD);
                        break;
                    case 1:
                        purchaseViewModel.setPaymentType(PaymentType.PHONE);
                        break;
                    case 2:
                        purchaseViewModel.setPaymentType(PaymentType.KAKAO);
                        break;
                    case 3:
                        purchaseViewModel.setPaymentType(PaymentType.NAVER);
                        break;
                    case 4:
                        purchaseViewModel.setPaymentType(PaymentType.TOSS);
                        break;
                }
            }
        });
    }

    protected String checkPayments(Resources rs){
        if(binding.includeLayoutPurchase.radioGroup.getCheckedRadioButtonId() == R.id.radio_extra){
            if(TextUtils.isEmpty(binding.includeLayoutPurchase.autoCompleteSelectedMenu.getText())){
                binding.includeLayoutPurchase.menuPaymentsExtra.setError(rs.getString(R.string.final_purchase_empty));
                binding.includeLayoutPurchase.menuPaymentsExtra.setErrorEnabled(true);

                return rs.getString(R.string.final_process_error);
            }
        }
        return null;
    }
}

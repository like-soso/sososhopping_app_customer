package com.sososhopping.customer.purchase.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.NetworkStatus;
import com.sososhopping.customer.common.textValidate.NameWatcher;
import com.sososhopping.customer.common.textValidate.PhoneWatcher;
import com.sososhopping.customer.purchase.viewmodel.PurchaseViewModel;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.databinding.PurchaseMainBinding;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lombok.Getter;

@Getter
public class PurchaseFragment_Visit {

    PurchaseMainBinding binding;
    PurchaseViewModel purchaseViewModel;
    Context context;

    ArrayList<TextInputLayout> delivery;
    ArrayList<TextInputLayout> visit;

    public PurchaseFragment_Visit(PurchaseMainBinding binding, PurchaseViewModel purchaseViewModel, Context context) {
        this.binding = binding;
        this.purchaseViewModel = purchaseViewModel;
        this.context = context;

        visit = new ArrayList<>();
        visit.add(binding.includeLayoutVisit.textFieldName);
        visit.add(binding.includeLayoutVisit.textFieldPhone);
        visit.add(binding.includeLayoutVisit.textFieldDate);
        visit.add(binding.includeLayoutVisit.textFieldTime);

        delivery = new ArrayList<>();
        delivery.add(binding.includeLayoutVisit.textFieldDeliveryName);
        delivery.add(binding.includeLayoutVisit.textFieldDeliveryPhone);
        delivery.add(binding.includeLayoutVisit.textFieldRoadAddress);
        delivery.add(binding.includeLayoutVisit.textFieldDetailAddress);
    }

    private void resetInput(int id){
        ArrayList<TextInputLayout> target;
        if(id == R.id.button_visit){
            target = delivery;
        }
        else{
            target = visit;
        }
        for(TextInputLayout t : target) {
            t.getEditText().setText(null);
            t.setError(null);
            t.setErrorEnabled(false);
        }
    }

    protected String checkInput(Resources rs){
        ArrayList<TextInputLayout> target;
        int id = binding.includeLayoutVisit.toggleButton.getCheckedButtonId();

        if(id == R.id.button_visit){
            target = visit;
        }
        else{
            target = delivery;
        }

        for(TextInputLayout t : target){
            if(TextUtils.isEmpty(t.getEditText().getText())){
                return rs.getString(R.string.final_visit_empty);
            }
            if(t.getError() != null){
                return rs.getString(R.string.final_process_error);
            }
        }
        return null;
    }

    protected void setVisitLayout(FragmentManager fm, Resources rs) {
        binding.includeLayoutVisit.layoutDelivery.setVisibility(View.GONE);
        binding.includeLayoutVisit.toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                switch (checkedId) {
                    case R.id.button_visit:
                        if (isChecked) {
                            binding.includeLayoutVisit.layoutDelivery.setVisibility(View.GONE);
                            binding.includeLayoutVisit.layoutVisit.setVisibility(View.VISIBLE);

                            //배달비 제거
                            purchaseViewModel.getDeliveryPrice().setValue(0);
                            purchaseViewModel.setOrderType(OrderType.ONSITE);
                        }
                        break;

                    case R.id.button_delivery:
                        if (isChecked) {
                            binding.includeLayoutVisit.layoutVisit.setVisibility(View.GONE);
                            binding.includeLayoutVisit.layoutDelivery.setVisibility(View.VISIBLE);

                            //배달비 추가
                            purchaseViewModel.getDeliveryPrice().setValue(purchaseViewModel.getShopInfo().getValue().getDeliveryCharge());
                            purchaseViewModel.setOrderType(OrderType.DELIVERY);
                        }
                        break;
                }
                resetInput(checkedId);
            }
        });

        //방문예약 입력 검증
        binding.includeLayoutVisit.editTextName.addTextChangedListener(
                new NameWatcher(binding.includeLayoutVisit.textFieldName, rs.getString(R.string.signup_error_name)));

        binding.includeLayoutVisit.editTextPhone.addTextChangedListener(
                new PhoneWatcher(binding.includeLayoutVisit.textFieldPhone, rs.getString(R.string.signup_error_phone))
        );

        binding.includeLayoutVisit.editTextDate.setFocusable(false);
        binding.includeLayoutVisit.editTextDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                binding.includeLayoutVisit.editTextDate.setText(
                                        DateFormatMethod.dateFormat(year, monthOfYear, dayOfMonth)
                                );
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));

                try{
                    setDialogDate(dialog);
                    dialog.show(fm, "픽업 날짜");
                }
                catch (Exception e){
                    Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        binding.includeLayoutVisit.editTextDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.includeLayoutVisit.editTextTime.setText(null);
            }
        });


        binding.includeLayoutVisit.editTextTime.setFocusable(false);
        binding.includeLayoutVisit.editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.includeLayoutVisit.editTextDate.getText())) {
                    Toast.makeText(context, "날짜를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar c = Calendar.getInstance();
                TimePickerDialog dialog = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                binding.includeLayoutVisit.editTextTime.setText(
                                        DateFormatMethod.timeFormat(hourOfDay, minute)
                                );
                            }
                        },
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true
                );
                try {
                    setDialogTime(dialog, String.valueOf(binding.includeLayoutVisit.editTextDate.getText()));
                    dialog.show(fm, "픽업 시간");
                }
                catch (Exception e) {
                    Toast.makeText(context, "오류가 발생하였습니다. 다른 요일을 선택해주세요", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    protected void setDeliveryLayout(Resources rs){

        //배달예약 입력 검증
        binding.includeLayoutVisit.editTextDeliveryName.addTextChangedListener(
                new NameWatcher(binding.includeLayoutVisit.textFieldDeliveryName, rs.getString(R.string.signup_error_name))
        );
        binding.includeLayoutVisit.editTextDeliveryPhone.addTextChangedListener(
                new PhoneWatcher(binding.includeLayoutVisit.textFieldDeliveryPhone, rs.getString(R.string.signup_error_phone))
        );

        binding.includeLayoutVisit.checkBoxMyInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(purchaseViewModel.getMyInfo().getValue() != null){
                        MyInfoModel md = purchaseViewModel.getMyInfo().getValue();
                        if(md.getName() != null){
                            binding.includeLayoutVisit.editTextDeliveryName.setText(md.getName());
                        }
                        if(md.getPhone() != null){
                            binding.includeLayoutVisit.editTextDeliveryPhone.setText(md.getPhone());
                        }
                        if(md.getStreetAddress() != null){
                            binding.includeLayoutVisit.editTextRoadAddress.setText(md.getStreetAddress());
                        }
                        if(md.getDetailedAddress() != null){
                            binding.includeLayoutVisit.editTextDetailAddress.setText(md.getDetailedAddress());
                        }
                    }
                }
                else{
                    binding.includeLayoutVisit.editTextDeliveryName.setText(null);
                    binding.includeLayoutVisit.editTextDeliveryPhone.setText(null);
                    binding.includeLayoutVisit.editTextRoadAddress.setText(null);
                    binding.includeLayoutVisit.editTextDetailAddress.setText(null);
                }
            }
        });
    }

    public void setDialogDate(DatePickerDialog dialog){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        //7일간 가능
        int lastdays = 7;

        //주문시각 1시간 뒤부터 가능
        try{
            //영업 안하면 exception 발생
            int start = compareStartTime(cal);
            int end = Integer.parseInt(getEndTime(cal.get(Calendar.DAY_OF_WEEK)));

            if(start >= end){
                cal.add(Calendar.DATE,1);
                dialog.setMinDate(cal);
                lastdays--;
            }
        }
        //애초에 안하는 날이니 안하게 체크
        catch (Exception e){
            Calendar[] c = new Calendar[1];
            c[0] = cal;
            dialog.setDisabledDays(c);
        }
        finally {
            dialog.setMinDate(cal);

            for (int i = 0; i < lastdays; i++) {
                cal.add(Calendar.DATE, 1);

                if (!checkUnAvailableDate(cal.get(Calendar.DAY_OF_WEEK))) {
                    Calendar[] c = new Calendar[1];
                    c[0] = cal;
                    dialog.setDisabledDays(c);
                }
            }
            dialog.setMaxDate(cal);
        }
    }

    public void setDialogTime(TimePickerDialog dialog, String date) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR,1);

        String start, end;

        //1시간 더한 날이랑 선택된 날이랑 같으면 시작시간 비교 필요
        if(sf.format(cal.getTime()).equals(date)){
            start = String.valueOf(compareStartTime(cal));
            end = getEndTime(cal.get(Calendar.DAY_OF_WEEK));
        }
        else{
            Date d = sf.parse(date);
            cal.setTime(d);
            end = getEndTime(cal.get(Calendar.DAY_OF_WEEK));
            start = getStartTime(cal.get(Calendar.DAY_OF_WEEK));
        }

        if(Integer.parseInt(start) >= Integer.parseInt(end)){
            throw new Exception();
        }

        //앞에 0 사라짐
        if(start.length() < 4){
            start = 0+start;
        }

        Timepoint[] t = getSelectableTime(start, end);
        dialog.setMinTime(t[0]);
        dialog.setMaxTime(t[1]);
    }

    public Timepoint[] getSelectableTime(String start, String end){
        int startH = Integer.parseInt(start.substring(0,2));
        int startM = Integer.parseInt(start.substring(2,4));

        int endH = Integer.parseInt(end.substring(0,2));
        int endM = Integer.parseInt(end.substring(2,4));

        Timepoint[] t = new Timepoint[2];
        t[0] = new Timepoint(startH, startM);
        t[1] = new Timepoint(endH, endM);
        return t;
    }

    public boolean checkUnAvailableDate(int dayOfWeek){
        dayOfWeek = dayOfWeekSwitcher(dayOfWeek);
        try{
            if(purchaseViewModel.getShopInfo().getValue() != null){
                return purchaseViewModel.getShopInfo().getValue().getBusinessDays().get(dayOfWeek).getIsOpen();
            }
        }catch (Exception e){
        }
        return true;
    }

    public int compareStartTime(Calendar cal){
        int currentMin = Integer.parseInt(new SimpleDateFormat("HHmm").format(cal.getTime()));
        int businessMin = Integer.parseInt(getStartTime(cal.get(Calendar.DAY_OF_WEEK)));
        return Math.max(currentMin, businessMin);
    }

    public String getStartTime(int dayOfWeek){
        dayOfWeek = dayOfWeekSwitcher(dayOfWeek);
        if(purchaseViewModel.getShopInfo().getValue() != null){
            return purchaseViewModel.getShopInfo().getValue().getBusinessDays().get(dayOfWeek).getOpenTime();
        }
        return null;
    }

    public String getEndTime(int dayOfWeek){
        dayOfWeek = dayOfWeekSwitcher(dayOfWeek);
        if(purchaseViewModel.getShopInfo().getValue() != null){
            return purchaseViewModel.getShopInfo().getValue().getBusinessDays().get(dayOfWeek).getCloseTime();
        }
        return null;
    }

    public int dayOfWeekSwitcher(int dayOfWeek){
        if(dayOfWeek == Calendar.SUNDAY){
            dayOfWeek = 6;
        }
        else{
            dayOfWeek = dayOfWeek -2;
        }
        return dayOfWeek;
    }
}

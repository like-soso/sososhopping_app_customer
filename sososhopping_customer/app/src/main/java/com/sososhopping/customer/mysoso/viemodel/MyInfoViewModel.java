package com.sososhopping.customer.mysoso.viemodel;

import com.sososhopping.customer.account.dto.NicknameDupCheckRequestDto;
import com.sososhopping.customer.account.dto.PhoneDupCheckRequestDto;
import com.sososhopping.customer.account.repository.SignUpRepository;
import com.sososhopping.customer.mysoso.dto.MyInfoEditDto;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.mysoso.repository.MysosoMyInfoRepository;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyInfoViewModel {
    private MysosoMyInfoRepository myInfoRepository = MysosoMyInfoRepository.getInstance();

    private String email;
    private String password;
    private String name;
    private String phone;
    private String nickName;
    private String roadAddress;
    private String detailAddress;

    private String pastNickName;
    private String pastPhone;

    public void setMyInfo(MyInfoModel myInfo){
        email = myInfo.getEmail();
        //비밀번호는 수정시
        password = (null);
        name = myInfo.getName();
        phone = myInfo.getPhone();
        nickName = myInfo.getNickname();
        roadAddress = myInfo.getStreetAddress();
        detailAddress = myInfo.getDetailedAddress();

        pastNickName = myInfo.getNickname();
        pastPhone = myInfo.getPhone();
    }

    public boolean checkPastNickname(String nickName){
        return nickName.equals(pastNickName);
    }
    public boolean checkPastPhone(String phone){
        return phone.equals(pastPhone);
    }

    public void requestMyInfo(String token,
                              Consumer<MyInfoModel> onSuccess,
                              Runnable onFailedLogIn,
                              Runnable onFailed,
                              Runnable onError){

        myInfoRepository.requestMyInfo(token, onSuccess, onFailedLogIn, onFailed, onError);
    }

    public void requestEditInfo(String token,
                                Runnable onEditSuccess,
                                Runnable onNetworkError){

        myInfoRepository.requestEditInfo(token, toMyInfoEditDto(), onEditSuccess, onNetworkError);
    }

    public void requestPhoneDupCheck(String phone,
                                     Runnable onNotDuplicated,
                                     Runnable onDuplicated,
                                     Runnable onError){
        SignUpRepository.getInstance().requestPhoneDuplicationCheck(this.toPhoneDupCheckRequestDto(phone), onNotDuplicated, onDuplicated, onError);
    }
    public void requestNicknameDupCheck(String nickname,
                                        Runnable onNotDuplicated,
                                        Runnable onDuplicated,
                                        Runnable onError) {
        SignUpRepository.getInstance().requestNicknameDuplicationCheck(this.toNicknameDupCheckRequestDto(nickname), onNotDuplicated, onDuplicated, onError);
    }

    public PhoneDupCheckRequestDto toPhoneDupCheckRequestDto(String phone){
        return new PhoneDupCheckRequestDto(phone);
    }

    public NicknameDupCheckRequestDto toNicknameDupCheckRequestDto(String nickname){
        return new NicknameDupCheckRequestDto(nickname);
    }

    public MyInfoEditDto toMyInfoEditDto(){
        return new MyInfoEditDto(
                this.password,
                this.name,
                this.phone,
                this.nickName,
                this.roadAddress,
                this.detailAddress
        );
    }
}

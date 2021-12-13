package com.sososhopping.customer.account.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.account.dto.EmailDupCheckRequestDto;
import com.sososhopping.customer.account.dto.NicknameDupCheckRequestDto;
import com.sososhopping.customer.account.dto.PhoneDupCheckRequestDto;
import com.sososhopping.customer.account.dto.SignUpRequestDto;
import com.sososhopping.customer.account.repository.SignUpRepository;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SignUpViewModel extends ViewModel {
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> phone = new MutableLiveData<>();
    private MutableLiveData<String> nickName = new MutableLiveData<>();
    private MutableLiveData<String> roadAddress = new MutableLiveData<>();
    private MutableLiveData<String> detailAddress = new MutableLiveData<>();

    //이메일 중복 확인
    private final MutableLiveData<Boolean> emailDupChecked = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> nicknameDupChecked = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> phoneDupChecked = new MutableLiveData<>(false);

    //Repository
    private final SignUpRepository signUpRepository = SignUpRepository.getInstance();

    public MutableLiveData<String> getEmail() {
        return email;
    }
    public void setEmail(String e) {
        email.setValue(e);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password.setValue(p);
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String n) {
        this.name.setValue(n);
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String p) {
        this.phone.setValue(p.replaceAll("-",""));
    }

    public MutableLiveData<String> getNickName() {
        return nickName;
    }

    public void setNickName(String n) {
        this.nickName.setValue(n);
    }

    public MutableLiveData<String> getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String rA) {
        this.roadAddress.setValue(rA);
    }

    public MutableLiveData<String> getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String dA) {
        this.detailAddress.setValue(dA);
    }

    public String printVal(){
        return email.getValue() + " " +
                password.getValue() + " " +
                name.getValue() + " " +
                phone.getValue() + " " +
                nickName.getValue() + " " +
                roadAddress.getValue() + " " +
                detailAddress.getValue();
    }

    public void requestEmailDupCheck(String email,
                                     Runnable onChecked,
                                     Runnable onDuplicated,
                                     Runnable onError) {
        Runnable onNotDuplicated = () -> {
            onChecked.run();
            emailDupChecked.setValue(true);
        };
        signUpRepository.requestEmailDuplicationCheck(this.toEmailDupCheckRequestDto(email), onNotDuplicated, onDuplicated, onError);
    }

    public void requestNicknameDupCheck(String nickname,
                                        Runnable onNotDuplicated,
                                        Runnable onDuplicated,
                                        Runnable onError) {
        signUpRepository.requestNicknameDuplicationCheck(this.toNicknameDupCheckRequestDto(nickname), onNotDuplicated, onDuplicated, onError);
    }

    public void requestSignup(Runnable onSuccess,
                              Runnable onError) {
        signUpRepository.requestSignup(this.toSignupRequestDto(), onSuccess, onError);
    }

    public void requestPhoneDupCheck(String phone,
                                     Runnable onNotDuplicated,
                                     Runnable onDuplicated,
                                     Runnable onError){
        signUpRepository.requestPhoneDuplicationCheck(this.toPhoneDupCheckRequestDto(phone), onNotDuplicated, onDuplicated, onError);
    }

    public EmailDupCheckRequestDto toEmailDupCheckRequestDto(String email) {
        return new EmailDupCheckRequestDto(email);
    }

    public NicknameDupCheckRequestDto toNicknameDupCheckRequestDto(String nickname){
        return new NicknameDupCheckRequestDto(nickname);
    }

    public PhoneDupCheckRequestDto toPhoneDupCheckRequestDto(String phone){
        return new PhoneDupCheckRequestDto(phone);
    }

    public SignUpRequestDto toSignupRequestDto() {
        return new SignUpRequestDto(
                this.email.getValue(),
                this.password.getValue(),
                this.name.getValue(),
                this.phone.getValue(),
                this.nickName.getValue(),
                this.roadAddress.getValue(),
                this.detailAddress.getValue()
        );
    }
}
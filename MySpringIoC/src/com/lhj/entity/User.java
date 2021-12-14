package com.lhj.entity;


import com.lhj.Annotation.Autowired;
import com.lhj.Annotation.Component;
import com.lhj.Annotation.Qualifier;
import com.lhj.Annotation.Value;

@Component
public class User {

    @Value("1")
    private Integer id;
    @Value("lhj")
    private String account;
    @Value("18102343")
    private String password;

    @Autowired
    @Qualifier("MyUserInfo")
    private UserInfo userInfo;




    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

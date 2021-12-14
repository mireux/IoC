package com.lhj.entity;


import com.lhj.Annotation.Component;
import com.lhj.Annotation.Value;

@Component("MyUserInfo")
public class UserInfo {

    @Value("1")
    private Integer id;
    @Value("lhj")
    private String name;
    @Value("22")
    private Integer age;
    @Value("222222")
    private String avatar;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

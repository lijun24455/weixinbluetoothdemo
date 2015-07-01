package com.sysu.lijun.wechatbluetoothdemo.json;

/**
 * Created by lijun on 15/7/1.
 */
public class UserInfo {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String name;
    private String time;
    private String store;
    private String score;

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", store='" + store + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}

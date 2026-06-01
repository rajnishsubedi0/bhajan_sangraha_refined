package com.rkant.bhajanapp.secondActivities;

public class DataHolder {

    String bhajan_name_nepali;
    String bhajan_name_english;
    String id;
    public DataHolder(String bhajan_name_nepali, String bhajan_name_english,String id){
        this.bhajan_name_nepali =bhajan_name_nepali;
        this.bhajan_name_english=bhajan_name_english;
        this.id=id;
    }
    public String getId(){return id;}
    public String getBhajan_name_english() {
        return bhajan_name_english;
    }


    public String getBhajan_name_nepali() {
        return bhajan_name_nepali;
    }


} 
package com.shadhin.ahsanalqasas;

public class AyatModel {
    private String ayah_ar;
    private String ayah_trans;
    private String ayah_bn;
    private int surah_no;
    private int ayat_no;
    public  int pay_s = 0;
    public  int pay_e = 0;

    public  AyatModel(String ayah_ar, String ayah_trans, String ayah_bn, int surah_no, int ayat_no)
    {
        this.ayah_ar = ayah_ar;
        this.ayah_trans = ayah_trans;
        this.ayah_bn = ayah_bn;
        this.surah_no = surah_no;
        this.ayat_no = ayat_no;
    }
    public String getAyah_ar() {
        return ayah_ar;
    }

    public void setAyah_ar(String ayah_ar) {
        this.ayah_ar = ayah_ar;
    }

    public String getAyah_trans() {
        return ayah_trans;
    }

    public void setAyah_trans(String ayah_trans) {
        this.ayah_trans = ayah_trans;
    }

    public String getAyah_bn() {
        return ayah_bn;
    }

    public void setAyah_bn(String ayah_bn) {
        this.ayah_bn = ayah_bn;
    }

    public int getAyat_no() {
        return ayat_no;
    }

    public void setAyat_no(int ayat_no) {
        this.ayat_no = ayat_no;
    }

    public int getSurah_no() {
        return surah_no;
    }

    public void setSurah_no(int surah_no) {
        this.surah_no = surah_no;
    }

    public String getArabicAyat_no(){
        String sura_id = String.valueOf(this.ayat_no);
        char init = 0x0660;
        String arabic_no = "";
        for (int i = sura_id.length()-1; i >= 0; i--) {
            int c = sura_id.charAt(i);
            char arabic = (char)(init + (c - '0'));
            arabic_no += arabic;
        }
        return arabic_no;
    }
}

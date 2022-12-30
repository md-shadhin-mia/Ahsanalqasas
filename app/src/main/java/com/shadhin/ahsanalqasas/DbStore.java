package com.shadhin.ahsanalqasas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DbStore {
    SQLiteDatabase sqldb;
    private static DbStore Instant;
    private DbStore(SQLiteDatabase sqldb){
            this.sqldb = sqldb;
    }
    public static void init(Context context){
        File database = context.getDatabasePath("extractDb.db");
        SQLiteDatabase sqldb = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        if (Instant == null)
            Instant = new DbStore(sqldb);
    }

    public static DbStore getDbStoreInstant(){
        return Instant;
    }
    public ArrayList<AyatModel>  getAyatModelFrom(String ayatsEn){
        String sqlString = "";
        if(ayatsEn.indexOf(',') == -1) {
            String[] surahBewteen = ayatsEn.split("-");
            String SurahId_id = "";
            String ayatId_be = "";
            String ayats = surahBewteen[0];
            String[] ayat = ayats.split(":");
            SurahId_id = ayat[0];
            ayatId_be = ayat[1];

            ayats = surahBewteen[1];
            ayat = ayats.split(":");
            String ayatId_en = ayat[1];
            sqlString += "Select * From quran_ayat Where sura_id="+SurahId_id+" And verse_id Between "+ayatId_be+" And "+ayatId_en+";";
        }else{
            String[] queris = ayatsEn.split(",");
            ArrayList<String> sqlset = new ArrayList<String>();
            int priority=0;
            for(String query: queris){
                if(query.indexOf("-") != -1){
                    String[] surahBewteen = query.split("-");
                    String SurahId_id = "";
                    String ayatId_be = "";
                    String ayats = surahBewteen[0];
                    String[] ayat = ayats.split(":");
                    SurahId_id = ayat[0];
                    ayatId_be = ayat[1];

                    ayats = surahBewteen[1];
                    ayat = ayats.split(":");
                    String ayatId_en = ayat[1];
                    sqlset.add("Select *, "+(priority++)+" as priority From quran_ayat Where sura_id="+SurahId_id+" And verse_id Between "+ayatId_be+" And "+ayatId_en);
                }else if(query.indexOf("&") != -1){
                    String[] surahBewteen = query.split("&");
                    String SurahId_id = "";
                    String ayatId_be = "";
                    String ayats = surahBewteen[0];
                    String[] ayat = ayats.split(":");
                    SurahId_id = ayat[0];
                    ayatId_be = ayat[1];

                    ayats = surahBewteen[1];
                    ayat = ayats.split(":");
                    String ayatId_en = ayat[1];
                    sqlset.add("Select *,"+(priority++)+" as priority From quran_ayat Where sura_id="+SurahId_id+" And (verse_id="+ayatId_be+" OR verse_id="+ayatId_en+")");
                }
            }
            sqlString = String.join(" UNION ", sqlset.toArray(new String[0]))+" order by priority;";
        }


        Log.d("SQL QUERY", "QSL: "+sqlString);
        Cursor cursor = sqldb.rawQuery(sqlString, null);
        ArrayList<AyatModel> ayatModels = new ArrayList<>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String outstr = "";
            @SuppressLint("Range") String arabic = cursor.getString(cursor.getColumnIndex("arabic_uthmanic"));
            @SuppressLint("Range") String trans = cursor.getString(cursor.getColumnIndex("trans"));
            @SuppressLint("Range") String bang = cursor.getString(cursor.getColumnIndex("bn_muhi"));
            @SuppressLint("Range") int surah = cursor.getInt(cursor.getColumnIndex("sura_id"));
            @SuppressLint("Range") int ayat_no = cursor.getInt(cursor.getColumnIndex("verse_id"));
            ayatModels.add(new AyatModel(arabic, trans, bang, surah, ayat_no));
            cursor.moveToNext();
        }

        return  ayatModels;
    }
    @SuppressLint("Range")
    public String getTafshir(int surah_id,int ayah_id){
        Cursor cursor = sqldb.rawQuery("SELECT * FROM tafsir_ahbayan WHERE surah_id ="+surah_id+" and ayah_id="+ayah_id, null);
        cursor.moveToFirst();
         String tafshir = cursor.getString(cursor.getColumnIndex("tafsir"));
         cursor.close();
         return tafshir;
    }
    @SuppressLint("Range")
    public String getSuraName(int surah_id){
        Cursor cursor = sqldb.rawQuery("SELECT * FROM surah_name WHERE surah_no ="+surah_id, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex("name_bangla"));
        cursor.close();
        return name;
    }
    @SuppressLint("Range")
    public String getTafshirText(int surah_id,int ayah_id) {
        Cursor cursor = sqldb.rawQuery("SELECT * FROM tafsir_ahbayan WHERE surah_id ="+surah_id+" and ayah_id="+ayah_id, null);
        cursor.moveToFirst();
        String tafshir = cursor.getString(cursor.getColumnIndex("text"));
        cursor.close();
        return tafshir;
    }
}

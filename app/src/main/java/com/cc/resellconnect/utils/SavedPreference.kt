package com.cc.resellconnect.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object SavedPreference {


    const val LIKESLIST = "likeslist"
    const val LINKLIST = "linklist"
    const val NOTIFLIST = "notiflist"
    const val MUTELIST = "mutelist"
    const val ISSEEN = "isseen"
    const val TOKEN = "device_token"

    private fun getSharedPreference(ctx: Context?): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    private fun editor(context: Context, key: String, value: String) {
        getSharedPreference(
            context
        )?.edit()?.putString(key, value)?.apply()
    }

    private fun editor(context: Context, key: String, value: Boolean) {
        getSharedPreference(
            context
        )?.edit()?.putBoolean(key, value)?.apply()
    }

    private fun editor(context: Context, key: String, value: Int) {
        getSharedPreference(
            context
        )?.edit()?.putInt(key, value)?.apply()
    }


    fun saveLikesList(context: Context, list:List<String> ) {

        val sharedPreferences =  getSharedPreference(context)
        val editor = sharedPreferences!!.edit()
        val gson = Gson()

        val json = gson.toJson(list)

        editor.putString(LIKESLIST, json)
        editor.apply()
    }

    fun getLikesList(context: Context): List<String> {

        val sharedPreferences = getSharedPreference(context)

        val gson = Gson()

        val json = sharedPreferences!!.getString(LIKESLIST, null)

        return if(json == null){
            ArrayList<String>()
        }else {
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
            gson.fromJson(json, type)
        }
    }


    fun saveVisiLinkList(context: Context, list:List<Long> ) {

        val sharedPreferences =  getSharedPreference(context)
        val editor = sharedPreferences!!.edit()
        val gson = Gson()

        val json = gson.toJson(list)

        editor.putString(LINKLIST, json)
        editor.apply()
    }

    fun getVisitLinkList(context: Context): List<Long> {

        val sharedPreferences = getSharedPreference(context)

        val gson = Gson()

        val json = sharedPreferences!!.getString(LINKLIST, null)

        return if(json == null){
            ArrayList<Long>()
        }else {
            val type: Type = object : TypeToken<ArrayList<Long?>?>() {}.getType()
            gson.fromJson(json, type)
        }
    }


    fun saveNotificationList(context: Context, list:List<String> ) {

        val sharedPreferences =  getSharedPreference(context)
        val editor = sharedPreferences!!.edit()
        val gson = Gson()

        val json = gson.toJson(list)

        editor.putString(NOTIFLIST, json)
        editor.apply()
    }

    fun getNotificationList(context: Context): List<String> {

        val sharedPreferences = getSharedPreference(context)

        val gson = Gson()

        val json = sharedPreferences!!.getString(NOTIFLIST, null)

        return if(json == null){
            ArrayList<String>()
        }else {
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
            gson.fromJson(json, type)
        }
    }


    fun saveMuteList(context: Context, list:List<String> ) {

        val sharedPreferences =  getSharedPreference(context)
        val editor = sharedPreferences!!.edit()
        val gson = Gson()

        val json = gson.toJson(list)

        editor.putString(MUTELIST, json)
        editor.apply()
    }

    fun getMuteList(context: Context): List<String> {

        val sharedPreferences = getSharedPreference(context)

        val gson = Gson()

        val json = sharedPreferences!!.getString(MUTELIST, null)

        return if(json == null){
            ArrayList<String>()
        }else {
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
            gson.fromJson(json, type)
        }
    }

    fun setToken(context: Context, token: String) {
        editor(
            context = context,
            key = TOKEN,
            value = token
        )
    }

    fun getToken(context: Context) = getSharedPreference(
        context
    )?.getString(TOKEN, "")


    fun setOnBoardSeen(context: Context, seen: Boolean) {
        editor(
            context = context,
            key = ISSEEN,
            value = seen
        )
    }

    fun getOnBoardSeen(context: Context) = getSharedPreference(
        context
    )!!.getBoolean(ISSEEN, false)



    fun clearPreferences(context: Context) {
        getSharedPreference(
            context
        )?.edit()?.clear()?.apply()
    }
}
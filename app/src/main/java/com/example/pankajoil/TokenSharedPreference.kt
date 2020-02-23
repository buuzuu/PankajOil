package com.example.pankajoil

import android.content.Context

class TokenSharedPreference(ctx: Context) {
    val PREFERENCE_NAME = "JWT_TOKEN"
    val PREFERENCE_TOKEN ="Authentication_Token"
    val PREFERENCE_NUMBER ="Mobile_Number"
    val preference = ctx.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)


    fun getAuthKey():String{

        return preference.getString(PREFERENCE_TOKEN,"null")!!
    }

    fun getMobileNumber() : String{
        return preference.getString(PREFERENCE_NUMBER,"null")!!
    }

    fun setAuthKey(key: String, number: String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_TOKEN,key)
        editor.putString(PREFERENCE_NUMBER, number)
        editor.apply()
    }

    fun isTokenPresent():Boolean{

        if (preference.contains(PREFERENCE_TOKEN)){
            return true
        }
        return false

    }

    fun deleteAuthKey(){
        preference.edit().remove(PREFERENCE_TOKEN).apply()
        preference.edit().remove(PREFERENCE_NUMBER).apply()
    }

}
package com.gurtam.wiatag_kit.example

import com.gurtam.wiatagkit.*


class MainActivity : BaseActivity() {

    override fun initMessageManager() {
        super.initMessageManager()
        /**If you are using push notifications for receiving messages,
         * we recommend passing the implementation of the DuplicateResolver interface to the registerDuplicateResolver function
         * to check incoming messages for repeatability. Each message contains a unique identifier
         * that can be used to filter identical messages.*/
        MessageManager.registerDuplicateResolver(object : DuplicateResolver() {
            override fun isDuplicate(id: String): Boolean {
                val preferences = Utils.getPreferences(this@MainActivity)
                if (preferences.getString(id, null) == null) {
                    preferences.edit().putString(id, id).apply()
                    return false
                }
                return true
            }
        })
    }

    override fun startTimer() {
        /** remove periodic check of message for testing push notifications*/
    }
}
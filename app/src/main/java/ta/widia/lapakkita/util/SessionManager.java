package ta.widia.lapakkita.util;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import ta.widia.lapakkita.LoginUser;

/**
 * Created by taufik on 29/05/18.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "GenKanPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID_PELANGGAN = "key_id_pelanggan";
    public static final String KEY_NM_PELANGGAN = "key_nm_pelanggan";
    public static final String KEY_MAIL_PELANGGAN = "key_mail_pelanggan";
    public static final String KEY_NOHP_PELANGGAN = "key_nohp_pelanggan";
    public static final String KEY_ALAMAT_PELANGGAN = "key_alamat_pelanggan";
    public static final String KEY_FOTO_PELANGGAN = "key_foto_pelanggan";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id_pelanggan, String nama_pelanggan, String email_pelanggan, String nohp_pelanggan, String alamat_pelanggan, String foto_pelanggan) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID_PELANGGAN, id_pelanggan);
        editor.putString(KEY_NM_PELANGGAN, nama_pelanggan);
        editor.putString(KEY_MAIL_PELANGGAN, email_pelanggan);
        editor.putString(KEY_NOHP_PELANGGAN, nohp_pelanggan);
        editor.putString(KEY_ALAMAT_PELANGGAN, alamat_pelanggan);
        editor.putString(KEY_FOTO_PELANGGAN, foto_pelanggan);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            //Anda belum login

            /*// user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginUser.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

        }
        else{
            //Anda sudah login
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID_PELANGGAN, pref.getString(KEY_ID_PELANGGAN, null));
        user.put(KEY_NM_PELANGGAN, pref.getString(KEY_NM_PELANGGAN, null));
        user.put(KEY_MAIL_PELANGGAN, pref.getString(KEY_MAIL_PELANGGAN, null));
        user.put(KEY_NOHP_PELANGGAN, pref.getString(KEY_NOHP_PELANGGAN, null));
        user.put(KEY_ALAMAT_PELANGGAN, pref.getString(KEY_ALAMAT_PELANGGAN, null));
        user.put(KEY_FOTO_PELANGGAN, pref.getString(KEY_FOTO_PELANGGAN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        /*// After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginUser.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}

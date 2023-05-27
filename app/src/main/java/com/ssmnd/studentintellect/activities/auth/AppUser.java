package com.ssmnd.studentintellect.activities.auth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ssmnd.studentintellect.activities.auth.data.User;
import com.ssmnd.studentintellect.activities.auth.data.UserType;
import com.ssmnd.studentintellect.models.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public class AppUser extends SQLiteOpenHelper {
    private static final FirebaseUser firebaseUser =
            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private static final DocumentReference userDocRef =
            FirebaseFirestore.getInstance().collection("Users")
                    .document(firebaseUser.getUid());
    private static SQLiteDatabase database = null;

    private static final String DATABASE_NAME = "UsersDatabase";
    private static final String USERS_TABLE = "UsersTable";
    private static final String MODULES_TABLE = "ModulesTable";

    private static final String USER_UID = "User_UID";
    private static final String USER_NAMES = "User_Names";
    private static final String USER_LAST_NAME = "User_LastName";
    private static final String USER_EMAIL = "User_Email";
    private static final String USER_PHONE = "User_Phone";
    private static final String USER_IMAGE_URL = "User_ImageUrl";
    private static final String USER_USER_TYPE = "User_UserType";
    private static final String USER_BALANCE = "User_Balance";
    private static final String USER_IS_ONLINE = "User_IsOnline";

    private static final String MODULE_ID = "Module_Id";
    private static final String MODULE_CODE = "Module_Code";
    private static final String MODULE_NAME = "Module_Name";
    private static final String MODULE_TIME_UPDATED = "Module_TimeUpdated";
    private static final String MODULE_ADDER_UID = "Module_AdderUID";
    private static final String MODULE_ADDER_NAME = "Module_AdderName";
    private static final String MODULE_ADDER_EMAIL = "Module_AdderEmail";
    private static final String MODULE_IS_VERIFIED = "Module_isVerified";
    private static final String MODULE_VERIFIER_UID = "Module_VerifierUID";
    private static final String MODULE_VERIFIER_NAME = "Module_VerifierName";
    private static final String MODULE_VERIFIER_EMAIL = "Module_VerifierEmail";

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        String usersTable = "CREATE TABLE IF NOT EXISTS "+USERS_TABLE+" ("
                +USER_UID+" TEXT PRIMARY KEY UNIQUE NOT NULL,"
                +USER_NAMES+" TEXT NOT NULL,"
                +USER_LAST_NAME+" TEXT,"
                +USER_EMAIL+" TEXT NOT NULL,"
                +USER_PHONE+" TEXT,"
                +USER_IMAGE_URL+" TEXT,"
                +USER_USER_TYPE+" INT NOT NULL default 0,"
                +USER_BALANCE+" DOUBLE NOT NULL default 0.0,"
                +USER_IS_ONLINE+" BOOLEAN NOT NULL)";
        String modulesTable = "CREATE TABLE IF NOT EXISTS "+MODULES_TABLE+" ("
                +MODULE_ID+" TEXT PRIMARY KEY UNIQUE NOT NULL,"
                +MODULE_CODE+" TEXT UNIQUE NOT NULL,"
                +MODULE_NAME+" TEXT NOT NULL,"
                +MODULE_TIME_UPDATED+" LONG NOT NULL,"
                +MODULE_ADDER_UID+" TEXT NOT NULL,"
                +MODULE_ADDER_NAME+" TEXT NOT NULL,"
                +MODULE_ADDER_EMAIL+" TEXT NOT NULL,"
                +MODULE_IS_VERIFIED+" BOOLEAN NOT NULL,"
                +MODULE_VERIFIER_UID+" TEXT,"
                +MODULE_VERIFIER_NAME+" TEXT,"
                +MODULE_VERIFIER_EMAIL+" TEXT)";

        db.execSQL(usersTable);
        db.execSQL(modulesTable);
    }
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+MODULES_TABLE);
        onCreate(db);
    }

    public AppUser(@NotNull Context context) throws Exception {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();

        User usr = getUser();
        Set<Module> modules = getModules();
        if (usr != null) {
            setUserValues(usr);
            setModulesSetValue(modules);
        } else {
            throw new Exception("Invalid User");
        }
    }
    public AppUser(@NotNull Context context, @NotNull User user) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();

        insertUser(user);
        insertModules(user.getModulesSet());

        setUserValues(user);
        setModulesSetValue(user.getModulesSet());
    }

    private void insertUser(@NonNull User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_UID, user.getUid());
        contentValues.put(USER_NAMES, user.getNames());
        contentValues.put(USER_LAST_NAME, user.getLastName());
        contentValues.put(USER_EMAIL, user.getEmail());
        contentValues.put(USER_PHONE, user.getPhone());
        contentValues.put(USER_IMAGE_URL, user.getImageUrl());
        contentValues.put(USER_USER_TYPE, user.getUserType().ordinal());
        contentValues.put(USER_BALANCE, user.getBalance());
        contentValues.put(USER_IS_ONLINE, user.isOnline());

        getReadableDatabase().insert(USERS_TABLE, null, contentValues);
    }
    private static void insertModule(@NonNull Module module) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MODULE_ID, module.getId());
        contentValues.put(MODULE_CODE, module.getCode());
        contentValues.put(MODULE_NAME, module.getName());
        contentValues.put(MODULE_TIME_UPDATED, module.getTimeUpdated().getSeconds());
        contentValues.put(MODULE_ADDER_UID, module.getAdderUid());
        contentValues.put(MODULE_ADDER_NAME, module.getAdderName());
        contentValues.put(MODULE_ADDER_EMAIL, module.getAdderEmail());
        contentValues.put(MODULE_IS_VERIFIED, module.isVerified);
        contentValues.put(MODULE_VERIFIER_UID, module.getVerifierUid());
        contentValues.put(MODULE_VERIFIER_NAME, module.getVerifierName());
        contentValues.put(MODULE_VERIFIER_EMAIL, module.getVerifierEmail());

        database.insert(MODULES_TABLE, null, contentValues);
    }
    private static void insertModules(@NonNull Set<Module> modulesSet) {
        for (Module module : modulesSet) {
            insertModule(module);
        }
    }


    /*public boolean updateUser(@NonNull User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAMES, user.getNames());
        contentValues.put(USER_LAST_NAME, user.getLastName());
        contentValues.put(USER_EMAIL, user.getEmail());
        contentValues.put(USER_PHONE, user.getPhone());
        contentValues.put(USER_IMAGE_URL, user.getImageUrl());
        contentValues.put(USER_USER_TYPE, user.getUserType().ordinal());
        contentValues.put(USER_BALANCE, user.getBalance());
        contentValues.put(USER_IS_ONLINE, user.isOnline());

        return getWritableDatabase().update(USERS_TABLE, contentValues, USER_UID+" = '"+user.getUid()+"'", null) != 0;
    }
    private void updateModule(@NonNull Module module) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MODULE_CODE, module.getCode());
        contentValues.put(MODULE_NAME, module.getName());
        contentValues.put(MODULE_TIME_UPDATED, module.getTimeUpdated().getSeconds());
        contentValues.put(MODULE_ADDER_UID, module.getAdderUid());
        contentValues.put(MODULE_ADDER_NAME, module.getAdderName());
        contentValues.put(MODULE_ADDER_EMAIL, module.getAdderEmail());
        contentValues.put(MODULE_IS_VERIFIED, module.isVerified);
        contentValues.put(MODULE_VERIFIER_UID, module.getVerifierUid());
        contentValues.put(MODULE_VERIFIER_NAME, module.getVerifierName());
        contentValues.put(MODULE_VERIFIER_EMAIL, module.getVerifierEmail());

        getReadableDatabase().update(MODULES_TABLE,  contentValues, MODULE_ID+" = '"+module.getId()+"'", null);
    }*/


    public static boolean deleteUser(@NotNull String uid) {
        return database.delete(USERS_TABLE, USER_UID+" = '"+uid+"'", null) != 0;
    }
    public static boolean deleteUsers() {
        return database.delete(USERS_TABLE, "1", null) != 0;
    }
    public static boolean deleteModule(@NotNull String id) {
        Set<Module> modules = modulesSet.getValue();
        if (modules != null) {
            modules.removeIf(module -> module.getId().equals(id));
            setModulesSet(modules);
        }
        return database.delete(MODULES_TABLE, MODULE_ID+" = '"+id+"'", null) != 0;
    }
    public static boolean deleteModules() {
        return database.delete(MODULES_TABLE, "1", null) != 0;
    }
    public static void deleteModules(@Nullable Set<String> modulesIds) {
        if (modulesIds != null) {
            for (String moduleId : modulesIds) {
                deleteModule(moduleId);
            }
        }
    }

    @Nullable
    private User getUser() {
        FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        String query = "SELECT * FROM "+USERS_TABLE+" WHERE "+USER_UID+" = '"+firebaseUser.getUid()+"'";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        User user = null;
        if (cursor.moveToFirst()){
            user = new User(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    UserType.values()[cursor.getInt(6)],
                    cursor.getDouble(7),
                    Boolean.parseBoolean(cursor.getString(8)),
                    new TreeSet<>()
            );
        }
        cursor.close();

        return user;
    }
    @NotNull
    private Set<Module> getModules() {
        String query = "SELECT * FROM "+MODULES_TABLE;
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        Set<Module> modules = new TreeSet<>();
        while (cursor.moveToNext()) {
            Module module = new Module();
            module.setId(cursor.getString(0));
            module.setCode(cursor.getString(1));
            module.setName(cursor.getString(2));
            module.setTimeUpdated(new Timestamp(cursor.getLong(3), 0));
            module.setAdderUid(cursor.getString(4));
            module.setAdderName(cursor.getString(5));
            module.setAdderEmail(cursor.getString(6));
            module.isVerified = Boolean.parseBoolean(cursor.getString(7));
            module.setVerifierUid(cursor.getString(8));
            module.setVerifierName(cursor.getString(9));
            module.setVerifierEmail(cursor.getString(10));

            modules.add(module);
        }
        cursor.close();

        return modules;
    }

    private static final MutableLiveData<String> uid = new MutableLiveData<>();
    private static final MutableLiveData<String> names = new MutableLiveData<>();
    private static final MutableLiveData<String> lastName = new MutableLiveData<>();
    private static final MutableLiveData<String> email = new MutableLiveData<>();
    private static final MutableLiveData<String> phone = new MutableLiveData<>();
    private static final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private static final MutableLiveData<UserType> userType = new MutableLiveData<>();
    private static final MutableLiveData<Double> balance = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> isOnline = new MutableLiveData<>();
    private static final MutableLiveData<Set<Module>> modulesSet = new MutableLiveData<>();


    public static LiveData<String> getUid() {
        return uid;
    }
    public static LiveData<String> getNames() {
        return names;
    }
    public static LiveData<String> getLastName() {
        return lastName;
    }
    public static LiveData<String> getEmail() {
        return email;
    }
    public static LiveData<String> getPhone() {
        return phone;
    }
    public static LiveData<String> getImageUrl() {
        return imageUrl;
    }
    public static LiveData<UserType> getUserType() {
        return userType;
    }
    public static LiveData<Double> getBalance() {
        return balance;
    }
    public static LiveData<Boolean> getIsOnline() {
        return isOnline;
    }
    public static LiveData<Set<Module>> getModulesSet() {
        return modulesSet;
    }


    protected void setUid(@NotNull String newUid) {
        AppUser.uid.setValue(newUid);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_UID, newUid);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("uid", newUid);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setNames(@NotNull String newNames) {
        AppUser.names.setValue(newNames);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAMES, newNames);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("names", newNames);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setLastName(@Nullable String newLastName) {
        AppUser.lastName.setValue(newLastName);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_LAST_NAME, newLastName);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("lastName", newLastName);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setEmail(@NotNull String newEmail) {
        AppUser.email.setValue(newEmail);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_EMAIL, newEmail);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("email", newEmail);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setPhone(@Nullable String newPhone) {
        AppUser.phone.setValue(newPhone);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_PHONE, newPhone);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("phone", newPhone);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setImageUrl(@Nullable String newImageUrl) {
        AppUser.imageUrl.setValue(newImageUrl);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_IMAGE_URL, newImageUrl);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("imageUrl", newImageUrl);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setUserType(@NotNull UserType newUserType) {
        AppUser.userType.setValue(newUserType);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_USER_TYPE, newUserType.ordinal());
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("userType", newUserType);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setBalance(double newBalance) {
        AppUser.balance.setValue(newBalance);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_BALANCE, newBalance);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("balance", newBalance);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setIsOnline(boolean newIsOnline) {
        AppUser.isOnline.setValue(newIsOnline);

        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_IS_ONLINE, newIsOnline);
        database.update(USERS_TABLE, contentValues, USER_UID+" = '"+firebaseUser.getUid()+"'", null);

        Map<String, Object> value = new HashMap<>();
        value.put("isOnline", newIsOnline);
        userDocRef.set(value, SetOptions.merge());
    }
    public static void setModulesSet(@NotNull Set<Module> newModulesSet) {
        AppUser.modulesSet.setValue(newModulesSet);

        insertModules(newModulesSet);

        Map<String, Object> value = new HashMap<>();
        value.put("modulesList", new ArrayList<>(newModulesSet));
        userDocRef.set(value, SetOptions.merge());
    }

    private void setUserValues(@NonNull User user) {
        uid.setValue(user.getUid());
        names.setValue(user.getNames());
        lastName.setValue(user.getLastName());
        email.setValue(user.getEmail());
        phone.setValue(user.getPhone());
        imageUrl.setValue(user.getImageUrl());
        userType.setValue(user.getUserType());
        balance.setValue(user.getBalance());
        isOnline.setValue(user.isOnline());
    }
    private void setModulesSetValue(@NotNull Set<Module> modulesSet) {
        AppUser.modulesSet.setValue(modulesSet);
    }


    public static void addModule(Module module) {

    }


    /*fun addModule(module : Module){
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .collection("MyModules")
                .document(module.id)
                .set(module)
    }
    fun deleteModule(module : Module){
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .collection("MyModules")
                .document(module.id)
                .delete()
    }
    fun deleteModule(moduleId : String){
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .collection("MyModules")
                .document(moduleId)
                .delete()
    }


    fun setUserInfo(userInfo : UserInfo) {
        if (userInfo.uid != currentUser.uid) return
                val contentValues = ContentValues().apply {
            put(NAME, userInfo.name)
            put(EMAIL, userInfo.email)
        }
        writableDatabase.update(TABLE_NAME, contentValues, "$UID = '${currentUser.uid}'", null) > 0
        _userInfo.value = userInfo
    }

    fun setName(userName : String) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(NAME, userName) },
        "$UID = '${currentUser.uid}'", null) > 0
        currentUser.updateProfile(userProfileChangeRequest {
            displayName = userName.trim()
        }).addOnSuccessListener {
            _name.value = userName
        }
    }
    fun setEmail(userEmail : String) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(EMAIL, userEmail) },
        "$UID = '${currentUser.uid}'", null) > 0

        _email.value = userEmail
    }
    fun setUserType(userType : Int) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(USER_TYPE, userType) },
        "$UID = '${currentUser.uid}'", null) > 0
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .update("userType", userType)
        _userType.value = UserType.values()[userType]
    }
    fun setPhone(userPhone : String) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(PHONE, userPhone) },
        "$UID = '${currentUser.uid}'", null) > 0
        _phone.value = userPhone
    }
    fun setBalance(userBalance : Double) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(BALANCE, userBalance) },
        "$UID = '${currentUser.uid}'", null) > 0
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .update("balance", userBalance)
        _balance.value = userBalance
    }
    private fun incrementBalance(amount : Double) {
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .update("balance", FieldValue.increment(amount))
    }
    private fun decrementBalance(amount : Double) {
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .update("balance", FieldValue.increment(-amount))
    }

    fun setImageUrl(userImageUri : Uri?) {
        writableDatabase.update(TABLE_NAME, ContentValues().apply { put(IMAGE_URL, userImageUri.toString()) },
            "$UID = '$uid'", null) > 0
        currentUser.updateProfile(userProfileChangeRequest {
            photoUri = userImageUri
        }).addOnSuccessListener {
            _imageUrl.value = userImageUri.toString()
        }
    }
    fun setImageUrl(userImageUrl : String?) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(IMAGE_URL, userImageUrl) },
        "$UID = '${currentUser.uid}'", null) > 0
        currentUser.updateProfile(userProfileChangeRequest {
            photoUri = if (userImageUrl == null) null else Uri.parse(userImageUrl)
        }).addOnSuccessListener {
            _imageUrl.value = userImageUrl
        }
    }
    fun setIsOnline(userIsOnline : Boolean) {
        writableDatabase.update(
                TABLE_NAME, ContentValues().apply { put(IS_ONLINE, userIsOnline) },
        "$UID = '${currentUser.uid}'", null) > 0
        Firebase.firestore.collection("Users")
                .document(currentUser.uid)
                .update("isOnline", userIsOnline)
        _isOnline.value = userIsOnline
    }*/
    /*
    * fun removeModule(moduleId: String, removeDB : Boolean = false): Boolean {
        if (removeDB){
            val myModules = getModulesSet().toMutableList()
            if (myModules.removeAll(myModules.filter { it.id.trim() == moduleId.trim() }))
                Firebase.firestore.collection("Users")
                    .document(Firebase.auth.currentUser!!.uid)
                    .update("myModulesList", myModules)
        }


        val isSuccess = writableDatabase.delete(TABLE_NAME, "$ID = '$moduleId'", null) != 0
        if (isSuccess) _myModulesSet.value = getModulesSet()
        return isSuccess
    }
    fun removeModules(moduleIds: List<String>?, removeDB : Boolean = false) {
        moduleIds?.forEach { moduleId ->
            if (removeDB){
                val myModules = getModulesSet().toMutableList()
                if (myModules.removeAll(myModules.filter { it.id.trim() == moduleId.trim() }))
                    Firebase.firestore.collection("Users")
                        .document(Firebase.auth.currentUser!!.uid)
                        .update("myModulesList", myModules)
            }
            writableDatabase.delete(TABLE_NAME, "$ID = '$moduleId'", null) != 0
        }
        _myModulesSet.value = getModulesSet()
    }
    fun removeAllMyModules(): Boolean {
        val isSuccess = writableDatabase.delete(TABLE_NAME, "1", null) != 0
        _myModulesSet.value = null
        return isSuccess
    }*/

}

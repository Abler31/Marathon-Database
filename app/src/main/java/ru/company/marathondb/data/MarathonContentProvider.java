package ru.company.marathondb.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MarathonContentProvider extends ContentProvider {
    MarathonDatabaseHelper marathonDatabaseHelper;

    private static final int MEMBERS = 1;
    private static final int MEMBER_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ClubMarathonContract.AUTHORITY, ClubMarathonContract.PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(ClubMarathonContract.AUTHORITY, ClubMarathonContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }
    @Override
    public boolean onCreate() {
        marathonDatabaseHelper = new MarathonDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase db = marathonDatabaseHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch (match){
            case MEMBERS:
                cursor = db.query(ClubMarathonContract.MemberEntry.TABLE_NAME,
                        strings, s, strings1, null, null, s1);
                break;
            case MEMBER_ID:
                s = ClubMarathonContract.MemberEntry._ID + "=?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ClubMarathonContract.MemberEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;

            default:
                throw new IllegalArgumentException("Can't query incorrect URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match){
            case MEMBERS:
                return ClubMarathonContract.MemberEntry.CONTENT_MULTIPLE_ITEMS;
            case MEMBER_ID:
                return ClubMarathonContract.MemberEntry.CONTENT_SINGLE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        String firstName = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_FIRST_NAME);
        if (firstName == null){
            throw new IllegalArgumentException("You have to input first name");
        }

        String lastName = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_LAST_NAME);
        if (lastName == null){
            throw new IllegalArgumentException("You have to input last name");
        }

       Integer gender = contentValues.getAsInteger(ClubMarathonContract.MemberEntry.COLUMN_GENDER);
        if (gender == null){
            throw new IllegalArgumentException("You have to input correct gender");
        }

        String sport = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_SPORT);
        if (sport == null){
            throw new IllegalArgumentException("You have to input correct sport");
        }

        SQLiteDatabase db = marathonDatabaseHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                long id = db.insert(ClubMarathonContract.MemberEntry.TABLE_NAME, null, contentValues);
                if (id == -1){
                    Log.e("insertMethod", "Insertion of data in the table failed for " + uri);
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);

            default:
                throw new IllegalArgumentException("Can't query incorrect URI");

        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = marathonDatabaseHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);

        int rowsDeleted;

        switch (match){
            case MEMBERS:
                rowsDeleted =  db.delete(ClubMarathonContract.MemberEntry.TABLE_NAME, s, strings);
                break;
                case MEMBER_ID:
                s = ClubMarathonContract.MemberEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  db.delete(ClubMarathonContract.MemberEntry.TABLE_NAME, s, strings);
                break;
                default:
                throw new IllegalArgumentException("Can't delete this URI" + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        if (contentValues.containsKey(ClubMarathonContract.MemberEntry.COLUMN_FIRST_NAME)) {
            String firstName = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_FIRST_NAME);
            if (firstName == null) {
                throw new IllegalArgumentException("You have to input first name");
            }
        }

        if (contentValues.containsKey(ClubMarathonContract.MemberEntry.COLUMN_LAST_NAME)) {
            String lastName = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_LAST_NAME);
            if (lastName == null) {
                throw new IllegalArgumentException("You have to input last name");
            }
        }

        if (contentValues.containsKey(ClubMarathonContract.MemberEntry.COLUMN_GENDER)) {
            Integer gender = contentValues.getAsInteger(ClubMarathonContract.MemberEntry.COLUMN_GENDER);
            if (gender == null || !(gender == ClubMarathonContract.MemberEntry.GENDER_UNKNOWN ||
                    gender == ClubMarathonContract.MemberEntry.GENDER_FEMALE || gender == ClubMarathonContract.MemberEntry.GENDER_MALE)) {
                throw new IllegalArgumentException("You have to input correct gender");
            }
        }

        if (contentValues.containsKey(ClubMarathonContract.MemberEntry.COLUMN_SPORT)) {
            String sport = contentValues.getAsString(ClubMarathonContract.MemberEntry.COLUMN_SPORT);
            if (sport == null) {
                throw new IllegalArgumentException("You have to input correct sport");
            }
        }

        SQLiteDatabase db = marathonDatabaseHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case MEMBERS:
                rowsUpdated = db.update(ClubMarathonContract.MemberEntry.TABLE_NAME, contentValues, s, strings);

                break;
            case MEMBER_ID:
                s = ClubMarathonContract.MemberEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(ClubMarathonContract.MemberEntry.TABLE_NAME, contentValues, s, strings);

                break;
            default:
                throw new IllegalArgumentException("Can't update this URI" + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

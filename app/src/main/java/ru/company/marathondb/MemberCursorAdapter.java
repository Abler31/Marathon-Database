package ru.company.marathondb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.company.marathondb.data.ClubMarathonContract;

public class MemberCursorAdapter extends CursorAdapter {
    public MemberCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.member_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView firstNameTextView = view.findViewById(R.id.tv_firstName);
        TextView lastNameTextView = view.findViewById(R.id.tv_lastName);
        TextView sportTextView = view.findViewById(R.id.tv_sport);

        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(ClubMarathonContract.MemberEntry.COLUMN_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(ClubMarathonContract.MemberEntry.COLUMN_LAST_NAME));
        String sport = cursor.getString(cursor.getColumnIndexOrThrow(ClubMarathonContract.MemberEntry.COLUMN_SPORT));

        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);
        sportTextView.setText(sport);
    }
}

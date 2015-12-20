package com.teabreaktechnology.dumcharades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teabreaktechnology.dumcharades.cache.GameCache;
import com.teabreaktechnology.dumcharades.util.AlertUtil;
import com.teabreaktechnology.dumcharades.util.CommonConstants;

import java.util.ArrayList;
import java.util.List;


public class CreateTeamsActivity extends Activity {


    private static final String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
    public int playerCount = 0;
    private LinearLayout team1LinearLayout;
    private LinearLayout team2LinearLayout;
    private List<String> contactNames = new ArrayList<String>();

    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teams);

        contactNames = getAllContactNames();

        Bundle extras = getIntent().getExtras();

        final String team1Name = extras.getString("team1Name");
        final String team2Name = extras.getString("team2Name");
        final String language = extras.getString("language");
        final String timeIntervalForEachPlay = extras.getString("timeIntervalForEachPlay");
        final Integer difficultyLevel = extras.getInt("difficultyLevel");

        TextView team1TextView = (TextView) findViewById(R.id.team1NameTextView);
        TextView team2TextView = (TextView) findViewById(R.id.team2NameTextView);

        Button startGameButton = (Button) findViewById(R.id.startGameButton);

        team1TextView.setText(team1Name);
        team2TextView.setText(team2Name);

        team1LinearLayout = (LinearLayout) findViewById(R.id.team1LinearLayout);
        team2LinearLayout = (LinearLayout) findViewById(R.id.team2LinearLayout);

        inflateEditRow("player " + nextPlayerId(), team1LinearLayout);
        inflateEditRow("player " + nextPlayerId(), team1LinearLayout);
        inflateEditRow("player " + nextPlayerId(), team2LinearLayout);
        inflateEditRow("player " + nextPlayerId(), team2LinearLayout);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button3);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent startGameIntent = new Intent(CreateTeamsActivity.this, GamePlayActivity.class);

                GameCache gameCache = GameCache.getInstance(true);

                StringBuilder errorString = new StringBuilder();

                int team1Id = gameCache.addTeam(team1Name);
                int team2Id = gameCache.addTeam(team2Name);
                int gameId = 1;

                int playersInTeam1 = 0;
                for (int i = 0; i < team1LinearLayout.getChildCount() - 1; i++) {
                    View childAt = team1LinearLayout.getChildAt(i);
                    EditText editText = (EditText) childAt.findViewById(R.id.autoCompleteTextView1);
                    String playerName = editText.getText().toString();
                    System.out.println("player name " + playerName + " " + playerName.length());
                    int playerId = gameCache.addPlayer(playerName);

                    if (playerId == CommonConstants.DUPLICATE_PLAYER) {
                        errorString.append("Duplicate player in Team 1, " + playerName + "\n");
                    }
                    if (playerId == CommonConstants.EMPTY_PLAYERNAME) {
                        //errorString.append("Empty player name in Team 1 \n");
                    } else {
                        gameCache.addPlayer(gameId, team1Id, playerId);
                        playersInTeam1++;
                    }
                }
                if (playersInTeam1 == 0) {
                    AlertUtil.showAlertPopup("Team 1 should contain at least one player", CreateTeamsActivity.this);
                    return;
                }


                int playersInTeam2 = 0;
                for (int i = 0; i < team2LinearLayout.getChildCount() - 1; i++) {
                    View childAt = team2LinearLayout.getChildAt(i);
                    EditText editText = (EditText) childAt.findViewById(R.id.autoCompleteTextView2);
                    String playerName = editText.getText().toString();
                    System.out.println("player name " + playerName + " " + playerName.length());
                    int playerId = gameCache.addPlayer(playerName);
                    //gameCache.addPlayer(gameId, team2Id, playerId);

                    if (playerId == CommonConstants.DUPLICATE_PLAYER) {
                        errorString.append("Duplicate player in Team 2, " + playerName + "\n");
                    }
                    if (playerId == CommonConstants.EMPTY_PLAYERNAME) {
                        //errorString.append("Empty player name in Team 2 \n");
                    } else {
                        gameCache.addPlayer(gameId, team2Id, playerId);
                        playersInTeam2++;
                    }
                }

                if (playersInTeam2 == 0) {
                    AlertUtil.showAlertPopup("Team 2 should contain at least one player", CreateTeamsActivity.this);
                    return;
                }

                startGameIntent.putExtra("gameId", gameId + "");
                startGameIntent.putExtra("timeIntervalForEachPlay", timeIntervalForEachPlay);
                startGameIntent.putExtra("language", language);
                startGameIntent.putExtra("difficultyLevel", difficultyLevel);

                startGameIntent.putExtra("team1Name", team1Name);
                startGameIntent.putExtra("team2Name", team2Name);

                if (errorString.length() > 0) {
                    String message = errorString.toString();
                    AlertUtil.showAlertPopup(message, CreateTeamsActivity.this);
                } else {
                    startActivity(startGameIntent);
                }

            }
        });
    }

    private List<String> getAllContactNames() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<String> contactNames = new ArrayList<String>();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNames.add(name);

            //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        }
        phones.close();
        return contactNames;
    }

    private int nextPlayerId() {
        return (++playerCount);
    }

    public void onAddForTeam1Clicked(View v) {
        inflateEditRow("player " + nextPlayerId(), team1LinearLayout);
    }

    public void onAddForTeam2Clicked(View v) {
        inflateEditRow("player " + nextPlayerId(), team2LinearLayout);

    }

    public void onDeleteForTeam1Clicked(View v) {
        team1LinearLayout.removeView((View) v.getParent());
    }

    public void onDeleteForTeam2Clicked(View v) {
        team2LinearLayout.removeView((View) v.getParent());
    }

    private void inflateEditRow(String name, LinearLayout linearLayout) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;


        if (linearLayout == team1LinearLayout) {
            rowView = inflater.inflate(R.layout.team1row, null);


        } else {

            rowView = inflater.inflate(R.layout.team2row, null);
        }


        linearLayout.addView(rowView, linearLayout.getChildCount() - 1);
        if (linearLayout == team1LinearLayout) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, contactNames);
            AutoCompleteTextView textView2 = (AutoCompleteTextView)
                    rowView.findViewById(R.id.autoCompleteTextView1);
            textView2.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, contactNames);
            AutoCompleteTextView textView2 = (AutoCompleteTextView)
                    rowView.findViewById(R.id.autoCompleteTextView2);
            textView2.setAdapter(adapter);

        }
    }


}
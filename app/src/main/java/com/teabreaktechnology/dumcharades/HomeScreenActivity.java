package com.teabreaktechnology.dumcharades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.teabreaktechnology.dumcharades.service.GameService;
import com.teabreaktechnology.dumcharades.service.GameServiceImpl;
import com.teabreaktechnology.dumcharades.util.AlertUtil;
import com.teabreaktechnology.dumcharades.util.Constants;
import com.teabreaktechnology.dumcharades.util.StringUtil;

import java.util.List;


public class HomeScreenActivity extends Activity {

    private static final String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_screen);//change activity to _1||_2||_3

        GameService gameService = GameServiceImpl.getInstance(true);
        gameService.createNewGame();
        gameService.createNewGame();
        Button createGameButton = (Button) findViewById(R.id.createGameButton);

        Button expressStartButton = (Button) findViewById(R.id.expressStartButton);

        final Spinner timeIntervalSpinner = (Spinner) findViewById(R.id.timeIntervalForEachPlay);
        ArrayAdapter<String> timeIntervalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.timeIntervals);
        timeIntervalSpinner.setAdapter(timeIntervalAdapter);
        timeIntervalSpinner.setSelection(2);

        final Spinner languageSpinner = (Spinner) findViewById(R.id.language);
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.languages);
        languageSpinner.setAdapter(languageAdapter);


        final Spinner difficultyLevelSpinner = (Spinner) findViewById(R.id.difficultyLevel);
        ArrayAdapter<String> difficultyLevelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.difficultyLevelForDropDown);
        difficultyLevelSpinner.setAdapter(difficultyLevelAdapter);

        final Spinner existingGamesSpinner = getExistingGameSpinner(gameService);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button3);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createTeamsIntent = new Intent(HomeScreenActivity.this, CreateTeamsActivity.class);
                createTeamsIntent(createTeamsIntent, mp, timeIntervalSpinner, languageSpinner, difficultyLevelSpinner, existingGamesSpinner);

                startActivity(createTeamsIntent);
            }
        });

        expressStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamePlayIntent = new Intent(HomeScreenActivity.this, GamePlayActivity.class);
                createTeamsIntent(gamePlayIntent, mp, timeIntervalSpinner, languageSpinner, difficultyLevelSpinner, existingGamesSpinner);

                GameService gameService = GameServiceImpl.getInstance(false);
                int gameId = gameService.getCurrentGameId();

                for (int teamId : gameService.getTeams(gameId)) {
                    String teamName = gameService.getTeamName(teamId);
                    int playerId = gameService.addPlayer(teamName + " player ");
                    gameService.addPlayer(gameId, teamId, playerId);
                }
                gamePlayIntent.putExtra("gameId", gameId + "");
                startActivity(gamePlayIntent);
            }
        });
    }

    private Spinner getExistingGameSpinner(GameService gameService) {
        List<String> existingGames = gameService.getExistingGames();
        System.out.println(existingGames);

        final Spinner existingGamesSpinner = (Spinner) findViewById(R.id.existingGamesSpinner);
        ArrayAdapter<String> existingGamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, existingGames);
        existingGamesSpinner.setAdapter(existingGamesAdapter);
        return existingGamesSpinner;
    }

    private Intent createTeamsIntent(Intent createTeamsIntent, MediaPlayer mp, Spinner timeIntervalSpinner, Spinner languageSpinner, Spinner difficultyLevelSpinner, Spinner existingGamesSpinner) {
        mp.start();

        final EditText team1EditText = (EditText) findViewById(R.id.team1Name);
        final EditText team2EditText = (EditText) findViewById(R.id.team2Name);
        team1EditText.setText("Team 1");
        team2EditText.setText("Team 2");

        String team2Name = team2EditText.getText().toString();
        String team1Name = team1EditText.getText().toString();
        if (!StringUtil.isValidStringWithNonZeroLength(team1Name)) {
            AlertUtil.showAlertPopup("Enter valid name for first team", HomeScreenActivity.this);
            return null;
        }
        if (!StringUtil.isValidStringWithNonZeroLength(team2Name)) {
            AlertUtil.showAlertPopup("Enter valid name for second team", HomeScreenActivity.this);
            return null;
        }
        if (team1Name.equalsIgnoreCase(team2Name)) {
            AlertUtil.showAlertPopup("Both team names cannot be the same", HomeScreenActivity.this);
            return null;
        }

        int selectedId = timeIntervalSpinner.getSelectedItemPosition();
        String timeIntervalForEachPlay = Constants.timeIntervalInSeconds[selectedId];
        String language = (String) languageSpinner.getSelectedItem();

        int selectedDifficultyLevelId = difficultyLevelSpinner.getSelectedItemPosition();
        int selectedDifficultyLevel = Constants.difficultyLevel[selectedDifficultyLevelId];

        Object existingGameObj = existingGamesSpinner.getSelectedItem();

        GameService gameService = GameServiceImpl.getInstance(false);
        if(existingGameObj==null) {
            gameService.createNewGame();
        }else{

            int selectedGame = gameService.getGameId(existingGameObj.toString());
            gameService.setCurrentGame(selectedGame);
        }
        createTeamsIntent.putExtra("team1Name", team1Name);
        createTeamsIntent.putExtra("team2Name", team2Name);
        gameService.addTeam(team1Name);
        gameService.addTeam(team2Name);
        createTeamsIntent.putExtra("timeIntervalForEachPlay", timeIntervalForEachPlay);
        createTeamsIntent.putExtra("language", language);
        createTeamsIntent.putExtra("difficultyLevel", selectedDifficultyLevel);
        return createTeamsIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        System.out.println("Entered onResume");
        GameService instance = GameServiceImpl.getInstance(false);
        System.out.println(instance.toString());
        getExistingGameSpinner(instance);
    }
}

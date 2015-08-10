package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;

/**
 * Created by Serloman on 09/08/2015.
 */
public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Intent mIntent;
    private int mAppWidgetId;
    private List<MatchData> matches;

    public StackRemoteViewsFactory(Context context, Intent intent){
        this.mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        matches = new ArrayList<>();
        onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {
        Date fragmentDate = new Date(System.currentTimeMillis());// + 86400000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, new String[] {dateFormat.format(fragmentDate)}, null);
        cursor.moveToFirst();

        if(cursor.getCount()>0){
            do{
                matches.add(new MatchData(cursor));
            }while(cursor.moveToNext());
        }
    }

    @Override
    public void onDestroy() {
        matches.clear();
    }

    @Override
    public int getCount() {
        return matches.size();
    }


    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_single_match);

        MatchData match = matches.get(i);

        rv.setTextViewText(R.id.home_name, match.homeName);
        rv.setTextViewText(R.id.home_goals, getScore(match.homeGoals));
        rv.setTextViewText(R.id.away_name, match.awayName);
        rv.setTextViewText(R.id.away_goals, getScore(match.awayGoals));
        rv.setTextViewText(R.id.score_match, "-");

        rv.setTextColor(R.id.home_name, Color.BLACK);
        rv.setTextColor(R.id.away_name, Color.BLACK);
        rv.setTextColor(R.id.home_goals, Color.BLACK);
        rv.setTextColor(R.id.away_goals, Color.BLACK);
        rv.setTextColor(R.id.score_match, Color.BLACK);

//        rv.setContentDescription(R.id.home_name, Utilies.getScores(match.homeName, match.homeGoals, match.awayName, match.awayGoals));

        return rv;
    }

    private String getScore(int score){
        if(score<0)
            return "";
        else
            return String.valueOf(score);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return (long) matches.get(i).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class MatchData{
        double id;
        String homeName;
        int homeGoals;
        String awayName;
        int awayGoals;
        String date;
        int matchDay;
        int league;

        public MatchData(Cursor cursor){
            id = cursor.getInt(scoresAdapter.COL_ID);
            homeName = cursor.getString(scoresAdapter.COL_HOME);
            homeGoals = cursor.getInt(scoresAdapter.COL_HOME_GOALS);
            awayName = cursor.getString(scoresAdapter.COL_AWAY);
            awayGoals = cursor.getInt(scoresAdapter.COL_AWAY_GOALS);
            date = cursor.getString(scoresAdapter.COL_DATE);
            matchDay = cursor.getInt(scoresAdapter.COL_MATCHDAY);
            league = cursor.getInt(scoresAdapter.COL_LEAGUE);
        }

        @Override
        public String toString() {
            return Utilies.getScores(homeName, homeGoals, awayName, awayGoals);
        }
    }
}

package com.vividgames.android.gotquiz;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vividgames.android.gotquiz.databinding.FragmentLevelMenuBinding;
import com.vividgames.android.gotquiz.databinding.ListItemLevelBinding;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class LevelMenuFragment extends Fragment
{
    private static final int REQUEST_CODE_VICTORY=0;
    private static final String STARTED_LEVEL_ID ="started_level_id";

    private static final int LEVEL_COMPLETED_SOUND_URI=R.raw.level_completed;
    private static final int LEVEL_FAILED_SOUND_URI=R.raw.level_failed;
    private static final int GAME_COMPLETED_SOUND_URI=R.raw.game_completed;

    LevelManager mLevelManager;
    UUID mStartedLevelId;
    LevelAdapter mLevelAdapter;
    private FragmentLevelMenuBinding mLevelMenuBinding;
    private boolean mSoundPlayed;

    public static LevelMenuFragment newInstance()
    {
        return new LevelMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLevelManager=LevelManager.get(getActivity());
        if (savedInstanceState!=null)
        {
            mStartedLevelId=(UUID)savedInstanceState.getSerializable(STARTED_LEVEL_ID);
        }
        mSoundPlayed=QueryPreferences.isSoundPlayed(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mLevelMenuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_level_menu, container, false);
        mLevelMenuBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mLevelAdapter=new LevelAdapter(mLevelManager.getLevels());
        mLevelMenuBinding.recyclerView.setAdapter(mLevelAdapter);
        return mLevelMenuBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_menu, menu);
        MenuItem mToggleSound=menu.findItem(R.id.toggle_sound);
        mToggleSound.setTitle(mSoundPlayed ? R.string.sound_off : R.string.sound_on);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.toggle_sound:
                QueryPreferences.setPlaySound(getActivity(), !mSoundPlayed);
                mSoundPlayed=QueryPreferences.isSoundPlayed(getActivity());
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LevelHolder extends RecyclerView.ViewHolder
    {
        //variable for reference to holders view must be declared within it's class!
        private ListItemLevelBinding mItemLevelBinding;
        private Resources mResources=getResources();

        private LevelHolder(ListItemLevelBinding binding)
        {
            super(binding.getRoot());
            mItemLevelBinding =binding;
            mItemLevelBinding.setViewModel(new LevelViewModel(mLevelManager, LevelMenuFragment.this));
        }

        public void bind(Level level)
        {
            mItemLevelBinding.getViewModel().setLevel(level);
            if (level.getLevelStatus()==LevelManager.LevelsSchema.StatusCodes.LEVEL_LOCKED)
            {
                mItemLevelBinding.getRoot().setBackground(mResources.getDrawable(R.drawable.level_icon_unplayable));
            }
            mItemLevelBinding.executePendingBindings();
        }
    }

    private class LevelAdapter extends RecyclerView.Adapter<LevelHolder>
    {
        List<Level> mLevels;

        public LevelAdapter(List<Level> levels)
        {
            mLevels = levels;
        }

        @Override
        public LevelHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            ListItemLevelBinding binding=DataBindingUtil.inflate(inflater, R.layout.list_item_level, parent, false);
            return new LevelHolder(binding);
        }

        @Override
        public void onBindViewHolder(LevelHolder holder, int position)
        {
            Level level=mLevels.get(position);
            holder.bind(level);
        }

        @Override
        public int getItemCount()
        {
            return mLevels.size();
        }
    }

    public void startLevel(Level level)
    {
        mLevelManager.setQuestions(level);
        mStartedLevelId=level.getId();
        Intent intent=GameplayActivity.newIntent(getActivity(), mStartedLevelId);
        startActivityForResult(intent, REQUEST_CODE_VICTORY);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STARTED_LEVEL_ID, mStartedLevelId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode!=RESULT_OK)
        {
            return;
        }

        if (requestCode==REQUEST_CODE_VICTORY)
        {
            if (data==null)
            {
                return;
            }

            boolean victorious=GameplayActivity.wasVictoryAchieved(data);

            String message;
            Resources res=getResources();
            if (victorious)
            {
                int completedLvlIndex=mLevelManager.getLevels().indexOf(mLevelManager.getLevel(mStartedLevelId));
                boolean gameCompleted=mLevelManager.onLevelCompleted(mStartedLevelId);

                if (gameCompleted)
                {
                    message=res.getString(R.string.game_completed_toast);
                    ((MainMenuActivity)Objects.requireNonNull(getActivity())).playSound(GAME_COMPLETED_SOUND_URI);
                }
                else
                {
                    message=String.format(res.getString(R.string.level_unlocked_toast), completedLvlIndex+2);
                    mLevelManager=LevelManager.get(getActivity());
                    mLevelAdapter=new LevelAdapter(mLevelManager.getLevels());
                    mLevelMenuBinding.recyclerView.setAdapter(mLevelAdapter);
                    ((MainMenuActivity)Objects.requireNonNull(getActivity())).playSound(LEVEL_COMPLETED_SOUND_URI);
                    //mLevelAdapter.notifyItemChanged(completedLvlIndex+1);
                }
               // mLevelAdapter.notifyItemChanged(completedLvlIndex);
            }
            else
            {
                message=res.getString(R.string.level_failed_toast);
                ((MainMenuActivity)Objects.requireNonNull(getActivity())).playSound(LEVEL_FAILED_SOUND_URI);
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}

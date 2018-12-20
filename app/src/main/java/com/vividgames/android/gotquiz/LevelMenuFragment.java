package com.vividgames.android.gotquiz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vividgames.android.gotquiz.databinding.FragmentLevelMenuBinding;
import com.vividgames.android.gotquiz.databinding.ListItemLevelBinding;

import java.util.List;
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
    private ListItemLevelBinding mItemLevelBinding;
    private FragmentLevelMenuBinding mLevelMenuBinding;

    public static LevelMenuFragment newInstance()
    {
        return new LevelMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLevelManager=LevelManager.get(getActivity());
        if (savedInstanceState!=null)
        {
            mStartedLevelId=(UUID)savedInstanceState.getSerializable(STARTED_LEVEL_ID);
        }
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

    private class LevelHolder extends RecyclerView.ViewHolder
    {


        private LevelHolder(ListItemLevelBinding binding)
        {
            super(binding.getRoot());
            mItemLevelBinding =binding;
            mItemLevelBinding.setViewModel(new LevelViewModel(mLevelManager, LevelMenuFragment.this));
        }

        public void bind(Level level)
        {
            mItemLevelBinding.getViewModel().setLevel(level);
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

            int messageResId;
            if (victorious)
            {
                int completedLvlIndex=mLevelManager.getLevels().indexOf(mLevelManager.getLevel(mStartedLevelId));
                messageResId=R.string.level_unlocked_toast;
                boolean gameCompleted=mLevelManager.onLevelCompleted(mStartedLevelId);

                if (gameCompleted)
                {
                    messageResId=R.string.game_completed_toast;
                    ((MainMenuActivity)getActivity()).playSound(GAME_COMPLETED_SOUND_URI);
                }
                else
                {
                    mLevelManager=LevelManager.get(getActivity());
                    mLevelAdapter=new LevelAdapter(mLevelManager.getLevels());
                    mLevelMenuBinding.recyclerView.setAdapter(mLevelAdapter);
                    ((MainMenuActivity)getActivity()).playSound(LEVEL_COMPLETED_SOUND_URI);
                    //mLevelAdapter.notifyItemChanged(completedLvlIndex+1);
                }
               // mLevelAdapter.notifyItemChanged(completedLvlIndex);
            }
            else
            {
                messageResId=R.string.level_failed_toast;
                ((MainMenuActivity)getActivity()).playSound(LEVEL_FAILED_SOUND_URI);
            }
            Toast.makeText(getActivity(), messageResId, Toast.LENGTH_LONG).show();
        }
    }
}

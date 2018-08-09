package com.ruslaniusupov.achievity.adapters;

import com.ruslaniusupov.achievity.data.model.Goal;

interface GoalItemContract {

    interface View {

        void showAuthor(String author);

        void showPubDate(String date);

        void showText(String text);

        void showLikesCount(String count);

        void showNotesCount(String count);

        void showSubscribersCount(String count);

        void setLikeBtnState(boolean isSelected);

        void setSubscribeBtnState(boolean isSelected);

    }

    interface Presenter {

        void updateUi(Goal goal);

        void like(String goalId);

        void cancelLike(String goalId);

        void subscribe(String goalId);

        void unsubscribe(String goalId);

    }

}

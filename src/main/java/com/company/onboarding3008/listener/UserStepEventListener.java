package com.company.onboarding3008.listener;

import com.company.onboarding3008.entity.OnBoardingStatus;
import com.company.onboarding3008.entity.User;
import com.company.onboarding3008.entity.UserStep;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserStepEventListener {
    @Autowired
    private DataManager dataManager;

    @EventListener
    public void onUserStepChangedBeforeCommit(final EntityChangedEvent<UserStep> event) {
        User user;
        // esli event created or updated => get id => load to user
        if (event.getType() != EntityChangedEvent.Type.DELETED){
            Id<UserStep> userStepId = event.getEntityId();
            UserStep userStep = dataManager.load(userStepId).one();
            user = userStep.getUser();
        } else {
            // esli deleted => cannot be loaded anymore => event.Get changes
            // show deleted object
            Id<User> userId = event.getChanges().getOldReferenceId("user");
            if (userId == null){
                throw new IllegalStateException("Cannot get User from deleted UserStep");
            }
            user = dataManager.load(userId).one();
        }

        long completedCount = user.getSteps().stream()
                .filter(us -> us.getCompletedDate() != null)
                .count();

        if (completedCount == 0){
            user.setOnboardingStatus(OnBoardingStatus.NOT_STARTED);
        } else if (completedCount == user.getSteps().size()) {
            user.setOnboardingStatus(OnBoardingStatus.COMPLETED);
        } else {
            user.setOnboardingStatus(OnBoardingStatus.IN_PROGRESS);
        }

        //save updated data
        dataManager.save(user);
    }
}